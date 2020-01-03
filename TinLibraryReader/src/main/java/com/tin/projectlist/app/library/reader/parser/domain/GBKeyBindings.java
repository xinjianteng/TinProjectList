package com.tin.projectlist.app.library.reader.parser.domain;

import com.core.file.GBFile;
import com.core.file.GBPaths;
import com.core.option.GBStringListOption;
import com.core.option.GBStringOption;
import com.core.xml.GBStringMap;
import com.core.xml.GBXMLReaderAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
/**
 * 类名： GBKeyBindings.java#ZLKeyBindings<br>
 * 描述： 按键事件绑定<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-3<br>
 * 版本：  <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public final class GBKeyBindings {
	private static final String ACTION = "Action";
	private static final String LONG_PRESS_ACTION = "LongPressAction";
	//按键事件组名称
	private final String mName;
	//按键事件集合
	private final GBStringListOption mKeysOption;
	//单击事件集合
	private final TreeMap<Integer,GBStringOption> mActionMap = new TreeMap<Integer,GBStringOption>();
	//长按事件集合
	private final TreeMap<Integer,GBStringOption> mLongPressActionMap = new TreeMap<Integer,GBStringOption>();

	public GBKeyBindings(String name) {
		mName = name;
		final Set<String> keys = new TreeSet<String>();
		new Reader(keys).readQuietly(GBFile.createFileByPath("default/keymap.xml"));
		try {
			new Reader(keys).readQuietly(GBFile.createFileByPath(GBPaths.systemShareDirectory() + "/keymap.xml"));
		} catch (Exception e) {
			// ignore
		}
		try {
			new Reader(keys).readQuietly(GBFile.createFileByPath(GBPaths.getBookPath() + "/keymap.xml"));
		} catch (Exception e) {
			// ignore
		}
		mKeysOption = new GBStringListOption(name, "KeyList", new ArrayList<String>(keys), ",");

 		/*
 		 * 音量键翻页绑定
 		 */
		// this code is for migration from FBReader versions <= 1.1.2
//		GBStringOption oldBackKeyOption = new GBStringOption(mName + ":" + ACTION, "<Back>", "");
//		if (!"".equals(oldBackKeyOption.getValue())) {
//			bindKey(KeyEvent.KEYCODE_BACK, false, oldBackKeyOption.getValue());
//			oldBackKeyOption.setValue("");
//		}
//		oldBackKeyOption = new GBStringOption(mName + ":" + LONG_PRESS_ACTION, "<Back>", "");
//		if (!"".equals(oldBackKeyOption.getValue())) {
//			bindKey(KeyEvent.KEYCODE_BACK, true, oldBackKeyOption.getValue());
//			oldBackKeyOption.setValue("");
//		}
//
//		final ZLBooleanOption volumeKeysOption =
//			new ZLBooleanOption("Scrolling", "VolumeKeys", true);
//		final ZLBooleanOption invertVolumeKeysOption =
//			new ZLBooleanOption("Scrolling", "InvertVolumeKeys", false);
//		if (!volumeKeysOption.getValue()) {
//			bindKey(KeyEvent.KEYCODE_VOLUME_UP, false, ZLApplication.NoAction);
//			bindKey(KeyEvent.KEYCODE_VOLUME_DOWN, false, ZLApplication.NoAction);
//		} else if (invertVolumeKeysOption.getValue()) {
//			bindKey(KeyEvent.KEYCODE_VOLUME_UP, false, ActionCode.VOLUME_KEY_SCROLL_FORWARD);
//			bindKey(KeyEvent.KEYCODE_VOLUME_DOWN, false, ActionCode.VOLUME_KEY_SCROLL_BACK);
//		}
//		volumeKeysOption.setValue(true);
//		invertVolumeKeysOption.setValue(false);
		// end of migration code
	}
	/**
	 * 功能描述： 封装一个新的按键事件<br>
	 * 创建者： jack<br>
	 * 创建日期：2013-4-3<br>
	 * @param key 按键唯一标示
	 * @param longPress 是否长按
	 * @param defaultValue 默认响应动作标示
	 * @return
	 */
	private GBStringOption createOption(int key, boolean longPress, String defaultValue) {
		final String group = mName + ":" + (longPress ? LONG_PRESS_ACTION : ACTION);
		return new GBStringOption(group, String.valueOf(key), defaultValue);
	}
	/**
	 * 功能描述：根据按键唯一标示获取事件信息如果没有则注册为一个空动作响应<br>
	 * 创建者： jack<br>
	 * 创建日期：2013-4-3<br>
	 * @param key 按键唯一标示
	 * @param longPress 是否长按
	 * @return
	 */
	public GBStringOption getOption(int key, boolean longPress) {
		final TreeMap<Integer,GBStringOption> map = longPress ? mLongPressActionMap : mActionMap;
		GBStringOption option = map.get(key);
		if (option == null) {
			option = createOption(key, longPress, GBApplication.NoAction);
			map.put(key, option);
		}
		return option;
	}
	/**
	 * 功能描述： 绑定一个新的按键事件<br>
	 * 创建者： jack<br>
	 * 创建日期：2013-4-3<br>
	 * @param key 按键唯一标示
	 * @param longPress 是否长按
	 * @param actionId 对应的响应事件业务码
	 */
	public void bindKey(int key, boolean longPress, String actionId) {
		final String stringKey = String.valueOf(key);
		List<String> keys = mKeysOption.getValue();
		if (!keys.contains(stringKey)) {
			keys = new ArrayList<String>(keys);
			keys.add(stringKey);
			Collections.sort(keys);
			mKeysOption.setValue(keys);
		}
		getOption(key, longPress).setValue(actionId);
	}
	/**
	 * 功能描述： 根据按键标示获取对应的业务码<br>
	 * 创建者： jack<br>
	 * 创建日期：2013-4-3<br>
	 * @param key 按键标示
	 * @param longPress 是否长按
	 * @return
	 */
	public String getBinding(int key, boolean longPress) {
		return getOption(key, longPress).getValue();
	}
	/**
	 * 类名： Reader.java<br>
	 * 描述： 按键事件配置文件解析类<br>
	 * 创建者： jack<br>
	 * 创建日期：2013-4-3<br>
	 * 版本：  <br>
	 * 修改者： <br>
	 * 修改日期：<br>
	 */
	private class Reader extends GBXMLReaderAdapter {
		private final Set<String> myKeySet;

		Reader(Set<String> keySet) {
			myKeySet = keySet;
		}

		@Override
		public boolean dontCacheAttributeValues() {
			return true;
		}

		@Override
		public boolean startElementHandler(String tag, GBStringMap attributes) {
			if ("binding".equals(tag)) {
				final String stringKey = attributes.getValue("key");
				final String actionId = attributes.getValue("action");
				if (stringKey != null && actionId != null) {
					try {
						final int key = Integer.parseInt(stringKey);
						myKeySet.add(stringKey);
						mActionMap.put(key, createOption(key, false, actionId));
					} catch (NumberFormatException e) {
					}
				}
			}
			return false;
		}
	}
}
