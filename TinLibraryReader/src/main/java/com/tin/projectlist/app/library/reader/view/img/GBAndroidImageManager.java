package com.tin.projectlist.app.library.reader.view.img;

import com.tin.projectlist.app.library.reader.parser.common.util.MimeType;
import com.tin.projectlist.app.library.reader.parser.file.image.GBImage;
import com.tin.projectlist.app.library.reader.parser.file.image.GBImageManager;
import com.tin.projectlist.app.library.reader.parser.file.image.GBLoadableImage;
import com.tin.projectlist.app.library.reader.parser.file.image.GBSingleImage;

public final class GBAndroidImageManager extends GBImageManager {
	@Override
	public GBAndroidImageData getImageData(GBImage image) {
		if (image instanceof GBSingleImage) {
			final GBSingleImage singleImage = (GBSingleImage)image;
			if (MimeType.IMAGE_PALM.equals(singleImage.mimeType())) {
				return null;
			}
			return new InputStreamImageData(singleImage);
		} else {
			//TODO
			return null;
		}
	}

	private GBAndroidImageLoader myLoader;

	@Override
	protected void startImageLoading(GBLoadableImage image, Runnable postLoadingRunnable) {
		if (myLoader == null) {
			myLoader = new GBAndroidImageLoader();
		}
		myLoader.startImageLoading(image, postLoadingRunnable);
	}



}
