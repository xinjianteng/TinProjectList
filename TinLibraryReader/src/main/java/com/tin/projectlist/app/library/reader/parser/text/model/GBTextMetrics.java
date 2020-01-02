package com.core.text.model;
/**
 *
 * 描述：字体样式度量
 * 创建者： 燕冠楠<br>
 * 创建日期：2013-3-26<br>
 */
public final class GBTextMetrics {
	public final int DPI;   //分辨率，每英寸像素点
	public final int DefaultFontSize;  //默认字体大小
	public final int FontSize; //字体大小
	public final int FontXHeight; //字体大小的1.5倍
	public final int FullWidth;      //全屏宽
	public final int FullHeight;     //全屏高

	public GBTextMetrics(int dpi, int defaultFontSize, int fontSize, int fontXHeight, int fullWidth, int fullHeight) {
		DPI = dpi;
		DefaultFontSize = defaultFontSize;
		FontSize = fontSize;
		FontXHeight = fontXHeight;
		FullWidth = fullWidth;
		FullHeight = fullHeight;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof GBTextMetrics)) {
			return false;
		}
		final GBTextMetrics oo = (GBTextMetrics)o;
		return
				FontSize == oo.FontSize &&
						FontXHeight == oo.FontXHeight &&
						FullWidth == oo.FullWidth &&
						FullHeight == oo.FullHeight;
	}

	@Override
	public int hashCode() {
		return FontSize + 13 * (FontXHeight + 13 * (FullHeight + 13 * FullWidth));
	}
}
