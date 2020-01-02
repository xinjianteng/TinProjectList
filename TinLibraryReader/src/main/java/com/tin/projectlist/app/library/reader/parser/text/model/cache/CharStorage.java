package com.core.text.model.cache;

/**
 *
 * 描述： Char缓存接口 创建者： 燕冠楠<br>
 * 创建日期：2013-3-26<br>
 */
public interface CharStorage {

    /**
     * 获取缓存文件数量
     */
    int size();

    int size(int chpFileNum);
    public int getChpFileSize();
    /**
     * 按索引获取char数组
     */
    char[] block(int index);

    /**
     * 按章、列表索引获取char数组
     *
     * @param chpFileIndex章索引
     * @param index列表索引
     * @return chpFileIndex index位置char[]
     */
    public char[] block(int chpFileIndex, int index);

    /**
     * 若minimumLength大于当前char[]len 则创建新char[] 并加入到array
     *
     * @param char[]最小长度
     */
    char[] createNewBlock(int chpFileIndex, int minimumLength);

    /**
     * 将array里最后一块Char数组缓存到文件
     *
     * @param isCheckExists 是否检查缓存已存在 若检查缓存 缓存存在则不执行缓存 若不存在执行缓存 否则不检查缓存存在总是执行缓存
     */
    void freezeLastBlock(int chpFileIndex, boolean ischeckExists);

    void freezeLastChpBlock();

    /**
     * 返回当前缓存章索引
     *
     * @return
     */
    int getChpFileNum();

    /**
     * 设置当前章索引
     *
     * @param chpFileNum
     */
    void setChpFileNum(int chpFileNum);

    /**
     * 重置章节数量
     *
     * @param newChpSize
     */
    void resetChpSize(int newChpSize);

    /**
     * 判断该本书时候有缓存
     *
     * @return 有缓存返回true 否则返回false
     */
    boolean isCacheExists(boolean isTemp);

    boolean isCacheItemExists(int chpFileIndex);

    boolean delCacheItemAll();
    /**
     * 删除章节分页索引
     * @return
     */
    boolean delLinkCache();
    /**
     * 添加关联缓存对象
     *
     * @param cacheName
     * @param obj 被缓存对象
     * @return缓存成功返回true否则返回false;
     */
    boolean setLinkCache(String cacheName, Object obj);

    /**
     * 获取关联缓存对象
     *
     * @param cacheName
     * @return找到关联缓存返回true否则返回false
     */
    Object getLinkCache(String cacheName);

    /**
     * 清除内存中 chpFileIndex 数据
     *
     * @param chpFileIndex
     */
    void clearMemoryByChpIndex(int chpFileIndex);

    /**
     * 删除name缓存文件
     *
     * @param name
     */
    void delCache(String name);
    /**
     * 初始化缓存目录
     */
    void initDir();

    void finalize();
}
