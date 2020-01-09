package com.tin.projectlist.app.library.reader.controller;

public interface ActionCode {
    // String SHOW_LIBRARY = "library";
    // String SHOW_PREFERENCES = "preferences";
    // String SHOW_BOOK_INFO = "bookInfo";
    // String SHOW_TOC = "toc";
    // String SHOW_BOOKMARKS = "bookmarks";
    // String SHOW_NETWORK_LIBRARY = "networkLibrary";

    String SWITCH_TO_NIGHT_PROFILE = "night";
    String SWITCH_TO_DAY_PROFILE = "day";

    String SHARE_BOOK = "shareBook";

    String SEARCH = "search";
    String FIND_PREVIOUS = "findPrevious";
    String FIND_NEXT = "findNext";
    String CLEAR_FIND_RESULTS = "clearFindResults";

    String SET_TEXT_VIEW_MODE_VISIT_HYPERLINKS = "hyperlinksOnlyMode";
    String SET_TEXT_VIEW_MODE_VISIT_ALL_WORDS = "dictionaryMode";

    String TURN_PAGE_BACK = "previousPage";
    String TURN_PAGE_FORWARD = "nextPage";

    String MOVE_CURSOR_UP = "moveCursorUp";
    String MOVE_CURSOR_DOWN = "moveCursorDown";
    String MOVE_CURSOR_LEFT = "moveCursorLeft";
    String MOVE_CURSOR_RIGHT = "moveCursorRight";

    String VOLUME_KEY_SCROLL_FORWARD = "volumeKeyScrollForward";
    String VOLUME_KEY_SCROLL_BACK = "volumeKeyScrollBackward";
    String SHOW_MENU = "menu";
    String SHOW_NAVIGATION = "navigate";

    String GO_BACK = "goBack";
    String EXIT = "exit";
    String SHOW_CANCEL_MENU = "cancelMenu";

    String SET_SCREEN_ORIENTATION_SYSTEM = "screenOrientationSystem";
    String SET_SCREEN_ORIENTATION_SENSOR = "screenOrientationSensor";
    String SET_SCREEN_ORIENTATION_PORTRAIT = "screenOrientationPortrait";
    String SET_SCREEN_ORIENTATION_LANDSCAPE = "screenOrientationLandscape";
    String SET_SCREEN_ORIENTATION_REVERSE_PORTRAIT = "screenOrientationReversePortrait";
    String SET_SCREEN_ORIENTATION_REVERSE_LANDSCAPE = "screenOrientationReverseLandscape";

    String INCREASE_FONT = "increaseFont";
    String DECREASE_FONT = "decreaseFont";

    String PROCESS_HYPERLINK = "processHyperlink";

    String SELECTION_SHOW_PANEL = "selectionShowPanel";
    String SELECTION_HIDE_PANEL = "selectionHidePanel";
    String SELECTION_CLEAR = "selectionClear";
    // 选中新增业务
    String SELECTION_FILE = "selectionOptionfile";//归档
    String SELECTION_COPY_TO_CLIPBOARD = "selectionCopyToClipboard";//复制
    String SELECTION_SHARE = "selectionShare";//分享
    String SELECTION_DICT = "selectionDict";//字典
    String SELECTION_NOTE_ANNOTATION = "selectionNoteAnnotation"; // 笔记
    String SELECTION_COMMENT_ANNOTATION = "selectioncommentAnnotation"; // 批注
    String DELETECOMMENTANNOTATION = "deletecommentAnnotation"; // 删除批注
    String UPDATACOMMENTANNOTATION = "updatacommentAnnotation"; // 更新批注

    String SELECTION_TRANSLATE = "selectionTranslate";
    String SELECTION_BOOKMARK = "selectionBookmark";
    String SELECTION_OPTIONSHOW_PANEL = "selectionOptionShowPanel";
    String SHOW_MEDIA_VIEW = "showMediaView";
    // 显示试读框
    String SHOW_READ_OUTOFRANGE = "showReadOutofRange";
    /** 批注内容点击 */
    String SELECTION_ANNOTATION_NOTE = "selectionAnnotationNote";
    String SELECTION_HIGHLIGHT = "selectionHighlight"; // 高亮
    String SELECTION_ERROR = "selectionError"; // 纠错
    // 亮度调节
    String SCREEN_LIGHT = "screenligth";
    // 页面跳转
    String GOTO_PAGE = "gotopage";
    // 刷新页码信息
    String RESET_PAGEINFO = "resetpageinfo";
    String REFRESH_TIMEBATTERY = "refreashtimebattery";
    String SCREEN_SLEEP = "screensleep";
    // 阅读设置动作定义
    String SET_BACK = "readsetback";

    //分享
    String SHARE_SINA_WEIBO = "shareSinaWeibo";
//    String SHARE_RENN = "shareRenren";
//    String SHARE_QQ_SPACE="shareQQSpace";
//    String SHARE_TENCENT_WEIBO="shareTencentWeibo";
    String UPLOAD_NOTES="upload_notes";

}
