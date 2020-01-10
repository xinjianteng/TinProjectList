package com.tin.projectlist.app.library.reader.model.book;

import java.math.BigDecimal;

/**
 * 类名： SeriesInfo.java<br>
 * 描述： <br>
 * 创建者： jack<br>
 * 创建日期：2013-4-25<br>
 * 版本：  <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public final class SeriesInfo {
	public static SeriesInfo createSeriesInfo(String title, String index) {
		if (title == null) {
			return null;
		}
		return new SeriesInfo(title, createIndex(index));
	}

	public static BigDecimal createIndex(String index) {
		try {
			return index != null ? new BigDecimal(index).stripTrailingZeros() : null;
		} catch (NumberFormatException e) {
			return null;
		}
	}

	public final Series Series;
	public final BigDecimal Index;

	SeriesInfo(String title, BigDecimal index) {
		Series = new Series(title);
		Index = index;
	}
}
