package com.android.utils.image;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;

public class ImageListener implements ImageLoadingListener,ImageLoadingProgressListener{
		
	 	private ImageView view; 
	 	private int defaultImageResId; 
	 	private int errorImageResId;
	 	
		public ImageListener() {
			super();
		}

		public ImageListener(ImageView view, int defaultImageResId, int errorImageResId) {
			super();
			this.view = view;
			this.defaultImageResId = defaultImageResId;
			this.errorImageResId = errorImageResId;
		}

		/**
		 * Univesal加载开始
		 *
		 * @author wangyang
		 * 2015-12-7 下午2:36:04
		 * @param imageUri
		 * @param view
		 */
		@Override
		public void onLoadingStarted(String imageUri, View view) {}
		
		/**
		 * Univesal加载失败
		 *
		 * @author wangyang
		 * 2015-12-7 下午2:36:04
		 * @param imageUri
		 * @param view
		 */
		@Override
		public void onLoadingFailed(String imageUri, View view, FailReason failReason) {}

		/**
		 * Univesal加载完成
		 *
		 * @author wangyang
		 * 2015-12-7 下午2:36:04
		 * @param imageUri
		 * @param view
		 */
		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {}

		/**
		 * Univesal加载取消
		 *
		 * @author wangyang
		 * 2015-12-7 下午2:36:04
		 * @param imageUri
		 * @param view
		 */
		@Override
		public void onLoadingCancelled(String imageUri, View view) {}
		
		/**
		 * Univesal加载进度
		 * @author wangyang
		 * 2015-12-7 下午2:38:34
		 * @param imageUri
		 * @param view
		 * @param current
		 * @param total
		 */
		@Override
		public void onProgressUpdate(String imageUri, View view, int current, int total) {}
	}