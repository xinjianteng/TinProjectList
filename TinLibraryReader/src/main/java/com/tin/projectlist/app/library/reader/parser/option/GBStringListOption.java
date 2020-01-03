package com.tin.projectlist.app.library.reader.parser.option;

import com.core.common.util.MiscUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GBStringListOption extends GBOption {
	private final List<String> myDefaultValue;
	private List<String> myValue;
	private final String myDelimiter;

	public GBStringListOption(String group, String optionName, List<String> defaultValue, String delimiter) {
		super(group, optionName);
		myDefaultValue = (defaultValue != null) ? defaultValue : Collections.<String>emptyList();
		myValue = myDefaultValue;
		myDelimiter = delimiter;
	}

	public GBStringListOption(String group, String optionName, String defaultValue, String delimiter) {
		super(group, optionName);
		myDefaultValue = (defaultValue != null) ? Collections.singletonList(defaultValue) : Collections.<String>emptyList();
		myValue = myDefaultValue;
		myDelimiter = delimiter;
	}

	public List<String> getValue() {
		if (!myIsSynchronized) {
			final String value = getConfigValue(MiscUtil.join(myDefaultValue, myDelimiter));
			if (value != null) {
				myValue = MiscUtil.split(value, myDelimiter);
			}
			myIsSynchronized = true;
		}
		return Collections.unmodifiableList(myValue);
	}

	public void setValue(List<String> value) {
		if (value == null) {
			value = Collections.emptyList();
		}
		if (myIsSynchronized && (myValue.equals(value))) {
			return;
		}
		myValue = new ArrayList<String>(value);
		if (value.equals(myDefaultValue)) {
			unsetConfigValue();
		} else {
			setConfigValue(MiscUtil.join(value, myDelimiter));
		}
		myIsSynchronized = true;
	}
}
