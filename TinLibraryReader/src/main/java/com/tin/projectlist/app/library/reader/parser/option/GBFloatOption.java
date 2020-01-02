package com.core.option;
/**
 * 类名： GBFloatOption<br>
 * 描述： 封装了一个带默认值的整形类型<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-15<br>
 * 版本：  <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public final class GBFloatOption extends GBOption {
	private final float myDefaultValue;
	private float myValue;

	public GBFloatOption(String group, String optionName, float defaultValue) {
		super(group, optionName);
		myDefaultValue = defaultValue;
		myValue = defaultValue;
	}

	public float getValue() {
		if (!myIsSynchronized) {
			String value = getConfigValue(null);
			if (value != null) {
				try {
					myValue = Integer.parseInt(value);
				} catch (NumberFormatException e) {
				}
			}
			myIsSynchronized = true;
		}
		return myValue;
	}

	public void setValue(float value) {
		if (myIsSynchronized && (myValue == value)) {
			return;
		}
		myValue = value;
		myIsSynchronized = true;
		if (value == myDefaultValue) {
			unsetConfigValue();
		} else {
			setConfigValue("" + value);
		}
	}
}
