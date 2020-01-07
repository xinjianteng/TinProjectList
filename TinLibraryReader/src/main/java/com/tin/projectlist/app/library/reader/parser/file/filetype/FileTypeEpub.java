package com.tin.projectlist.app.library.reader.parser.file.filetype;


import com.tin.projectlist.app.library.reader.parser.common.util.MimeType;
import com.tin.projectlist.app.library.reader.parser.file.GBFile;

import java.util.List;

class FileTypeEpub extends FileType {
	FileTypeEpub() {
		super("ePub");
	}

	@Override
	public boolean checkFile(GBFile file) {
		final String extension = file.getExtension();
		return
				"epub".equalsIgnoreCase(extension) ||
						"oebzip".equalsIgnoreCase(extension) ||
						"opf".equalsIgnoreCase(extension) ||
						"geb".equalsIgnoreCase(extension)&&"epub".equalsIgnoreCase(file.getExtendMapping());
	}

	/*
	@Override
	public String extension() {
		return "epub";
	}
	*/

	@Override
	public List<MimeType> mimeTypes() {
		return MimeType.TYPES_EPUB;
	}

	@Override
	public MimeType mimeType(GBFile file) {
		final String extension = file.getExtension();
		if ("epub".equalsIgnoreCase(extension)) {
			return MimeType.APP_EPUB_ZIP;
		}
		// TODO: process other extensions (?)
		return MimeType.NULL;
	}

	@Override
	public MimeType simplifiedMimeType(GBFile file) {
		final String extension = file.getExtension();
		if ("epub".equalsIgnoreCase(extension)) {
			return MimeType.APP_ZIP;
		}
		// TODO: process other extensions (?)
		return MimeType.NULL;
	}
}
