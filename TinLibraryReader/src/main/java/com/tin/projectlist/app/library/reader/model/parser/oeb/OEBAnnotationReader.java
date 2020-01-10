package com.tin.projectlist.app.library.reader.model.parser.oeb;

import java.io.IOException;

import com.core.common.XMLNamespaces;
import com.core.file.GBFile;
import com.core.xml.GBStringMap;
import com.core.xml.GBXMLProcessor;
import com.core.xml.GBXMLReaderAdapter;

/**
 * annotation 注释
 * 类名： OEBAnnotationReader.java<br>
 * 描述： <br>
 * 创建者： jack<br>
 * 创建日期：2013-4-26<br>
 * 版本：  <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
class OEBAnnotationReader extends GBXMLReaderAdapter implements XMLNamespaces {
	private static final int READ_NONE = 0;
	private static final int READ_DESCRIPTION = 1;
	private int myReadState;

	private final StringBuilder myBuffer = new StringBuilder();

	String readAnnotation(GBFile file) {
		myReadState = READ_NONE;
		myBuffer.delete(0, myBuffer.length());

		try {
			GBXMLProcessor.read(this, file, 512);
			final int len = myBuffer.length();
			if (len > 1) {
				if (myBuffer.charAt(len - 1) == '\n') {
					myBuffer.delete(len - 1, len);
				}
				return myBuffer.toString();
			}
			return null;
		} catch (IOException e) {
			return null;
		}
	}

	@Override
	public boolean processNamespaces() {
		return true;
	}

	@Override
	public boolean startElementHandler(String tag, GBStringMap attributes) {
		tag = tag.toLowerCase();
		if (testTag(DublinCore, "description", tag) ||
				testTag(DublinCoreLegacy, "description", tag)) {
			myReadState = READ_DESCRIPTION;
		} else if (myReadState == READ_DESCRIPTION) {
			// TODO: process tags
			myBuffer.append(" ");
		}
		return false;
	}

	@Override
	public void characterDataHandler(char[] data, int start, int len) {
		if (myReadState == READ_DESCRIPTION) {
			myBuffer.append(new String(data, start, len).trim());
		}
	}

	@Override
	public boolean endElementHandler(String tag) {
		if (myReadState != READ_DESCRIPTION) {
			return false;
		}
		tag = tag.toLowerCase();
		if (testTag(DublinCore, "description", tag) ||
				testTag(DublinCoreLegacy, "description", tag)) {
			return true;
		}
		// TODO: process tags
		myBuffer.append(" ");
		return false;
	}
}
