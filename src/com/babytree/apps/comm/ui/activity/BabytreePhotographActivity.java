package com.babytree.apps.comm.ui.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.MenuItem;

import com.babytree.apps.comm.bitmap.BabytreeBitmapCache;
import com.babytree.apps.comm.tools.BabytreeLog;
import com.babytree.apps.comm.util.BabytreeUtil;
import com.babytree.apps.comm.util.FolderOper;

/**
 * 拍照activity
 * 
 * @author wangbingqi
 * 
 */
public abstract class BabytreePhotographActivity extends BabytreeTitleAcitivty {

	/**
	 * 相机
	 */
	private final int PHOTO_CAMERA = 2;
	/**
	 * 相册
	 */
	private final int PHOTO_ALBUM = 1;

	/**
	 * 系统相册activity request id 3001
	 */
	private static final int REQUEST_PHOTO_LIBRARY = 3001;

	/**
	 * 手机拍照activity request id 3000;
	 */
	private static final int CAMERA_WITH_DATA = 3000;

	/**
	 * 旋转图片activity request id 3002
	 */
	private static final int REQUEST_PHOTO_CHANGE = 3002;

	/**
	 * 图片文件
	 */
	private File mImageFile;
	/**
	 * 图片缓存
	 */
	private Bitmap mBitmap;

	/**
	 * 图片宽度
	 */
	private int mBitmapW;
	/**
	 * 图片高度
	 */
	private int mBitmapH;

	/**
	 * 图片质量,默认是80,最大值100
	 */
	private int quality = 80;

	/**
	 * 照片缓存图片的据对路径
	 */
	protected String mBitmapPath = "";

	/**
	 * 图片缓存成功
	 */
	private final int CACHE_SUCCESS = 0;

