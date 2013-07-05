package com.babytree.apps.comm.util;

import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Handler;
import android.os.Message;

public class ImageCacheLoaderForBitmap {
	private Map<String, SoftReference<Bitmap>> imageCache = new HashMap<String, SoftReference<Bitmap>>();

	public Bitmap loadDrawableForBitmap(final String baseUrl, final String imageUrl, final Context context,
			final ImageCallbackForBitmap callback) {
		final String md5Url = Md5Util.md5(imageUrl);
		if (imageCache.containsKey(md5Url)) {
			SoftReference<Bitmap> softReference = imageCache.get(md5Url);
			if (softReference.get() != null) {
				return softReference.get();
			}
		}
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				callback.imageLoadedForBitmap((Bitmap) msg.obj, md5Url);
			}
		};

		if (!imageCache.containsKey(imageUrl)) {
			new Thread() {
				public void run() {
					Bitmap bp = loadImageFromUrl2Bt(baseUrl, imageUrl, context);
					imageCache.put(md5Url, new SoftReference<Bitmap>(bp));
					handler.sendMessage(handler.obtainMessage(0, bp));
				};
			}.start();
		}
		return null;
	}

	protected Bitmap loadImageFromUrl2Bt(String baseUrl, String imageUrl, Context context) {
		try {
			InputStream imageInput = context.getAssets().open(baseUrl + imageUrl);
			BitmapFactory.Options options = new Options();
			options.inSampleSize = 4;
			Bitmap bp = BitmapFactory.decodeStream(imageInput, null, options);
			imageInput.close();
			return bp;
		} catch (Exception e) {
			return null;
		}
	}

	public interface ImageCallbackForBitmap {
		public void imageLoadedForBitmap(Bitmap bp, String imageUrl);
	}
}
