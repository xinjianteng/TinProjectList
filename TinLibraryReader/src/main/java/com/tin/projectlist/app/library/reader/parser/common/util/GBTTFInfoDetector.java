package com.tin.projectlist.app.library.reader.parser.common.util;

import java.util.*;
import java.io.*;


/*
 * TTF(TrueTypeFont)是一种字库名称。是Apple公司和Microsoft公司共同推出的字体文件格式，随着windows的流行，已经变成最常用的一种字体文件表示方式
 */
/*
 * ASCII（American Standard Code for Information Interchange）美国信息交换标准代码
 */

/**
 * TTFInfo观察者
 * @author fuchen
 * @date 2013-4-8
 */
public class GBTTFInfoDetector {


	/**
	 * 搜集字体
	 * @param files 字体文件
	 * @return Map  key为字体名字 value为文件数组，根据子名字将文件放在数组不同的位置
	 * @author fuchen
	 * @date 2013-4-8
	 */
	public Map<String,File[]> collectFonts(Iterable<File> files) {
		final HashMap<String,File[]> fonts = new HashMap<String,File[]>();
		if (files == null) {
			return fonts;
		}

		for (File f : files) {
			try {
				final GBTTFInfo info = detectInfo(f);
				if (info != null && info.FamilyName != null && !"".equals(info.FamilyName.trim())) {
					File[] table = fonts.get(info.FamilyName);
					if (table == null) {
						table = new File[4];
						fonts.put(info.FamilyName, table);
					}
					if ("bold".equalsIgnoreCase(info.SubFamilyName)) {
						table[1] = f;
					} else if ("italic".equalsIgnoreCase(info.SubFamilyName) ||
							"oblique".equalsIgnoreCase(info.SubFamilyName)) {
						table[2] = f;
					} else if ("bold italic".equalsIgnoreCase(info.SubFamilyName) ||
							"bold oblique".equalsIgnoreCase(info.SubFamilyName)) {
						table[3] = f;
					} else {
						table[0] = f;
					}
				}
			} catch (IOException e) {
			}
		}
		return fonts;
	}


    /*
     * detect 发现，检查
     */
	/**
	 * 检查传入文件的字体信息
	 * @param file 文件
	 * @return GBTTFInfo信息
	 * @throws IOException
	 * @author fuchen
	 * @date 2013-4-9
	 */
	public GBTTFInfo detectInfo(File file) throws IOException {
		myStream = new FileInputStream(file);
		myPosition = 0;

		final byte[] subtable = new byte[12];
		myPosition += myStream.read(subtable);

		final int numTables = getInt16(subtable, 4);
		final byte[] tables = new byte[16 * numTables];
		myPosition += myStream.read(tables);

		TableInfo nameInfo = null;
		for (int i = 0; i < numTables; ++i) {
			if ("name".equals(new String(tables, i * 16, 4, "ascii"))) {
				nameInfo = new TableInfo(tables, i * 16);
				break;
			}
		}
		if (nameInfo == null) {
			return null;
		}
		return readFontInfo(nameInfo);
	}

	/**
	 * 获取16位表示的一个整形数
	 * @param buffer 字节数据源
	 * @param offset 开始下标
	 * @return
	 * @author fuchen
	 * @date 2013-4-9
	 */
	private static int getInt16(byte[] buffer, int offset) {
		return
				((buffer[offset] & 0xFF) << 8) +
						(buffer[offset + 1] & 0xFF);
	}

	/**
	 * 获取32位表示的一个整形数
	 * @param buffer 字节数据源
	 * @param offset 开始下标
	 * @return
	 * @author fuchen
	 * @date 2013-4-9
	 */
	private static int getInt32(byte[] buffer, int offset) {
		if (offset <= buffer.length - 4) {
			return
					((buffer[offset++] & 0xFF) << 24) +
							((buffer[offset++] & 0xFF) << 16) +
							((buffer[offset++] & 0xFF) << 8) +
							(buffer[offset++] & 0xFF);
		} else {
			int result = 0;
			for (int i = 0; i < 4; ++i) {
				result += offset < buffer.length ? (buffer[offset++] & 0xFF) : 0;
				result <<= 8;
			}
			return result;
		}
	}

	private InputStream myStream;
	private int myPosition;

	private static class TableInfo {
		final String Name;
		//final int CheckSum;
		final int Offset;
		final int Length;

		TableInfo(byte[] buffer, int off) throws IOException {
			Name = new String(buffer, off, 4, "ascii");
			//CheckSum = getInt32(buffer, off + 4);
			Offset = getInt32(buffer, off + 8);
			Length = getInt32(buffer, off + 12);
		}

		/*void print(PrintStream writer) {
			writer.println(Name + " : " + Offset + " : " + Length + " : " + CheckSum);
		}*/
	}

	byte[] readTable(TableInfo info) throws IOException {
		myPosition += (int)myStream.skip(info.Offset - myPosition);
		byte[] buffer = new byte[info.Length];
		while (myPosition < info.Offset) {
			int len = myStream.read(buffer, 0, Math.min(info.Offset - myPosition, info.Length));
			if (len <= 0) {
				throw new IOException("Table " + info.Name + " not found in TTF file");
			}
			myPosition += len;
		}
		myPosition += myStream.read(buffer);
		/*
		int sum = 0;
		for (int i = 0; i < info.Length; i += 4) {
			sum += getInt32(buffer, i);
		}
		if (info.CheckSum != sum) {
			//System.out.println(info.Length + ":" + info.CheckSum + ":" + sum);
			//throw new IOException("Checksum for table " + info.Name + " is not correct");
		}
		*/
		return buffer;
	}

	private GBTTFInfo readFontInfo(TableInfo nameInfo) throws IOException {
		if (nameInfo == null || nameInfo.Offset < myPosition || nameInfo.Length <= 0) {
			return null;
		}
		byte[] buffer;
		try {
			buffer = readTable(nameInfo);
		} catch (Throwable e) {
			return null;
		}
		if (getInt16(buffer, 0) != 0) {
			throw new IOException("Name table format is invalid");
		}
		final int count = Math.min(getInt16(buffer, 2), (buffer.length - 6) / 12);
		final int stringOffset = getInt16(buffer, 4);
		final GBTTFInfo fontInfo = new GBTTFInfo();
		for (int i = 0; i < count; ++i) {
			final int platformId = getInt16(buffer, 12 * i + 6);
			//final int platformSpecificId = getInt16(buffer, 12 * i + 8);
			final int languageId = getInt16(buffer, 12 * i + 10);
			final int nameId = getInt16(buffer, 12 * i + 12);
			final int length = getInt16(buffer, 12 * i + 14);
			final int offset = getInt16(buffer, 12 * i + 16);
			switch (nameId) {
				case 1:
					if ((fontInfo.FamilyName == null || languageId == 1033) &&
							stringOffset + offset + length <= buffer.length) {
						fontInfo.FamilyName = new String(
								buffer, stringOffset + offset, length,
								platformId == 1 ? "windows-1252" : "UTF-16BE"
						);
					}
					break;
				case 2:
					if ((fontInfo.FamilyName == null || languageId == 1033) &&
							stringOffset + offset + length <= buffer.length) {
						fontInfo.SubFamilyName = new String(
								buffer, stringOffset + offset, length,
								platformId == 1 ? "windows-1252" : "UTF-16BE"
						);
					}
					break;
			}
		}
		return fontInfo;
	}
}
