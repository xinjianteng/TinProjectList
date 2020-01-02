package com.core.option;

public final class GBEnumOption<T extends Enum<T>> extends GBOption {
	private final T myDefaultValue;
	private T myValue;

	public GBEnumOption(String group, String optionName, T defaultValue) {
		super(group, optionName);
		myDefaultValue = defaultValue;
		myValue = defaultValue;
	}

	public T getValue() {
		if (!myIsSynchronized) {
			final String value = getConfigValue(null);
			if (value != null) {
				try {
					myValue = T.valueOf(myDefaultValue.getDeclaringClass(), value);
				} catch (Throwable t) {
				}
			}
			myIsSynchronized = true;
		}
		return myValue;
	}

	public void setValue(T value) {
		if (myIsSynchronized && (myValue == value)) {
			return;
		}
		myValue = value;
		myIsSynchronized = true;
		if (value == myDefaultValue) {
			unsetConfigValue();
		} else {
			setConfigValue("" + value.toString());
		}
	}
}
