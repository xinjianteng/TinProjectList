package com.tin.projectlist.app.library.reader.parser.text.widget;
/**
 * 界面标签控制元素
 * original:ZLTextControlElement
 * 类名： .java<br>
 * 描述： <br>
 * 创建者： yangn<br>
 * 创建日期：2013-4-11<br>
 * 版本：  <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public class GBTextControlElement extends GBTextElement {
	private final static GBTextControlElement[] myStartElements = new GBTextControlElement[256];
	private final static GBTextControlElement[] myEndElements = new GBTextControlElement[256];

	/**
	 *
	 * 功能描述： <br>
	 * 创建者： yangn<br>
	 * 创建日期：2013-4-11<br>
	 * @param  kind 控件类别
	 * @param isStart  开始|结束 控件标志
	 */
	public static GBTextControlElement get(byte kind, boolean isStart) {
		GBTextControlElement[] elements = isStart ? myStartElements : myEndElements;
		GBTextControlElement element = elements[kind & 0xFF];
		if (element == null) {
			element = new GBTextControlElement(kind, isStart);
			elements[kind & 0xFF] = element;
		}
		return element;
	}
	//标签标识
	public final byte Kind;
	//是否开始 true 开始，false 结束
	public final boolean IsStart;

	protected GBTextControlElement(byte kind, boolean isStart) {
		Kind = kind;
		IsStart = isStart;
	}
}
