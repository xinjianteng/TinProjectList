package com.tin.projectlist.app.library.reader.parser.option;
/**
 * 类名： GBIntegerRangeOption.java#ZLIntegerRangeOption<br>
 * 描述： 自定义一个区间范围和默认值的整形对象<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-15<br>
 * 版本：  <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public final class GBIntegerRangeOption extends GBOption {
	public final int MinValue;
	public final int MaxValue;

	private final int myDefaultValue;
	private int myValue;

	public GBIntegerRangeOption(String group, String optionName, int minValue, int maxValue, int defaultValue) {
		super(group, optionName);
		MinValue = minValue;
		MaxValue = maxValue;
		if (defaultValue < MinValue) {
			defaultValue = MinValue;
		} else if (defaultValue > MaxValue) {
			defaultValue = MaxValue;
		}
		myDefaultValue = defaultValue;
		myValue = defaultValue;
	}

	public int getValue() {
		if (!myIsSynchronized) {
			String value = getConfigValue(null);
			if (value != null) {
				try {
					int intValue = Integer.parseInt(value);
					if (intValue < MinValue) {
						intValue = MinValue;
					} else if (intValue > MaxValue) {
						intValue = MaxValue;
					}
					myValue = intValue;
				} catch (NumberFormatException e) {
				}
			}
			myIsSynchronized = true;
		}
		return myValue;
	}

	public void setValue(int value) {
		if (value < MinValue) {
			value = MinValue;
		} else if (value > MaxValue) {
			value = MaxValue;
		}
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
