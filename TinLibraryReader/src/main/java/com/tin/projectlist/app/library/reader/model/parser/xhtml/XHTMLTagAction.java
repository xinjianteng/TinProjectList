package com.tin.projectlist.app.library.reader.model.parser.xhtml;


import com.tin.projectlist.app.library.reader.parser.xml.GBStringMap;

/**
 * 类名： XHTMLTagAction.java<br>
 * 描述： xhtml标签处理类抽象定义<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-25<br>
 * 版本：  <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public abstract class XHTMLTagAction {
	/**
	 * 功能描述：解析到标签开始位置的处理方法<br>
	 * 创建者： jack<br>
	 * 创建日期：2013-4-25<br>
	 * @param reader 当前解析器
	 * @param xmlattributes 该标签的属性
	 */
	protected abstract void doAtStart(XHTMLReader reader, GBStringMap xmlattributes);
	/**
	 * 功能描述： 解析到标签结束位置时的处理方法<br>
	 * 创建者： jack<br>
	 * 创建日期：2013-4-25<br>
	 * @param reader 当前解析器
	 */
	protected abstract void doAtEnd(XHTMLReader reader);
};
