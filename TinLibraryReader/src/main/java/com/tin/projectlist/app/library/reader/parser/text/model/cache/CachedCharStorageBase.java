package com.core.text.model.cache;

import com.core.log.L;
import com.core.text.model.cache.exception.CachedCharStorageException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 *
 * 描述： Char[]缓存基类 创建者： 燕冠楠<br>
 * 创建日期：2013-3-26<br>
 */
public abstract class CachedCharStorageBase implements CharStorage {
	static final String TAG = "CachedCharStorageBase";

	protected ArrayList<WeakReference<char[]>>[] myArray;

	private final String myDirectoryName;
	private final String myBookName;
	private final String myFileExtension;

	protected int mChpFileIndex = 0;
	protected int mChpFileSize = 0;

	public int getChpFileSize() {
		return mChpFileSize;
	}

	/**
	 *
	 * @param directoryName
	 *            缓存文件名称
	 * @param fileExtension
	 *            缓存文件后缀
	 */
	public CachedCharStorageBase(String directoryName, String bookName,
								 String fileExtension, int chpFileSize) {
		myDirectoryName = directoryName + '/';
		myBookName = bookName;
		myFileExtension = '.' + fileExtension;
		mChpFileSize = chpFileSize;
		myArray = new ArrayList[mChpFileSize];
		for (int i = 0; i < myArray.length; i++) {
			myArray[i] = new ArrayList<WeakReference<char[]>>();
		}

		L.e(TAG, "array size=" + mChpFileSize);

	}

	/**
	 * 按索引获取缓存文件路径
	 */
	protected String fileName(int chpFileIndex, int index) {// myBookName + "_"
		// +
		mChpFileIndex = chpFileIndex;
		return myDirectoryName + chpFileIndex + "_" + index + myFileExtension;
	}

	protected String fileName(String linkCacheName) {
		return myDirectoryName + myBookName + "_" + linkCacheName
				+ myFileExtension;
	}

	@Override
	public boolean delCacheItemAll() {
		File file = new File(myDirectoryName);
		File[] fileList = file.listFiles();
		if (null != fileList && fileList.length > 0) {
			for (File item : fileList) {
				if (item.isDirectory()) {
					continue;
				}
				int lastPoint = item.getName().lastIndexOf(".") - 1;
				if (lastPoint < 0) {
					continue;
				}
				int mark = item.getName().charAt(lastPoint);
				// 判断后缀相同并且名字最后一个字符为数字说明是子项缓存 和删除分页缓存
				if (item.getName().endsWith(myFileExtension)
						&& ((mark > 47 && mark < 58))) { // ||
					// item.getName().contains(myBookName)
					if (!item.delete())
						item.deleteOnExit();
				}
			}
		}
		return true;
	}

	@Override
	public boolean delLinkCache() {
		File file = new File(myDirectoryName);
		File[] fileList = file.listFiles();
		if (null != fileList && fileList.length > 0) {
			for (File item : fileList) {
				if (item.isDirectory()) {
					continue;
				}
				int lastPoint = item.getName().lastIndexOf(".") - 1;
				if (lastPoint < 0) {
					continue;
				}
				// 判断后缀相同并且名字最后一个字符为数字说明是子项缓存 和删除分页缓存
				if (item.getName().endsWith(myFileExtension)
						&& item.getName().contains(myBookName)) {
					if (!item.delete())
						item.deleteOnExit();
				}
			}
		}
		return true;
	}

	/**
	 * 返回char[]块数（不是cha[]长度，而是表示当前使用了几个char[]）
	 */
	@Override
	public int size() {
		return myArray[mChpFileIndex].size();
	}

	@Override
	public int size(int chpFileNum) {
		// L.e(TAG,"myArray.length "+myArray.length+" chpFileNum "+chpFileNum);
		return myArray[chpFileNum].size();
	}

