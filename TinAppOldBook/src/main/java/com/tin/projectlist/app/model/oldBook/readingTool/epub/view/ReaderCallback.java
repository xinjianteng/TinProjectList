package com.tin.projectlist.app.model.oldBook.readingTool.epub.view;

/**
 * @author yuyh.
 * @date 2016/12/15.
 */
public interface ReaderCallback {

    String getPageHref(int position);

    void toggleToolBarVisible();

    void hideToolBarIfVisible();


}
