package com.tin.projectlist.app.library.reader.parser.text.hyphenation;

import com.tin.projectlist.app.library.reader.parser.common.util.ArrayUtils;
import com.tin.projectlist.app.library.reader.parser.xml.GBStringMap;
import com.tin.projectlist.app.library.reader.parser.xml.GBXMLReaderAdapter;

final class GBTextHyphenationReader extends GBXMLReaderAdapter {
	private static final String PATTERN = "pattern";

	private final GBTextTeXHyphenator myHyphenator;
	private boolean myReadPattern;
	private char[] myBuffer = new char[10];
	private int myBufferLength;

	GBTextHyphenationReader(GBTextTeXHyphenator hyphenator) {
		myHyphenator = hyphenator;
	}

	public boolean startElementHandler(String tag, GBStringMap attributes) {
		if (PATTERN.equals(tag)) {
			myReadPattern = true;
		}
		return false;
	}

	public boolean endElementHandler(String tag) {
		if (PATTERN.equals(tag)) {
			myReadPattern = false;
			final int len = myBufferLength;
			if (len != 0) {
				myHyphenator.addPattern(new GBTextTeXHyphenationPattern(myBuffer, 0, len, true));
			}
			myBufferLength = 0;
		}
		return false;
	}

	public void characterDataHandler(char[] ch, int start, int length) {
		if (myReadPattern) {
			char[] buffer = myBuffer;
			final int oldLen = myBufferLength;
			final int newLen = oldLen + length;
			if (newLen > buffer.length) {
				buffer = ArrayUtils.createCopy(buffer, oldLen, newLen + 10);
				myBuffer = buffer;
			}
			System.arraycopy(ch, start, buffer, oldLen, length);
			myBufferLength = newLen;
		}
	}
}
