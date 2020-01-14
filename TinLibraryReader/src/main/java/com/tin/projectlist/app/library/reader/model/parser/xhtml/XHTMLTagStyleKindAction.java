package com.tin.projectlist.app.library.reader.model.parser.xhtml;

import java.util.ArrayList;
import java.util.Stack;

import com.tin.projectlist.app.library.reader.parser.text.model.style.GBTextStyleEntry;
import com.tin.projectlist.app.library.reader.parser.text.model.style.GBTextStyleEntryProxy;
import com.tin.projectlist.app.library.reader.parser.xml.GBStringMap;

/**
 * 样式标签处理
 * @author yangn
 *
 */
public abstract class XHTMLTagStyleKindAction extends XHTMLTagAction {

	final String TAG = "XHTMLTagStyleAction";
	Stack<Integer> styleSpoorStack = null;// 记录样式偏移量
	int FALSE = -1;

	/**
	 *准备doAtStart之前       样式标签解析
	 * @param reader
	 * @param xmlattributes
	 */
	protected abstract void readStyleKind(XHTMLReader reader, GBStringMap xmlattributes);

	@Override
	protected void doAtStart(XHTMLReader reader, GBStringMap xmlattributes) {

		readStyleKind(reader,xmlattributes);

		styleSpoorStack = reader.myModelReader.getStylePossible();
		// 最终应使用样式
		ArrayList<GBTextStyleEntry> prepareStyle = reader.styleReader
				.getCurrentEntrys();

		if (null == prepareStyle || prepareStyle.size() == 0) {
			styleSpoorStack.push(FALSE);
		} else {
			// 将样式信息写入数据 style write...
			try {

				GBTextStyleEntryProxy proxy = new GBTextStyleEntryProxy();
				// proxy.putAll(prepareStyle);
				for (GBTextStyleEntry styleEntry : prepareStyle) {
					proxy.put(styleEntry);
				}
				int ret = reader.myModelReader.addStyleEntry2(proxy);
				styleSpoorStack.push(ret);

			} catch (Exception ex) {
				// doAtStartProxy(reader,styleReader,xmlattributes,tag,referencePrefix,isInner);
				ex.printStackTrace();
				styleSpoorStack.push(FALSE);
			}
		}
	}

	@Override
	protected void doAtEnd(XHTMLReader reader) {

		if (styleSpoorStack == null || styleSpoorStack.isEmpty()) {
			return;
		}

		if (styleSpoorStack.pop() >= 0) {
			reader.myModelReader.addStyleColse();
		}
	}

}
