package com.android.ui.widget.pulltorefresh.example;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.ui.widget.SlideListView;

public class SlideListViewDemo extends Activity implements OnItemClickListener {

	private SlideListView mListView;
	private SlideAdapter adapter;
	private TextView textView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		adapter = new SlideAdapter();
		mListView.initSlideMode(SlideListView.MOD_RIGHT);
		mListView.setAdapter(adapter);
		mListView.setOnItemClickListener(this);
	}

	private class SlideAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return 0;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder = new ViewHolder();
			
			holder.delete2.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					// 锟絟锟斤拷锟斤拷锟斤拷
					Toast.makeText(SlideListViewDemo.this, "锟斤拷删锟斤拷" + position, Toast.LENGTH_SHORT).show();
					mListView.slideBack();
				}
			});
			holder.other2.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Toast.makeText(SlideListViewDemo.this, "锟斤拷锟斤拷锟斤拷" + position, Toast.LENGTH_SHORT).show();
					mListView.slideBack();
				}
			});
			if (convertView.getPaddingRight() != 0) {
				holder.com.setLayoutParams(new LayoutParams(0, LayoutParams.MATCH_PARENT, 1));
			} else {
				holder.com.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			}

			return convertView;
		}

	}

	private static class ViewHolder {
		public TextView title;
		public TextView time;
		public TextView content;
		public RelativeLayout delete2;
		public RelativeLayout other2;
		public RelativeLayout com;

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Toast.makeText(SlideListViewDemo.this, "锟斤拷锟�" + position, Toast.LENGTH_SHORT).show();
	}

}
