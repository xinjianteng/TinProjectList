package com.core.file.filetype;

import com.core.common.util.MimeType;
import com.core.file.GBFile;

import java.util.ArrayList;
import java.util.List;

class FileTypeFB2 extends FileType {
	FileTypeFB2() {
		super("fb2");
	}

	@Override
	public boolean checkFile(GBFile file) {
		final String lName = file.getShortName().toLowerCase();
		return lName.endsWith(".fb2") || lName.endsWith(".fb2.zip");
	}

	/*
	@Override
	public String extension() {
		return "fb2";
	}
	*/

	private final List<MimeType> myMimeTypes = new ArrayList<MimeType>();

	@Override
	public List<MimeType> mimeTypes() {
		if (myMimeTypes.isEmpty()) {
			myMimeTypes.addAll(MimeType.TYPES_FB2);
			myMimeTypes.addAll(MimeType.TYPES_FB2_ZIP);
		}
		return myMimeTypes;
	}

	@Override
	public MimeType mimeType(GBFile file) {
		final String lName = file.getShortName().toLowerCase();
		if (lName.endsWith(".fb2")) {
			return MimeType.TEXT_FB2;
		} else if (lName.endsWith(".fb2.zip")) {
			return MimeType.APP_FB2_ZIP;
		} else {
			return MimeType.NULL;
		}
	}

	@Override
	public MimeType simplifiedMimeType(GBFile file) {
		final String lName = file.getShortName().toLowerCase();
		if (lName.endsWith(".fb2")) {
			return MimeType.TEXT_XML;
		} else if (lName.endsWith(".fb2.zip")) {
			return MimeType.APP_ZIP;
		} else {
			return MimeType.NULL;
		}
	}
}
