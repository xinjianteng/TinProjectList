package com.tin.projectlist.app.library.reader.view.widget;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.core.object.GBColor;

/**
 * 类名： GBAndroidColorUtil.java<br>
 * 描述： android平台颜色处理类<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-26<br>
 * 版本：  <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public abstract class GBAndroidColorUtil {
	/**
	 * 功能描述： 获取一个带透明度的颜色<br>
	 * 创建者： jack<br>
	 * 创建日期：2013-4-26<br>
	 * @param color 颜色rgb封装
	 * @param alpha 透明度
	 * @return
	 */
	public static int rgba(GBColor color, int alpha) {
		return Color.argb(alpha, color.Red, color.Green, color.Blue);
	}
	/**
	 * 功能描述： 获取一个颜色<br>
	 * 创建者： jack<br>
	 * 创建日期：2013-4-26<br>
	 * @param color 颜色rgb封装
	 * @return
	 */
	public static int rgb(GBColor color) {
		return Color.rgb(color.Red, color.Green, color.Blue);
	}
	/**
	 * 功能描述：根据图片获取一个均色<br>
	 * 创建者： jack<br>
	 * 创建日期：2013-4-26<br>
	 * @param bitmap 图片对象
	 * @return
	 */
	public static GBColor getAverageColor(Bitmap bitmap) {
		final int w = Math.min(bitmap.getWidth(), 7);
		final int h = Math.min(bitmap.getHeight(), 7);
		long r = 0, g = 0, b = 0;
		for (int i = 0; i < w; ++i) {
			for (int j = 0; j < h; ++j) {
				int color = bitmap.getPixel(i, j);
				r += color & 0xFF0000;
				g += color & 0xFF00;
				b += color & 0xFF;
			}
		}
		r /= w * h;
		g /= w * h;
		b /= w * h;
		r >>= 16;
		g >>= 8;
		return new GBColor((int)(r & 0xFF), (int)(g & 0xFF), (int)(b & 0xFF));
	}
}
