package com.tin.projectlist.app.library.reader.parser.common.util;

/**
 * 描述：字符工具类 <br>
 * 创建者： T_xin<br>
 * 创建日期：2020-01-02<br>
 */
public abstract class GBCharacterUtil {
	/**
	 * 传入的char对象是否是英文字母或拉丁符号|斯拉夫符号
	 * @param ch 需要匹配的字符
	 * @return 若是返回true 否则返回false 如:中文
	 */
	public static boolean isLetter(char ch) {
		return
				(('a' <= ch) && (ch <= 'z')) ||
						(('A' <= ch) && (ch <= 'Z')) ||
						// ' is "letter" (in French, for example)
						(ch == '\'') ||
						// ^ is "letter" (in Esperanto)
						(ch == '^') ||
						// latin1  拉丁
						((0xC0 <= ch) && (ch <= 0xFF) && (ch != 0xD7) && (ch != 0xF7)) ||
						// extended latin1
						((0x100 <= ch) && (ch <= 0x178)) ||
						// cyrillic  //斯拉夫字母
						((0x410 <= ch) && (ch <= 0x44F)) ||
						// cyrillic YO & yo
						(ch == 0x401) || (ch == 0x451);
	}
}
