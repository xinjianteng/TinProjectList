package com.tin.projectlist.app.library.reader.view.img;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.tin.projectlist.app.library.reader.parser.file.image.GBImageData;
import com.tin.projectlist.app.library.reader.parser.object.GBSize;
import com.tin.projectlist.app.library.reader.parser.view.PageEnum;

//import com.core.file.image.GBImageData;
//import com.core.object.GBSize;
//import com.core.view.PageEnum;
//import com.core.view.PageEnum.ImgFitType;

/**
 * 类名： GBAndroidImageData.java<br>
 * 描述： 图片数据封装<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-26<br>
 * 版本：  <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public abstract class GBAndroidImageData implements GBImageData {
	private Bitmap myBitmap;
	private int myRealWidth;
	private int myRealHeight;
	private GBSize myLastRequestedSize = null;
	private PageEnum.ImgFitType myLastRequestedScaling = PageEnum.ImgFitType.ORIGINAL;

	protected GBAndroidImageData() {
	}

	protected abstract Bitmap decodeWithOptions(BitmapFactory.Options options);

	public Bitmap getFullSizeBitmap() {
		return getBitmap(null, PageEnum.ImgFitType.ORIGINAL);
	}

	public Bitmap getBitmap(int maxWidth, int maxHeight) {
		return getBitmap(new GBSize(maxWidth, maxHeight), PageEnum.ImgFitType.MAX_FIT);
	}

	public synchronized Bitmap getBitmap(GBSize maxSize, PageEnum.ImgFitType scaling) {
		if (scaling != PageEnum.ImgFitType.ORIGINAL) {
			if (maxSize == null || maxSize.mWidth <= 0 || maxSize.mHeight <= 0) {
				return null;
			}
		}
		if (maxSize == null) {
			maxSize = new GBSize(-1, -1);
		}
		if (!maxSize.equals(myLastRequestedSize) || scaling != myLastRequestedScaling) {
			myLastRequestedSize = maxSize;
			myLastRequestedScaling = scaling;

			if (myBitmap != null) {
				myBitmap.recycle();
				myBitmap = null;
			}
			try {
				final BitmapFactory.Options options = new BitmapFactory.Options();
				if (myRealWidth <= 0) {
					options.inJustDecodeBounds = true;
					decodeWithOptions(options);
					myRealWidth = options.outWidth;
					myRealHeight = options.outHeight;
				}
				options.inJustDecodeBounds = false;
				int coefficient = 1;
				if (scaling == PageEnum.ImgFitType.AUTO_FIT) {
					if (myRealHeight > maxSize.mHeight || myRealWidth > maxSize.mWidth) {
						coefficient = 1 + Math.max(
								(myRealHeight - 1) / maxSize.mHeight,
								(myRealWidth - 1) / maxSize.mWidth
						);
					}
				}
				options.inSampleSize = coefficient;
				myBitmap = decodeWithOptions(options);
				if (myBitmap != null) {
					switch (scaling) {
						case ORIGINAL:
							break;
						case MAX_FIT:
						{
							final int bWidth = myBitmap.getWidth();
							final int bHeight = myBitmap.getHeight();
							if (bWidth > 0 && bHeight > 0 &&
									bWidth != maxSize.mWidth && bHeight != maxSize.mHeight) {
								final int w, h;
								if (bWidth * maxSize.mHeight > bHeight * maxSize.mWidth) {
									w = maxSize.mWidth;
									h = Math.max(1, bHeight * w / bWidth);
								} else {
									h = maxSize.mHeight;
									w = Math.max(1, bWidth * h / bHeight);
								}
								final Bitmap scaled =
										Bitmap.createScaledBitmap(myBitmap, w, h, false);
								if (scaled != null) {
									myBitmap = scaled;
								}
							}
							break;
						}
						case AUTO_FIT:
						{
							final int bWidth = myBitmap.getWidth();
							final int bHeight = myBitmap.getHeight();
							if (bWidth > 0 && bHeight > 0 &&
									(bWidth > maxSize.mWidth || bHeight > maxSize.mHeight)) {
								final int w, h;
								if (bWidth * maxSize.mHeight > bHeight * maxSize.mWidth) {
									w = maxSize.mWidth;
									h = Math.max(1, bHeight * w / bWidth);
								} else {
									h = maxSize.mHeight;
									w = Math.max(1, bWidth * h / bHeight);
								}
								final Bitmap scaled =
										Bitmap.createScaledBitmap(myBitmap, w, h, false);
								if (scaled != null) {
									myBitmap = scaled;
								}
							}
							break;
						}
					}
				}
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
			}
		}
		return myBitmap;
	}
}
