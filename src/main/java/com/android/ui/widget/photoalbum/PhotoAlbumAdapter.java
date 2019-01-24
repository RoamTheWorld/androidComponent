/**   
 * @Title: PhotoAlbumAdapter.java 
 * @Package com.up72.wemall.adapter 
 * @author imhzwen@gmail.com   
 * @date 2014-9-17 下午5:46:26 
 * @version V1.0
 * @encoding UTF-8   
 */
package com.android.ui.widget.photoalbum;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap.Config;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import com.android.master.R;
import com.android.ui.widget.NetworkImageView.DisplayMethod;
import com.android.ui.widget.NetworkImageView.LoadMethod;
import com.android.ui.widget.zoom.ZoomView;
import com.android.ui.widget.zoom.ZoomViewAttacher;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

/**
 * 
 * 相册的adpater
 *
 * @author wangyang
 * @version 1.0.0
 * @create 2014-12-1
 */
public class PhotoAlbumAdapter extends PagerAdapter {

	ZoomViewAttacher.OnClickOneListener listener;
	private List<String> imagePaths;

	public PhotoAlbumAdapter(ZoomViewAttacher.OnClickOneListener listener, Context mContext, List<String> imagePaths) {
		this.listener = listener;
		this.imagePaths = imagePaths;
	}

	@Override
	public int getCount() {
		return imagePaths == null ? 0 : imagePaths.size();
	}

	@Override
	public View instantiateItem(ViewGroup container, int position) {
		String imagePath = imagePaths.get(position);
		ZoomView photoView = new ZoomView(container.getContext());
		photoView.setDefalutImageRes(R.drawable.empty_photo);
		photoView.setOnClickOneListener(listener);
		photoView.setDisplayMethod(DisplayMethod.Rectangle);
		photoView.setLoadMethod(LoadMethod.Universal);
		photoView.setImageUrl(imagePath,Config.ARGB_8888,ImageScaleType.NONE); 
		container.addView(photoView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		return photoView;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}
}
