package com.core.object;

/**
 * 描述：搜索条件实体 <br>
 * 创建者： 符晨<br>
 * 创建日期：2013-3-28<br>
 */
public class GBSearchPattern {
	public final boolean IgnoreCase;//是否无视大小写
	public final char[] LowerCasePattern;//小写形式
	public final char[] UpperCasePattern;//大写形式

	public GBSearchPattern(String pattern, boolean ignoreCase) {
		IgnoreCase = ignoreCase;
		if (IgnoreCase) {
			LowerCasePattern = pattern.toLowerCase().toCharArray();
			UpperCasePattern = pattern.toUpperCase().toCharArray();
		} else {
			LowerCasePattern = pattern.toCharArray();
			UpperCasePattern = null;
		}
	}

	public int getLength() {
		return LowerCasePattern.length;
	}
}
