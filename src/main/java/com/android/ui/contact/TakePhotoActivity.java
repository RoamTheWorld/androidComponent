package com.android.ui.contact;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.android.master.R;
import com.android.ui.BaseActivity;
import com.android.utils.Constants;
import com.android.utils.FileUtil;
import com.android.utils.image.ImageUtil;

/**
 * 
 * 
 * 描述 :选择图片,参考组件清单文件配置该Activity
 * 
 * @author wangyang
 * @version 1.0.0
 * @create 2013-8-2
 */
public class TakePhotoActivity extends BaseActivity {
	/** 从相册中选择的状态码 */
	private static final int FLAG_CHOOSE_IMG = 1;
	/** 拍照的状态码 */
	private static final int FLAG_CHOOSE_PHOTOGRAPH = 2;
	
	public static final String IMAGE_PATH = "path";
	/**
	 * 图片本地地址
	 */
	private String path;
	private File imageFile;
	
	/**
	 * 拍照
	 */
	private Button ibTake;
	/**
	 * 取消
	 */
	private Button ibCancel;
	/**
	 * 从相册中选择
	 */
	private Button ibFromAlbum;

	/**
	 * 从相册中选
	 */
	private void doGoToImg() {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_PICK);
		intent.setType("image/*");
		startActivityForResult(intent, FLAG_CHOOSE_IMG);
	}

	/**
	 * 拍照
	 */
	private void doTakePhoto() {
		File saveDir = FileUtil.getFileDir(this, Constants.FILE_ROOT_DIRECTORY + "/camera");
		Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		imageFile = new File(saveDir, System.currentTimeMillis()+".png");
		path = imageFile.getAbsolutePath();
		Uri uri = Uri.fromFile(imageFile);
		intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		startActivityForResult(intent, FLAG_CHOOSE_PHOTOGRAPH);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == FLAG_CHOOSE_IMG && resultCode == RESULT_OK) {
			if (data != null) {
				Uri uri = data.getData();
				if (uri != null && uri.getAuthority() != null && !TextUtils.isEmpty(uri.getAuthority())) {
					Cursor cursor = getContentResolver().query(uri, new String[] { MediaStore.Images.Media.DATA }, null, null, null);
					if (null == cursor) {
						Toast.makeText(this, "图片没找到", Toast.LENGTH_SHORT).show();
						return;
					}
					cursor.moveToFirst();
					path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
					cursor.close();
				} else {
					path = uri.getPath();
				}
			}
			path = copyPicture2Cache(path);
			Intent intent = new Intent();
			intent.putExtra(IMAGE_PATH, path);
			setResult(RESULT_OK, intent);
		} else if (requestCode == FLAG_CHOOSE_PHOTOGRAPH && resultCode == RESULT_OK) {
			if (imageFile != null && imageFile.exists()) {
				String imageTakePath = ImageUtil.handleCameraImage(this, imageFile.getAbsolutePath(), getImageFileDir());
				imageFile = null;
				path = null;
				Intent intent = new Intent();
				intent.putExtra(IMAGE_PATH,  imageTakePath);
				setResult(RESULT_OK, intent);
			}
		}
		finish();
	}
	
	protected String copyPicture2Cache(String path) {
		String destPath = path;
		try {
			FileInputStream in = new FileInputStream(path);
			File saveFile = new File(FileUtil.getFileDir(this, getImageFileDir()), path.substring(path.lastIndexOf("/")+1, path.length()));
			FileOutputStream out = new FileOutputStream(saveFile);
			int len = 0;
			byte[] arr = new byte[1024];
			while ((len = in.read(arr)) != -1) {
				out.write(arr, 0, len);
			}
			in.close();
			out.close();
			destPath = saveFile.getAbsolutePath();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return destPath;
	}

	protected String getImageFileDir() {
		return Constants.FILE_ROOT_DIRECTORY + "/"+Constants.FILE_IMAGE_DIRECTORY;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		finish();
		return true;
	}

	@Override
	protected void findViewById() {
		ibFromAlbum = (Button) findViewById(R.id.ib_change_photo_fromalbum);
		ibTake = (Button) findViewById(R.id.ib_change_photo_take);
		ibCancel = (Button) findViewById(R.id.ib_change_photo_cancel);
	}

	@Override
	protected void setListeners() {
		ibCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		ibTake.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				doTakePhoto();
			}
		});
		ibFromAlbum.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				doGoToImg();
			}
		});
	}

	@Override
	protected void init() {
	}

	@Override
	protected int getContentViewId() {
		return R.layout.change_photo;
	}

	@Override
	public void startActivity(Class clazz, boolean isfinsh) {

	}
}
