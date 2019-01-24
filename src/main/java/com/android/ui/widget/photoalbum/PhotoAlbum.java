package com.android.ui.widget.photoalbum;

import java.util.List;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.android.master.R;
import com.android.ui.widget.zoom.ZoomViewAttacher;

/**
 * 
 * @author wangyang
 * @version 1.0.0
 * @create 2014-12-1
 */
public class PhotoAlbum {

	private static final String TAG = "PhotoAlbum";

	private Context mContext;
	private List<String> imagePaths;
	private ZoomViewAttacher.OnClickOneListener listener;
	private ViewPager pager;
	private View view;

	private Integer currItem;

	public PhotoAlbum(Context context, ZoomViewAttacher.OnClickOneListener listener, List<String> imagePaths) {
		this(context, listener, imagePaths, null, null);
	}

	public PhotoAlbum(Context context, ZoomViewAttacher.OnClickOneListener listener, List<String> imagePaths, Integer currItem) {
		this(context, listener, imagePaths, null, currItem);
	}

	public PhotoAlbum(Context context, ZoomViewAttacher.OnClickOneListener listener, List<String> imagePaths, View view, Integer currItem) {
		super();
		this.mContext = context;
		this.listener = listener;
		this.imagePaths = imagePaths;

		if (currItem != null) {
			this.currItem = currItem;
		} else {
			this.currItem = 0;
		}

		this.view = view;

	}

	public View getView() {

		if (view == null) {
			view = View.inflate(this.mContext, R.layout.layout_photo_album, null);
		}

		pager = (ViewPager) view.findViewById(R.id.vp_pager);
		pager.setAdapter(new PhotoAlbumAdapter(listener, mContext, imagePaths));

		pager.setOffscreenPageLimit(imagePaths.size());
		pager.setCurrentItem(currItem);

		return view;
	}

}
