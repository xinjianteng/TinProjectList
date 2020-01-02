package com.core.option;

import com.core.object.GBBoolean3;

/**
 * Boolean3的Option<br>
 *
 * 用户配置信息封装，
 * Boolean3类型的配置信息，继承GBOption，对外提供getValue setValue方法。
 *
 * 其他子类雷同，不重复劳动。个人感觉配置信息没必要封装这么多类从而降低性能。
 *
 * 创建者： 符晨<br>
 * 创建日期：2013-4-2<br>
 */
public final class GBBoolean3Option extends GBOption {
	private GBBoolean3 myValue;
	private final GBBoolean3 myDefaultValue;

	public GBBoolean3Option(String group, String optionName, GBBoolean3 defaultValue) {
		super(group, optionName);
		myDefaultValue = defaultValue;
		myValue = myDefaultValue;
	}

	/**
	 * 获取当前option配置的值
	 * @return
	 * @author fuchen
	 * @date 2013-4-10
	 */
	public GBBoolean3 getValue() {
		if (!myIsSynchronized) {
			String value = getConfigValue(null);
			if (value != null) {
				myValue = GBBoolean3.getByName(value);
			}
			myIsSynchronized = true;
		}
		return myValue;
	}

	/**
	 * 设置当前option配置的值
	 * @param value
	 * @author fuchen
	 * @date 2013-4-10
	 */
	public void setValue(GBBoolean3 value) {
		if (myIsSynchronized && (myValue == value)) {
			return;
		}
		myValue = value;
		myIsSynchronized = true;

		if (myValue == myDefaultValue) {
			unsetConfigValue();
		} else {
			setConfigValue(myValue.Name);
		}
	}
}
