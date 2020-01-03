package com.tin.projectlist.app.library.reader.parser.file;

import java.util.*;
import java.io.*;
/**
 * 类名： GBPhysicalFile.java#ZLPhysicalFile<br>
 * 描述： 物理文件flie对象封装<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-10<br>
 * 版本：  <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public final class GBPhysicalFile extends GBFile {
	private final File myFile;

	GBPhysicalFile(String path) {
		this(new File(path),null);
	}
	GBPhysicalFile(String path,String sufixx) {

		this(new File(path),sufixx);
	}

	public GBPhysicalFile(File file,String sufixx) {
		myFile = file;
		mExtendMapping=sufixx;
		init();
	}

	public GBPhysicalFile(File file) {
		this(file,null);
	}

	@Override
	public boolean exists() {
		return myFile.exists();
	}

	@Override
	public long size() {
		return myFile.length();
	}

	@Override
	public boolean isDirectory() {
		return myFile.isDirectory();
	}

	@Override
	public boolean isReadable() {
		return myFile.canRead();
	}

	public boolean delete() {
		return myFile.delete();
	}

	public File javaFile() {
		return myFile;
	}

	@Override
	public String getPath() {
		try {
			return myFile.getCanonicalPath();
		} catch (Exception e) {
			// should be never thrown
			return myFile.getPath();
		}
	}

	@Override
	public String getFullName() {
		return isDirectory() ? getPath() : myFile.getName();
	}

	@Override
	public GBFile getParent() {
		return isDirectory() ? null : new GBPhysicalFile(myFile.getParent());
	}

	@Override
	public GBPhysicalFile getPhysicalFile() {
		return this;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return new FileInputStream(myFile);
	}
	/**
	 * 获取文件夹下的文件列表
	 */
	protected List<GBFile> directoryEntries() {
		File[] subFiles = myFile.listFiles();
		if ((subFiles == null) || (subFiles.length == 0)) {
			return Collections.emptyList();
		}

		ArrayList<GBFile> entries = new ArrayList<GBFile>(subFiles.length);
		for (File f : subFiles) {
			if (!f.getName().startsWith(".")) {
				entries.add(new GBPhysicalFile(f,null));
			}
		}
		return entries;
	}
}
