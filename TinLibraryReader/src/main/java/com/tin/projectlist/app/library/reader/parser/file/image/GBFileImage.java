package com.tin.projectlist.app.library.reader.parser.file.image;

import com.core.common.util.Base64InputStream;
import com.core.common.util.HexInputStream;
import com.core.common.util.MergedInputStream;
import com.core.common.util.MimeType;
import com.core.common.util.SliceInputStream;
import com.core.file.GBFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * 类名： GBFileImage.java#ZLFileImage<br>
 * 描述： <br>
 * 创建者： jack<br>
 * 创建日期：2013-4-11<br>
 * 版本：  <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public class GBFileImage extends GBSingleImage {
	public static final String SCHEME = "imagefile";
	//图片编码定义
	public static final String ENCODING_NONE = "";
	public static final String ENCODING_HEX = "hex";
	public static final String ENCODING_BASE64 = "base64";

	/**
	 * 功能描述： 根据封装的图片路径（文件地址，编码，开始位置和获取长度等）获取图片封装对象<br>
	 * 创建者： jack<br>
	 * 创建日期：2013-4-11<br>
	 * @param urlPath
	 * @return
	 */
	public static GBFileImage byUrlPath(String urlPath) {
		try {
			final String[] data = urlPath.split("\000");
			int count = Integer.parseInt(data[2]);
			int[] offsets = new int[count];
			int[] lengths = new int[count];
			for (int i = 0; i < count; ++i) {
				offsets[i] = Integer.parseInt(data[3 + i]);
				lengths[i] = Integer.parseInt(data[3 + count + i]);
			}
			return new GBFileImage(
					MimeType.IMAGE_AUTO,
					GBFile.createFileByPath(data[0]),
					data[1],
					offsets,
					lengths
			);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	//图片文件封装对象
	private final GBFile myFile;
	//图片的编码格式
	private final String myEncoding;
	//要获取图片的编码片段开始位置
	private final int[] myOffsets;
	//图片编码片度的对应长度
	private final int[] myLengths;

	public GBFileImage(String mimeType, GBFile file, String encoding, int[] offsets, int[] lengths) {
		this(MimeType.get(mimeType), file, encoding, offsets, lengths);
	}
	/**
	 * 构造方法
	 * @param mimeType 图片类型
	 * @param file 文件对象
	 * @param encoding 编码类型
	 * @param offsets 获取文件的开始位置（位置片短）
	 * @param lengths 要获取内容的长度
	 */
	public GBFileImage(MimeType mimeType, GBFile file, String encoding, int[] offsets, int[] lengths) {
		super(mimeType);
		myFile = file;
		myEncoding = encoding != null ? encoding : ENCODING_NONE;
		myOffsets = offsets;
		myLengths = lengths;
	}

	public GBFileImage(String mimeType, GBFile file, String encoding, int offset, int length) {
		this(MimeType.get(mimeType), file, encoding, offset, length);
	}

	public GBFileImage(MimeType mimeType, GBFile file, String encoding, int offset, int length) {
		super(mimeType);
		myFile = file;
		myEncoding = encoding != null ? encoding : ENCODING_NONE;
		myOffsets = new int[1];
		myLengths = new int[1];
		myOffsets[0] = offset;
		myLengths[0] = length;
	}

	public GBFileImage(MimeType mimeType, GBFile file) {
		this(mimeType, file, ENCODING_NONE, 0, (int)file.size());
	}

	public String getURI() {
		String result = SCHEME + "://" + myFile.getPath() + "\000" + myEncoding + "\000" + myOffsets.length;
		for (int offset : myOffsets) {
			result += "\000" + offset;
		}
		for (int length : myLengths) {
			result += "\000" + length;
		}
		return result;
	}

	@Override
	public InputStream inputStream() {
		try {
			final InputStream stream;
			if (myOffsets.length == 1) {
				final int offset = myOffsets[0];
				final int length = myLengths[0];
				stream = new SliceInputStream(myFile.getInputStream(), offset, length != 0 ? length : Integer.MAX_VALUE);
			} else {
				final InputStream[] streams = new InputStream[myOffsets.length];
				for (int i = 0; i < myOffsets.length; ++i) {
					final int offset = myOffsets[i];
					final int length = myLengths[i];
					streams[i] = new SliceInputStream(myFile.getInputStream(), offset, length != 0 ? length : Integer.MAX_VALUE);
				}
				stream = new MergedInputStream(streams);
			}
			if (ENCODING_NONE.equals(myEncoding)) {
				return stream;
			} else if (ENCODING_HEX.equals(myEncoding)) {
				return new HexInputStream(stream);
			} else if (ENCODING_BASE64.equals(myEncoding)) {
				return new Base64InputStream(stream);
			} else {
				System.err.println("unsupported encoding: " + myEncoding);
				return null;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
