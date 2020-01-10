package com.tin.projectlist.app.library.reader.model.parser.oeb;

import com.core.common.CopyVersionInfo;
import com.core.common.util.IFunction;
import com.core.file.GBFile;
import com.core.file.image.GBImage;
import com.core.support.AutoEncodingCollection;
import com.core.text.widget.GBTextPosition;
import com.geeboo.read.model.book.Book;
import com.geeboo.read.model.bookmodel.BookModel;
import com.geeboo.read.model.bookmodel.BookReadingException;
import com.geeboo.read.model.parser.JavaFormatPlugin;

/**
 * 类名： OEBPlugin.java<br>
 * 描述： epub文件解析插件<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-26<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public class OEBPlugin extends JavaFormatPlugin {
	public OEBPlugin() {
		super("ePub");
	}

	// 获取opf文件
	private GBFile getOpfFile(GBFile oebFile) throws BookReadingException {
		if ("opf".equals(oebFile.getExtension())) {
			return oebFile;
		}
		// 校验是否加密图书
		final GBFile encryInfoFile = GBFile.createFile(oebFile,
				"META-INF/encryption.xml");
		if (encryInfoFile.exists()) {
			throw new BookReadingException("encryFile", oebFile);
		}

		final GBFile containerInfoFile = GBFile.createFile(oebFile,
				"META-INF/container.xml");
		if (containerInfoFile.exists()) {
			// 获取opf文件
			final ContainerFileReader reader = new ContainerFileReader();
			if (!reader.readQuietly(containerInfoFile)) {
				throw new BookReadingException("opfFileNotFound", oebFile);
			}

			final String opfPath = reader.getRootPath();
			if (opfPath != null) {
				return GBFile.createFile(oebFile, opfPath);
			}
		}
		// 如果从container文件获取opf文件信息失败直接从压缩包获取opf文件
		for (GBFile child : oebFile.children()) {
			if (child.getExtension().equals("opf")) {
				return child;
			}
		}
		throw new BookReadingException("opfFileNotFound", oebFile);
	}

	@Override
	public void readMetaInfo(Book book) throws BookReadingException {
		new OEBMetaInfoReader(book).readMetaInfo(getOpfFile(book.File));
	}

	@Override
	public void readModel(BookModel model, GBTextPosition lastPosition)
			throws BookReadingException {
		model.Book.File.setCached(true);
		OebBookReader = new OEBBookReader(model);
		CopyVersionInfo info = model.Book.getmCopyVersionInfo();
		if (info != null)
			OebBookReader.readBook(getOpfFile(model.Book.File), lastPosition,
					info);
		else
			OebBookReader.readBook(getOpfFile(model.Book.File), lastPosition);
	}

	public OEBBookReader OebBookReader = null;

	@Override
	public GBImage readCover(GBFile file) {
		try {
			return new OEBCoverReader().readCover(getOpfFile(file));
		} catch (BookReadingException e) {
			return null;
		}
	}

	@Override
	public String readAnnotation(GBFile file) {
		try {
			return new OEBAnnotationReader().readAnnotation(getOpfFile(file));
		} catch (BookReadingException e) {
			return null;
		}
	}

	@Override
	public AutoEncodingCollection supportedEncodings() {
		return new AutoEncodingCollection();
	}

	@Override
	public void detectLanguageAndEncoding(Book book) {
		book.setEncoding("auto");
	}

	@Override
	public void readModel(GBTextPosition lastPosition,
						  IFunction<Integer> function) throws BookReadingException {
		if (null != OebBookReader) {
			OebBookReader.readBookByChpFileIndex(
					lastPosition.getChpFileIndex(), function);
		}

	}

	public void stopReadMode() {
		if (null != OebBookReader) {
			OebBookReader.stopRead(true);
		}
	}

	@Override
	public boolean isLoadChp(int chpFileIndex) {
		boolean flag = OebBookReader.isLoadChp(chpFileIndex);
		// L.e("OEBPlugin", chpFileIndex + "isLoadChp " + flag);
		return flag;
	}

	@Override
	public void startBuildCache() {

		if (null != OebBookReader) {
			OebBookReader.startBuildCache();
		}
	}

	@Override
	public void getReadProgress(IFunction<Integer> handler) {
		OebBookReader.setmProgressHander(handler);

	}
}
