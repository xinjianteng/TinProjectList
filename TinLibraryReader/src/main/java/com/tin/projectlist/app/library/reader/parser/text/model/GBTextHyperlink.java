package com.tin.projectlist.app.library.reader.parser.text.model;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
/**
 * 类名： GBTextHyperlink.java#ZLTextHyperlink<br>
 * 描述： 超链接文本信息封装<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-19<br>
 * 版本：  <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public class GBTextHyperlink {
	public final byte Type;  //超链接类型 ，内部/外部/空
	public final String Id;  //链接内容
	//链接文字元素在段落（GBTextParagrah)对象中元素集合的下标列表
	private List<Integer> myElementIndexes;
	//空链接
	public static final GBTextHyperlink NO_LINK = new GBTextHyperlink((byte)0, null);

	public GBTextHyperlink(byte type, String id) {
		Type = type;
		Id = id;
	}

	public void addElementIndex(int elementIndex) {
		if (myElementIndexes == null) {
			myElementIndexes = new LinkedList<Integer>();
		}
		myElementIndexes.add(elementIndex);
	}

	public List<Integer> elementIndexes() {
		return myElementIndexes != null
				? Collections.unmodifiableList(myElementIndexes)
				: Collections.<Integer>emptyList();
	}
}
