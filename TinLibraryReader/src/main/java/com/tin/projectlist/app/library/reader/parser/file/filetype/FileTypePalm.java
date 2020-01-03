package com.tin.projectlist.app.library.reader.parser.file.filetype;

import com.core.file.GBFile;
import com.core.option.GBStringOption;

import java.io.IOException;
import java.io.InputStream;

abstract class FileTypePalm extends FileType {
	private static String palmFileType(final GBFile file) {
		// TODO: use database instead of option (?)
		final GBStringOption palmTypeOption = new GBStringOption(file.getPath(), "PalmType", "");
		String palmType = palmTypeOption.getValue();
		if (palmType.length() != 8) {
			byte[] id = new byte[8];
			try {
				final InputStream stream = file.getInputStream();
				if (stream == null) {
					return null;
				}
				stream.skip(60);
				stream.read(id);
				stream.close();
			} catch (IOException e) {
			}
			palmType = new String(id).intern();
			palmTypeOption.setValue(palmType);
		}
		return palmType.intern();
	}

	private final String myPalmId;

	FileTypePalm(String id, String palmId) {
		super(id);
		myPalmId = palmId;
	}

	@Override
	public boolean checkFile(GBFile file) {
		final String extension = file.getExtension();
		return
				("pdb".equalsIgnoreCase(extension) || "prc".equalsIgnoreCase(extension)) &&
						myPalmId.equals(palmFileType(file));
	}

	/*
	@Override
	public String extension() {
		return "pdb";
	}
	*/
}
