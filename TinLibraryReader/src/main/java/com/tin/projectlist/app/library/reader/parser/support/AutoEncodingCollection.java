package com.tin.projectlist.app.library.reader.parser.support;

import java.util.Collections;
import java.util.List;

/**
 * 自动编码集
 * @author fuchen
 * @date 2013-4-12
 */
public final class AutoEncodingCollection extends EncodingCollection {
	private final Encoding myEncoding = new Encoding(null, "auto", "auto");

	public List<Encoding> encodings() {
		return Collections.singletonList(myEncoding);
	}

	public Encoding getEncoding(String alias) {
		return myEncoding;
	}

	public Encoding getEncoding(int code) {
		return myEncoding;
	}
}
