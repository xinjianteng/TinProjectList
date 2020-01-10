package com.tin.projectlist.app.library.reader.model.parser.oeb;

import com.core.common.XMLNamespaces;
import com.core.common.util.MimeType;
import com.core.common.util.MiscUtil;
import com.core.file.GBFile;
import com.core.file.image.GBFileImage;
import com.core.xml.GBStringMap;
import com.core.xml.GBXMLReaderAdapter;

import java.util.ArrayList;
import java.util.List;

/*
 * epub图书图片/封面文件解析
 */
class OEBCoverBackgroundReader extends GBXMLReaderAdapter implements
		XMLNamespaces {
	private GBFileImage myImage;
	private String myPathPrefix;
	// 解析状态
	private static final int READ_NOTHING = 0; // 没开始解析
	private static final int READ_METADATA = 1; // 解析到metadata
	private static final int READ_MANIFEST = 2; // 解析到manifest
	private static final int READ_GUIDE = 3; // 解析到guide
	private int myReadState = READ_NOTHING;

	private String myCoverId;

	public GBFileImage readCover(GBFile file) {
		myPathPrefix = MiscUtil.htmlDirectoryPrefix(file);
		myReadState = READ_NOTHING;
		myCoverId = null;
		myImage = null;
		readQuietly(file);
		if (myImage == null && listImages.size() > 0)
			myImage = imageByHref(listImages.get(0));
		return myImage;
	}

	private static final String GUIDE = "guide";
	private static final String MANIFEST = "manifest";

	@Override
	public boolean processNamespaces() {
		return true;
	}

	private List<String> listImages = new ArrayList<String>();

	@Override
	public boolean startElementHandler(String tag, GBStringMap attributes) {
		tag = tag.toLowerCase();
		switch (myReadState) {
			case READ_NOTHING:
				if (GUIDE.equals(tag)) {
					myReadState = READ_GUIDE;
				} else if (MANIFEST.equals(tag)) { // && myCoverId != null
					myReadState = READ_MANIFEST;
				} else if (testTag(OpenPackagingFormat, "metadata", tag)) {
					myReadState = READ_METADATA;
				}
				break;
			case READ_GUIDE:
				if ("reference".equals(tag)) {
					final String type = attributes.getValue("type");
					if ("cover" == type) {
						final String href = attributes.getValue("href");
						if (href != null) {
							final GBFile coverFile = GBFile
									.createFileByPath(myPathPrefix
											+ MiscUtil.decodeHtmlReference(href));
							myImage = XHTMLImageFinder.getCoverImage(coverFile);
							return true;
						}
					} else if ("other.ms-coverimage-standard".equals(type)) {
						myImage = imageByHref(attributes.getValue("href"));
						if (myImage != null) {
							return true;
						}
					}
				}
				break;
			case READ_METADATA:
				if (testTag(OpenPackagingFormat, "meta", tag)) {
					final String name = attributes.getValue("name");
					if ("cover".equals(name)) {
						myCoverId = attributes.getValue("content");
					}
				}
				break;
			case READ_MANIFEST:
				if ("item".equals(tag)) {
					if (myCoverId != null
							&& myCoverId.equals(attributes.getValue("id"))) {
						if (attributes.getValue("media-type").toLowerCase()
								.startsWith(MimeType.IMAGE_PREFIX)) {
							myImage = imageByHref(attributes.getValue("href"));
							if (myImage != null) {
								return true;
							}
						} else {
							final String href = attributes.getValue("href");
							if (href != null) {
								final GBFile coverFile = GBFile
										.createFileByPath(myPathPrefix
												+ MiscUtil
												.decodeHtmlReference(href));
								myImage = XHTMLImageFinder.getCoverImage(coverFile);
								return true;
							}
						}
					} else if (myCoverId == null
							&& attributes.getValue("media-type").toLowerCase()
							.startsWith(MimeType.IMAGE_PREFIX)) {
						if (attributes.getValue("id").toLowerCase()
								.contains("cover")) {
							myImage = imageByHref(attributes.getValue("href"));
							if (myImage != null) {
								return true;
							}
						} else {
							listImages.add(attributes.getValue("href"));
						}
					} else if (myCoverId == null
							&& attributes.getValue("media-type").toLowerCase()
							.startsWith(MimeType.APP_XHTML.Name)) {
						if (attributes.getValue("id").toLowerCase()
								.contains("cover")) {
							final String href = attributes.getValue("href");
							if (href != null) {
								final GBFile coverFile = GBFile
										.createFileByPath(myPathPrefix
												+ MiscUtil
												.decodeHtmlReference(href));
								myImage = XHTMLImageFinder.getCoverImage(coverFile);
								return true;
							}
						}
					}
				}
				break;
		}
		return false;
	}

	private GBFileImage imageByHref(String href) {
		if (href == null) {
			return null;
		}
		return new GBFileImage(MimeType.IMAGE_AUTO,
				GBFile.createFileByPath(myPathPrefix
						+ MiscUtil.decodeHtmlReference(href)));
	}

	@Override
	public boolean endElementHandler(String tag) {
		tag = tag.toLowerCase();
		switch (myReadState) {
			case READ_GUIDE:
				if (GUIDE.equals(tag)) {
					myReadState = READ_NOTHING;
				}
				break;
			case READ_MANIFEST:
				if (MANIFEST.equals(tag)) {
					myReadState = READ_NOTHING;
				}
				break;
			case READ_METADATA:
				if (testTag(OpenPackagingFormat, "metadata", tag)) {
					myReadState = READ_NOTHING;
				}
				break;
		}
		return false;
	}
}
