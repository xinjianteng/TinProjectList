package com.tin.projectlist.app.library.reader.model.bookmodel;


import com.tin.projectlist.app.library.reader.model.book.Book;
import com.tin.projectlist.app.library.reader.parser.file.GBPaths;
import com.tin.projectlist.app.library.reader.parser.text.model.GBTextModel;
import com.tin.projectlist.app.library.reader.parser.text.model.cache.impl.CachedCharStorage;
import com.tin.projectlist.app.library.reader.parser.text.model.impl.GBTextWritablePlainModel;

public class JavaBookModel extends BookModelImpl {
    final String TAG = "JavaBookModel";
    public final GBTextModel BookTextModel;
    public final GBTextModel GenerPageNumModel;
    public final String CacheVersion = "v1";

    public JavaBookModel(com.tin.projectlist.app.library.reader.model.book.Book book) {
        super(book);
        myInternalHyperlinks = new CachedCharStorage(32768, GBPaths.cacheDirectory(), book.getId() + "", "links", 1);
        // arraySize 章节数组长度 dataBlockSize 缓存char长度
        final int arraySize = 512, dataBlockSize = 10240;// 10240
        String bookname = book.getId() + CacheVersion;
        BookTextModel = new GBTextWritablePlainModel(null, book.getLanguage(), arraySize, dataBlockSize,
                GBPaths.cacheDirectory(), bookname, "cache", myImageMap, GBTextModel.cacheChapNumber);
        GenerPageNumModel = new GBTextWritablePlainModel(null, book.getLanguage(), arraySize, dataBlockSize,
                GBPaths.cacheDirectory(), bookname, "cache", myImageMap, 1);
        /*
         * BookTextModel = new GBTextWritablePlainModel(null,
         * book.getLanguage(), myImageMap);
         */
    }

    @Override
    public GBTextModel getTextModel() {
        return BookTextModel;
    }

    @Override
    public GBTextModel getFootnoteModel(String id) {
        GBTextModel model = myFootnotes.get(id);
        if (model == null) {
            model = new GBTextWritablePlainModel(id, Book.getLanguage(), 512, 512, GBPaths.cacheDirectory(), id,
                    "readBookCache" + myFootnotes.size(), myImageMap, BookTextModel.getChapterSize());
            myFootnotes.put(id, model);
        }
        return model;
    }

    private char[] myCurrentLinkBlock; // 当前用来缓存链接信息的char数组
    private int myCurrentLinkBlockOffset; // 当前写入到的位置

    /*
     * 添加链接标签
     * @param label 链接唯一标示 （章节目录#标签id）
     * @param model 文本内容模型层
     * @param paragraphNumber 段落标示 添加顺序格式为：链接标示长度，链接标示，内容模型id，段落标示
     */
    void addHyperlinkLabel(String label, GBTextModel model, int chpFileIndex, int paragraphNumber) {
        final String modelId = model.getId();
        final int labelLength = label.length();
        final int idLength = (modelId != null) ? modelId.length() : 0;
        final int len = 4 + labelLength + idLength;

        char[] block = myCurrentLinkBlock;
        int offset = myCurrentLinkBlockOffset;

        /*
         * if (frontChpFileIndex!=chpFileIndex) {
         * frontChpFileIndex=chpFileIndex; // 获取新章之前 将当前放入缓存 if (block != null)
         * { if (chpFileIndex == 0) {
         * myInternalHyperlinks.freezeLastBlock(chpFileIndex); } else {
         * myInternalHyperlinks.freezeLastBlock(chpFileIndex - 1); } } // 获取新章块
         * myInternalHyperlinks.setChpFileNum(chpFileIndex); block =
         * myInternalHyperlinks.createNewBlock(chpFileIndex, len); offset = 0; }
         */
        if ((block == null) || (offset + len > block.length)) {
            if (block != null) {
                myInternalHyperlinks.freezeLastBlock(chpFileIndex > 0 ? chpFileIndex - 1 : 0, false);
            }
            try {
                block = myInternalHyperlinks.createNewBlock(chpFileIndex, len);
            } catch (Exception ex) {
                L.e(TAG, "currnt index=" + chpFileIndex);
                block = myInternalHyperlinks.createNewBlock(chpFileIndex, len);
            }
            myCurrentLinkBlock = block;
            offset = 0;
        }
        block[offset++] = (char) labelLength;
        label.getChars(0, labelLength, block, offset);
        offset += labelLength;
        block[offset++] = (char) idLength;
        if (idLength > 0) {
            modelId.getChars(0, idLength, block, offset);
            offset += idLength;
        }
        block[offset++] = (char) paragraphNumber;
        block[offset++] = (char) (paragraphNumber >> 16);
        myCurrentLinkBlockOffset = offset;
    }
}
