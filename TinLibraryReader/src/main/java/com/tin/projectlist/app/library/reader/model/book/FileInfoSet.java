package com.tin.projectlist.app.library.reader.model.book;

import com.tin.projectlist.app.library.reader.parser.common.util.MiscUtil;
import com.tin.projectlist.app.library.reader.parser.file.GBFile;
import com.tin.projectlist.app.library.reader.parser.file.GBPhysicalFile;
import com.tin.projectlist.app.library.reader.parser.file.zip.GBArchiveEntryFile;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * 类名： FileInfoSet.java<br>
 * 描述： 文件信息集合<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-25<br>
 * 版本：  <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public final class FileInfoSet {
	private static final class Pair {
		private final String myName;
		private final FileInfo myParent;

		Pair(String name, FileInfo parent) {
			myName = name;
			myParent = parent;
		}

		@Override
		public int hashCode() {
			return (myParent == null) ? myName.hashCode() : myParent.hashCode() + myName.hashCode();
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (!(o instanceof Pair)) {
				return false;
			}
			Pair p = (Pair)o;
			return myName.equals(p.myName) && MiscUtil.equals(myParent, p.myParent);
		}
	}

	private final HashMap<GBFile,FileInfo> myInfosByFile = new HashMap<GBFile,FileInfo>();
	private final HashMap<FileInfo,GBFile> myFilesByInfo = new HashMap<FileInfo,GBFile>();
	private final HashMap<Pair,FileInfo> myInfosByPair = new HashMap<Pair,FileInfo>();
	private final HashMap<Long,FileInfo> myInfosById = new HashMap<Long,FileInfo>();

	private final LinkedHashSet<FileInfo> myInfosToSave = new LinkedHashSet<FileInfo>();
	private final LinkedHashSet<FileInfo> myInfosToRemove = new LinkedHashSet<FileInfo>();
	//图书信息存储对象
	private final BooksDatabase myDatabase;

	public FileInfoSet(BooksDatabase database) {
		myDatabase = database;
		load(database.loadFileInfos());
	}

	public FileInfoSet(BooksDatabase database, GBFile file) {
		myDatabase = database;
		load(database.loadFileInfos(file));
	}

	FileInfoSet(BooksDatabase database, long fileId) {
		myDatabase = database;
		load(database.loadFileInfos(fileId));
	}

	private void load(Collection<FileInfo> infos) {
		for (FileInfo info : infos) {
			myInfosByPair.put(new Pair(info.Name, info.Parent), info);
			myInfosById.put(info.Id, info);
		}
	}

	public void save() {
		myDatabase.executeAsTransaction(new Runnable() {
			public void run() {
				for (FileInfo info : myInfosToRemove) {
					myDatabase.removeFileInfo(info.Id);
					myInfosByPair.remove(new Pair(info.Name, info.Parent));
				}
				myInfosToRemove.clear();
				for (FileInfo info : myInfosToSave) {
					myDatabase.saveFileInfo(info);
				}
				myInfosToSave.clear();
			}
		});
	}

	public boolean check(GBPhysicalFile file, boolean processChildren) {
		if (file == null) {
			return true;
		}
		final long fileSize = file.size();
		FileInfo info = get(file);
		if (info.FileSize == fileSize) {
			return true;
		} else {
			info.FileSize = fileSize;
			if (processChildren && !"epub".equals(file.getExtension())) {
				removeChildren(info);
				myInfosToSave.add(info);
				addChildren(file);
			} else {
				myInfosToSave.add(info);
			}
			return false;
		}
	}

	public List<GBFile> archiveEntries(GBFile file) {
		final FileInfo info = get(file);
		if (!info.hasChildren()) {
			return Collections.emptyList();
		}
		final LinkedList<GBFile> entries = new LinkedList<GBFile>();
		for (FileInfo child : info.subTrees()) {
			if (!myInfosToRemove.contains(child)) {
				entries.add(GBArchiveEntryFile.createArchiveEntryFile(file, child.Name));
			}
		}
		return entries;
	}

	private FileInfo get(String name, FileInfo parent) {
		final Pair pair = new Pair(name, parent);
		FileInfo info = myInfosByPair.get(pair);
		if (info == null) {
			info = new FileInfo(name, parent);
			myInfosByPair.put(pair, info);
			myInfosToSave.add(info);
		}
		return info;
	}


	private FileInfo get(GBFile file) {
		if (file == null) {
			return null;
		}
		FileInfo info = myInfosByFile.get(file);
		if (info == null) {
			info = get(file.getFullName(), get(file.getParent()));
			myInfosByFile.put(file, info);
		}
		return info;
	}

	public long getId(GBFile file) {
		final FileInfo info = get(file);
		if (info == null) {
			return -1;
		}
		if (info.Id == -1) {
			save();
		}
		return info.Id;
	}

	private GBFile getFile(FileInfo info) {
		if (info == null) {
			return null;
		}
		GBFile file = myFilesByInfo.get(info);
		if (file == null) {
			file = GBFile.createFile(getFile(info.Parent), info.Name);
			myFilesByInfo.put(info, file);
		}
		return file;
	}

	public GBFile getFile(long id) {
		return getFile(myInfosById.get(id));
	}

	private void removeChildren(FileInfo info) {
		for (FileInfo child : info.subTrees()) {
			if (myInfosToSave.contains(child)) {
				myInfosToSave.remove(child);
			} else {
				myInfosToRemove.add(child);
			}
			removeChildren(child);
		}
	}

	private void addChildren(GBFile file) {
		for (GBFile child : file.children()) {
			final FileInfo info = get(child);
			if (myInfosToRemove.contains(info)) {
				myInfosToRemove.remove(info);
			} else {
				myInfosToSave.add(info);
			}
			addChildren(child);
		}
	}
}
