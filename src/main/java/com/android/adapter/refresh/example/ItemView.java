package com.android.adapter.refresh.example;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.android.adapter.refresh.BaseAdapterView;
import com.android.master.R;

/**
 * 使用时 放开注释
 * 
 * @author wangyang 2014-7-30 上午9:45:03
 */
public class ItemView extends BaseAdapterView<News> {

	private ViewHolder holder;

	public ItemView(Context context) {
		super(context);
	}

	@Override
	public void bindData(News t,int position,boolean isLoadImage) {
		System.out.println("asdasdasd========"+t);
		holder = (ViewHolder) getTag();
		if (holder == null) {
			holder = new ViewHolder();
			View view = View.inflate(getContext(), R.layout.examle_adapter_refresh_list_item, null);
			holder.titleTv = (TextView) view.findViewById(R.id.tv_title);
			holder.summaryTv = (TextView) view.findViewById(R.id.tv_summary);
			holder.iconTv = (TextView) view.findViewById(R.id.tv_icon);
			setTag(holder);
			setView(view);
		}
		holder.titleTv.setText(t.getTitle());
		holder.summaryTv.setText(t.getSummary());
		holder.iconTv.setText(t.getIcon());
	}

	class ViewHolder {
		TextView titleTv, summaryTv,iconTv;
	}

}
