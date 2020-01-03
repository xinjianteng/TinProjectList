package com.tin.projectlist.app.library.reader.parser.domain;

import com.core.object.GBBoolean3;

/**
 * 类名： GBAction.java#ZLApplication.ZLAction<br>
 * 描述： 业务动作抽象类<br>
 * 创建者： jack<br>
 * 创建日期：2012-11-13<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public abstract class GBAction {
    // 当前业务是否可用
    private boolean mEnable = true;
    /**
     * 功能描述： 设置业务状态<br>
     * 创建者： jack<br>
     * 创建日期：2012-11-13<br>
     *
     * @param isEnable 是否可用
     */
    public void setEnable(boolean isEnable) {
        mEnable = isEnable;
    }
    /**
     * 功能描述： 选项是否可见<br>
     * 创建者： jack<br>
     * 创建日期：2013-4-1<br>
     *
     * @return
     */
    public boolean isVisible() {
        return true;
    }
    /**
     * 功能描述： 选项是否选中<br>
     * 创建者： jack<br>
     * 创建日期：2013-4-1<br>
     *
     * @return
     */
    public GBBoolean3 isChecked() {
        return GBBoolean3.B3_UNDEFINED;
    }

    // public ZLBoolean3 isChecked() {
    // return ZLBoolean3.B3_UNDEFINED;
    // }
    /**
     * 功能描述： 当前业务是否可用<br>
     * 创建者： jack<br>
     * 创建日期：2012-11-13<br>
     *
     * @param
     */
    public boolean isEnabled() {
        return mEnable;
    }
    /**
     * 功能描述： 检查执行<br>
     * 创建者： jack<br>
     * 创建日期：2012-11-13<br>
     *
     * @param
     */
    public final boolean checkAndRun(Object... o) {
        if (isEnabled()) {
            run(o);
            return true;
        }
        return false;
    }
    /**
     * 操作执行业务
     */
    public abstract void run(Object... o);
}
