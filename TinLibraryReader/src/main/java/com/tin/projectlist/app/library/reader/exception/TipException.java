package com.tin.projectlist.app.library.reader.exception;

import android.app.Activity;
import android.os.Looper;

import com.hjq.toast.ToastUtils;
import com.tin.projectlist.app.library.base.utils.LogUtils;


/**
 *
 * 类名： .java<br>
 * 描述：阅读器错误信息提示和中断 <br>
 * 创建者： yangn<br>
 * 创建日期：2014-1-13<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public class TipException extends Exception {

	final String TAG="TipException";
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 提示消息
	 */
	public final String Tip;

	/**
	 *
	 * @param tip
	 *            异常被抛到阅读器界面，提示消息
	 */
	public TipException(String tip) {
		Tip = tip;
		IsInterrupt = false;
	}

	/**
	 *
	 * @param tip
	 *            异常被抛到阅读器界面，提示消息
	 * @param isInterrup
	 *            标记是否中断阅读器 是中断 否不中断
	 *
	 */
	public TipException(String tip, boolean isInterrup) {
		Tip = tip;
		IsInterrupt = isInterrup;
	}

	/**
	 * 标识是否中段退出阅读器
	 */
	public final boolean IsInterrupt;

	/**
	 *
	 * 功能描述：吐司提示异常消息<br>
	 * 创建者： yangn<br>
	 * 创建日期：2014-1-13<br>
	 *
	 * @param
	 */
	public void toast(Activity context) {
		if (null == Tip || null == context||Looper.myLooper()!=Looper.getMainLooper()){
			LogUtils.e(TAG,Tip);
		}else{
			ToastUtils.show(Tip);
		}
		if (IsInterrupt)
			context.finish();
	}

}
