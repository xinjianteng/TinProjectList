package com.tin.projectlist.app.library.reader.parser.file.filetype;


import com.tin.projectlist.app.library.reader.parser.common.util.MimeType;
import com.tin.projectlist.app.library.reader.parser.file.GBFile;

import java.util.List;

class FileTypeHtml extends FileType {
	FileTypeHtml() {
		super("HTML");
	}

	@Override
	public boolean checkFile(GBFile file) {
		final String extension = file.getExtension().toLowerCase();
		return extension.endsWith("html") || "htm".equals(extension);
	}

	/*
	@Override
	public String extension() {
		return "html";
	}
	*/

	@Override
	public List<MimeType> mimeTypes() {
		return MimeType.TYPES_HTML;
	}

	@Override
	public MimeType mimeType(GBFile file) {
		return checkFile(file) ? MimeType.TEXT_HTML : MimeType.NULL;
	}
}
