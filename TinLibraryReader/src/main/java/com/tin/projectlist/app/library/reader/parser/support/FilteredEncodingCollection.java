package com.core.support;

import com.core.file.GBResourceFile;
import com.core.xml.GBStringMap;
import com.core.xml.GBXMLReaderAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 筛选的编码集合
 * @author jack
 * @date 2013-4-12
 */
abstract class FilteredEncodingCollection extends EncodingCollection {
	private final List<Encoding> myEncodings = new ArrayList<Encoding>();//编码集
	private final Map<String,Encoding> myEncodingByAlias = new HashMap<String,Encoding>();//使用alias（别名）为key的，Encoding实体为value的map


	/**
	 * 构造方法， 解析Encodings.xml
	 */
	FilteredEncodingCollection() {
		new EncodingCollectionReader().readQuietly(
				GBResourceFile.createResourceFile("encodings/Encodings.xml")
		);
	}

	/**
	 * 是否支持该编码格式。
	 * @param name
	 * @return
	 * @author fuchen
	 * @date 2013-4-12
	 */
	public abstract boolean isEncodingSupported(String name);

	@Override
	public List<Encoding> encodings() {
		return Collections.unmodifiableList(myEncodings);
	}

	@Override
	public Encoding getEncoding(String alias) {
		Encoding e = myEncodingByAlias.get(alias);
		if (e == null && isEncodingSupported(alias)) {
			e = new Encoding(null, alias, alias);
			myEncodingByAlias.put(alias, e);
			myEncodings.add(e);
		}
		return e;
	}

	@Override
	public Encoding getEncoding(int code) {
		return getEncoding(String.valueOf(code));
	}

	public boolean providesConverterFor(String alias) {
		return myEncodingByAlias.containsKey(alias) || isEncodingSupported(alias);
	}


	/**
	 * 解析Encodingx.xml
	 * @author fuchen
	 * @date 2013-4-12
	 */
	private class EncodingCollectionReader extends GBXMLReaderAdapter {
		private String myCurrentFamilyName;
		private Encoding myCurrentEncoding;

		public boolean dontCacheAttributeValues() {
			return true;
		}

		public boolean startElementHandler(String tag, GBStringMap attributes) {
			if ("group".equals(tag)) {
				myCurrentFamilyName = attributes.getValue("name");
			} else if ("encoding".equals(tag)) {
				final String name = attributes.getValue("name").toLowerCase();
				final String region = attributes.getValue("region");
				if (isEncodingSupported(name)) {
					myCurrentEncoding = new Encoding(
							myCurrentFamilyName, name, name + " (" + region + ")"
					);
					myEncodings.add(myCurrentEncoding);
					myEncodingByAlias.put(name, myCurrentEncoding);
				} else {
					myCurrentEncoding = null;
				}
			} else if ("code".equals(tag)) {
				if (myCurrentEncoding != null) {
					myEncodingByAlias.put(attributes.getValue("number"), myCurrentEncoding);
				}
			} else if ("alias".equals(tag)) {
				if (myCurrentEncoding != null) {
					myEncodingByAlias.put(attributes.getValue("name").toLowerCase(), myCurrentEncoding);
				}
			}
			return false;
		}
	}
}
