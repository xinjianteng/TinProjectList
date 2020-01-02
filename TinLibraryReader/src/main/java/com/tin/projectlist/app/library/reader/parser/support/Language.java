package com.core.support;

import com.core.common.GBResource;

import java.text.Normalizer;

/**
 * 国际化实体
 * @author fuchen
 * @date 2013-4-11
 */
public class Language implements Comparable<Language> {
	public static final String OTHER_CODE = "other";
	public static final String MULTI_CODE = "multi";
	public static final String SYSTEM_CODE = "system";

	private static enum Order {
		Before,
		Normal,
		After
	}

	public final String Code;
	public final String Name;
	private final String mySortKey;
	private final Order myOrder;

	public Language(String code) {
		this(code, GBResource.resource("language").getResource(code).getValue());
	}

	public Language(String code, String name) {
		Code = code;
		Name = name;
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
//			mySortKey = normalize(name);
//		} else {
		mySortKey = name.toLowerCase();
//		}
		if (SYSTEM_CODE.equals(code)) {
			myOrder = Order.Before;
		} else if (MULTI_CODE.equals(code) || OTHER_CODE.equals(code)) {
			myOrder = Order.After;
		} else {
			myOrder = Order.Normal;
		}
	}

	public int compareTo(Language other) {
		final int diff = myOrder.compareTo(other.myOrder);
		return diff != 0 ? diff : mySortKey.compareTo(other.mySortKey);
	}

	@Override
	public boolean equals(Object lang) {
		if (this == lang) {
			return true;
		}
		if (!(lang instanceof Language)) {
			return false;
		}
		return Code.equals(((Language)lang).Code);
	}

	@Override
	public int hashCode() {
		return Code.hashCode();
	}

	//	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	private static String normalize(String s) {
		return Normalizer.normalize(s, Normalizer.Form.NFKD);
	}
}
