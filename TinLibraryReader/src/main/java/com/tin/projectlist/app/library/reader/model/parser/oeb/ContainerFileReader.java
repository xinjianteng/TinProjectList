package com.tin.projectlist.app.library.reader.model.parser.oeb;

import com.core.xml.GBStringMap;
import com.core.xml.GBXMLReaderAdapter;

/**
 * 类名： ContainerFileReader.java<br>
 * 描述： 解析获取epub文件中opf文件存放地址<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-26<br>
 * 版本：  <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
class ContainerFileReader extends GBXMLReaderAdapter {
	private String myRootPath;

	public String getRootPath() {
		return myRootPath;
	}

	@Override
	public boolean startElementHandler(String tag, GBStringMap xmlattributes) {
		if ("rootfile".equalsIgnoreCase(tag)) {
			myRootPath = xmlattributes.getValue("full-path");
			if (myRootPath != null) {
				return true;
			}
		}
		return false;
	}
}
