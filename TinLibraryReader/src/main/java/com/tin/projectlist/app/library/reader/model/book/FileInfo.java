package com.tin.projectlist.app.library.reader.model.book;

/**
 * 类名： FileInfo.java<br>
 * 描述： 文件信息树形结构定义<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-25<br>
 * 版本：  <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public final class FileInfo extends com.core.object.GBTree<FileInfo> {
	public final String Name;
	public long Id;
	public long FileSize = -1;

	FileInfo(String name, FileInfo parent) {
		this(name, parent, -1);
	}

	FileInfo(String name, FileInfo parent, long id) {
		super(parent);
		Name = name;
		Id = id;
	}
}
