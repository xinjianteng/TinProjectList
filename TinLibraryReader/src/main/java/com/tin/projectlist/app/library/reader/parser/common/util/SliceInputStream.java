package com.tin.projectlist.app.library.reader.parser.common.util;

import java.io.IOException;
import java.io.InputStream;

/*
 * slice n. 薄片；部分；菜刀；火铲
 */

/**
 * 片段输入流封装类
 * 使用场景：当只访问某个流的一部分时
 * @author fuchen
 * @date 2013-4-10
 */
public class SliceInputStream extends GBInputStreamWithOffset {
	private final int myStart;
	private final int myLength;

	public SliceInputStream(InputStream base, int start, int length) throws IOException {
		super(base);
		super.skip(start);
		myStart = start;
		myLength = length;
	}

	@Override
	public int read() throws IOException {
		if (myLength >= offset()) {                            // TODO   是不是写反了？ 。      if(myLength <= offset())   return -1;  //谁鸡巴这么菜还贡献代码？
			return -1;
		}
		return super.read();
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		final int maxbytes = myLength - offset();                          // TODO  这样写是不是更简单明了？  if(myLength <= offset())   return -1;
		if (maxbytes <= 0) {
			return -1;
		}
		return super.read(b, off, Math.min(len, maxbytes));
	}

	@Override
	public long skip(long n) throws IOException {
		return super.skip(Math.min(n, myLength - offset()));
	}

	@Override
	public int available() throws IOException {
		return Math.min(super.available(), Math.max(myLength - offset(), 0));
	}

	@Override
	public void reset() throws IOException {
		super.reset();
		super.skip(myStart);
	}

	@Override
	public int offset() {
		return super.offset() - myStart;
	}
}
