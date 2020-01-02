package com.core.support;

import java.util.List;
import java.util.Collections;

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
