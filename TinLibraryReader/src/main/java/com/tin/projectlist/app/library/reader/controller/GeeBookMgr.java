package com.tin.projectlist.app.library.reader.controller;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.tin.projectlist.app.library.reader.model.book.Bookmark;
import com.tin.projectlist.app.library.reader.parser.text.widget.GBTextPosition;
import com.tin.projectlist.app.library.reader.exception.TipException;

import org.json.JSONException;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * 类名： .java<br>
 * 描述： 图书信息管理<br>
 * 创建者： yangn<br>
 * 创建日期：2014-1-11<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public abstract class GeeBookMgr {

    /**
     *
     * 功能描述： 获取图书最后阅读位置，总是在打开阅读图书时该函数被触发<br>
     * 创建者： yangn<br>
     * 创建日期：2014-1-11<br>
     *
     * @throws TipException 函数异常提示消息，总是在该函数发生异常时，获取Tip并在阅读器界面吐司提示
     */
    public abstract GBTextPosition getLastReadPostion() throws TipException;

    /**
     *
     * 功能描述： 保存图书阅读位置，总是在退出阅读图书时该函数被触发<br>
     * 创建者： yangn<br>
     * 创建日期：2014-1-11<br>
     *
     * @param lastPosition 图书最后阅读位置
     * @param process 阅读进度百分比
     * @param closeBook 是否关闭图书
     * @throws TipException 函数异常提示消息，总是在该函数发生异常时，获取Tip并在阅读器界面吐司提示
     */
    public abstract void storedPosition(GBTextPosition lastPosition, double process,boolean closeBook) throws TipException;

    /**
     *
     * 功能描述：添加图书标签 <br>
     * 创建者： yangn<br>
     * 创建日期：2014-1-11<br>
     *
     * @param bookMark 需要保存的图书标签
     * @throws TipException 函数异常提示消息，总是在该函数发生异常时，获取Tip并在阅读器界面吐司提示
     */
    public abstract void saveBookmark(Bookmark bookMark) throws TipException;

    /**
     *
     * 功能描述： 删除图书标签<br>
     * 创建者： yangn<br>
     * 创建日期：2014-1-11<br>
     *
     * @param bookmark 被删除图书标签
     * @throws TipException 函数异常提示消息，总是在该函数发生异常时，获取Tip并在阅读器界面吐司提示
     */
    public abstract void deleteBookmark(Bookmark bookmark) throws TipException;

    /**
     *
     * 功能描述： 获取可显示图书标签<br>
     * 创建者： yangn<br>
     * 创建日期：2014-1-11<br>
     *
     * @throws TipException 函数异常提示消息，总是在该函数发生异常时，获取Tip并在阅读器界面吐司提示
     */

    public abstract List<Bookmark> visibleBookmarks() throws TipException;

    /**
     *
     * 功能描述：添加或更新 批注、高亮、档案 id为-1时为添加，否则为修改<br>
     * 创建者： yangn<br>
     * 创建日期：2014-1-11<br>
     *
     * @param annotations 被添加的图书或高亮或笔记
     * @throws TipException 函数异常提示消息，总是在该函数发生异常时，获取Tip并在阅读器界面吐司提示
     * @return 返回批注ID
     */
    public abstract long addAnnatitions(Annotations annotations) throws TipException;

    /**
     *
     * 功能描述：根据图书编号加载该图书关联的批注、高亮、笔记等信息 <br>
     * 创建者： yangn<br>
     * 创建日期：2014-1-11<br>
     *
     * @param chpIndex 章节索引
     * @throws TipException 函数异常提示消息，总是在该函数发生异常时，获取Tip并在阅读器界面吐司提示
     */
    public abstract List<Annotations> loadAnotations(int chpIndex,long libBookId ) throws TipException;

    /**
     *
     * 功能描述： 删除annotationId对应的批注或高亮或笔记信息<br>
     * 创建者： yangn<br>
     * 创建日期：2014-1-11<br>
     *
     * @param annotationId 批注或高亮或笔记的编号
     * @throws TipException 函数异常提示消息，总是在该函数发生异常时，获取Tip并在阅读器界面吐司提示
     */
    public abstract void deleteAnnotation(int annotationId, String annotationType) throws TipException;

    /**
     *
     * 功能描述：获取与annotationId关联的 批注或高亮或笔记信息 <br>
     * 创建者： yangn<br>
     * 创建日期：2014-1-11<br>
     *
     * @param annotationId 批注或高亮或笔记的编号
     * @throws TipException 函数异常提示消息，总是在该函数发生异常时，获取Tip并在阅读器界面吐司提示
     */
    public abstract Annotations getAnnotationById(int annotationId, String annotationType) throws TipException;

    /**
     *
     * 功能描述：判断复制到剪切板的文字是否已经超过限制长度 <br>
     * 创建者： yangn<br>
     * 创建日期：2014-1-17<br>
     *
     * @return 超出限制返回true 否则返回false
     */
    public abstract boolean isValidateClipBoardTextLen();

    /**
     *
     * 功能描述：保存本地分页计算总页码 ，分页计算完毕调用此方法 <br>
     * 创建者： yangn<br>
     * 创建日期：2014-1-17<br>
     *
     * @param totalCharNum 本地计算完毕的图书总字符数
     */
    public abstract void notifyLocalOverCountClipBoardTextLen(long totalCharNum);

    /**
     *
     * 功能描述：添加复制到剪切板文字长度 <br>
     * 创建者： yangn<br>
     * 创建日期：2014-1-17<br>
     *
     * @param len 当前剪切版文字长度
     */
    public abstract boolean putClipBoardTextLen(long len);

    // public void addAnnotationListener(IFunction<Void> callback,
    // IGiveback<String> giveback);

    /**
     * 功能描述：启动批注、档案、高亮输入框<br>
     * 创建者： jack<br>
     * 创建日期：2014-1-28<br>
     *
     * @param id 批注编号
     * @param type 批注类型
     * @param text 批注选择文本
     */
    public abstract void inputAnnotion(int id, String type, String text);
    /**
     * 功能描述： 通知新增批注完成<br>
     * 创建者： jack<br>
     * 创建日期：2014-1-28<br>
     *
     * @param type 批注类型
     * @param text 批注选择文本
     */
    public void notifyAnntionText(String type, String text, int tag) {
        GBApplication.Instance().runAction(ActionCode.SELECTION_NOTE_ANNOTATION, AnnotationOptype.INSERT, type, text,
                tag);
    }

    /**
     * 功能描述： 启动更新批注框<br>
     * 创建者： jack<br>
     * 创建日期：2014-1-28<br>
     *
     * @param id
     * @param text
     */
    // public abstract void updateAnnotion(int id, String text);
    /**
     * 功能描述：通知修改批注完成<br>
     * 创建者： jack<br>
     * 创建日期：2014-1-28<br>
     *
     * @param id
     * @param text
     */
    public void notifyUpdateAnntionText(int id, String text) {
        GBApplication.Instance().runAction(ActionCode.SELECTION_NOTE_ANNOTATION, AnnotationOptype.UPDATE, id, text);
    }
    /**
     * 功能描述： 启动读书笔记界面<br>
     * 创建者： jack<br>
     * 创建日期：2014-1-28<br>
     */
    public abstract void startReadNoteView(String currentChapter);

    // abstract class AnnotaionListener {
    //
    // public void notifyAnntionText(String text) {
    // GBApplication.Instance().runAction(ActionCode.SELECTION_NOTE_ANNOTATION,
    // AnnotationOptype.INSERT, text);
    // }

    // AnnotaionListener mAnotaionListener = null;
    // public void setOnRefreshAnnotaionListener(AnnotaionListener
    // annotaionListener) {
    // mAnotaionListener = annotaionListener;
    // }
    // }

    /**
     *
     * 功能描述： 支付购买图书<br>
     * 创建者： yangn<br>
     * 创建日期：2014-2-19<br>
     *
     * @param
     * @return 支付成功返回true 否则返回false
     */
    public abstract boolean pay();
    /**
     * 功能描述：获取语音引擎地址<br>
     * 创建者： jack<br>
     * 创建日期：2014-3-24<br>
     *
     * @return
     */
    public abstract String getVoiceEngineUri();

    /**
     * 功能描述：安装语音引擎<br>
     * 创建者： 周波<br>
     * 创建日期：2014-12-31下午2:48:50<br>
     * 版本： V0.1 <br>
     * 修改者： <br>
     * 修改日期：<br>
     */
    public abstract void installVoiceEngine();

    /**
     * 功能描述：获取PDF插件地址<br>
     * 创建者： 周波<br>
     * 创建日期：2014-12-24<br>
     *
     * @return
     */
    public abstract String getPdfSoUri();

    /**
     * 功能描述：安装PDF插件<br>
     * 创建者： 周波<br>
     * 创建日期：2014-12-31下午2:48:50<br>
     * 版本： V0.1 <br>
     * 修改者： <br>
     * 修改日期：<br>
     */
    public abstract void installPdfSo();

    /**
     * 功能描述：图书分享<br>
     * 创建者： 周波<br>
     * 创建日期：2015-1-16下午3:08:56<br>
     *
     * @return
     */
    public abstract void showShareBook();

    /**
     * 功能描述：图书推荐<br>
     * 创建者： 周波<br>
     * 创建日期：2015-1-16下午3:08:56<br>
     *
     * @return
     */
    public abstract void showCommendBook();

    /**
     * 功能描述：图书举报<br>
     * 创建者： 周波<br>
     * 创建日期：2015-5-18下午3:08:56<br>
     *
     * @return
     */
    public abstract void showReportBook();

    /**
     * 功能描述：图书邀请<br>
     * 创建者： 周波<br>
     * 创建日期：2015-5-18下午3:08:56<br>
     *
     * @return
     */
    public abstract void showInviteBook();

    /**
     * 功能描述：根据业务code获取是否是首次使用<br>
     * 创建者： 周波<br>
     * 创建日期：2015-7-9下午3:08:56<br>
     *
     * @param code:101读书邀请
     *
     * @return
     */
    public abstract boolean getIsNewByBusiCode(int code);

    /**
     * 功能描述：错误图书提示<br>
     * 创建者： 周波<br>
     * 创建日期：2015-3-27上午10:44:22<br>
     * 版本： V0.1 <br>
     * 修改者： <br>
     * 修改日期：<br>
     */
    public abstract void resolveBrokenBook();

    /**
     * 功能描述：获取当前账户ID<br>
     * 创建者： 周波<br>
     * 创建日期：2015-1-28下午5:54:27<br>
     * 版本： V0.1 <br>
     * 修改者： <br>
     * 修改日期：<br>
     * @return
     */
    public abstract int getAccountId();

    /**
     * 功能描述：进入别人的藏书馆<br>
     * 创建者： 周波<br>
     * 创建日期：2015-1-16下午3:08:56<br>
     *
     * @return
     */
    public abstract void goToHisLibrary(int accountId,String nickName);

    /**
     * 功能描述：下载文件<br>
     * 创建者： 周波<br>
     * 创建日期：2014-12-24<br>
     *
     * @return
     */
    public abstract void downloadFile(String url, String filename, boolean isCache, String title, IFunction<Integer> callback);
    /**
     * 功能描述：下载文件<br>
     * 创建者： 周波<br>
     * 创建日期：2014-12-24<br>
     *
     * @return
     */
    public abstract void downloadFontFile(String url,String filename,boolean isCache,String title,IFunction<Integer> callback);

    /**
     * 功能描述： 获取字体列表<br>
     * 创建者： yangn<br>
     * 创建日期：2014-4-1<br>
     *
     * @param localFamiliesList 本地已有字体列表
     * @throws JSONException
     * @return 填充成功返回true否则返回false
     */
    public abstract List<TypeFace> fillFamiliesList(List<TypeFace> localFamiliesList);

    /**
     * 功能描述： 下载文件<br>
     * 创建者： yangn<br>
     * 创建日期：2014-4-1<br>
     *
     * @paramurl 文件地址
     * @param output 文件本地输出路径
     */
    public abstract boolean downFile(String url, String output);

    /**
     * 功能描述： 显示全屏高清图<br>
     * 创建者： 周波<br>
     * 创建日期：2015-6-25<br>
     *
     * @param url 文件地址
     */
    public abstract void showPicFullScreen(String url);

    /**
     * 功能描述：获取服务器端阅读进度等信息<br>
     * 创建者： jack<br>
     * 创建日期：2014-4-15<br>
     */
    public abstract void getServerReadInfo();

    public abstract void shareText(String bookname, String text);

    /**
     *
     * @param content 我的写笔记
     * @param text 选择的书的片段
     * @param treeStr 章节
     * @param isPublic 是否公开
     */
    public abstract long addTextToNotes( String content,String text,String treeStr,boolean isPublic);
    /**
     *
     * @param ReaderApplication 书的ID
     */
    public abstract void addNotes(ReaderApplication ReaderApplication,ReaderActivity baseActivity,String type, int tag);

    public abstract void addNotesAnnotation(ReaderApplication ReaderApplication,ReaderActivity baseActivity,int id,int type);

    public abstract void Cabinet(String range,String content);

    public abstract boolean isMyBook();

    public abstract void addNotify();

    public abstract  void openActShareGoodFriendActivity(Activity act);

    public abstract  void shareToBookgroup(Activity act,String bookcontent, View view,int type);

    public abstract  void sharetoQQ(Activity act);

    public abstract  void sharetoWX(Activity act);

    public abstract  void sharetoWXfriendGroup(Activity act);

    public abstract  void sharetoWB(Activity act);

    public abstract void downLoadImg(Activity act, ImageView img);

    public abstract int getHoldStatus();

    public abstract boolean isLocal();

    public abstract void getDataByRel(Activity act);

    public abstract void downLoadText(Activity act, TextView text);

    public abstract  String bookauthor();

    public abstract void  sharetoWXBook(int flag,Activity act,View v);

    public abstract void  sharetoWbBook(Activity act,View v);

    public abstract void  sharetoQQBook(Activity act,View v);

    public abstract void   isShowTimer(Activity activity,Timer timer,TimerTask task);

    public abstract void  showChengjiu(Activity activity);

    public abstract void  shareToFriend(int requestCode, int resultCode, Intent data, final Activity act,View v);



}
