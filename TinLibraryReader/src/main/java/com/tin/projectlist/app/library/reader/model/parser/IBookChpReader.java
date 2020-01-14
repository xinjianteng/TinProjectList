package com.tin.projectlist.app.library.reader.model.parser;


import com.tin.projectlist.app.library.reader.model.bookmodel.BookReadingException;
import com.tin.projectlist.app.library.reader.parser.common.util.IFunction;
import com.tin.projectlist.app.library.reader.parser.file.GBFile;
import com.tin.projectlist.app.library.reader.parser.text.widget.GBTextPosition;

/**
 * 可按章节加载图书的解析器
 *
 * @author yangn
 *
 */
public interface IBookChpReader {

    /**
     * 打开阅读图书
     *
     * @param file
     * @param lastPosition
     */
    void readBook(GBFile file, GBTextPosition lastPosition) throws BookReadingException;

    /**
     * 按章阅读图书
     *
     * @param chpFileIndex
     * @param function
     */
    void readBookByChpFileIndex(final int chpFileIndex, final IFunction<Integer> function);

    /**
     * 准备加载章节
     *
     * @param chpFileIndex
     * @return
     */
//    int prepareLoadChp(int chpFileIndex);

    /**
     * 章节加载完毕
     *
     * @param chpFileIndex
     * @param isNoticeRefrech
     */
//    void chpLoadOver(int chpFileIndex, boolean isNoticeRefrech, IFunction<Integer> function);

    /**
     * 章节是否加载完毕
     *
     * @param chpFileIndex
     * @return
     */
    boolean isLoad(int chpFileIndex);

    /**
     * 章节是否加载中
     *
     * @param chpFileIndex
     * @return
     */
    boolean isChpLoading(int chpFileIndex);
}
