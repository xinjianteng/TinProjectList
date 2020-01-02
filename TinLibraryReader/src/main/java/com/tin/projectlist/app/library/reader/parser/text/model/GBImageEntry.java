package com.core.text.model;

import com.core.file.image.GBImage;

import java.util.Map;
/**
 *
 * 描述： 图片条目
 * 创建者： 燕冠楠<br>
 * 创建日期：2013-3-26<br>
 */
public final class GBImageEntry {
	private final Map<String,GBImage> myImageMap;
	public final String Id;
	public final short VOffset;
	public final boolean IsCover;

	public GBImageEntry(Map<String,GBImage> imageMap, String id, short vOffset, boolean isCover) {
		myImageMap = imageMap;
		Id = id;
		VOffset = vOffset;
		IsCover = isCover;
	}


	public GBImage getImage() {
		return myImageMap.get(Id);
	}
}