	/**
	 * 图片缓存失败
	 */
	private final int CACHE_FAIL = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mBitmapPath = BabytreeBitmapCache.getAppCacheDirectory(this) + "/tmpBitmap.jpg";

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case PHOTO_ALBUM:
			// 系统相册
			selectPhoto();
			break;
		case PHOTO_CAMERA:
			// 手机拍照
			takePhoto();
			break;
		default:
			break;
		}

		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case REQUEST_PHOTO_LIBRARY:
			// 系统相册
			if (resultCode == RESULT_CANCELED) {
				break;
			}
			try {
				Uri uri = data.getData();
				setImageForUri(uri);
				Intent i = new Intent();
				i.putExtra("img_url", mImageFile.getPath());
				i.setClass(getApplicationContext(), BabytreeModifyActivity.class);
				startActivityForResult(i, REQUEST_PHOTO_CHANGE);
			} catch (Exception e) {
				BabytreeLog.e("data = null");
			}

			break;
		case REQUEST_PHOTO_CHANGE:
			// 旋转图片
			if (data != null) {
				int rotate = data.getIntExtra("img_rotate", 0);
				setBitmap(mImageFile.getPath(), rotate);
			} else {
				Message msg = new Message();
				msg.what = CACHE_FAIL;
				photoHandler.sendMessage(msg);
			}
			break;
		case CAMERA_WITH_DATA:
			// 系统拍照
			if (resultCode == RESULT_CANCELED) {
				break;
			}
			try {
				Intent i2 = new Intent();
				i2.putExtra("img_url", mImageFile.getPath());
				i2.setClass(getApplicationContext(), BabytreeModifyActivity.class);
				startActivityForResult(i2, REQUEST_PHOTO_CHANGE);
			} catch (Exception e) {
				finish();
			}

			break;
		default:
			break;
		}

	}

	/**
	 * 获取缓存图片
	 */
	protected abstract void getBitmap(Bitmap bitmap);

	private void setImageForUri(Uri uri) {
		File fromFile = null;
		if (uri.toString().toLowerCase(Locale.getDefault()).startsWith("file://")) {
			fromFile = new File(uri.toString().substring(7));
		} else {
			String[] proj = { MediaStore.Images.Media.DATA };
			Cursor cursor = managedQuery(uri, proj, null, null, null);
			int actual = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			String filePath = cursor.getString(actual);
			fromFile = new File(filePath);
		}
		if (FolderOper.isExistSdcard(this)) {
			File toFile = new File(BabytreeBitmapCache.getAppCacheDirectory(this), getPhotoFilename(new Date()));
			BabytreeUtil.copyFile(fromFile, toFile, true);
			mImageFile = toFile;
		}
	}

	/**
	 * 显示view
	 * 
	 * @param popView
	 * @param w
	 *            最终需求图片宽度
	 * @param h
	 *            最终需求图片高度
	 */
	protected void showPhotoMenu(int w, int h) {
		// view.showContextMenu();
		String[] str = { "手机相册", "系统拍照", "取消" };
		showAlertItemDialog("", str, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
					// 系统相册
					selectPhoto();
					break;
				case 1:
					// 手机拍照
					takePhoto();
					break;
				case 2:

					break;
				default:
					break;
				}
				dismissLoadingDialog();
			}
		});
		this.mBitmapW = w;
		this.mBitmapH = h;
	}

	/**
	 * 显示view并且指定路径
	 * 
	 * @param w
	 * @param h
	 * @param url
	 */
	protected void showPhotoMenuSave(int w, int h, String url) {
		showPhotoMenu(w, h);
		this.mBitmapPath = url;
	}

	protected void showPhotoMenu(int w, int h, String other) {
		// view.showContextMenu();
		String[] str = { "手机相册", "系统拍照", other, "取消" };

		showAlertItemDialog("", str, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
					// 系统相册
					selectPhoto();
					break;
				case 1:
					// 手机拍照
					takePhoto();
					break;
				case 2:
					otherdoing();
					break;
				default:
					break;
				}
				dismissLoadingDialog();
			}
		});
		this.mBitmapW = w;
		this.mBitmapH = h;
	}

	protected void showPhotoMenu(int w, int h, String other, String url) {
		// view.showContextMenu();
		String[] str = { "手机相册", "系统拍照", other, "取消" };

		showAlertItemDialog("", str, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
					// 系统相册
					selectPhoto();
					break;
				case 1:
					// 手机拍照
					takePhoto();
					break;
				case 2:
					otherdoing();
					break;
				default:
					break;
				}
				dismissLoadingDialog();
			}
		});
		this.mBitmapW = w;
		this.mBitmapH = h;
		this.mBitmapPath = url;
	}

	/**
	 * 弹出窗口其他选项 暂时这么写 以后要换成list
	 */
	public void otherdoing() {

	}

	/**
	 * 系统相册
	 */
	private void selectPhoto() {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(intent, REQUEST_PHOTO_LIBRARY);
	}

	/**
	 * 手机拍照
	 */
	private void takePhoto() {
		String filename = getPhotoFilename(new Date());
		if (FolderOper.isExistSdcard(this)) {
			mImageFile = new File(BabytreeBitmapCache.getAppCacheDirectory(this), filename);
			Intent intent = getTakePickIntent(mImageFile);
			startActivityForResult(intent, CAMERA_WITH_DATA);
		}
	}

	private Intent getTakePickIntent(File f) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
		return intent;
	}

	/**
	 * 读取图片文件名
	 * 
	 * @param date
	 * @return
	 */
	private String getPhotoFilename(Date date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddKms", Locale.CHINA);
		return dateFormat.format(date) + ".jpg";
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mImageFile != null) {
			mImageFile.deleteOnExit();
			mImageFile = null;
		}
		try {
			if (mBitmap != null) {
				mBitmap.recycle();
				mBitmap = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 设置bitmap
	 * 
	 * @param s
	 *            路径
	 * @param rotate
	 *            顺时针旋转角度
	 */
	private void setBitmap(String s, int rotate) {
		Message msg = new Message();
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(s, opts);
		// dada add 先导入缩放并导入内存
		opts.inSampleSize = BabytreeUtil.computeSampleSize(opts, -1, mBitmapW * mBitmapH);
		opts.inJustDecodeBounds = false;
		try {
			if (rotate != 0) {
				mBitmap = BabytreeUtil.rotationBitmap(BitmapFactory.decodeFile(s, opts), rotate);
			} else {
				mBitmap = BitmapFactory.decodeFile(s, opts);
			}
			// 压缩
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			mBitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
			// 存文件
			FileOutputStream tmpFox = new FileOutputStream(mBitmapPath);
			tmpFox.write(baos.toByteArray());
			tmpFox.close();
		} catch (Exception e) {
			e.printStackTrace();
			if (mBitmap != null) {
				mBitmap.recycle();
				mBitmap = null;
			}
			msg.what = CACHE_FAIL;
			photoHandler.sendMessage(msg);
		}
		msg.what = CACHE_SUCCESS;
		photoHandler.sendMessage(msg);
	}

	/**
	 * 设置图片质量
	 * 
	 * @param quality
	 */
	protected void setQuality(int quality) {
		if (quality >= 80 || quality <= 20) {
			return;
		} else {
			this.quality = quality;
		}
	}

	@SuppressLint("HandlerLeak")
	private Handler photoHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case CACHE_SUCCESS:
				getBitmap(mBitmap);
				break;
			case CACHE_FAIL:
				break;
			default:
				break;
			}
		}
	};
}