	@Override
	public char[] block(int chpFileIndex, int index) {

		String cacheName = "";
		try {
			char[] block = myArray[chpFileIndex].get(index).get();
			if (block == null) {
				try {
					cacheName = fileName(chpFileIndex, index);
					File file = new File(fileName(chpFileIndex, index));
					if (!file.exists()) {
						return null;
					}
					int size = (int) file.length();
					if (size < 0) {
						throw new CachedCharStorageException(
								"Error during reading "
										+ fileName(chpFileIndex, index));
					}
					block = new char[size / 2];
					InputStreamReader reader = new InputStreamReader(
							new FileInputStream(file), "UTF-16LE");
					if (reader.read(block) != block.length) {
						throw new CachedCharStorageException(
								"2Error during reading "
										+ fileName(chpFileIndex, index));
					}
					reader.close();
				} catch (IOException e) {
					throw new CachedCharStorageException(
							"Error during reading "
									+ fileName(chpFileIndex, index));
				}
				myArray[chpFileIndex].set(index, new WeakReference<char[]>(
						block));

			}
			return block;
		} catch (Exception ex) {
			StringBuilder sb = new StringBuilder("get block by chpFileIndex ");
			sb.append(chpFileIndex);
			sb.append(" index ");
			sb.append(index);
			sb.append(" is null");
			L.e(TAG, sb.toString());
		}
		// System.out.println(cacheName);
		return null;

	}

	/**
	 * 按索引在内存里检索Char数组，内存里若没有到相对应的缓存文件获取
	 */
	@Override
	public char[] block(int index) {
		return block(mChpFileIndex, index);
	}

	final String TEMP_PREFIX = "temp_cache_file_";

	@Override
	public boolean isCacheExists(boolean isTemp) {

		String prefix = isTemp ? TEMP_PREFIX : "";

		File file = new File(fileName(prefix + "TextSizes_new"));
		return file.exists();
	}

	@Override
	public boolean isCacheItemExists(int chpFileIndex) {
		File file = new File(fileName(chpFileIndex, 0));
		return file.exists();
	}

	@Override
	public boolean setLinkCache(String cacheName, Object obj) {
		final String realCacehName = fileName(cacheName);
		FileOutputStream fileOut = null;
		ObjectOutputStream out = null;
		boolean flag = false;
		try {
			fileOut = new FileOutputStream(realCacehName);
			out = new ObjectOutputStream(fileOut);
			out.writeObject(obj);
			flag = true;
		} catch (IOException e) {
			e.fillInStackTrace();
		} finally {
			try {
				if (fileOut != null) {
					fileOut.close();
				}
				if (out != null) {
					out.close();
					out.flush();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return flag;
	}

	@Override
	public Object getLinkCache(String cacheName) {

		final String realCacheName = fileName(cacheName);
		File file = new File(realCacheName);
		L.i("getLinkCache :"+file.getAbsolutePath());
		if (!file.exists()) {
			return null;
		}
		Object obj = null;
		FileInputStream fieIn = null;
		ObjectInputStream in = null;
		try {
			fieIn = new FileInputStream(file);
			in = new ObjectInputStream(fieIn);
			obj = in.readObject();
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			try {
				if (fieIn != null) {
					fieIn.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (obj == null && !file.delete()) {
				file.deleteOnExit();
			}
		}
		return obj;
	}

	@Override
	public void clearMemoryByChpIndex(int chpFileIndex) {
		/* try{ */
		if (myArray != null && myArray.length > chpFileIndex) {
			if (null != myArray[chpFileIndex]) {
				myArray[chpFileIndex].clear();
			}
		}
	}

	@Override
	public void delCache(String name) {

		final String realCacheName = fileName(name);
		File file = new File(realCacheName);
		if (file.exists()) {
			file.delete();
		}

	}

	@Override
	public void initDir() {
		File file = new File(myDirectoryName);
		if (!file.exists()) {
			file.mkdir();
		}

	}

	@Override
	public void finalize() {
		if (myArray != null) {
			for (ArrayList<WeakReference<char[]>> al : myArray) {
				al.clear();
			}
		}
		System.gc();
	}
}
