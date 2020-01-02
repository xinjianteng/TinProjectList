
package com.core.html;
/**
 * 类名： GBHtmlReader.java#ZLHtmlReader<br>
 * 描述： html文件阅读接口<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-9<br>
 * 版本：  <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public interface GBHtmlReader {
	/**
	 * 功能描述： 解析文件开始<br>
	 * 创建者： jack<br>
	 * 创建日期：2013-4-9<br>
	 */
	public void startDocumentHandler();
	/**
	 * 功能描述： 解析文件结束<br>
	 * 创建者： jack<br>
	 * 创建日期：2013-4-9<br>
	 */
	public void endDocumentHandler();
	/**
	 * 功能描述： 解析完一个开始标签<br>
	 * 创建者： jack<br>
	 * 创建日期：2013-4-9<br>
	 * @param tag 标签名称
	 * @param offset 标签名下标
	 * @param attributes 标签包含属性
	 */
	public void startElementHandler(String tag, int offset, GBHtmlAttrMap attributes);
	/**
	 * 功能描述： 解析完一个结束标签<br>
	 * 创建者： jack<br>
	 * 创建日期：2013-4-9<br>
	 * @param tag 标签名称
	 */
	public void endElementHandler(String tag);
	/**
	 * 功能描述： 解析到一段文字<br>
	 * 创建者： jack<br>
	 * 创建日期：2013-4-9<br>
	 * @param ch 文字字节数组
	 * @param start 段落开始位置
	 * @param length 段落长度
	 */
	public void byteDataHandler(byte[] ch, int start, int length);
	/**
	 * 功能描述： 转义标签解析<br>
	 * 创建者： jack<br>
	 * 创建日期：2013-4-9<br>
	 * @param entity 转义码
	 */
	public void entityDataHandler(String entity);
}
