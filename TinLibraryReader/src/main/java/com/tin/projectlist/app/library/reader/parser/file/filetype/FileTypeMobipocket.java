package com.tin.projectlist.app.library.reader.parser.file.filetype;


import com.tin.projectlist.app.library.reader.parser.common.util.MimeType;
import com.tin.projectlist.app.library.reader.parser.file.GBFile;

import java.util.List;

class FileTypeMobipocket extends FileTypePalm {
	FileTypeMobipocket() {
		super("Mobipocket", "BOOKMOBI");
	}

	@Override
	public boolean checkFile(GBFile file) {
		return "mobi".equalsIgnoreCase(file.getExtension()) || super.checkFile(file);
	}

	/*
	@Override
	public String extension() {
		return "mobi";
	}
	*/

	@Override
	public List<MimeType> mimeTypes() {
		return MimeType.TYPES_MOBIPOCKET;
	}

	@Override
	public MimeType mimeType(GBFile file) {
		return checkFile(file) ? MimeType.APP_MOBIPOCKET : MimeType.NULL;
	}
}
