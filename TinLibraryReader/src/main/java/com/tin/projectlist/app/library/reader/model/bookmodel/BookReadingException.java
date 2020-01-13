package com.tin.projectlist.app.library.reader.model.bookmodel;

import java.io.IOException;

import com.tin.projectlist.app.library.reader.parser.common.GBResource;
import com.tin.projectlist.app.library.reader.parser.file.GBFile;
import com.tin.projectlist.app.library.reader.parser.zip.ZipException;

public final class BookReadingException extends Exception {
	public static void throwForFile(String resourceId, GBFile file) throws BookReadingException {
		throw new BookReadingException(resourceId, file);
	}

	private static String getResourceText(String resourceId) {
		return GBResource.resource("bookReadingException").getResource(resourceId).getValue();
	}

	public final GBFile File;

	public BookReadingException(String resourceId, String param, GBFile file) {
		super(getResourceText(resourceId).replace("%s", param));
		File = file;
	}

	public BookReadingException(String resourceId, GBFile file) {
		super(getResourceText(resourceId).replace("%s", file.getPath()));
		File = file;
	}

	public BookReadingException(Exception e, GBFile file) {
		super(getResourceText(
				e instanceof ZipException ? "errorReadingZip" : "errorReadingFile"
		).replace("%s", file.getPath()), e);
		File = file;
	}
}
