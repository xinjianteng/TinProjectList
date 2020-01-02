package com.core.zip;

import java.io.*;

/**
 * 描述：zip文件输入流 <br>
 * 创建者： 符晨<br>
 * 创建日期：2013-4-1<br>
 */
class ZipInputStream extends InputStream {
	private final ZipFile myParent;
	private final MyBufferedInputStream myBaseStream;
	private final Decompressor myDecompressor;
	private boolean myIsClosed;

	public ZipInputStream(ZipFile parent, LocalFileHeader header) throws IOException {
		myParent = parent;
		myBaseStream = parent.getBaseStream();
		myBaseStream.setPosition(header.DataOffset);
		myDecompressor = Decompressor.init(myBaseStream, header);
	}

	@Override
	public int available() throws IOException {
		return myDecompressor.available();
	}

	@Override
	public int read(byte b[], int off, int len) throws IOException {
		if (b == null) {
			throw new NullPointerException();
		} else if ((off < 0) || (off > b.length) || (len < 0) ||
				((off + len) > b.length) || ((off + len) < 0)) {
			throw new IndexOutOfBoundsException();
		} else if (len == 0) {
			return 0;
		}

		return myDecompressor.read(b, off, len);
	}

	@Override
	public int read() throws IOException {
		return myDecompressor.read();
	}

	public void close() throws IOException {
		if (!myIsClosed) {
			myIsClosed = true;
			myParent.storeBaseStream(myBaseStream);
			Decompressor.storeDecompressor(myDecompressor);
		}
	}

	protected void finalize() throws Throwable {
		try {
			close();
		} finally {
			super.finalize();
		}
	}
}
