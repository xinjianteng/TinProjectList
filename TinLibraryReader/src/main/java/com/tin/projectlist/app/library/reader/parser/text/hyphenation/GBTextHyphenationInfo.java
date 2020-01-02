package com.core.text.hyphenation;
/**
 * 断字信息
 * 类名： .java<br>
 * 描述： <br>
 * 创建者： yangn<br>
 * 创建日期：2013-4-22<br>
 * 版本：  <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public final class GBTextHyphenationInfo {
	final boolean[] Mask;

	public GBTextHyphenationInfo(int length) {
		Mask = new boolean[length - 1];
	}

	/**
	 * 判断该段是否有效（字母）
	 * 功能描述： <br>
	 * 创建者： yangn<br>
	 * 创建日期：2013-4-22<br>
	 * @param
	 */
	public boolean isHyphenationPossible(int position) {
		return (position < Mask.length && Mask[position]);
	}
}
