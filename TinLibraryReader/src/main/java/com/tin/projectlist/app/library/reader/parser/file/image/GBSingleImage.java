package com.tin.projectlist.app.library.reader.parser.file.image;

import com.core.common.util.MimeType;

import java.io.InputStream;
/**
 * 类名： GBSingleImage.java<br>
 * 描述： <br>
 * 创建者： jack<br>
 * 创建日期：2013-4-10<br>
 * 版本：  <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public abstract class GBSingleImage implements GBImage {
	//图片类型
	private final MimeType myMimeType;

	public GBSingleImage(final MimeType mimeType) {
		myMimeType = mimeType;
	}
	/**
	 * 功能描述： 获取图片文件流<br>
	 * 创建者： jack<br>
	 * 创建日期：2013-4-11<br>
	 * @return
	 */
	public abstract InputStream inputStream();

	public final MimeType mimeType() {
		return myMimeType;
	}
}
