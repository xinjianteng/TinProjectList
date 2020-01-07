package com.tin.projectlist.app.library.reader.parser.text.iterator;


import com.tin.projectlist.app.library.reader.parser.platform.GBLibrary;

/**
 * 类名： GBTextSelectionCursor.java<br>
 * 描述： 文本选择拖把信息定义<br>
 * 创建者： jack<br>
 * 创建日期：2013-5-10<br>
 * 版本：  <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public enum GBTextSelectionCursor {
	None,  // 无
	Left,   //左锚点
	Right; //右锚点

	private static int ourHeight;   //拖把高
	private static int ourWidth;   //拖把宽
	private static int ourAccent;  //拖把偏移量

	private static void init() {
		if (ourHeight == 0) {
			final int dpi = GBLibrary.Instance().getDisplayDPI();
			ourAccent = dpi / 12;
			ourWidth = dpi / 6;
			ourHeight = dpi / 4;
		}
	}

	public static int getHeight() {
		init();
		return ourHeight;
	}

	public static int getWidth() {
		init();
		return ourWidth;
	}

	public static int getAccent() {
		init();
		return ourAccent;
	}
}
