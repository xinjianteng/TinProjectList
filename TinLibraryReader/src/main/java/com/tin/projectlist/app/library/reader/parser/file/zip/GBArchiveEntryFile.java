package com.tin.projectlist.app.library.reader.parser.file.zip;

import com.core.file.GBFile;
import com.core.file.GBPhysicalFile;
import com.core.file.tar.GBTarEntryFile;

import java.util.Collections;
import java.util.List;
/**
 * 类名： GBArchiveEntryFile.java#ZLArchiveEntryFile<br>
 * 描述： 压缩文件中的文件抽象类<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-10<br>
 * 版本：  <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public abstract class GBArchiveEntryFile extends GBFile {
	/**
	 * 功能描述： 格式化文件名称移除名称中包含的 /./ /../ <br>
	 * 创建者： jack<br>
	 * 创建日期：2013-4-10<br>
	 * @param entryName
	 * @return
	 */
	public static String normalizeEntryName(String entryName) {
		while (entryName.startsWith("./")) {
			entryName = entryName.substring(2);
		}
		while (true) {
			final int index = entryName.lastIndexOf("/./");
			if (index == -1) {
				break;
			}
			entryName = entryName.substring(0, index) + entryName.substring(index + 2);
		}
		while (true) {
			final int index = entryName.indexOf("/../");
			if (index <= 0) {
				break;
			}
			final int prevIndex = entryName.lastIndexOf('/', index - 1);
			if (prevIndex == -1) {
				entryName = entryName.substring(index + 4);
				break;
			}
			entryName = entryName.substring(0, prevIndex) + entryName.substring(index + 3);
		}
		return entryName;
	}
	/**
	 * 功能描述： 创建一个压缩文件（子文件）对象<br>
	 * 创建者： jack<br>
	 * 创建日期：2013-4-10<br>
	 * @param archive 压缩文件
	 * @param entryName 子文件名称（相对路径）
	 * @return
	 */
	public static GBArchiveEntryFile createArchiveEntryFile(GBFile archive, String entryName) {
		if (archive == null) {
			return null;
		}
		entryName = normalizeEntryName(entryName);
		switch (archive.myArchiveType & ArchiveType.ARCHIVE) {
			case ArchiveType.ZIP:
				return new GBZipEntryFile(archive, entryName);
			case ArchiveType.TAR:
				return new GBTarEntryFile(archive, entryName);
			default:
				return null;
		}
	}
	/**
	 * 功能描述： 获取压缩文件列表<br>
	 * 创建者： jack<br>
	 * 创建日期：2013-4-10<br>
	 * @param archive 压缩文件对象
	 * @return
	 */
	public static List<GBFile> archiveEntries(GBFile archive) {
		switch (archive.myArchiveType & ArchiveType.ARCHIVE) {
			case ArchiveType.ZIP:
				return GBZipEntryFile.archiveEntries(archive);
			case ArchiveType.TAR:
				return GBTarEntryFile.archiveEntries(archive);
			default:
				return Collections.emptyList();
		}
	}
	//压缩文件对象
	protected final GBFile myParent;
	//压缩文件中子文件名称（相对路径）
	protected final String myName;
	/**
	 * 构造方法
	 * @param parent 压缩文件对象
	 * @param name 指定的文件名称（相对路径）
	 */
	protected GBArchiveEntryFile(GBFile parent, String name) {
		myParent = parent;
		myName = name;
		init();
	}

	@Override
	public boolean isDirectory() {
		return false;
	}

	@Override
	public String getPath() {
		return myParent.getPath() + ":" + myName;
	}

	@Override
	public String getFullName() {
		return myName;
	}

	@Override
	public GBFile getParent() {
		return myParent;
	}

	@Override
	public GBPhysicalFile getPhysicalFile() {
		GBFile ancestor = myParent;
		while ((ancestor != null) && !(ancestor instanceof GBPhysicalFile)) {
			ancestor = ancestor.getParent();
		}
		return (GBPhysicalFile)ancestor;
	}
}
