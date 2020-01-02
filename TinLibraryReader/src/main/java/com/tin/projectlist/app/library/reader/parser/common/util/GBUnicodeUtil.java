package com.core.common.util;

/*
 * Unicode(统一码、万国码、单一码) 是基于通用字符集（Universal Character Set）的标准来发展，并且同时也以书本的形式
 */


/**
 * Unicode工具类
 * @author fuchen
 * @date 2013-4-9
 */
public class GBUnicodeUtil {

	//FIXME 么明白啥意思
	public static int utf8Length(byte[] buffer, int str, int len) {
		final int last = str + len;
		int counter = 0;
		while (str < last) {
			final int bt = buffer[str];
			if ((bt & 0x80) == 0) {
				++str;
			} else if ((bt & 0x20) == 0) {
				str += 2;
			} else if ((bt & 0x10) == 0) {
				str += 3;
			} else {
				str += 4;
			}
			++counter;
		}
		return counter;
	}
}
