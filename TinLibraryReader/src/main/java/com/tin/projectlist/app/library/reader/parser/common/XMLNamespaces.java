package com.tin.projectlist.app.library.reader.parser.common;

/**
 * 地址
 * @author fuchen
 * @date 2013-4-12
 */
@Deprecated
public interface XMLNamespaces {
	String DublinCore = "http://purl.org/dc/elements/1.1/";
	String DublinCoreLegacy = "http://purl.org/metadata/dublin_core";
	String XLink = "http://www.w3.org/1999/xlink";
	String OpenPackagingFormat = "http://www.idpf.org/2007/opf";

	String Atom = "http://www.w3.org/2005/Atom";
	String Opds = "http://opds-spec.org/2010/catalog";
	String DublinCoreTerms = "http://purl.org/dc/terms/";
	String DublinCoreSyndication = "http://purl.org/syndication/thread/1.0";
	String OpenSearch = "http://a9.com/-/spec/opensearch/1.1/";
	String CalibreMetadata = "http://calibre.kovidgoyal.net/2009/metadata";

	String FBReaderCatalogMetadata = "http://data.fbreader.org/catalog/metadata/";
}
