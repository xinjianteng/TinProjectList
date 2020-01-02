package com.core.text.style;

import com.core.object.GBBoolean3;
import com.core.option.GBIntegerOption;
import com.core.option.GBIntegerRangeOption;
import com.core.text.model.GBTextHyperlink;


/**
 * 类名： ZLTextFullStyleDecoration.java<br>
 * 描述： 文本显示样式完整配置封装<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-22<br>
 * 版本：  <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public class GBTextFullDecorationStyleOption extends GBTextDecorationStyleOption {
	public final GBIntegerRangeOption SpaceBeforeOption;
	public final GBIntegerRangeOption SpaceAfterOption;
	public final GBIntegerRangeOption LeftIndentOption;
	public final GBIntegerRangeOption RightIndentOption;
	public final GBIntegerRangeOption FirstLineIndentDeltaOption;

	public final GBIntegerRangeOption AlignmentOption;

	public final GBIntegerOption LineSpacePercentOption;
	/**
	 * 构造方式
	 * @param name 样式名称
	 * @param fontFamily 字体名
	 * @param fontSizeDelta 默认字体大小
	 * @param bold 是否粗体
	 * @param italic 是否斜体
	 * @param underline 是否有下划线
	 * @param strikeThrough  是否有删除线
	 * @param spaceBefore  前
	 * @param spaceAfter
	 * @param leftIndent
	 * @param rightIndent
	 * @param firstLineIndentDelta
	 * @param verticalShift
	 * @param alignment
	 * @param lineSpace
	 * @param allowHyphenations
	 */
	public GBTextFullDecorationStyleOption(String name, String fontFamily, int fontSizeDelta, GBBoolean3 bold,
										   GBBoolean3 italic, GBBoolean3 underline, GBBoolean3 strikeThrough, int spaceBefore, int spaceAfter,
										   int leftIndent, int rightIndent, int firstLineIndentDelta, int verticalShift, byte alignment,
										   int lineSpace, GBBoolean3 allowHyphenations) {
		super(name, fontFamily, fontSizeDelta, bold, italic, underline, strikeThrough, verticalShift, allowHyphenations);
		SpaceBeforeOption = new GBIntegerRangeOption(STYLE, name + ":spaceBefore", -10, 100, spaceBefore);
		SpaceAfterOption = new GBIntegerRangeOption(STYLE, name + ":spaceAfter", -10, 100, spaceAfter);
		LeftIndentOption = new GBIntegerRangeOption(STYLE, name + ":leftIndent", -300, 300, leftIndent);
		RightIndentOption = new GBIntegerRangeOption(STYLE, name + ":rightIndent", -300, 300, rightIndent);
		FirstLineIndentDeltaOption = new GBIntegerRangeOption(STYLE, name + ":firstLineIndentDelta", -300, 300, firstLineIndentDelta);
		AlignmentOption = new GBIntegerRangeOption(STYLE, name + ":alignment", 0, 4, alignment);
		LineSpacePercentOption = new GBIntegerOption(STYLE, name + ":lineSpacePercent", lineSpace);
	}

	public GBTextStyle createDecoratedStyle(GBTextStyle base, GBTextHyperlink hyperlink) {
		return new GBTextFullyDecoratedStyle(base, this, hyperlink);
	}
}
