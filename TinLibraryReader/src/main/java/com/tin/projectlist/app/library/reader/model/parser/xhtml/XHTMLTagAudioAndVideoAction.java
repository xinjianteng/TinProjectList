package com.tin.projectlist.app.library.reader.model.parser.xhtml;


import com.tin.projectlist.app.library.reader.model.bookmodel.BookReader;
import com.tin.projectlist.app.library.reader.model.bookmodel.GBTextKind;
import com.tin.projectlist.app.library.reader.parser.common.util.MimeType;
import com.tin.projectlist.app.library.reader.parser.common.util.MiscUtil;
import com.tin.projectlist.app.library.reader.parser.file.GBFile;
import com.tin.projectlist.app.library.reader.parser.file.image.GBFileImage;
import com.tin.projectlist.app.library.reader.parser.text.model.GBAudioEntry;
import com.tin.projectlist.app.library.reader.parser.text.model.GBVideoEntry;
import com.tin.projectlist.app.library.reader.parser.xml.GBStringMap;

/**
 * html5 音频标签
 *
 * @author android
 *
 */
public class XHTMLTagAudioAndVideoAction extends XHTMLTagAction {
	private final byte mKind;

	public XHTMLTagAudioAndVideoAction(byte kind) {
		mKind = kind;
	}

	@Override
	protected void doAtStart(XHTMLReader reader, GBStringMap xmlattributes) {

		String src = xmlattributes.getValue("src");
		final String preload = xmlattributes.getValue("preload");
		final String loop = xmlattributes.getValue("loop");
		final String controls = xmlattributes.getValue("controls");
		final String autoplay = xmlattributes.getValue("autoplay");
		final String height = xmlattributes.getValue("height");
		final String width = xmlattributes.getValue("width");

		if (null != src) {
			src = MiscUtil.decodeHtmlReference(src);
		}

		final GBFile imageFile = GBFile.createFileByPath(reader.myPathPrefix
				+ src);

		if (null == imageFile) {
			if (!src.startsWith("http://") && !src.startsWith("rtsp://")) {
				return;
			}

		}

		final BookReader modelReader = reader.getModelReader();

		modelReader.pushKind(mKind);
		// modelReader.beginParagraph(GBTextKind.AUDIO);

		GBAudioEntry mediaEntry = null;
		if (mKind == GBTextKind.AUDIO) {
			mediaEntry = new GBAudioEntry(src, preload, loop, controls,
					autoplay);
		} else if (mKind == GBTextKind.VIDEO) {
			mediaEntry = new GBVideoEntry(src, preload, loop, controls,
					autoplay, height, width);
		}

		GBFileImage media = null;

		if (mKind == GBTextKind.AUDIO) {
			modelReader.addHtml5AudioControl(mediaEntry.toChars());
			if (null != imageFile) {
				media = new GBFileImage(MimeType.MP3, imageFile);
			}

		} else if (mKind == GBTextKind.VIDEO) {
			modelReader.addHtml5VideoControl(mediaEntry.toChars());
			if (null != imageFile) {
				media = new GBFileImage(MimeType.VIDEO_3GPP, imageFile);
			}

		}
		modelReader.addImage(src, media);

	}

	@Override
	protected void doAtEnd(XHTMLReader reader) {
		// final BookReader modelReader = reader.getModelReader();
		// modelReader.endParagraph();
		// modelReader.popKind();

	}

}
