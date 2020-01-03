package com.tin.projectlist.app.library.reader.parser.html;

import java.io.InputStream;
import java.io.IOException;
/**
 * 类名： GBHtmlProcessor.java#ZLHtmlProcessor<br>
 * 描述： html解析工具类<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-9<br>
 * 版本：  <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public abstract class GBHtmlProcessor {
	/**
	 * 功能描述： html文件解析<br>
	 * 创建者： jack<br>
	 * 创建日期：2013-4-9<br>
	 * @param reader html解析器
	 * @param stream 文件流
	 * @return 是否解析成功
	 */
	public static boolean read(GBHtmlReader reader, InputStream stream) {
		try {
			GBHtmlParser parser = new GBHtmlParser(reader, stream);
			reader.startDocumentHandler();
			parser.doIt();
			reader.endDocumentHandler();
		} catch (IOException e) {
			return false;
		}
		return true;
	}
}
