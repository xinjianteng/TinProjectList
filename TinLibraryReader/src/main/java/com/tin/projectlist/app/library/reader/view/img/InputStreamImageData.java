package com.tin.projectlist.app.library.reader.view.img;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;


import com.tin.projectlist.app.library.reader.parser.file.image.GBSingleImage;

import java.io.IOException;
import java.io.InputStream;

final class InputStreamImageData extends GBAndroidImageData {
	private final GBSingleImage myImage;

	InputStreamImageData(GBSingleImage image) {
		myImage = image;
	}

	protected Bitmap decodeWithOptions(BitmapFactory.Options options) {
		final InputStream stream = myImage.inputStream();
		if (stream == null) {
			return null;
		}

		final Bitmap bmp = BitmapFactory.decodeStream(stream, new Rect(), options);
		try {
			stream.close();
		} catch (IOException e) {
		}
		return bmp;
	}
}
