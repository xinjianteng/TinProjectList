package com.tin.projectlist.app.library.reader.model.parser.css;


import com.tin.projectlist.app.library.reader.parser.text.model.style.GBTextStyleEntry;

/**
 *
 * 类名： .java<br>
 * 描述：处理属性 将属性转换为对应entry 创建者： yangn<br>
 * 创建日期：2013-6-8<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public abstract class AttrAction {

	/**
	 *
	 * 功能描述：创建属性对应entry 创建者： yangn<br>
	 * 创建日期：2013-5-17<br>
	 *
	 * @param attrName
	 *            属性名称
	 * @param attrVal
	 *            属性值
	 * @return 返回对应entry
	 */
	protected abstract GBTextStyleEntry create(String attrName, String attrVal);

	/**
	 *
	 * 功能描述：将属性赋值entry 创建者： yangn<br>
	 * 创建日期：2013-6-8<br>
	 *
	 * @param
	 */
	protected abstract void doIt(String attrName, String attrVal,
                                 GBTextStyleEntry entry);

	/**
	 *
	 * 功能描述：获取属性对应entry类型 创建者： yangn<br>
	 * 创建日期：2013-6-8<br>
	 *
	 */
	protected abstract Class<?> getEntryType();

	/**
	 * 是否经常创建新对象(用于样式属性与父节点总是不冲突情况)
	 *
	 * @return
	 */

	protected boolean isAlwaysNew() {
		return this.isAlwaysNew;
	}

	protected void setAlwaysNew(boolean isAlwaysNew) {
		this.isAlwaysNew = isAlwaysNew;

	}

	boolean isAlwaysNew = false;
}
