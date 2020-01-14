package com.tin.projectlist.app.library.reader.model.parser.xhtml;


import com.tin.projectlist.app.library.reader.model.bookmodel.BookReader;
import com.tin.projectlist.app.library.reader.model.bookmodel.GBTextKind;
import com.tin.projectlist.app.library.reader.parser.common.util.ArrayUtils;
import com.tin.projectlist.app.library.reader.parser.xml.GBStringMap;

/**
 * 类名： XHTMLTagHyperlinkAction.java<br>
 * 描述： 链接标签处理类<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-26<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
class XHTMLTagHyperlinkAction extends XHTMLTagAction {
	private byte[] myHyperlinkStack = new byte[10];
	private int myHyperlinkStackSize;

	private static boolean isReference(String text) {
		switch (text.charAt(0)) {
			default:
				return false;
			case 'f':
				return text.startsWith("fbreader-action:")
						|| text.startsWith("ftp://");
			case 'h':
				return text.startsWith("http://") || text.startsWith("https://");
			case 'm':
				return text.startsWith("mailto:");
		}
	}

	protected void doAtStart(XHTMLReader reader, GBStringMap xmlattributes) {
		final BookReader modelReader = reader.getModelReader();
		final String href = xmlattributes.getValue("href");
		if (myHyperlinkStackSize == myHyperlinkStack.length) {
			myHyperlinkStack = ArrayUtils.createCopy(myHyperlinkStack,
					myHyperlinkStackSize, 2 * myHyperlinkStackSize);
		}
		if (href != null && href.length() > 0) {
			String link = href;
			final byte hyperlinkType;
			// 检查时候是网络连接
			if (isReference(link)) {
				hyperlinkType = GBTextKind.EXTERNAL_HYPERLINK;
			} else {
				hyperlinkType = GBTextKind.INTERNAL_HYPERLINK;
				final int index = href.indexOf('#');
				if (index == 0) {
					link = new StringBuilder(reader.myReferencePrefix).append(
							href, 1, href.length()).toString();
				} else if (index > 0) {
					link = new StringBuilder(reader.getLocalFileAlias(href
							.substring(0, index))).append(href, index,
							href.length()).toString();
				} else {
					link = reader.getLocalFileAlias(href);
				}
			}
			myHyperlinkStack[myHyperlinkStackSize++] = hyperlinkType;
			modelReader.addHyperlinkControl(hyperlinkType, link);
		} else {
			myHyperlinkStack[myHyperlinkStackSize++] = GBTextKind.REGULAR;
		}
		final String name = xmlattributes.getValue("name");
		if (name != null) {
			modelReader.addHyperlinkLabel(reader.myReferencePrefix + name);
		}
	}

	protected void doAtEnd(XHTMLReader reader) {
		byte kind = myHyperlinkStack[--myHyperlinkStackSize];
		if (kind != GBTextKind.REGULAR) {
			reader.getModelReader().addControl(kind, false);
		}
	}
}
