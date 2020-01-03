package com.tin.projectlist.app.library.reader.parser.file;

import com.core.platform.GBLibrary;

/**
 * 类名： GBResourceFile.java#ZLResourceFile<br>
 * 描述： 应用配置文件封装<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-10<br>
 * 版本：  <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public abstract class GBResourceFile extends GBFile {
	/**
	 * 功能描述： 获取配置文件<br>
	 * 创建者： jack<br>
	 * 创建日期：2013-4-10<br>
	 * @param path 文件相对路径
	 * @return
	 */
	public static GBResourceFile createResourceFile(String path) {
		return GBLibrary.Instance().createResourceFile(path);
	}
	/**
	 * 功能描述： 获取配置文件<br>
	 * 创建者： jack<br>
	 * 创建日期：2013-4-10<br>
	 * @param parent 父文件夹
	 * @param name 文件名称
	 * @return
	 */
	static GBResourceFile createResourceFile(GBResourceFile parent, String name) {
		return GBLibrary.Instance().createResourceFile(parent, name);
	}

	private final String myPath;

	protected GBResourceFile(String path) {
		myPath = path;
		init();
	}

	@Override
	public String getPath() {
		return myPath;
	}

	@Override
	public String getFullName() {
		return myPath.substring(myPath.lastIndexOf('/') + 1);
	}

	@Override
	public GBPhysicalFile getPhysicalFile() {
		return null;
	}
}
