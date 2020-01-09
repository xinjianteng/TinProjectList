package com.tin.projectlist.app.library.reader.controller;

import android.app.Activity;

import com.core.platform.GBLibrary;

/**
 * 类名： LightAction.java<br>
 * 描述： 亮度调节<br>
 * 创建者： jack<br>
 * 创建日期：2012-11-25<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public class LightAction extends ReadAction {

    private Activity mActivity;

    public LightAction(ReaderApplication application, Activity activity) {
        super(application);
        this.mActivity = activity;
    }
    /**
     * o[0] int  屏幕亮度值
     */
    @Override
    public void run(Object... o) {
        if (o.length > 0 && o[0] instanceof Integer)
            setScreenBrightness((Integer) o[0]);
    }

    /**
     * 功能描述：设置屏幕亮度 <br>
     * 创建者： jack<br>
     * 创建日期：2012-11-25<br>
     *
     * @param
     */
    private void setScreenBrightness(int progress) {
        GBLibrary.Instance().setScreenBrightness(progress);
//        if (progress >= 0 && progress <= 100) {
//            float light = (float) progress / 100;
//            light = light < 0.1f ? 0.1f : light;
//            light = light > 0.9f ? 0.9f : light;
//            L.i(">>>>>>>>>>当前亮度progress:"+progress+",light:"+light);
//            // 设置亮度
//            WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
//            lp.screenBrightness = light;
//            mActivity.getWindow().setAttributes(lp);
//            Reader.LightOption.setValue(light);
//
//        }
    }
}
