package com.tin.projectlist.app.library.reader.parser.support;

import java.util.List;

/**
 * 编码集
 * @author fuchen
 * @date 2013-4-12
 */
public abstract class EncodingCollection {
	/**
	 * 获取所有的编码列表
	 * @return
	 * @author fuchen
	 * @date 2013-4-12
	 */
	public abstract List<Encoding> encodings();

	/*
	 * alias 别名
	 */

	/**
	 * 根据别名获取编码实体
	 * @param alias
	 * @return
	 * @author fuchen
	 * @date 2013-4-12
	 */
	public abstract Encoding getEncoding(String alias);

	/**
	 * 根据code获取编码
	 * @param code
	 * @return
	 * @author fuchen
	 * @date 2013-4-12
	 */
	public abstract Encoding getEncoding(int code);
}
