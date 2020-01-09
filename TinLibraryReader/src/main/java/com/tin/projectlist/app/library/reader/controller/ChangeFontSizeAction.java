package com.tin.projectlist.app.library.reader.controller;

import com.tin.projectlist.app.library.reader.parser.option.GBIntegerRangeOption;
import com.tin.projectlist.app.library.reader.parser.text.style.GBTextStyleCollection;

/**
 * 类名： ChangeFontSizeAction.java<br>
 * 描述： 改变字体大小的业务处理类<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-26<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
class ChangeFontSizeAction extends ReadAction {
    private final int myDelta;
    /**
     * 构造方法
     *
     * @param fbreader 阅读上下文
     * @param delta 字体设置步长
     */
    ChangeFontSizeAction(ReaderApplication fbreader, int delta) {
        super(fbreader);
        myDelta = delta;
    }

    @Override
    public void run(Object... params) {
        final GBIntegerRangeOption option = GBTextStyleCollection.Instance().getBaseStyle().FontSizeOption;
        if (params != null && params.length > 0) {
            int pro = (Integer) params[0];
            option.setValue(pro);
//            option.setValue(option.MinValue + ((option.MaxValue - option.MinValue) * pro / 100));
        } else {
//        	option.setValue(option.getValue() + myDelta);
            option.setValue(15);
        }
        Reader.clearTextCaches();
        Reader.getViewImp().repaint();
        Reader.runAction(ActionCode.RESET_PAGEINFO);
    }
}
