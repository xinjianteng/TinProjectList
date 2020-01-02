package com.core.common.util;

import com.core.object.GBSearchPattern;

/**
 * 搜索工具类<br>
 *
 * 创建者： 符晨<br>
 * 创建日期：2013-4-3<br>
 */
public abstract class GBSearchUtil {
	private GBSearchUtil() {
	}

	/**
	 * 搜索，返回传入char数组中第一个符合搜索条件的字符下标
	 * @param text 需要被搜索的文本内容
	 * @param offset 被搜索文本开始下标
	 * @param length 被搜索文本总长度
	 * @param pattern 搜索条件
	 * @return -1 或  text中第一个符合搜索条件的字符下标
	 */
	public static int find(char[] text, int offset, int length, final GBSearchPattern pattern) {
		return find(text, offset, length, pattern, 0);
	}

	/**
	 * 搜索，返回传入char数组中第一个符合搜索条件的字符下标
	 * @param text 需要被搜索的文本内容
	 * @param offset 被搜索文本开始下标
	 * @param length 被搜索文本总长度
	 * @param pattern 搜索条件
	 * @param pos 开始搜索下标（相对offset位置）
	 * @return -1 或  text中第一个符合搜索条件的字符下标
	 */
	public static int find(char[] text, int offset, int length, final GBSearchPattern pattern, int pos) {
		if (pos < 0) {
			pos = 0;
		}
		final char[] lower = pattern.LowerCasePattern;
		final int patternLength = lower.length;
		final int last = offset + length - patternLength;
		if (pattern.IgnoreCase) {
			final char[] upper = pattern.UpperCasePattern;
			final char firstCharLower = lower[0];
			final char firstCharUpper = upper[0];
			for (int i = offset + pos; i <= last; i++) {
				final char current = text[i];
				if ((current == firstCharLower) || (current == firstCharUpper)) {
					int j = 1;
					for (int k = i + 1; j < patternLength; ++j, ++k) {
						final char symbol = text[k];
						if ((lower[j] != symbol) &&
								(upper[j] != symbol)) {
							break;
						}
					}
					if (j >= patternLength) {
						return i - offset;
					}
				}
			}
		} else {
			final char firstChar = lower[0];
			for (int i = offset + pos; i <= last; i++) {
				if (text[i] == firstChar) {
					int j = 1;
					for (int k = i + 1; j < patternLength; ++j, ++k) {
						if (lower[j] != text[k]) {
							break;
						}
					}
					if (j >= patternLength) {
						return i - offset;
					}
				}
			}
		}
		return -1;
	}
}
