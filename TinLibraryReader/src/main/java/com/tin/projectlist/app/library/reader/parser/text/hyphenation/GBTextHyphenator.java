package com.tin.projectlist.app.library.reader.parser.text.hyphenation;

import com.tin.projectlist.app.library.reader.parser.common.util.GBCharacterUtil;
import com.tin.projectlist.app.library.reader.parser.text.widget.GBTextWord;

import java.util.List;

public abstract class GBTextHyphenator {
	private static GBTextHyphenator ourInstance;

	public static GBTextHyphenator Instance() {
		if (ourInstance == null) {
			ourInstance = new GBTextTeXHyphenator();
		}
		return ourInstance;
	}

	public static void deleteInstance() {
		if (ourInstance != null) {
			ourInstance.unload();
			ourInstance = null;
		}
	}

	protected GBTextHyphenator() {
	}

	public abstract List<String> languageCodes();
	public abstract void load(final String languageCode);
	public abstract void unload();

	public GBTextHyphenationInfo getInfo(final GBTextWord word) {
		final int len = word.Length;
		final boolean[] isLetter = new boolean[len];//记录word包含的每个字符是否是字母或字符
		final char[] pattern = new char[len + 2];//若是字母则记录该字母的小写形式  否则记录空格' '
		final char[] data = word.Data;
		pattern[0] = ' ';
		for (int i = 0, j = word.Offset; i < len; ++i, ++j) {
			char character = data[j];
			if (GBCharacterUtil.isLetter(character)) {//判断是否是字母或字符
				isLetter[i] = true;
				pattern[i + 1] = Character.toLowerCase(character);
			} else {
				pattern[i + 1] = ' ';
			}
		}
		pattern[len + 1] = ' ';

		final GBTextHyphenationInfo info = new GBTextHyphenationInfo(len + 2);
		final boolean[] mask = info.Mask;
		hyphenate(pattern, mask, len + 2);
		for (int i = 0, j = word.Offset - 1; i <= len; ++i, ++j) {
			if ((i < 2) || (i > len - 2)) {
				mask[i] = false;
			} else {
				switch (data[j]) {
					case (char)0xAD: // soft hyphen
						mask[i] = true;
						break;
					case '-':
						mask[i] = (i >= 3)
								&& isLetter[i - 3]
								&& isLetter[i - 2]
								&& isLetter[i]
								&& isLetter[i + 1];
						break;
					default:
						mask[i] = mask[i]
								&& isLetter[i - 2]
								&& isLetter[i - 1]
								&& isLetter[i]
								&& isLetter[i + 1];
						break;
				}
			}
		}

		return info;
	}

	protected abstract void hyphenate(char[] stringToHyphenate, boolean[] mask, int length);
}
