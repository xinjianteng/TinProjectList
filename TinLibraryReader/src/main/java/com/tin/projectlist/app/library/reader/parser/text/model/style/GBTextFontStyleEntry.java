package com.tin.projectlist.app.library.reader.parser.text.model.style;


import com.tin.projectlist.app.library.base.utils.LogUtils;
import com.tin.projectlist.app.library.reader.parser.common.util.NumUtil;

/**
 *
 * 类名： .java<br>
 * 描述：字体样式实体类 创建者： yangn<br>
 * 创建日期：2013-5-24<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public final class GBTextFontStyleEntry extends GBTextStyleEntry {
	final String TAG = "GBTextOtherStyleEntry";

	public GBTextFontStyleEntry() {
	}

	public interface FontModifier {
		// font weight val
		short NORMAL = 400;// normal :　 默认值。正常的字体。相当于 400 。声明此值将取消之前任何设置
		short BOLD = 700;// bold :　 粗体。相当于 700 。也相当于 b 对象的作用
		short BOLDER = 450;// bolder :　 比 normal 粗
		short LIGHTER = 200;// lighter 比 normal 细

	}

	private int myColor = -1;// 颜色
	private Length fontSize;// 尺寸
	private String fontFamily = null;// 字体
	private Length lineHeight = null;// line-height
	private short fontWeight = 0;

	public short getFontWeight() {
		return fontWeight;
	}

	public void setFontWeight(short fontWeight) {
		this.fontWeight = fontWeight;
	}

	public void setFontWeight(String fontWeightStr) {
		if (null == fontWeightStr || "".equals(fontWeightStr)) {
			return;
		}

		if ("normal".equalsIgnoreCase(fontWeightStr)) {
			this.fontWeight = FontModifier.NORMAL;
		} else if ("bold".equalsIgnoreCase(fontWeightStr)) {
			this.fontWeight = FontModifier.BOLD;
		} else if ("bolder".equalsIgnoreCase(fontWeightStr)) {
			this.fontWeight = FontModifier.BOLDER;
		} else if ("lighter".equalsIgnoreCase(fontWeightStr)) {
			this.fontWeight = FontModifier.LIGHTER;
		} else {
			this.fontWeight = Short.parseShort(fontWeightStr);
		}
	}

	public Length getLineHeight() {
		return lineHeight;
	}

	public void setLineHeight(Length lineHeight) {
		this.lineHeight = lineHeight;
	}

	public void setLineHeight(String lineHeightStr) {
		if (null == lineHeightStr || "".equals(lineHeightStr)) {
			return;
		}
		this.lineHeight = super.parseLength(lineHeightStr);
	}

	public String getFamily() {
		if (null == fontFamily || "".equals(fontFamily)) {
			return "";
		} else {
			if (fontFamily.contains("'")) {
				String familyStr = fontFamily.replace("'", "");
				return familyStr;
			} else {
				return fontFamily;
			}
		}

	}

	public void setFamily(String fontFamily) {
		this.fontFamily = fontFamily;
	}

	public Length getFontSize() {
		return fontSize;
	}

	byte defaultVal = 0;

	public void setFontSize(String fontSize) {
		if (null == fontSize || "".equals(fontSize)) {
			return;
		}
		if ("xx-small".equals(fontSize.trim())) {
			this.fontSize = new GBTextStyleEntry.Length(0,
					GBTextStyleEntry.SizeUnit.XX_SMALL);
		} else if ("x-small".equals(fontSize.trim())) {
			this.fontSize = new GBTextStyleEntry.Length(0,
					GBTextStyleEntry.SizeUnit.X_SMALL);
		} else if ("small".equals(fontSize.trim())) {
			this.fontSize = new GBTextStyleEntry.Length(0,
					GBTextStyleEntry.SizeUnit.SMALL);
		} else if ("medium".equals(fontSize.trim())) {
			this.fontSize = new GBTextStyleEntry.Length(0,
					GBTextStyleEntry.SizeUnit.MEDIUM);
		} else if ("large".equals(fontSize.trim())) {
			this.fontSize = new GBTextStyleEntry.Length(0,
					GBTextStyleEntry.SizeUnit.LARGE);
		} else if ("x-large".equals(fontSize.trim())) {
			this.fontSize = new GBTextStyleEntry.Length(0,
					GBTextStyleEntry.SizeUnit.X_LARGE);
		} else if ("xx-large".equals(fontSize.trim())) {
			this.fontSize = new GBTextStyleEntry.Length(0,
					GBTextStyleEntry.SizeUnit.XX_LARGE);
		} else if ("larger".equals(fontSize.trim())) {
			this.fontSize = new GBTextStyleEntry.Length(0,
					GBTextStyleEntry.SizeUnit.LARGER);
		} else if ("smaller".equals(fontSize.trim())) {
			this.fontSize = new GBTextStyleEntry.Length(0,
					GBTextStyleEntry.SizeUnit.SMALLER);
		} else {

			try {
				// 无单位情况处理

				int size = getSignVal(fontSize);
				if (size > 7) {
					size = 7;
				}
				if (size < 1) {
					size = 1;
				}
				size /= 3;
				size *= 100;
				this.fontSize = GBTextStyleEntry.parseLength(size + "%");

			} catch (NumberFormatException ex) {// 有单位情况处理
				this.fontSize = GBTextStyleEntry.parseLength(fontSize);
			}

		}

	}

	private int getSignVal(String fontSize) {
		int size = 3;
		char start = fontSize.trim().charAt(0);
		if (start == '+') {
			size += Integer.parseInt(fontSize.substring(1));
		} else if (start == '-') {
			size -= Integer.parseInt(fontSize.substring(1));
		} else {
			size = Integer.parseInt(fontSize);
		}
		return size;
	}

	public final void setColor(int color) {
		myColor = color;
	}

	public final void setColor(String colorStr) {
		if (null == colorStr || "".equals(colorStr)) {
			return;
		}
		try {
			myColor = NumUtil.parseColor(colorStr.trim());
		} catch (NumberFormatException ex) {
			LogUtils.e(TAG, "NumberFormatException  from GBTextFontStyleEntry");
		}
	}

	public final int getColor() {
		return myColor;
	}

	/*
	 * @Override public String toString() { StringBuilder sb=new
	 * StringBuilder(); sb.append(GBTextStyleEntry.Feature.FONT_COLOR);
	 * sb.append(getColor()); return super.toString(); }
	 */

	public GBTextFontStyleEntry(char[] data, int offset, int len) {
		loadData(data, offset, len);
	}

	public char[] toChars() {
		int fLen = fontFamily == null ? 0 : fontFamily.length();
		char[] block = new char[13 + fLen + 2];// font family mark len
		int offset = 0, realLen = 0;

		if (null != fontFamily && !"".equals(fontFamily)) {
			realLen += 2;
			block[offset++] = GBTextStyleEntry.Feature.FONT_FAMILY;
			block[offset++] = (char) (fontFamily.length());// 一个字符存储长度
			realLen += fontFamily.length();
			char[] data = fontFamily.toCharArray();
			System.arraycopy(data, 0, block, offset, data.length);
			offset += data.length;
		}

		if (0 != myColor) {
			realLen += 3;
			block[offset++] = GBTextStyleEntry.Feature.FONT_COLOR;
			block[offset++] = (char) getColor();
			block[offset++] = (char) (getColor() >> 16);
		}

		if (null != fontSize) {
			realLen += 4;
			block[offset++] = GBTextStyleEntry.Feature.FONT_SIZE;
			final int size = NumUtil.parseInt(fontSize.Size);
			block[offset++] = (char) size;
			block[offset++] = (char) (size >> 16);
			block[offset++] = (char) fontSize.Unit;
		}

		if (null != this.lineHeight) {
			realLen += 4;
			block[offset++] = Feature.LINE_HEIGHT;
			offset = setVal(block, offset, this.lineHeight);
		}

		if (0 != this.fontWeight) {
			realLen += 2;
			block[offset++] = Feature.FONT_WEIGHT;
			block[offset++] = (char) this.fontWeight;
		}

		return getRealData(block, realLen);
	}

	@Override
	public void loadData(char[] data, int offset, int len) {
		int length = offset + len;
		int ret = 0;
		byte unit = 0;
		while (offset < length) {
			switch (data[offset++]) {
				case GBTextStyleEntry.Feature.FONT_COLOR:
					final int color = (int) data[offset++]
							+ (((int) data[offset++]) << 16);
					setColor(color);
					break;
				case GBTextStyleEntry.Feature.FONT_SIZE:
					ret = (int) data[offset++] + (((int) data[offset++]) << 16);
					unit = (byte) data[offset++];
					this.fontSize = new Length(NumUtil.parseFloat(ret), unit);
					break;
				case GBTextStyleEntry.Feature.FONT_FAMILY:
					final int familyLen = (int) data[offset++];// 一个字符存储长度
					this.fontFamily = new String(data, offset, familyLen);
					offset += familyLen;
					break;
				case GBTextStyleEntry.Feature.LINE_HEIGHT:
					ret = (int) data[offset++] + (((int) data[offset++]) << 16);
					unit = (byte) data[offset++];
					this.lineHeight = new Length(NumUtil.parseFloat(ret), unit);
					break;
				case Feature.FONT_WEIGHT:
					this.fontWeight = (short) data[offset++];
					break;
			}
		}

	}

}
