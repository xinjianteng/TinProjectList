package com.tin.projectlist.app.library.reader.model.parser.oeb;

import com.core.common.util.MiscUtil;
import com.core.file.GBFile;
import com.core.file.zip.GBArchiveEntryFile;
import com.core.xml.GBStringMap;
import com.core.xml.GBXMLReaderAdapter;
import com.geeboo.read.model.bookmodel.BookReader;
import com.geeboo.read.model.bookmodel.BookReadingException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * 类名： NCXReader.java<br>
 * 描述：  epub图书中描述章节目录的ncx文件解析器<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-26<br>
 * 版本：  <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
class NCXReader extends GBXMLReaderAdapter {
	static class NavPoint {
		final int Order;
		final int Level;
		String Text = "";
		String ContentHRef = "";

		NavPoint(int order, int level) {
			Order = order;
			Level = level;
		}
	}

	private final TreeMap<Integer,NavPoint> myNavigationMap = new TreeMap<Integer,NavPoint>();
	private final ArrayList<NavPoint> myPointStack = new ArrayList<NavPoint>();

	private static final int READ_NONE = 0;
	private static final int READ_MAP = 1;
	private static final int READ_POINT = 2;
	private static final int READ_LABEL = 3;
	private static final int READ_TEXT = 4;

	int myReadState = READ_NONE;
	int myPlayIndex = -65535;
	private String myLocalPathPrefix;

	NCXReader(BookReader modelReader) {
	}

	void readFile(GBFile file) throws BookReadingException {
		myLocalPathPrefix = MiscUtil.archiveEntryName(MiscUtil.htmlDirectoryPrefix(file));
		try {
			read(file);
		} catch (IOException e) {
			throw new BookReadingException(e, file);
		}
	}

	Map<Integer,NavPoint> navigationMap() {
		return myNavigationMap;
	}

	private static final String TAG_NAVMAP = "navmap";
	private static final String TAG_NAVPOINT = "navpoint";
	private static final String TAG_NAVLABEL = "navlabel";
	private static final String TAG_CONTENT = "content";
	private static final String TAG_TEXT = "text";

	private static final String ATTRIBUTE_PLAYORDER = "playOrder";

	private int atoi(String number) {
		try {
			return Integer.parseInt(number);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	@Override
	public boolean startElementHandler(String tag, GBStringMap attributes) {
		tag = tag.toLowerCase().intern();
		switch (myReadState) {
			case READ_NONE:
				if (tag == TAG_NAVMAP) {
					myReadState = READ_MAP;
				}
				break;
			case READ_MAP:
				if (tag == TAG_NAVPOINT) {
					final String order = attributes.getValue(ATTRIBUTE_PLAYORDER);
					final int index = (order != null) ? atoi(order) : myPlayIndex++;
					myPointStack.add(new NavPoint(index, myPointStack.size()));
					myReadState = READ_POINT;
				}
				break;
			case READ_POINT:
				if (tag == TAG_NAVPOINT) {
					final String order = attributes.getValue(ATTRIBUTE_PLAYORDER);
					final int index = (order != null) ? atoi(order) : myPlayIndex++;
					myPointStack.add(new NavPoint(index, myPointStack.size()));
				} else if (tag == TAG_NAVLABEL) {
					myReadState = READ_LABEL;
				} else if (tag == TAG_CONTENT) {
					final int size = myPointStack.size();
					if (size > 0) {
						myPointStack.get(size - 1).ContentHRef =
								GBArchiveEntryFile.normalizeEntryName(
										myLocalPathPrefix + MiscUtil.decodeHtmlReference(attributes.getValue("src"))
								);
					}
				}
				break;
			case READ_LABEL:
				if (TAG_TEXT == tag) {
					myReadState = READ_TEXT;
				}
				break;
			case READ_TEXT:
				break;
		}
		return false;
	}

	@Override
	public boolean endElementHandler(String tag) {
		tag = tag.toLowerCase().intern();
		switch (myReadState) {
			case READ_NONE:
				break;
			case READ_MAP:
				if (TAG_NAVMAP == tag) {
					myReadState = READ_NONE;
				}
				break;
			case READ_POINT:
				if (TAG_NAVPOINT == tag) {
					NavPoint last = myPointStack.get(myPointStack.size() - 1);
					if (last.Text.length() == 0) {
						last.Text = "...";
					}
					myNavigationMap.put(last.Order, last);
					myPointStack.remove(myPointStack.size() - 1);
					myReadState = (myPointStack.isEmpty()) ? READ_MAP : READ_POINT;
				}
			case READ_LABEL:
				if (TAG_NAVLABEL == tag) {
					myReadState = READ_POINT;
				}
				break;
			case READ_TEXT:
				if (TAG_TEXT == tag) {
					myReadState = READ_LABEL;
				}
				break;
		}
		return false;
	}

	@Override
	public void characterDataHandler(char[] ch, int start, int length) {
		if (myReadState == READ_TEXT) {
			final ArrayList<NavPoint> stack = myPointStack;
			final NavPoint last = stack.get(stack.size() - 1);
			last.Text += new String(ch, start, length);
		}
	}

	@Override
	public boolean dontCacheAttributeValues() {
		return true;
	}
}
