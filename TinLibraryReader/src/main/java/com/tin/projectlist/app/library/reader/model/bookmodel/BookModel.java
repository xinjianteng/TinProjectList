package com.tin.projectlist.app.library.reader.model.bookmodel;

import com.tin.projectlist.app.library.reader.model.book.Book;
import com.tin.projectlist.app.library.reader.model.parser.FormatPlugin;
import com.tin.projectlist.app.library.reader.parser.platform.GBLibrary;
import com.tin.projectlist.app.library.reader.parser.text.model.GBTextModel;
import com.tin.projectlist.app.library.reader.parser.text.widget.GBTextPosition;

import java.util.List;


/**
 * 类名： BookModel.java<br>
 * 描述： <br>
 * 创建者： jack<br>
 * 创建日期：2013-4-25<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public abstract class BookModel {
    static final String TAG = "BookModel";

    public static BookModel createModel(Book book, GBTextPosition lastPosition) throws BookReadingException {
        final FormatPlugin plugin = book.getPlugin();

        if (plugin.supportedFileType().equals("plain text"))
            GBLibrary.Instance().UseCssOption.setValue(false);
        else
            GBLibrary.Instance().UseCssOption.setValue(true);

        final BookModel model;
        switch (plugin.type()) {
            // case NATIVE:
            // model = new NativeBookModel(book);
            // break;
            case JAVA :
                model = new JavaBookModel(book);
                break;
            default :
                throw new BookReadingException("unknownPluginType", plugin.type().toString(), null);
        }

        // if (book.getId() != Collection.getRecentBook(0).getId()) {
        model.getTextModel().delCacheItemAll();
        // }
        plugin.readModel(model, lastPosition);

        return model;
    }

    public final Book book;
    public final TOCTree TOCTree = new TOCTree();

    /*
     * 链接标记封装
     */
    public static final class Label {
        public final String ModelId; // 文本业务层id
        public final int ChpFileIndex; // 章节文件索引
        public final int ParagraphIndex; // 段落索引

        public Label(String modelId, int chpFileIndex, int paragraphIndex) {
            ModelId = modelId;
            ChpFileIndex = chpFileIndex;
            ParagraphIndex = paragraphIndex;
        }
    }

    protected BookModel(Book book) {
        Book = book;
    }

    public abstract GBTextModel getTextModel();

    public abstract GBTextModel getFootnoteModel(String id);

    /*
     * 根据id获取内部链接信息
     * @param id 链接标示
     */
    protected abstract Label getLabelInternal(String id);

    public interface LabelResolver {
        List<String> getCandidates(String id);
    }

    private LabelResolver myResolver;

    public void setLabelResolver(LabelResolver resolver) {
        myResolver = resolver;
    }

    public Label getLabel(String id) {
        Label label = getLabelInternal(id);
        if (label == null && myResolver != null) {
            for (String candidate : myResolver.getCandidates(id)) {
                label = getLabelInternal(candidate);
                if (label != null) {
                    break;
                }
            }
        }
        return label;
    }

}
