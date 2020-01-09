package com.tin.projectlist.app.library.reader.controller;

import com.core.common.GBResource;
import com.geeboo.read.view.GBAndroidLibrary;
import com.geeboo.utils.UIUtil;

/**
 * 类名： SwitchProfileAction.java<br>
 * 描述： 切换阅读模式（白天/黑夜）<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-26<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
class SwitchProfileAction extends ReadAction {
    // 当前阅读模式
    private String myProfileName;

    SwitchProfileAction(ReaderApplication fbreader, String profileName) {
        super(fbreader);
        myProfileName = profileName;
    }

    @Override
    public boolean isVisible() {
        return !myProfileName.equals(Reader.getColorProfileName());
    }

    @Override
    public void run(Object... params) {
        if (Reader.isReadPdf) {
            UIUtil.showMessageText(((GBAndroidLibrary) GBAndroidLibrary.Instance()).getActivity(), GBResource.resource("readerPage").getResource("pdfNotSwitch").getValue());
        } else {
            Reader.setColorProfileName(myProfileName, Reader.mDayModel);
            Reader.getViewImp().reset();
            Reader.getViewImp().repaint();
        }
    }
}
