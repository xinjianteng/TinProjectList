package com.tin.projectlist.app.library.reader.parser.object;

/**
 * 颜色实体
 */
public final class GBColor {
	public final short Red;
	public final short Green;
	public final short Blue;

	public GBColor(int r, int g, int b) {
		Red = (short)(r & 0xFF);
		Green = (short)(g & 0xFF);
		Blue = (short)(b & 0xFF);
	}

	public GBColor(int intValue) {
		Red = (short)((intValue >> 16) & 0xFF);
		Green = (short)((intValue >> 8) & 0xFF);
		Blue = (short)(intValue & 0xFF);
	}

	public int getIntValue() {
		return (Red << 16) + (Green << 8) + Blue;
	}

	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (!(o instanceof GBColor)) {
			return false;
		}

		GBColor color = (GBColor)o;
		return (color.Red == Red) && (color.Green == Green) && (color.Blue == Blue);
	}

	public int hashCode() {
		return getIntValue();
	}
}
