package com.tin.projectlist.app.library.reader.parser.option;

import com.core.object.GBColor;

public final class GBColorOption extends GBOption {
	private final GBColor myDefaultValue;
	private GBColor myValue;

	public GBColorOption(String group, String optionName, GBColor defaultValue) {
		super(group, optionName);
		myDefaultValue = (defaultValue != null) ? defaultValue : new GBColor(0);
		myValue = myDefaultValue;
	}

	public GBColor getValue() {
		if (!myIsSynchronized) {
			String value = getConfigValue(null);
			if (value != null) {
				try {
					int intValue = Integer.parseInt(value);
					if (myValue.getIntValue() != intValue) {
						myValue = new GBColor(intValue);
					}
				} catch (NumberFormatException e) {
				}
			}
			myIsSynchronized = true;
		}
		return myValue;
	}

	public void setValue(GBColor colorValue) {
		if (colorValue != null) {
			final boolean sameValue = myValue.equals(colorValue);
			if (myIsSynchronized && sameValue) {
				return;
			}
			if (!sameValue) {
				myValue = colorValue;
			}
			myIsSynchronized = true;
			if (colorValue.equals(myDefaultValue)) {
				unsetConfigValue();
			} else {
				setConfigValue("" + colorValue.getIntValue());
			}
		}
	}
}
