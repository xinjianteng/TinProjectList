package com.tin.projectlist.app.library.reader.view.img;

import android.os.Handler;
import android.os.Message;


import com.tin.projectlist.app.library.reader.parser.file.image.GBLoadableImage;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

class GBAndroidImageLoader {
	void startImageLoading(final GBLoadableImage image, Runnable postLoadingRunnable) {
		LinkedList<Runnable> runnables = myOnImageSyncRunnables.get(image.getId());
		if (runnables != null) {
			if (!runnables.contains(postLoadingRunnable)) {
				runnables.add(postLoadingRunnable);
			}
			return;
		}

		runnables = new LinkedList<Runnable>();
		runnables.add(postLoadingRunnable);
		myOnImageSyncRunnables.put(image.getId(), runnables);

		final ExecutorService pool =
				image.sourceType() == GBLoadableImage.SourceType.DISK
						? mySinglePool : myPool;
		pool.execute(new Runnable() {
			public void run() {
				image.synchronize();
				myImageSynchronizedHandler.fireMessage(image.getId());
			}
		});
	}

	private static class MinPriorityThreadFactory implements ThreadFactory {
		private final ThreadFactory myDefaultThreadFactory = Executors.defaultThreadFactory();

		public Thread newThread(Runnable r) {
			final Thread th = myDefaultThreadFactory.newThread(r);
			th.setPriority(Thread.MIN_PRIORITY);
			return th;
		}
	}

	private static final int IMAGE_LOADING_THREADS_NUMBER = 3; // TODO: how many threads ???

	private final ExecutorService myPool = Executors.newFixedThreadPool(IMAGE_LOADING_THREADS_NUMBER, new MinPriorityThreadFactory());
	private final ExecutorService mySinglePool = Executors.newFixedThreadPool(1, new MinPriorityThreadFactory());

	private final HashMap<String, LinkedList<Runnable>> myOnImageSyncRunnables = new HashMap<String, LinkedList<Runnable>>();

	private class ImageSynchronizedHandler extends Handler {
		@Override
		public void handleMessage(Message message) {
			final String imageUrl = (String) message.obj;
			final LinkedList<Runnable> runables = myOnImageSyncRunnables.remove(imageUrl);
			for (Runnable runnable: runables) {
				runnable.run();
			}
		}

		public void fireMessage(String imageUrl) {
			sendMessage(obtainMessage(0, imageUrl));
		}
	};

	private final ImageSynchronizedHandler myImageSynchronizedHandler = new ImageSynchronizedHandler();
}
