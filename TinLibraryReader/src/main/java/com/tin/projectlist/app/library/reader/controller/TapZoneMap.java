package com.tin.projectlist.app.library.reader.controller;

import com.core.file.GBFile;
import com.core.option.GBIntegerRangeOption;
import com.core.option.GBStringListOption;
import com.core.option.GBStringOption;
import com.core.xml.GBStringMap;
import com.core.xml.GBXMLReaderAdapter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 类名： TapZoneMap.java<br>
 * 描述： 阅读器热区配置集合<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-24<br>
 * 版本：  <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public class TapZoneMap {
	private static final List<String> ourPredefinedMaps = new LinkedList<String>();
	private static final GBStringListOption ourMapsOption;
	static {
		// TODO: list files from default/tapzones
		ourPredefinedMaps.add("right_to_left");
		ourPredefinedMaps.add("left_to_right");
		ourPredefinedMaps.add("down");
		ourPredefinedMaps.add("up");
		ourPredefinedMaps.add("default");
		ourMapsOption = new GBStringListOption("TapZones", "List", ourPredefinedMaps, "\000");
	}
	private static final Map<String,TapZoneMap> ourMaps = new HashMap<String,TapZoneMap>();

	public static List<String> zoneMapNames() {
		return ourMapsOption.getValue();
	}

	public static TapZoneMap zoneMap(String name) {
		TapZoneMap map = ourMaps.get(name);
		if (map == null) {
			map = new TapZoneMap(name);
			ourMaps.put(name, map);
		}
		return map;
	}

	public static TapZoneMap createZoneMap(String name, int width, int height) {
		if (ourMapsOption.getValue().contains(name)) {
			return null;
		}

		final TapZoneMap map = zoneMap(name);
		map.myWidth.setValue(width);
		map.myHeight.setValue(height);
		final List<String> lst = new LinkedList<String>(ourMapsOption.getValue());
		lst.add(name);
		ourMapsOption.setValue(lst);
		return map;
	}

	public static void deleteZoneMap(String name) {
		if (ourPredefinedMaps.contains(name)) {
			return;
		}

		ourMaps.remove(name);

		final List<String> lst = new LinkedList<String>(ourMapsOption.getValue());
		lst.remove(name);
		ourMapsOption.setValue(lst);
	}
	/*
	 * 点击类型
	 */
	public static enum Tap {
		singleTap,   //单击（不支持双击时兼容双击）
		singleNotDoubleTap,  // 强制单击
		doubleTap  //双击
	};

	public final String Name;
	private final String myOptionGroupName;
	private GBIntegerRangeOption myHeight;
	private GBIntegerRangeOption myWidth;
	private final HashMap<Zone,GBStringOption> myZoneMap = new HashMap<Zone,GBStringOption>();
	private final HashMap<Zone,GBStringOption> myZoneMap2 = new HashMap<Zone,GBStringOption>();

	private TapZoneMap(String name) {
		Name = name;
		myOptionGroupName = "TapZones:" + name;
		myHeight = new GBIntegerRangeOption(myOptionGroupName, "Height", 2, 5, 3);
		myWidth = new GBIntegerRangeOption(myOptionGroupName, "Width", 2, 5, 3);
		final GBFile mapFile = GBFile.createFileByPath(
				"default/tapzones/" + name.toLowerCase() + ".xml"
		);
		new Reader().readQuietly(mapFile);
	}

	public boolean isCustom() {
		return !ourPredefinedMaps.contains(Name);
	}

	public int getHeight() {
		return myHeight.getValue();
	}

	public int getWidth() {
		return myWidth.getValue();
	}
	/**
	 * 功能描述： 根据点击区域获取要执行的事件<br>
	 * 创建者： jack<br>
	 * 创建日期：2013-5-3<br>
	 * @param x x坐标
	 * @param y y坐标
	 * @param width  版面宽
	 * @param height 版面高
	 * @param tap 点击动作
	 * @return
	 */
	public String getActionByCoordinates(int x, int y, int width, int height, Tap tap) {
		if (width == 0 || height == 0) {
			return null;
		}
		return getActionByZone(myWidth.getValue() * x / width, myHeight.getValue() * y / height, tap);
	}
	/**
	 * 功能描述： 根据点击区域获取操作类型<br>
	 * 创建者： jack<br>
	 * 创建日期：2013-5-3<br>
	 * @param h 水平点击区域
	 * @param v 竖直点击区域
	 * @param tap
	 * @return
	 */
	public String getActionByZone(int h, int v, Tap tap) {
		final GBStringOption option = getOptionByZone(new Zone(h, v), tap);
		return option != null ? option.getValue() : null;
	}

	private GBStringOption getOptionByZone(Zone zone, Tap tap) {
		switch (tap) {
			default:
				return null;
			case singleTap:
			{
				final GBStringOption option = myZoneMap.get(zone);
				return option != null ? option : myZoneMap2.get(zone);
			}
			case singleNotDoubleTap:
				return myZoneMap.get(zone);
			case doubleTap:
				return myZoneMap2.get(zone);
		}
	}

	private GBStringOption createOptionForZone(Zone zone, boolean singleTap, String action) {
		return new GBStringOption(
				myOptionGroupName,
				(singleTap ? "Action" : "Action2") + ":" + zone.HIndex + ":" + zone.VIndex,
				action
		);
	}

	public void setActionForZone(int h, int v, boolean singleTap, String action) {
		final Zone zone = new Zone(h, v);
		final HashMap<Zone,GBStringOption> map = singleTap ? myZoneMap : myZoneMap2;
		GBStringOption option = map.get(zone);
		if (option == null) {
			option = createOptionForZone(zone, singleTap, null);
			map.put(zone, option);
		}
		option.setValue(action);
	}
	/*
	 * 热点区域封装
	 */
	private static class Zone {
		int HIndex;  //水平坐标
		int VIndex;  //竖直坐标

		Zone(int h, int v) {
			HIndex = h;
			VIndex = v;
		}

		/*void mirror45() {
			final int swap = HIndex;
			HIndex = VIndex;
			VIndex = swap;
		}*/

		@Override
		public boolean equals(Object o) {
			if (o == this) {
				return true;
			}

			if (!(o instanceof Zone)) {
				return false;
			}

			final Zone tz = (Zone)o;
			return HIndex == tz.HIndex && VIndex == tz.VIndex;
		}

		@Override
		public int hashCode() {
			return (HIndex << 5) + VIndex;
		}
	}
	/**
	 * 类名： TapZoneMap.java<br>
	 * 描述： 热区配置文件解析类<br>
	 * 创建者： jack<br>
	 * 创建日期：2013-4-24<br>
	 * 版本：  <br>
	 * 修改者： <br>
	 * 修改日期：<br>
	 */
	private class Reader extends GBXMLReaderAdapter {
		@Override
		public boolean startElementHandler(String tag, GBStringMap attributes) {
			try {
				if ("zone".equals(tag)) {
					final Zone zone = new Zone(
							Integer.parseInt(attributes.getValue("x")),
							Integer.parseInt(attributes.getValue("y"))
					);
					final String action = attributes.getValue("action");
					final String action2 = attributes.getValue("action2");
					if (action != null) {
						//单击区域
						myZoneMap.put(zone, createOptionForZone(zone, true, action));
					}
					if (action2 != null) {
						// 双击区域
						myZoneMap2.put(zone, createOptionForZone(zone, false, action2));
					}
				} else if ("tapZones".equals(tag)) {
					final String v = attributes.getValue("v");
					if (v != null) {
						myHeight.setValue(Integer.parseInt(v));
					}
					final String h = attributes.getValue("h");
					if (h != null) {
						myWidth.setValue(Integer.parseInt(h));
					}
				}
			} catch (Throwable e) {
			}
			return false;
		}
	}
}
