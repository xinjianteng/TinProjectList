package com.tin.projectlist.app.library.reader.parser.option;

/**
 * Boolean的Option<br>
 *
 * 创建者： 符晨<br>
 * 创建日期：2013-4-2<br>
 */
public final class GBBooleanOption extends GBOption {
	private final boolean myDefaultValue;
	private boolean myValue;
	/**
	 * 构造方法
	 * @param group 组名称
	 * @param optionName  配置名称
	 * @param defaultValue 配置值
	 */
	public GBBooleanOption(String group, String optionName, boolean defaultValue) {
		super(group, optionName);
		myDefaultValue = defaultValue;
		myValue = defaultValue;
	}

	public boolean getValue() {
		if (!myIsSynchronized) {
			String value = getConfigValue(null);
			if (value != null) {
				if ("true".equals(value)) {
					myValue = true;
				} else if ("false".equals(value)) {
					myValue = false;
				}
			}
			myIsSynchronized = true;
		}
		return myValue;
	}

	public void setValue(boolean value) {
		if (myIsSynchronized && (myValue == value)) {
			return;
		}
		myValue = value;
		myIsSynchronized = true;
		if (value == myDefaultValue) {
			unsetConfigValue();
		} else {
			setConfigValue(value ? "true" : "false");
		}
	}
}
