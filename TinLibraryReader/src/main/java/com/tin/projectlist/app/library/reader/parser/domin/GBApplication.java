package com.tin.projectlist.app.library.reader.parser.domin;

import com.core.object.GBBoolean3;
import com.core.text.iterator.GBTextWordCursor;
import com.core.text.widget.GBTextFixedPosition;
import com.core.view.GBView;
import com.core.view.GBViewInter;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * 类名： GBApplication.java#zlApplication<br>
 * 描述： 应用管理类<br>
 * 创建者： jack<br>
 * 创建日期：2013-3-29<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public abstract class GBApplication {

    public static final String NoAction = "none";

    private static GBApplication me;

    protected GBApplication() {
        me = this;
    }

    public static GBApplication Instance() {
        return me;
    }

    // 当前显示窗体
    private volatile GBApplicationWindow mWindow;
    // 当前显示的阅读控件
    private volatile GBView mView;
    // 业务处理类集合
    protected final HashMap<String, GBAction> mActionMap = new HashMap<String, GBAction>();

    /**
     * 功能描述： 设置view控件并重绘<br>
     * 创建者： jack<br>
     * 创建日期：2013-3-29<br>
     *
     * @param view
     */
    protected final void setView(GBView view) {
        if (view != null) {
            mView = view;
            final GBViewInter widget = getViewImp();
            if (widget != null) {
                widget.reset();
                widget.repaint();
            }
            onViewChanged();
        }
    }

    /**
     * 功能描述：获取翻页控件业务实现类 <br>
     * 创建者： jack<br>
     * 创建日期：2013-3-29<br>
     *
     * @return
     */
    public final GBViewInter getViewImp() {
        return mWindow != null ? mWindow.getViewImp() : null;
    }

    public final GBView getCurrentView() {
        return mView;
    }

    final void setWindow(GBApplicationWindow window) {
        mWindow = window;
    }

    public final void initWindow() {
        setView(mView);
    }

    protected void setTitle(String title) {
        if (mWindow != null) {
            mWindow.setTitle(title);
        }
    }

    protected void runWithMessage(String key, Runnable runnable, Runnable postAction) {
        if (mWindow != null) {
            mWindow.runWithMessage(key, runnable, postAction);
        }
    }

    /**
     * 功能描述： 异常处理<br>
     * 创建者： jack<br>
     * 创建日期：2013-4-1<br>
     *
     * @param e
     */
    protected void processException(Exception e) {
        if (mWindow != null) {
            mWindow.processException(e);
        }
    }

    /**
     * 功能描述： 版面重绘结束时控件刷新<br>
     * 创建者： jack<br>
     * 创建日期：2013-4-1<br>
     */
    public final void onRepaintFinished() {
        if (mWindow != null) {
            mWindow.refresh();
        }
        for (GBPopPanel popup : popupPanels()) {
            popup.update();
        }
    }

    /**
     * 功能描述： 当窗体发生改变时隐藏弹出框<br>
     * 创建者： jack<br>
     * 创建日期：2013-4-1<br>
     */
    public final void onViewChanged() {
        hideActivePopup();
    }

    /**
     * 功能描述： 隐藏弹出框<br>
     * 创建者： jack<br>
     * 创建日期：2013-4-1<br>
     */
    public final void hideActivePopup() {
        if (mActivePopup != null) {
            mActivePopup.hide();
            mActivePopup = null;
        }
    }

    /**
     * 功能描述： 显示指定弹出框<br>
     * 创建者： jack<br>
     * 创建日期：2013-4-1<br>
     * e
     *
     * @param id 窗体id
     */
    public final void showPopup(String id) {
        hideActivePopup();
        mActivePopup = mPopups.get(id);
        if (mActivePopup != null) {
            mActivePopup.show();
        }
    }

    /**
     * 功能描述： 添加一个业务处理<br>
     * 创建者： jack<br>
     * 创建日期：2013-4-1<br>
     *
     * @param actionId
     * @param action
     */
    public final void addAction(String actionId, GBAction action) {
        mActionMap.put(actionId, action);
    }

    public final void removeAction(String actionId) {
        mActionMap.remove(actionId);
    }

    public final boolean isActionVisible(String actionId) {
        final GBAction action = mActionMap.get(actionId);
        return action != null && action.isVisible();
    }

    public final boolean isActionEnabled(String actionId) {
        final GBAction action = mActionMap.get(actionId);
        return action != null && action.isEnabled();
    }

    public final GBBoolean3 isActionChecked(String actionId) {
        final GBAction action = mActionMap.get(actionId);
        return action != null ? action.isChecked() : GBBoolean3.B3_UNDEFINED;
    }

    /**
     * 功能描述： 执行业务层<br>
     * 创建者： jack<br>
     * 创建日期：2013-4-1<br>
     *
     * @param actionId
     * @param params
     */
    public final void runAction(String actionId, Object... params) {
        final GBAction action = mActionMap.get(actionId);
        if (action != null) {
            action.checkAndRun(params);
        }
    }

    // may be protected
    public abstract GBKeyBindings keyBindings();

    public final boolean hasActionForKey(int key, boolean longPress) {
        final String actionId = keyBindings().getBinding(key, longPress);
        return actionId != null && !NoAction.equals(actionId);
    }

    public final boolean runActionByKey(int key, boolean longPress) {
        final String actionId = keyBindings().getBinding(key, longPress);
        if (actionId != null) {
            final GBAction action = mActionMap.get(actionId);
            return action != null && action.checkAndRun();
        }
        return false;
    }

    public boolean closeWindow() {
        onWindowClosing();
        if (mWindow != null) {
            mWindow.close();
        }
        return true;
    }

    public void onWindowClosing() {
    }

    // 弹出窗体集合
    protected final HashMap<String, GBPopPanel> mPopups = new HashMap<String, GBPopPanel>();
    // 当前显示的弹出框
    private GBPopPanel mActivePopup;

    public final Collection<GBPopPanel> popupPanels() {
        return mPopups.values();
    }

    public final GBPopPanel getActivePopup() {
        return mActivePopup;
    }

    public final GBPopPanel getPopupById(String id) {
        return mPopups.get(id);
    }

    // public int getBatteryLevel() {
    // return (mWindow != null) ? mWindow.getBatteryLevel() : 0;
    // }
    // 定时任务
    private volatile Timer mTimer;
    // 定时任务时间间隔集合
    private final HashMap<Runnable, Long> mTimerTaskPeriods = new HashMap<Runnable, Long>();
    // 定时任务集合
    private final HashMap<Runnable, TimerTask> mTimerTasks = new HashMap<Runnable, TimerTask>();

    // 定时任务封装类
    private static class mTimerTask extends TimerTask {
        private final Runnable myRunnable;

        mTimerTask(Runnable runnable) {
            myRunnable = runnable;
        }

        public void run() {
            myRunnable.run();
        }
    }

    /**
     * 功能描述： 新增一个定时任务并在一半间隔时间后启动<br>
     * 创建者： jack<br>
     * 创建日期：2013-4-3<br>
     *
     * @param runnable 业务对处理对象
     * @param periodMilliseconds 间隔时间
     */
    private void addTimerTaskInternal(Runnable runnable, long periodMilliseconds) {
        final TimerTask task = new mTimerTask(runnable);
        mTimer.schedule(task, periodMilliseconds / 2, periodMilliseconds);
        mTimerTasks.put(runnable, task);
    }

    // 同步锁
    private final Object mTimerLock = new Object();

    /**
     * 功能描述： 启动定时任务<br>
     * 创建者： jack<br>
     * 创建日期：2013-4-3<br>
     * public void openBookChapByFileIndex(int chapterFileIndex, ICallback
     * callback){
     */
    public final void startTimer() {
        synchronized (mTimerLock) {
            if (mTimer == null) {
                mTimer = new Timer();
                for (Map.Entry<Runnable, Long> entry : mTimerTaskPeriods.entrySet()) {
                    addTimerTaskInternal(entry.getKey(), entry.getValue());
                }
            }
        }
    }

    /**
     * 功能描述： 停止定时任务<br>
     * 创建者： jack<br>
     * 创建日期：2013-4-3<br>
     */
    public final void stopTimer() {
        synchronized (mTimerLock) {
            if (mTimer != null) {
                mTimer.cancel();
                mTimer = null;
                mTimerTasks.clear();
            }
        }
    }

    /**
     * 功能描述： 新增定时任务<br>
     * 创建者： jack<br>
     * 创建日期：2013-4-3<br>
     *
     * @param runnable 要执行的业务
     * @param periodMilliseconds 任务间隔执行时间
     */
    public final void addTimerTask(Runnable runnable, long periodMilliseconds) {
        synchronized (mTimerLock) {
            removeTimerTask(runnable);
            mTimerTaskPeriods.put(runnable, periodMilliseconds);
            if (mTimer != null) {
                addTimerTaskInternal(runnable, periodMilliseconds);
            }
        }
    }

    /**
     * 功能描述： 移除一个定时任务<br>
     * 创建者： jack<br>
     * 创建日期：2013-4-3<br>
     *
     * @param runnable
     */
    public final void removeTimerTask(Runnable runnable) {
        synchronized (mTimerLock) {
            TimerTask task = mTimerTasks.get(runnable);
            if (task != null) {
                task.cancel();
                mTimerTasks.remove(runnable);
            }
            mTimerTaskPeriods.remove(runnable);
        }
    }

    /**
     * 功能描述：获取资源存放路径 <br>
     * 创建者： jack<br>
     * 创建日期：2013-4-3<br>
     *
     * @return
     */
    public abstract String getCardDirectory();

    /**
     * // 按章节文件索引加载章节
     *
     * @param chapterFileIndex章节索引
     * @param callback是否加载成功 成功返回true否则返回false;
     */
    public abstract void openBookByChapFileIndex(int chapterFileIndex, IFunction callback);

    /**
     * 获取阅读进度
     * @param handler 阅读进度毁掉函数  每当阅读进度发生改变触发该回调  该回调在子线程执行
     */
    public abstract void getReadProgress(IFunction<Integer> progressChangeHandler);
    /**
     * 判断章节是否已经加载
     *
     * @param chpFileIndex
     * @return已加载返回true否则返回false
     */
    public abstract boolean isLoadBookChp(int chpFileIndex);
    /**
     * 功能描述： 获取当前页的书签列表<br>
     * 创建者： jack<br>
     * 创建日期：2013-8-13<br>
     *
     * @param start 页面开始索引
     * @param end 页面结束索引
     * @return
     */
    public abstract List<GBTextFixedPosition> getPageBookmark(GBTextWordCursor start, GBTextWordCursor end);
}
