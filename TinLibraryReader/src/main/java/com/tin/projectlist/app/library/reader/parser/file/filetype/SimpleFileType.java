package com.core.file.filetype;

import com.core.common.util.MimeType;
import com.core.file.GBFile;

import java.util.List;

/**
 * 类名： SimpleFileType.java<br>
 * 描述： 基本文件类型封装<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-10<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
class SimpleFileType extends FileType {
	private final String myExtension;
	private final List<MimeType> myMimeTypes;

	SimpleFileType(String id, String extension, List<MimeType> mimeTypes) {
		super(id);
		myExtension = extension;
		myMimeTypes = mimeTypes;
	}

	@Override
	public boolean checkFile(GBFile file) {
		if (null == file.getExtendMapping()) {
			return myExtension.equalsIgnoreCase(file.getExtension());
		} else {
			return myExtension.equalsIgnoreCase(file.getExtendMapping());
		}

	}

	/*
	 * @Override public String extension() { return myExtension; }
	 */

	@Override
	public List<MimeType> mimeTypes() {
		return myMimeTypes;
	}

	@Override
	public MimeType mimeType(GBFile file) {
		return checkFile(file) ? myMimeTypes.get(0) : MimeType.NULL;
	}

	@Override
	public String toString() {
		return "SimpleFileType [" + mExtension + "]";
	}
}
