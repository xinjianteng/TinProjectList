package com.core.file.filetype;

import com.core.common.util.MimeType;
import com.core.file.GBFile;

import java.util.List;

class FileTypeDjVu extends FileType {
	FileTypeDjVu() {
		super("DjVu");
	}

	@Override
	public boolean checkFile(GBFile file) {
		final String extension = file.getExtension();
		return "djvu".equalsIgnoreCase(extension) || "djv".equalsIgnoreCase(extension);
	}

	/*
	@Override
	public String extension() {
		return "djvu";
	}
	*/

	@Override
	public List<MimeType> mimeTypes() {
		return MimeType.TYPES_DJVU;
	}

	@Override
	public MimeType mimeType(GBFile file) {
		return checkFile(file) ? MimeType.IMAGE_VND_DJVU : MimeType.NULL;
	}
}
