package com.core.text.model;
/**
 *
 * 描述： 对齐类型
 * 创建者： 燕冠楠<br>
 * 创建日期：2013-3-26<br>
 */
public interface GBTextAlignmentType {
	byte ALIGN_UNDEFINED = 0;
	byte ALIGN_LEFT = 1;
	byte ALIGN_RIGHT = 2;
	byte ALIGN_CENTER = 3;
	byte ALIGN_JUSTIFY = 4;
	byte ALIGN_LINESTART = 5; // left for LTR languages and right for RTL
}
