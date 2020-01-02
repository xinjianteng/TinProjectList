package com.core.text.model.cache.impl;

import com.core.log.L;
import com.core.text.model.cache.CachedCharStorageBase;
import com.core.text.model.cache.exception.CachedCharStorageException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 *
 * 描述： 缓存管理实现类 创建者： 燕冠楠<br>
 * 创建日期：2013-3-26<br>
 */
public final class CachedCharStorage extends CachedCharStorageBase {
	final String TAG = "CachedCharStorage";

	private final int myBlockSize;

	/**
	 *
	 * @param blockSize
	 *            char数组长度
	 * @param directoryName
	 *            缓存路径
	 * @param fileExtension
	 */
	public CachedCharStorage(int blockSize, String directoryName,
							 String bookName, String fileExtension, int chpFileSize) {
		super(directoryName, bookName, fileExtension, chpFileSize);
		myBlockSize = blockSize;
		// 创建缓存文件夹
		new File(directoryName).mkdirs();

	}

	public char[] createNewBlock(int chpFileIndex, int minimumLength) {
		System.gc();
		int blockSize = myBlockSize;
		if (minimumLength > blockSize) {
			blockSize = minimumLength;
		}
		char[] block = new char[blockSize];
		// try{
		myArray[chpFileIndex].add(new WeakReference<char[]>(block));
		/*
		 * }catch(Exception ex){ L.e(TAG,
		 * "myArray length"+myArray.length+"chp file index"+chpFileIndex); }
		 */
		// L.e(TAG, "createNewBlock index=" + (myArray[chpFileIndex].size() -
		// 1));
		// mChpFileIndex=chpFileIndex;
		// System.err.print("myArray=="+myArray);
		return block;
	}

	/*
	 * public char[] createNewChpBlock(int chpFileIndex,int minimumLength){
	 * mChpFileIndex=chpFileIndex; int blockSize = myBlockSize; if
	 * (minimumLength > blockSize) { blockSize = minimumLength; } char[] block =
	 * new char[blockSize]; myArray[mChpFileIndex].add(new
	 * WeakReference<char[]>(block)); return null; }
	 */

	public void freezeLastBlock(int chpFileIndex, boolean ischeckExists) {

		if (chpFileIndex < 0 || chpFileIndex >= myArray.length) {
			return;
		}
		int index = myArray[chpFileIndex].size() - 1;

		if (index >= 0) {
			// L.e(TAG + "@@test", "freezeLastBlock chpFileIndex="+
			// chpFileIndex);

			char[] block = myArray[chpFileIndex].get(index).get();
			if (block == null) {
				// L.e(TAG, "chpFileIndex=" + chpFileIndex + " index=" +
				// index);
				/*
				 * throw new CachedCharStorageException(
				 * "Block reference in null during freeze");
				 */
				return;
			}
			try {
				String cacheName = fileName(chpFileIndex, index);
				if (ischeckExists) {
					File file = new File(cacheName);
					if (file.exists()) {
						return;
					}
				}
				// L.e(TAG, "freezelastblock "+chpFileIndex);
				// System.err.print("cacheName=====" + cacheName);
				final OutputStreamWriter writer = new OutputStreamWriter(
						new FileOutputStream(cacheName), "UTF-16LE");
				writer.write(block);
				writer.close();
			} catch (IOException e) {
				throw new CachedCharStorageException("Error during writing "
						+ fileName(chpFileIndex, index));
			}
		}
	}

	@Override
	public void freezeLastChpBlock() {

		String cacheName = null;
		OutputStreamWriter writer;
		try {

			for (int index = 0; index < myArray[mChpFileIndex].size(); index++) {

				cacheName = fileName(mChpFileIndex, index);
				writer = new OutputStreamWriter(
						new FileOutputStream(cacheName), "UTF-16LE");

				char[] block = myArray[mChpFileIndex].get(index).get();
				if (block == null) {
					L.e(TAG, "chpFileIndex=" + mChpFileIndex + " index="
							+ index);
					continue;
					/*
					 * throw new CachedCharStorageException(
					 * "Block reference in null during freeze");
					 */
				}
				writer.write(block);
				writer.close();
				// writer.flush();
			}

		} catch (IOException e1) {
			throw new CachedCharStorageException("Error during writing "
					+ cacheName);
		}

	}

	@Override
	public int getChpFileNum() {

		return this.mChpFileIndex;
	}

	@Override
	public void setChpFileNum(int chpFileNum) {
		this.mChpFileIndex = chpFileNum;
	}

	@Override
	public void resetChpSize(int newChpSize) {
		if (newChpSize < myArray.length)
			return;
		mChpFileSize = newChpSize;
		ArrayList<WeakReference<char[]>>[] newArray = new ArrayList[newChpSize];
		int currentSize = myArray.length;
		System.arraycopy(myArray, 0, newArray, 0, myArray.length);
		for (int i = currentSize; i < newArray.length; i++) {
			newArray[i] = new ArrayList<WeakReference<char[]>>();
		}
		L.e(TAG, "array reset" + newChpSize);
		myArray = newArray;

	}

}
