package com.tin.projectlist.app.library.reader.model.parser.oeb;

import com.core.file.GBFile;
import com.core.file.image.GBImageProxy;
import com.core.file.image.GBSingleImage;

/*
 * epub图书图片加载
 */
class OEBCoverReader {
	private static class OEBCoverImage extends GBImageProxy {
		private final GBFile myFile;

		OEBCoverImage(GBFile file) {
			myFile = file;
		}

		@Override
		public GBSingleImage getRealImage() {
			return new OEBCoverBackgroundReader().readCover(myFile);
		}

		@Override
		public int sourceType() {
			return SourceType.DISK;
		}

		@Override
		public String getId() {
			return myFile.getPath();
		}
	}

	public GBImageProxy readCover(GBFile file) {
		return new OEBCoverImage(file);
	}
}
