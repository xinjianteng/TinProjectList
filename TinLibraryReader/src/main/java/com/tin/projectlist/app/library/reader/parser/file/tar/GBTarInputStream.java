package com.tin.projectlist.app.library.reader.parser.file.tar;

import java.io.InputStream;
import java.io.IOException;

class GBTarInputStream extends InputStream {
	private final InputStream myBase;
	//private final String myFileName;

	GBTarInputStream(InputStream base, String fileName) throws IOException {
		myBase = base;
		//myFileName = fileName;

		GBTarHeader header = new GBTarHeader();
		while (header.read(myBase)) {
			if ((header.IsRegularFile) && fileName.equals(header.Name)) {
				return;
			}
			final int sizeToSkip = (header.Size + 0x1ff) & -0x200;
			if (sizeToSkip < 0) {
				throw new IOException("Bad tar archive");
			}
			if (myBase.skip(sizeToSkip) != sizeToSkip) {
				break;
			}
			header.erase();
		}
		throw new IOException("Item " + fileName + " not found in tar archive");
	}

	public int read() throws IOException {
		return myBase.read();
	}

	@Override
	public int read(byte b[]) throws IOException {
		return myBase.read(b);
	}

	@Override
	public int read(byte b[], int off, int len) throws IOException {
		return myBase.read(b, off, len);
	}

	@Override
	public long skip(long n) throws IOException {
		return myBase.skip(n);
	}

	@Override
	public int available() throws IOException {
		return myBase.available();
	}
}
