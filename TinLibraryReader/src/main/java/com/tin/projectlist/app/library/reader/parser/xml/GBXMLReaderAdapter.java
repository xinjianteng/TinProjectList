package com.tin.projectlist.app.library.reader.parser.xml;


import com.tin.projectlist.app.library.reader.parser.common.CopyVersionInfo;
import com.tin.projectlist.app.library.reader.parser.file.GBFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * XML读取者适配器<br>
 * 通过继承此类实现ZL
 *
 * 创建者： 符晨<br>
 * 创建日期：2013-4-2<br>
 */
public abstract class GBXMLReaderAdapter implements GBXMLReader {
	private Map<String, String> myNamespaceMap = Collections.emptyMap();

	public boolean readQuietly(GBFile file) {
		try {
			read(file);
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	public boolean readQuietly(String string) {
		try {
			read(string);
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	public void read(GBFile file, CopyVersionInfo copyright) throws IOException {
		GBXMLProcessor.read(this, file, copyright);
	}

	public void read(GBFile file) throws IOException {
		GBXMLProcessor.read(this, file);
	}

	public void read(InputStream stream) throws IOException {
		GBXMLProcessor.read(this, stream, 65536);
	}

	public void read(String string) throws IOException {
		GBXMLProcessor.read(this, new StringReader(string), 65536);
	}

	public void read(Reader reader) throws IOException {
		GBXMLProcessor.read(this, reader, 65536);
	}

	public boolean dontCacheAttributeValues() {
		return false;
	}

	public boolean startElementHandler(String tag, GBStringMap attributes) {
		return false;
	}

	public boolean endElementHandler(String tag) {
		return false;
	}

	public void characterDataHandler(char[] ch, int start, int length) {
	}

	public void characterDataHandlerFinal(char[] ch, int start, int length) {
		characterDataHandler(ch, start, length);
	}

	public void startDocumentHandler() {
	}

	public void endDocumentHandler() {
	}

	public boolean processNamespaces() {
		return false;
	}

	public void namespaceMapChangedHandler(Map<String, String> namespaces) {
		myNamespaceMap = namespaces != null ? namespaces : Collections
				.<String, String> emptyMap();
	}

	public boolean testTag(String namespace, String name, String tag) {
		if (name.equals(tag) && namespace.equals(myNamespaceMap.get(""))) {
			return true;
		}
		final int nameLen = name.length();
		final int tagLen = tag.length();
		if (tagLen < nameLen + 2) {
			return false;
		}
		if (tag.endsWith(name) && tag.charAt(tagLen - nameLen - 1) == ':') {
			return namespace.equals(myNamespaceMap.get(tag.substring(0, tagLen
					- nameLen - 1)));
		}
		return false;
	}

	public String getAttributeValue(GBStringMap attributes, String namespace,
									String name) {
		if (namespace == null) {
			return attributes.getValue(name);
		}

		final int size = attributes.getSize();
		if (size == 0) {
			return null;
		}
		final String postfix = ":" + name;
		for (int i = size - 1; i >= 0; --i) {
			final String key = attributes.getKey(i);
			if (key.endsWith(postfix)) {
				final String nsKey = key.substring(0,
						key.length() - postfix.length());
				if (namespace.equals(myNamespaceMap.get(nsKey))) {
					return attributes.getValue(i);
				}
			}
		}
		return null;
	}

	interface Predicate {
		boolean accepts(String namespace);
	}

	protected String getAttributeValue(GBStringMap attributes,
									   Predicate predicate, String name) {
		final int size = attributes.getSize();
		if (size == 0) {
			return null;
		}
		final String postfix = ":" + name;
		for (int i = size - 1; i >= 0; --i) {
			final String key = attributes.getKey(i);
			if (key.endsWith(postfix)) {
				final String ns = myNamespaceMap.get(key.substring(0,
						key.length() - postfix.length()));
				if (ns != null && predicate.accepts(ns)) {
					return attributes.getValue(i);
				}
			}
		}
		return null;
	}

	public void collectExternalEntities(HashMap<String, char[]> entityMap) {
	}

	public List<String> externalDTDs() {
		return Collections.emptyList();
	}
}
