package com.tin.projectlist.app.library.reader.model.parser.css;

import com.core.text.model.style.GBTextBoxStyleEntry;
import com.core.text.model.style.GBTextStyleEntry;

/**
 *
 * 类名： .java<br>
 * 描述：边距样式处理 创建者： yangn<br>
 * 创建日期：2013-6-8<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public class AttrBoxAction extends AttrAction {

	public static String MAGRIN = "margin", MAGRIN_TOP = "margin-top",
			MAGRIN_RIGTH = "margin-right", MAGRIN_BOTTOM = "margin-bottom",
			MAGRIN_LEFT = "margin-left", PADDING = "padding",
			PADDING_TOP = "padding-top", PADDING_RIGHT = "padding-right",
			PADDING_BOTTOM = "padding-bottom", PADDING_LEFT = "padding-left";

	private GBTextBoxStyleEntry boxStyle = null;

	@Override
	protected void doIt(String attrName, String attrVal, GBTextStyleEntry entry) {
		if (null == attrVal || "".equals(attrVal.trim())) {
			return;
		}

		this.boxStyle = (GBTextBoxStyleEntry) entry;

		if (attrName.trim().equalsIgnoreCase(MAGRIN)) {
			this.boxStyle.setMarginStr(attrVal);
		} else if (attrName.trim().equalsIgnoreCase(MAGRIN_TOP)) {
			this.boxStyle.setMarginTop(attrVal);
		} else if (attrName.trim().equalsIgnoreCase(MAGRIN_RIGTH)) {
			this.boxStyle.setMarginRight(attrVal);
		} else if (attrName.trim().equalsIgnoreCase(MAGRIN_BOTTOM)) {
			this.boxStyle.setMarginBottom(attrVal);
		} else if (attrName.trim().equalsIgnoreCase(MAGRIN_LEFT)) {
			this.boxStyle.setMarginLeft(attrVal);
		} else if (attrName.trim().equalsIgnoreCase(PADDING)) {
			this.boxStyle.setPaddingStr(attrVal);
		} else if (attrName.trim().equalsIgnoreCase(PADDING_TOP)) {
			this.boxStyle.setPaddingTop(attrVal);
		} else if (attrName.trim().equalsIgnoreCase(PADDING_RIGHT)) {
			this.boxStyle.setPaddingRight(attrVal);
		} else if (attrName.trim().equalsIgnoreCase(PADDING_BOTTOM)) {
			this.boxStyle.setPaddingBottom(attrVal);
		} else if (attrName.trim().equalsIgnoreCase(PADDING_LEFT)) {
			this.boxStyle.setPaddingLeft(attrVal);
		}

	}

	@Override
	protected GBTextStyleEntry create(String attrName, String attrVal) {
		boxStyle = new GBTextBoxStyleEntry();
		this.doIt(attrName, attrVal, boxStyle);
		this.isAlwaysNew=true;
		return this.boxStyle;
	}

	@Override
	protected Class<?> getEntryType() {

		return GBTextBoxStyleEntry.class;
	}





}
