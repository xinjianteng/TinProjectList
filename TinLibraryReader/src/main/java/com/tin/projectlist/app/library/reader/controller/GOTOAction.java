package com.tin.projectlist.app.library.reader.controller;

import com.core.text.widget.GBTextView;

/**
 * 类名： GOTOAction.java<br>
 * 描述： 跳转页面<br>
 * 创建者： jack<br>
 * 创建日期：2013-5-3<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public class GOTOAction extends ReadAction {

    public GOTOAction(ReaderApplication fbreader) {
        super(fbreader);
    }

    @Override
    public void run(Object... arg0) {
        if (arg0 != null && arg0.length > 0) {
            int pro = (Integer) arg0[0];
            if (pro <= 0)
                gotoPage(1);
            else if (pro >= 100) {
                gotoPage(Reader.isReadPdf ? Reader.getmPdfReaderView().getPageCount() : Reader.BookTextView
                        .getTotalPage());
            } else {
                gotoPage((Reader.isReadPdf ? Reader.getmPdfReaderView().getPageCount() : Reader.BookTextView
                        .getTotalPage()) * pro / 100);
            }
        }
    }

    private void gotoPage(int page) {
        if (Reader.isReadPdf)
            Reader.getmPdfReaderView().setDisplayedViewIndex(page);
        else {
            final GBTextView view = Reader.getTextView();
            if (page == 1) {
                view.gotoHome();
            } else {
                view.gotoPage(page);
            }
            Reader.getViewImp().reset();
            Reader.getViewImp().repaint();
        }
//        GBApplication.Instance().runAction(ActionCode.RESET_PAGEINFO);
    }
}
