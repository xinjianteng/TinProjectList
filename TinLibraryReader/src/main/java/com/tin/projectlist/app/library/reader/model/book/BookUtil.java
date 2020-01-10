package com.tin.projectlist.app.library.reader.model.book;

import com.core.file.GBFile;
import com.core.file.GBResourceFile;
import com.core.file.image.GBImage;
import com.core.file.zip.GBArchiveEntryFile;
import com.geeboo.read.model.bookmodel.BookReadingException;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.Locale;

/**
 * 类名： BookUtil.java<br>
 * 描述： <br>
 * 创建者： jack<br>
 * 创建日期：2013-4-25<br>
 * 版本：  <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public abstract class BookUtil {
	public static GBImage getCover(Book book) {
		return book != null ? book.getCover() : null;
	}

	public static String getAnnotation(Book book) {
		try {
			return book.getPlugin().readAnnotation(book.File);
		} catch (BookReadingException e) {
			return null;
		}
	}

	public static GBResourceFile getHelpFile() {
		final Locale locale = Locale.getDefault();

		GBResourceFile file = GBResourceFile.createResourceFile(
				"data/help/MiniHelp." + locale.getLanguage() + "_" + locale.getCountry() + ".fb2"
		);
		if (file.exists()) {
			return file;
		}

		file = GBResourceFile.createResourceFile(
				"data/help/MiniHelp." + locale.getLanguage() + ".fb2"
		);
		if (file.exists()) {
			return file;
		}

		return GBResourceFile.createResourceFile("data/help/MiniHelp.en.fb2");
	}

	public static boolean canRemoveBookFile(Book book) {
		GBFile file = book.File;
		if (file.getPhysicalFile() == null) {
			return false;
		}
		while (file instanceof GBArchiveEntryFile) {
			file = file.getParent();
			if (file.children().size() != 1) {
				return false;
			}
		}
		return true;
	}
	/**
	 * 功能描述： 获取SHA-256算法的文件摘要信息标识<br>
	 * 创建者： jack<br>
	 * 创建日期：2013-4-25<br>
	 * @param file 要获取的文件
	 * @return
	 */
	public static UID createSHA256Uid(GBFile file) {
		InputStream stream = null;

		try {
			//创建具有指定算法名称的信息摘要
			final MessageDigest hash = MessageDigest.getInstance("SHA-256");
			stream = file.getInputStream();

			final byte[] buffer = new byte[2048];
			while (true) {
				final int nread = stream.read(buffer);
				if (nread == -1) {
					break;
				}
				hash.update(buffer, 0, nread);
			}

			final Formatter f = new Formatter();
			for (byte b : hash.digest()) {
				f.format("%02X", b & 0xFF);
			}
			return new UID("SHA256", f.toString());
		} catch (IOException e) {
			return null;
		} catch (NoSuchAlgorithmException e) {
			return null;
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
				}
			}
		}
	}

}
