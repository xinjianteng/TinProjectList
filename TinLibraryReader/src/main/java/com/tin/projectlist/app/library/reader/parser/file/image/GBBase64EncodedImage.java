package com.tin.projectlist.app.library.reader.parser.file.image;

import com.tin.projectlist.app.library.reader.parser.common.util.MimeType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
/**
 * 类名： ZLBase64EncodedImage.java<br>
 * 描述： base64编码图片封装<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-10<br>
 * 版本：  <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public abstract class GBBase64EncodedImage extends GBSingleImage {
	private boolean myIsDecoded;

	protected GBBase64EncodedImage(MimeType mimeType) {
		super(mimeType);
	}
	/**
	 * 功能描述： base64编码图片字符解码<br>
	 * 创建者： jack<br>
	 * 创建日期：2013-4-11<br>
	 * @param encodedByte
	 * @return
	 */
	protected static byte decodeByte(byte encodedByte) {
		switch (encodedByte) {
			case 'A': case 'B': case 'C': case 'D': case 'E': case 'F':
			case 'G': case 'H': case 'I': case 'J': case 'K': case 'L':
			case 'M': case 'N': case 'O': case 'P': case 'Q': case 'R':
			case 'S': case 'T': case 'U': case 'V': case 'W': case 'X':
			case 'Y': case 'Z':
				return (byte)(encodedByte - 'A');
			case 'a': case 'b': case 'c': case 'd': case 'e': case 'f':
			case 'g': case 'h': case 'i': case 'j': case 'k': case 'l':
			case 'm': case 'n': case 'o': case 'p': case 'q': case 'r':
			case 's': case 't': case 'u': case 'v': case 'w': case 'x':
			case 'y': case 'z':
				return (byte)(encodedByte - 'a' + 26);
			case '0': case '1': case '2': case '3': case '4':
			case '5': case '6': case '7': case '8': case '9':
				return (byte)(encodedByte - '0' + 52);
			case '+':
				return 62;
			case '/':
				return 63;
			case '=':
				return 64;
		}
		return -1;
	}

	public String getURI() {
		try {
			decode();
			final File file = new File(decodedFilePath());
			return GBFileImage.SCHEME + "://" + decodedFilePath() + "\000\0000\000" + (int)file.length();
		} catch (Exception e) {
			return null;
		}
	}
	/*
	 * encodedFileName
	 * 获取base64编码图片路径
	 */
	protected abstract String encodedFilePath();
	/*
	 * decodedFileName
	 * 获取base64编码图片解码后文件路径
	 */
	protected abstract String decodedFilePath();

	protected boolean isCacheValid(File file) {
		return false;
	}
	/*
	 * 解码base64编码图片
	 */
	private void decode() throws IOException {
		if (myIsDecoded) {
			return;
		}
		myIsDecoded = true;

		final File outputFile = new File(decodedFilePath());
		if (isCacheValid(outputFile)) {
			return;
		}

		FileOutputStream outputStream = new FileOutputStream(outputFile);
		try {
			int dataLength;
			byte[] encodedData;

			final File file = new File(encodedFilePath());
			final FileInputStream inputStream = new FileInputStream(file);
			try {
				dataLength = (int)file.length();
				encodedData = new byte[dataLength];
				inputStream.read(encodedData);
			} finally {
				inputStream.close();
			}
			file.delete();

			final byte[] data = new byte[dataLength * 3 / 4 + 4];
			int dataPos = 0;
			for (int pos = 0; pos < dataLength; ) {
				byte n0 = -1, n1 = -1, n2 = -1, n3 = -1;
				while (pos < dataLength && n0 == -1) {
					n0 = decodeByte(encodedData[pos++]);
				}
				while (pos < dataLength && n1 == -1) {
					n1 = decodeByte(encodedData[pos++]);
				}
				while (pos < dataLength && n2 == -1) {
					n2 = decodeByte(encodedData[pos++]);
				}
				while (pos < dataLength && n3 == -1) {
					n3 = decodeByte(encodedData[pos++]);
				}
				data[dataPos++] = (byte)(n0 << 2 | n1 >> 4);
				data[dataPos++] = (byte)(((n1 & 0xf) << 4) | ((n2 >> 2) & 0xf));
				data[dataPos++] = (byte)(n2 << 6 | n3);
			}
			outputStream.write(data, 0, dataPos);
		} finally {
			outputStream.close();
		}
	}

	@Override
	public final InputStream inputStream() {
		try {
			decode();
			return new FileInputStream(new File(decodedFilePath()));
		} catch (IOException e) {
			return null;
		}
	}
}
