package com.core.file;

import com.core.file.zip.GBArchiveEntryFile;
import com.core.file.zip.GBZipEntryFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * 类名： GBFile.java#ZLFile<br>
 * 描述： 自定义文件类型<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-10<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public abstract class GBFile {
	private final static HashMap<String, GBFile> ourCachedFiles = new HashMap<String, GBFile>();

	/**
	 * 支持的文件类型
	 */
	public interface ArchiveType {
		int NONE = 0;
		int GZIP = 0x0001;
		int BZIP2 = 0x0002;
		int COMPRESSED = 0x00ff;
		int ZIP = 0x0100;
		int TAR = 0x0200;
		int ARCHIVE = 0xff00;
	};

	private String mExtension; // 文件后缀

	public void setmExtension(String mExtension) {
		this.mExtension = mExtension;
	}

	protected String mExtendMapping;

	/**
	 *
	 * @param extendMaping
	 *            文件映射后缀
	 */
	public void setExtendMapping(String extendMaping) {
		mExtendMapping = extendMaping;
	}

	public String getExtendMapping() {
		return mExtendMapping;
	}

	private String mShortName; // 文件全名
	public int myArchiveType;// 文件类型
	private boolean myIsCached;// 是否缓存
	private boolean myIsDecrypt;// 是否加密

	/**
	 * 初始化
	 */
	protected void init() {
		final String name = getFullName();
		final int index = name.lastIndexOf('.');
		mExtension = (index > 0) ? name.substring(index + 1).toLowerCase()
				.intern() : "";
		mShortName = name.substring(name.lastIndexOf('/') + 1);

		/*
		 * if (lowerCaseName.endsWith(".gz")) { myNameWithoutExtension =
		 * myNameWithoutExtension.substring(0, myNameWithoutExtension.length() -
		 * 3); lowerCaseName = lowerCaseName.substring(0, lowerCaseName.length()
		 * - 3); myArchiveType = myArchiveType | ArchiveType.GZIP; } if
		 * (lowerCaseName.endsWith(".bz2")) { myNameWithoutExtension =
		 * myNameWithoutExtension.substring(0, myNameWithoutExtension.length() -
		 * 4); lowerCaseName = lowerCaseName.substring(0, lowerCaseName.length()
		 * - 4); myArchiveType = myArchiveType | ArchiveType.BZIP2; }
		 */
		int archiveType = ArchiveType.NONE;
		if (mExtension == "zip") {
			archiveType |= ArchiveType.ZIP;
		} else if (mExtension == "oebzip") {
			archiveType |= ArchiveType.ZIP;
		} else if (mExtension == "epub") {
			archiveType |= ArchiveType.ZIP;
		} else if (mExtension == "geb" && mExtendMapping == "epub") {
			archiveType |= ArchiveType.ZIP;
		} else if (mExtension == "tar") {
			archiveType |= ArchiveType.TAR;
			// } else if (lowerCaseName.endsWith(".tgz")) {
			// nothing to-do myNameWithoutExtension =
			// myNameWithoutExtension.substr(0, myNameWithoutExtension.length()
			// - 3) + "tar";
			// myArchiveType = myArchiveType | ArchiveType.TAR |
			// ArchiveType.GZIP;
		}
		myArchiveType = archiveType;
	}

	/**
	 * 功能描述：根据文件名称创建file对象 <br>
	 * 创建者： jack<br>
	 * 创建日期：2013-4-10<br>
	 *
	 * @param parent
	 *            父文件对象
	 * @param name
	 *            文件名
	 * @return
	 */
	public static GBFile createFile(GBFile parent, String name) {
		GBFile file = null;
		if (parent == null) {
			GBFile cached = ourCachedFiles.get(name);
			if (cached != null) {
				return cached;
			}
			if (!name.startsWith("/")) {
				return GBResourceFile.createResourceFile(name);
			} else {
				return new GBPhysicalFile(name);
			}
		} else if ((parent instanceof GBPhysicalFile)
				&& (parent.getParent() == null)) {
			// parent is a directory
			file = new GBPhysicalFile(parent.getPath() + '/' + name);
		} else if (parent instanceof GBResourceFile) {
			file = GBResourceFile.createResourceFile((GBResourceFile) parent,
					name);
		} else {
			file = GBArchiveEntryFile.createArchiveEntryFile(parent, name);
		}

		if (!ourCachedFiles.isEmpty() && (file != null)) {
			GBFile cached = ourCachedFiles.get(file.getPath());
			if (cached != null) {
				return cached;
			}
			// 这是是否需要一个else把文件加进缓存中？
		}
		return file;
	}

	/**
	 * 通过URL创建文件对象
	 */
	public static GBFile createFileByUrl(String url) {
		if (url == null || !url.startsWith("file://")) {
			return null;
		}
		return createFileByPath(url.substring("file://".length()));
	}

	public static GBFile createFileByPath(String path) {
		return createFileByPath(path, null);
	}

	/**
	 * 通过路径创建文件
	 */

	public static GBFile createFileByPath(String path, String suffix) {
		if (path == null) {
			return null;
		}

		GBFile cached = ourCachedFiles.get(path);
		if (cached != null) {
			return cached;
		}

		if (!path.startsWith("/")) {
			while (path.startsWith("./")) {
				path = path.substring(2);
			}
			return GBResourceFile.createResourceFile(path);
		}
		int index = path.lastIndexOf(':');
		if (index > 1) {
			return GBArchiveEntryFile.createArchiveEntryFile(
					createFileByPath(path.substring(0, index), suffix),
					path.substring(index + 1));
		}

		return new GBPhysicalFile(path, suffix);

	}

	/**
	 * 文件大小
	 */
	public abstract long size();

	/**
	 * 文件是否存在
	 */
	public abstract boolean exists();

	/**
	 * 是否是文件夹
	 */
	public abstract boolean isDirectory();

	/**
	 * 获取文件路径
	 */
	public abstract String getPath();

	/**
	 * 获取父文件夹
	 */
	public abstract GBFile getParent();

	/**
	 * 功能描述： 获取文件的物理文件封装对象<br>
	 * 创建者： jack<br>
	 * 创建日期：2013-4-10<br>
	 *
	 * @return
	 */
	public abstract GBPhysicalFile getPhysicalFile();

	/**
	 * 获取输入流
	 */
	public abstract InputStream getInputStream() throws IOException;

	/**
	 * 获取URL
	 */
	public String getUrl() {
		return "file://" + getPath();
	}

	/**
	 * 是否可读
	 */
	public boolean isReadable() {
		return true;
	}

	/**
	 * 是否经过编码
	 */
	public final boolean isCompressed() {
		return (0 != (myArchiveType & ArchiveType.COMPRESSED));
	}

	/**
	 *
	 */
	public final boolean isArchive() {
		return (0 != (myArchiveType & ArchiveType.ARCHIVE));
	}

	/**
	 * getLongName 功能描述： 获取文件全名/压缩文件的相对路径<br>
	 * 创建者： jack<br>
	 * 创建日期：2013-4-10<br>
	 *
	 * @return
	 */
	public abstract String getFullName();

	/**
	 * 功能描述： 获取文件全名<br>
	 * 创建者： jack<br>
	 * 创建日期：2013-4-10<br>
	 *
	 * @return
	 */
	public final String getShortName() {
		return mShortName;
	}

	public final String getExtension() {
		return mExtension;
	}

	protected List<GBFile> directoryEntries() {
		return Collections.emptyList();
	}

	public final List<GBFile> children() {
		if (exists()) {
			if (isDirectory()) {
				return directoryEntries();
			} else if (isArchive()) {
				return GBArchiveEntryFile.archiveEntries(this);
			}
		}
		return Collections.emptyList();
	}

	@Override
	public int hashCode() {
		return getPath().hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof GBFile)) {
			return false;
		}
		return getPath().equals(((GBFile) o).getPath());
	}

	@Override
	public String toString() {
		return "ZLFile [" + getPath() + "]";
	}

	public boolean isCached() {
		return myIsCached;
	}

	public boolean isDecrypt() {
		return myIsDecrypt;
	}

	public void setDecrypt(boolean isDecrypt) {
		myIsDecrypt = isDecrypt;
	}

	public void setCached(boolean cached) {
		myIsCached = cached;
		if (cached) {
			ourCachedFiles.put(getPath(), this);
		} else {
			ourCachedFiles.remove(getPath());
			if (0 != (myArchiveType & ArchiveType.ZIP)) {
				GBZipEntryFile.removeFromCache(this);

			}
		}
	}
}
