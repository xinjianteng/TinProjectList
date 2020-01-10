package com.tin.projectlist.app.library.reader.model.parser.oeb;

import com.core.common.XMLNamespaces;
import com.core.common.util.MimeType;
import com.core.common.util.MiscUtil;
import com.core.file.GBFile;
import com.core.file.image.GBFileImage;
import com.core.xml.GBStringMap;
import com.core.xml.GBXMLReaderAdapter;

/*
 * html文件image图片解析类
 */
public class XHTMLImageFinder extends GBXMLReaderAdapter {
	public static GBFileImage getCoverImage(GBFile coverFile) {
		if (coverFile == null) {
			return null;
		}

		final String ext = coverFile.getExtension();
		if ("gif".equals(ext) || "jpg".equals(ext) || "jpeg".equals(ext)) {
			return new GBFileImage(MimeType.IMAGE_AUTO, coverFile);
		} else {
			return new XHTMLImageFinder().readImage(coverFile);
		}
	}

	private String myXHTMLPathPrefix;
	private GBFileImage myImage;

	GBFileImage readImage(GBFile file) {
		myXHTMLPathPrefix = MiscUtil.htmlDirectoryPrefix(file);
		myImage = null;
		readQuietly(file);
		return myImage;
	}

	@Override
	public boolean processNamespaces() {
		return true;
	}

	@Override
	public boolean startElementHandler(String tag, GBStringMap attributes) {
		tag = tag.toLowerCase();
		String href = null;
		if ("img".equals(tag)) {
			href = attributes.getValue("src");
		} else if ("image".equals(tag)) {
			href = getAttributeValue(attributes, XMLNamespaces.XLink, "href");
		}

		if (href != null) {
			GBFile gbFile=GBFile.createFileByPath(myXHTMLPathPrefix + MiscUtil.decodeHtmlReference(href));
			if(null==gbFile){
				return false;
			}
			myImage = new GBFileImage(
					MimeType.IMAGE_AUTO,gbFile

			);
			return true;
		}

		return false;
	}
}
