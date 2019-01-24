/*
 * Copyright (C) 2011 Patrik �kerfeldt
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.ui.guide;

import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.android.master.R;
import com.android.ui.widget.NetworkImageView;

/**
 * 
 * 引导页图片的adapter 需要写到你自己的项目中
 * Copyright: 版权所有 (c) 2002 - 2003
 *
 */
public class ImageAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Object[] mImages;

	public ImageAdapter(Context context, Object[] images) {
		this.mImages = images;
		mInflater = LayoutInflater.from(context);
		// mInflater = (LayoutInflater)
		// context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return mImages.length;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.image_item, null);
		}

		// Options op = new Options();
		// op.inSampleSize = 2;

		// Bitmap bm = BitmapFactory.decodeResource(context.getResources(),
		// ids[position],op);
		// Bitmap bm = readBitMap(context,
		// Integer.parseInt(mImages[position].toString()));
		// Drawable bd = new BitmapDrawable(context.getResources(),bm);
		NetworkImageView imageView = (NetworkImageView) convertView.findViewById(R.id.imgView);
		if (mImages[position].toString().contains("http://")) {
			imageView.setImageUrl(mImages[position].toString());
		} else {
			imageView.setImageResource(Integer.parseInt(mImages[position].toString()));
		}

		// ((ImageView)
		// convertView.findViewById(R.id.imgView)).setImageBitmap(bm);//
		// ImageResource(ids[position]);
		// ((ImageView)
		// convertView.findViewById(R.id.imgView)).setImageResource(mImages[position]);
		// bm = null;
		return convertView;
	}

	public static Bitmap readBitMap(Context context, int resId) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		opt.inSampleSize = computeSampleSize(opt, -1, 128 * 128); // 计算出图片使用的inSampleSize
		opt.inJustDecodeBounds = false;
		// 获取资源图片
		InputStream is = context.getResources().openRawResource(resId);
		return BitmapFactory.decodeStream(is, null, opt);
	}

	public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);

		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}

		return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;

		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));

		if (upperBound < lowerBound) {
			// return the larger one when there is no overlapping zone.
			return lowerBound;
		}

		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}
}
