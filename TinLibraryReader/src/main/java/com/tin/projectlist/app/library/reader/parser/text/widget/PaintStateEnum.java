package com.core.text.widget;

interface PaintStateEnum {
	int NOTHING_TO_PAINT = 0;
	int READY = 1;
	/**
	 * 开始
	 * */
	int START_IS_KNOWN = 2;
	/**
	 * 结束
	 */
	int END_IS_KNOWN = 3;
	int TO_SCROLL_FORWARD = 4;
	int TO_SCROLL_BACKWARD = 5;
	/**
	 * 加载中
	 */
//	int WAITING =5; 
};
