package com.tin.projectlist.app.library.reader.model.parser;


import com.tin.projectlist.app.library.reader.model.book.Book;
import com.tin.projectlist.app.library.reader.model.bookmodel.BookModel;
import com.tin.projectlist.app.library.reader.model.bookmodel.BookReadingException;
import com.tin.projectlist.app.library.reader.parser.common.util.IFunction;
import com.tin.projectlist.app.library.reader.parser.file.GBFile;
import com.tin.projectlist.app.library.reader.parser.file.image.GBImage;
import com.tin.projectlist.app.library.reader.parser.file.image.GBImageProxy;
import com.tin.projectlist.app.library.reader.parser.file.image.GBSingleImage;
import com.tin.projectlist.app.library.reader.parser.support.EncodingCollection;
import com.tin.projectlist.app.library.reader.parser.support.JavaEncodingCollection;
import com.tin.projectlist.app.library.reader.parser.text.widget.GBTextPosition;

public class NativeFormatPlugin extends FormatPlugin {
	public static NativeFormatPlugin create(String fileType) {
//		if ("fb2".equals(fileType)) {
//			return new FB2NativePlugin();
//		} else if ("ePub".equals(fileType)) {
//			return new OEBNativePlugin();
//		} else {
		return new NativeFormatPlugin(fileType);
//		}
	}

	protected NativeFormatPlugin(String fileType) {
		super(fileType);
	}

	@Override
	synchronized public void readMetaInfo(Book book) throws BookReadingException {
		if (!readMetaInfoNative(book)) {
			throw new BookReadingException("errorReadingFile", book.File);
		}
	}

	private native boolean readMetaInfoNative(Book book);

	@Override
	public void detectLanguageAndEncoding(Book book) {
		detectLanguageAndEncodingNative(book);
	}

	public native void detectLanguageAndEncodingNative(Book book);

	@Override
	synchronized public void readModel(BookModel model, GBTextPosition lastPosition) throws BookReadingException {
		if (!readModelNative(model)) {
			throw new BookReadingException("errorReadingFile", model.Book.File);
		}
	}

	private native boolean readModelNative(BookModel model);

	@Override
	public GBImage readCover(final GBFile file) {
		return new GBImageProxy() {
			@Override
			public int sourceType() {
				return SourceType.DISK;
			}

			@Override
			public String getId() {
				return file.getPath();
			}

			@Override
			public GBSingleImage getRealImage() {
				final GBImage[] box = new GBImage[1];
				readCoverInternal(file, box);
				return (GBSingleImage)box[0];
			}
		};
	}

	protected native void readCoverInternal(GBFile file, GBImage[] box);

	// FIXME: temporary implementation; implement as a native code (?)
	@Override
	public String readAnnotation(GBFile file) {
		final FormatPlugin plugin = PluginCollection.Instance().getPlugin(file, FormatPlugin.Type.JAVA);
		if (plugin != null) {
			return plugin.readAnnotation(file);
		}
		return null;
	}

	@Override
	public Type type() {
		return Type.NATIVE;
	}

	@Override
	public EncodingCollection supportedEncodings() {
		return JavaEncodingCollection.Instance();
	}

	@Override
	public String toString() {
		return "NativeFormatPlugin [" + supportedFileType() + "]";
	}

	@Override
	public void readModel(GBTextPosition lastPosition,
						  IFunction<Integer> function) throws BookReadingException {
		// TODO Auto-generated method stub

	}

	@Override
	public void stopReadMode() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isLoadChp(int chpFileIndex) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void startBuildCache() {
		// TODO Auto-generated method stub

	}

	@Override
	public void getReadProgress(IFunction<Integer> handler) {
		// TODO Auto-generated method stub

	}
}
