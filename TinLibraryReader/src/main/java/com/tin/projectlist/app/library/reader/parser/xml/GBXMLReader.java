package com.core.xml;

import java.util.*;

/**
 * XML读取者<br>
 *
 * 创建者： 符晨<br>
 * 创建日期：2013-4-2<br>
 */
public interface GBXMLReader {
	public boolean dontCacheAttributeValues();

	public void startDocumentHandler();
	public void endDocumentHandler();

	// returns true iff xml processing should be interrupted
	public boolean startElementHandler(String tag, GBStringMap attributes);
	public boolean endElementHandler(String tag);
	public void characterDataHandler(char[] ch, int start, int length);
	public void characterDataHandlerFinal(char[] ch, int start, int length);

	boolean processNamespaces();
	void namespaceMapChangedHandler(Map<String,String> namespaces);

	void collectExternalEntities(HashMap<String,char[]> entityMap);
	List<String> externalDTDs();
}
