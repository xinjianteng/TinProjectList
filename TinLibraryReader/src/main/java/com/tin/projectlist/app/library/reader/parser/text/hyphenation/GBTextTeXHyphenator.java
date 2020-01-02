package com.core.text.hyphenation;

import com.core.file.GBFile;
import com.core.file.GBResourceFile;
import com.core.support.GBLanguageUtil;
import com.core.support.Language;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

final class GBTextTeXHyphenator extends GBTextHyphenator {
	private final HashMap<GBTextTeXHyphenationPattern,GBTextTeXHyphenationPattern> myPatternTable =
			new HashMap<GBTextTeXHyphenationPattern,GBTextTeXHyphenationPattern>();
	private String myLanguage;

	void addPattern(GBTextTeXHyphenationPattern pattern) {
		myPatternTable.put(pattern, pattern);
	}

	private List<String> myLanguageCodes;
	public List<String> languageCodes() {
		if (myLanguageCodes == null) {
			final TreeSet<String> codes = new TreeSet<String>();
			final GBFile patternsFile = GBResourceFile.createResourceFile("hyphenationPatterns");
			for (GBFile file : patternsFile.children()) {
				final String name = file.getShortName();
				if (name.endsWith(".pattern")) {
					codes.add(name.substring(0, name.length() - ".pattern".length()));
				}
			}

			codes.add("zh");
			myLanguageCodes = new ArrayList<String>(codes);
		}

		return Collections.unmodifiableList(myLanguageCodes);
	}

	public void load(String language) {
		if (language == null || Language.OTHER_CODE.equals(language)) {
			language = GBLanguageUtil.defaultLanguageCode();
		}
		if (language == null || language.equals(myLanguage)) {
			return;
		}
		myLanguage = language;
		unload();

		if (language != null) {
			new GBTextHyphenationReader(this).readQuietly(GBResourceFile.createResourceFile(
					"hyphenationPatterns/" + language + ".pattern"
			));
		}
	}

	public void unload() {
		myPatternTable.clear();
	}

	public void hyphenate(char[] stringToHyphenate, boolean[] mask, int length) {
		if (myPatternTable.isEmpty()) {
			for (int i = 0; i < length - 1; i++) {
				mask[i] = false;
			}
			return;
		}

		byte[] values = new byte[length + 1];

		final HashMap<GBTextTeXHyphenationPattern,GBTextTeXHyphenationPattern> table = myPatternTable;
		GBTextTeXHyphenationPattern pattern =
				new GBTextTeXHyphenationPattern(stringToHyphenate, 0, length, false);
		for (int offset = 0; offset < length - 1; offset++) {
			int len = length - offset + 1;
			pattern.update(stringToHyphenate, offset, len - 1);
			while (--len > 0) {
				pattern.myLength = len;
				pattern.myHashCode = 0;
				GBTextTeXHyphenationPattern toApply =
						(GBTextTeXHyphenationPattern)table.get(pattern);
				if (toApply != null) {
					toApply.apply(values, offset);
				}
			}
		}

		for (int i = 0; i < length - 1; i++) {
			mask[i] = (values[i + 1] % 2) == 1;
		}
	}
}
