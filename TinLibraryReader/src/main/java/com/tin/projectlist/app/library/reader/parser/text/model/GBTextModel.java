package com.core.text.model;

import java.util.List;
import java.util.Stack;

import com.core.text.model.cache.CharStorage;
import com.core.xml.GBIntMap;

/**
 *
 * 描述： 文本模型 创建者： 燕冠楠<br>
 * 创建日期：2013-3-26<br>
 */
public interface GBTextModel {
    String getId();

    String getLanguage();

    // 获取总章节文件数
    int getChapterSize();
    void setChapterSize(int chapterSize);

    int getChapterFileNumber();

    int getChapterFileNumber(int chapFileNum);

    // 获取指定章节段落数
    int getParagraphsNumber(int chpFileIndex);

    GBIntMap getChpFileNumMapping();

    /**
     * 返回当前章段数
     *
     * @return
     */
    int getParagraphsNumber();

    // 获取总段落数
    int getTotalParagraphsNumber();

    GBTextParagraph getParagraph(int chpFileIndx, int index);

    /**
     * 获取当前章 段信息
     *
     * @param index段索引
     * @return
     */
    GBTextParagraph getParagraph(int index);

    /**
     * 删除所有书签
     */
    void removeAllMarks();

    /**
     * 获取第一个书签
     */
    GBTextMark getFirstMark();

    /**
     * 获取最后一个书签
     */
    GBTextMark getLastMark();

    /**
     * 获取下一个书签
     */
    GBTextMark getNextMark(GBTextMark position);

    /**
     * 上一个书签
     */
    GBTextMark getPreviousMark(GBTextMark position);

    /**
     * 获取所有书签
     */
    List<GBTextMark> getMarks();

    // text length for paragraphs from 0 to index
    int getTextLength(int chpFileIndex, int index);

    /**
     * 类名： GBTextPlainModel.java<br>
     * 描述： 位置信息<br>
     * 创建者： jack<br>
     * 创建日期：2013-7-8<br>
     * 版本： <br>
     * 修改者： <br>
     * 修改日期：<br>
     */
    public static class PositionInfo {
        public final int mChpFileIndex;
        public final int mParagraphIndex;

        public PositionInfo(int chpFileindex, int paragraphIndex) {
            mChpFileIndex = chpFileindex;
            mParagraphIndex = paragraphIndex;
        }
    }

    PositionInfo findParagraphByTextLength(int length);

    PositionInfo preParagraph(PositionInfo info);

    PositionInfo nextParagraph(PositionInfo info);

    // int search(final String text, int startIndex, int endIndex, boolean
    // ignoreCase);
    int search(final String text, int startFileIndex, int startIndex, int endFileIndex, int endIndex, boolean ignoreCase);

    /**
     *
     * 功能描述： 根据样式段码获取包含其的样式标签段码 创建者： yangn<br>
     * 创建日期：2013-6-3<br>
     *
     * @param 样式段码
     * @return >=0:包含其的样式段码数组 -1:非法段码
     */
    int[] getStyleParagraphIncluded(int chpFileIndex, int index);

    /**
     *
     * 功能描述：获取当前段有效的样式段码 创建者： yangn<br>
     * 创建日期：2013-6-4<br>
     *
     * @return 从栈底到栈顶依次顺序0,1,2,3,4...
     *
     */
    Stack<Integer> getStylePossible();

    void init(int[][] entryIndices, int[][] entryOffsets, int[][] paragraphLenghts, int[][] textSizes,
              byte[][] paragraphKinds, CharStorage storage, int[][][] styleParagraph, int[] paragraphNumber);

    /**
     * 开始下一章节
     *
     * @return
     */
    int nextChp();

    /**
     * 重置章长度
     *
     * @param newChpSize
     */
    int resetChpSize(int newChpSize, boolean isFromCache);
    /**
     * 扩展textsize长度
     *
     * @param newChpSize
     */
    void extendTextSizes(int newChpSize, boolean fromCache);

    /**
     * 判断是否存在缓存文件
     *
     * @param isTemp true检查临时缓存 false检查正常缓存
     * @return 已存在缓存返回true否则返回false;
     */
    boolean isCacheExists(boolean isTemp);

    /**
     * 判断chpFileIndex章节缓存文件是否存在
     *
     * @param chpFileIndex
     * @return
     */
    boolean isCacheItemExists(int chpFileIndex);

    /**
     * 删除缓存子项
     *
     * @return
     */
    boolean delCacheItemAll();
    /**
     * 删除章节页码缓存
     *
     * @return
     */
    boolean delLinkCache();

    /**
     * 生成页码缓存
     *
     * @param isTemp 是否是临时文件
     * @return 生成缓存成功返回true否则返回false;
     */
    boolean buildLinkCache(boolean isTemp);// String cacheName,Object obj

    /**
     * 获取关联对象
     *
     * @param cacheName
     * @return 获取失败返回true 回去成功返回false
     */
    boolean getLinkCache(boolean isTemp);

    void clearCache(int chpFileIndex, boolean isMapping);
    /**
     * 缓存当前章未缓存数据到文件
     */
    void buildLastDataCache(int chpFileIndex);
    /**
     * 功能描述： 打开图书结束<br>
     * 创建者： jack<br>
     * 创建日期：2013-8-8<br>
     */
    void loadBookOver();
    public boolean isLoadBookOver();

    /**
     * 初始化缓存路径
     */
    public void initCacheDir();

    /**
     * 当前正在阅读章节
     *
     * @return
     */
    public int getChpSelected();

    /**
     * 设置当前真在阅读章节
     *
     * @param chpFileIndex
     */
    public void setChpSelected(int chpFileIndex);

    public void addCacheItem(String key, Object item);
    public Object getCacheItem(String key);
    /*
     * 显示位置标示：-1－不显示、1－上左、2－上中、 3－上右、4－下左、5－下中、 6－下右、7－左上、8－左中、
     * 9－左下、10－右上、11－右中、12－右下
     */
    public byte getBookNameDisplayMode();
    public void setBookNameDisplayMode(byte val);

    public byte getChpNameDisplayMode();
    public void setChpNameDisplayMode(byte val);

    public void finalize();
    // 缓存章节数量
    public static final int cacheChapNumber = 3;
    // 是否有嵌入版权页
    public void setIsHaveCopyRight(boolean isHave);
    public boolean isHaveCopyRight();
}
