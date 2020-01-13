package com.tin.projectlist.app.library.reader.model.bookmodel;


import com.tin.projectlist.app.library.reader.model.book.Book;
import com.tin.projectlist.app.library.reader.parser.file.image.GBImage;
import com.tin.projectlist.app.library.reader.parser.text.model.GBTextModel;
import com.tin.projectlist.app.library.reader.parser.text.model.cache.CharStorage;

import java.util.HashMap;

abstract class BookModelImpl extends BookModel {
	protected CharStorage myInternalHyperlinks;  // 内部链接缓存
	protected final HashMap<String, GBImage> myImageMap = new HashMap<String,GBImage>();
	protected final HashMap<String, GBTextModel> myFootnotes = new HashMap<String,GBTextModel>();

	BookModelImpl(com.tin.projectlist.app.library.reader.model.book.Book book) {
		super(book);
	}

	public CharStorage getInternalHyperLinks(){
		return myInternalHyperlinks;
	}

	@Override
	protected Label getLabelInternal(String id) {
		final int len = id.length();
		final int size = myInternalHyperlinks.size();

		for (int i = 0; i < size; ++i) {
			final char[] block = myInternalHyperlinks.block(i);
			for (int offset = 0; offset < block.length; ) {
				final int labelLength = (int)block[offset++];
				if (labelLength == 0) {
					break;
				}
				final int idLength = (int)block[offset + labelLength];
				if ((labelLength != len) || !id.equals(new String(block, offset, labelLength))) {
					offset += labelLength + idLength + 3;
					continue;
				}
				offset += labelLength + 1;
				final String modelId = (idLength > 0) ? new String(block, offset, idLength) : null;
				offset += idLength;
				final int paragraphNumber = (int)block[offset] + (((int)block[offset + 1]) << 16);
				return new Label(modelId, myInternalHyperlinks.getChpFileNum(),paragraphNumber);
			}
		}
		return null;
	}

	public void addImage(String id, GBImage image) {
		myImageMap.put(id, image);
	}
}
