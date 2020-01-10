package com.tin.projectlist.app.library.reader.model.parser;

import com.core.common.util.IFunction;
import com.core.file.GBFile;
import com.core.file.image.GBImage;
import com.core.support.EncodingCollection;
import com.core.text.widget.GBTextPosition;
import com.geeboo.read.model.book.Book;
import com.geeboo.read.model.bookmodel.BookModel;
import com.geeboo.read.model.bookmodel.BookReadingException;

/**
 * 类名： FormatPlugin.java<br>
 * 描述： 文件解析插件<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-25<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public abstract class FormatPlugin {
	private final String myFileType;

	protected FormatPlugin(String fileType) {
		myFileType = fileType;
	}

	public final String supportedFileType() {
		return myFileType;
	}

	public GBFile realBookFile(GBFile file) throws BookReadingException {
		return file;
	}

	public abstract void readMetaInfo(Book book) throws BookReadingException;

	public abstract void readModel(BookModel model, GBTextPosition lastPosition)
			throws BookReadingException;

	public abstract boolean isLoadChp(int chpFileIndex);

	public abstract void readModel(GBTextPosition lastPosition,
								   IFunction<Integer> function) throws BookReadingException;

	public abstract void getReadProgress(IFunction<Integer> handler);

	public abstract void startBuildCache();

	public abstract void stopReadMode();

	public abstract void detectLanguageAndEncoding(Book book)
			throws BookReadingException;

	public abstract GBImage readCover(GBFile file);

	public abstract String readAnnotation(GBFile file);

	public enum Type {
		ANY, JAVA, NATIVE, EXTERNAL, NONE
	};

	public abstract Type type();

	public abstract EncodingCollection supportedEncodings();
}
