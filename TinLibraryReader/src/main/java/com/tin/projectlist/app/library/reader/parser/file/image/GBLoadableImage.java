package com.tin.projectlist.app.library.reader.parser.file.image;

import com.core.common.util.MimeType;

public abstract class GBLoadableImage extends GBSingleImage {
	private volatile boolean myIsSynchronized;

	public GBLoadableImage(MimeType mimeType) {
		super(mimeType);
	}

	public final boolean isSynchronized() {
		return myIsSynchronized;
	}

	protected final void setSynchronized() {
		myIsSynchronized = true;
	}

	public void startSynchronization(Runnable postSynchronizationAction) {
		GBImageManager.Instance().startImageLoading(this, postSynchronizationAction);
	}

	public static interface SourceType {
		int DISK = 0;
		int NETWORK = 1;
	};
	public abstract int sourceType();

	public abstract void synchronize();
	public abstract void synchronizeFast();
	public abstract String getId();
}
