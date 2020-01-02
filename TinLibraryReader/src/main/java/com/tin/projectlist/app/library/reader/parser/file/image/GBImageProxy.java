package com.core.file.image;

import com.core.common.util.MimeType;

import java.io.InputStream;

public abstract class GBImageProxy extends GBLoadableImage {
	private GBSingleImage myImage;

	public GBImageProxy(MimeType mimeType) {
		super(mimeType);
	}

	public GBImageProxy() {
		this(MimeType.IMAGE_AUTO);
	}

	public abstract GBSingleImage getRealImage();

	public String getURI() {
		final GBImage image = getRealImage();
		return image != null ? image.getURI() : "image proxy";
	}

	@Override
	public final InputStream inputStream() {
		return myImage != null ? myImage.inputStream() : null;
	}

	@Override
	public final synchronized void synchronize() {
		myImage = getRealImage();
		setSynchronized();
	}

	@Override
	public final void synchronizeFast() {
		setSynchronized();
	}
}
