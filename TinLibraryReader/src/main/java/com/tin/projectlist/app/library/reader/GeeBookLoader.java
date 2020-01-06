package com.tin.projectlist.app.library.reader;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.tin.projectlist.app.library.reader.parser.common.util.BookDescriptor;
import com.tin.projectlist.app.library.reader.controller.GeeBookMgr;
import com.tin.projectlist.app.library.reader.pdf.PdfActivity;
import com.tin.projectlist.app.library.reader.view.ReaderActivity;

/**
 *
 * 类名： .java<br>
 * 描述：图书加载管理类，<br>
 * 1.提供epub、txt、pdf、geb后缀的图书文件加载，geb后缀文件为简帛加密后的epub、txt、pdf文件， 打开此类文件需提供对应密钥<br>
 * 2.提供设置图书加载前、图书加载时和图书加载完成后的相关监听函数。<br>
 * 创建者： yangn<br>
 * 创建日期：2014-1-11<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public class GeeBookLoader {
    /**
     * 不可用
     */
    private GeeBookLoader() {
    }

    /**
     *
     * 功能描述：执行加载未加密txt格式图书 或已加密以geb为后缀的txt图书 <br>
     * 创建者： yangn<br>
     * 创建日期：2014-1-11<br>
     *
     * @param context 当前上下文
     * @param descriptor 图书描述
     * @return 加载成功返回 true 否则返回false
     * @throws Exception
     */
    public static Boolean execTxt(Context context, BookDescriptor descriptor) throws Exception {
        if (isInValidate(context, descriptor)) {
            return false;
        }
        descriptor.RealSuffer = BookDescriptor.Suffix.TXT;
        return exec(context, descriptor);
    }

    /**
     *
     * 功能描述：执行加载未加密pdf格式图书 或已加密以geb为后缀的pdf图书 <br>
     * 创建者： yangn<br>
     * 创建日期：2014-1-11<br>
     *
     * @param context
     * @param descriptor 图书文件描述
     * @return 加载成功返回true 否则返回false
     * @throws Exception
     */
    public static Boolean execPdf(Context context, BookDescriptor descriptor) throws Exception {
        if (isInValidate(context, descriptor)) {
            return false;
        }
        descriptor.RealSuffer = BookDescriptor.Suffix.PDF;
        return exec(context, descriptor);
    }

    /**
     *
     功能描述：执行加载未加密epub格式图书 或已加密以geb为后缀的epub图书 <br>
     * 创建者： yangn<br>
     * 创建日期：2014-1-11<br>
     *
     * @param context
     * @param descriptor 图书文件描述
     * @return 加载成功返回true 否则返回false
     * @throws Exception
     */
    public static Boolean execEpub(Context context, BookDescriptor descriptor) throws Exception {
        if (isInValidate(context, descriptor)) {
            return false;
        }
        descriptor.RealSuffer = BookDescriptor.Suffix.EPUB;
        return exec(context, descriptor);
    }

    static boolean isInValidate(Context context, BookDescriptor descriptor) throws Exception {
        if (null == context || null == descriptor) {
            throw new Exception("in validate book descriptor  or  cache path");
        } else {
            return false;
        }
    }

    /**
     *
     功能描述： 加载已加密图书 <br>
     * 创建者： yangn<br>
     * 创建日期：2014-1-11<br>
     *
     * @param context 当前上下文
     * @param descriptor 图书描述
     * @return 加载成功返回 true 否则返回false
     */
    private static Boolean exec(Context context, BookDescriptor descriptor) {
        if (null == descriptor) {
            return false;
        }
        Intent readerIntent = new Intent();
        readerIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle param = new Bundle();
        param.putSerializable(INTENT_PARAM, descriptor);
        if (descriptor.RealSuffer == BookDescriptor.Suffix.PDF) {
            readerIntent.setClass(context, PdfActivity.class);
        } else {
            readerIntent.setClass(context, ReaderActivity.class);
        }
        readerIntent.putExtras(param);
        readerIntent.setAction(Intent.ACTION_VIEW);
        context.startActivity(readerIntent);
        return true;
    }

    /**
     *
     * 功能描述：设置获取数据库操作对象监听 <br>
     * 创建者： yangn<br>
     * 创建日期：2014-1-11<br>
     *
     * @param function 监听函数 返回操作数据库对象
     */
    public static void setOnBookMgr(IGiveback<GeeBookMgr> function) {
        mFunction = function;
        isOnBookMgr = true;
    }

    static boolean isOnBookMgr = false;
    static OnExceptionListener mOnExceptionListener = null;

    /**
     *
     * 功能描述： 设置加载图书异常监听函数，图书加载发生异常时会触发该函数<br>
     * 创建者： yangn<br>
     * 创建日期：2014-1-11<br>
     *
     * @param
     */
    public static void setOnExceptionListener(OnExceptionListener onExceptionListener) {
        mOnExceptionListener = onExceptionListener;
    }

    /**
     *
     * 功能描述： 获取图书加载异常监听<br>
     * 创建者： yangn<br>
     * 创建日期：2014-1-11<br>
     *
     * @param
     */
    public static OnExceptionListener getOnExceptionListener() {
        if (null == mOnExceptionListener)
            return null;
        return mOnExceptionListener;
    }

    static IGiveback<GeeBookMgr> mFunction = null;



    private static final String INTENT_PARAM = "bookDescriptor";

    /**
     *
     * 功能描述： 图书加载器初始化，此函数总是放在应用程序启动时调用，且只调用一次<br>
     * 创建者： yangn<br>
     * 创建日期：2014-1-11<br>
     *
     * @param
     */
    public static void initApp(Application app) {
        new GBAndroidImageManager();
        new GBAndroidLibrary(app);
    }


    /**
     *
     * 类名： .java<br>
     * TAG 描述：图书加载异常监听 <br>
     * 创建者： yangn<br>
     * 创建日期：2014-1-11<br>
     * 版本： <br>
     * 修改者： <br>
     * 修改日期：<br>
     */
    public static interface OnExceptionListener {
        /**
         *
         * 功能描述： 捕捉异常函数 该函数在子线程被调用<br>
         * 创建者： yangn<br>
         * 创建日期：2014-1-11<br>
         *

         * @param ex 异常信息
         */
        void onCatchException(Exception ex);
    }

}
