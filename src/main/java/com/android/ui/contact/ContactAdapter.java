package com.android.ui.contact;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.master.R;
import com.android.ui.widget.HeadImageView;
import com.android.utils.ChineseUtil;

public class ContactAdapter<T extends BaseUser> extends BaseAdapter {
	protected Context mContext;
	protected List<T> users;

	/**
	 * 构建适配器 数据如果为空 则使用系统联系人数据
	 * 
	 * @param context
	 *            必传
	 * @param list
	 *            可为空
	 */
	@Deprecated
	public ContactAdapter(Context context, List<T> users, boolean isChoice) {
		this(context, users);
	}

	public ContactAdapter(Context context, List<T> users) {
		this.mContext = context;
		if (users != null)
			Collections.sort(users);
		this.users = users;
	}

	@Override
	public int getCount() {
		return users == null ? 0 : users.size();
	}

	@Override
	public Object getItem(int position) {
		return users == null ? null : users.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressWarnings("unchecked")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ContactViewHolder holder = null;
		T t = users.get(position);
		if (convertView == null) {
			holder = getViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(getConvertViewId(), null);
			findViewById(convertView, holder);
			convertView.setTag(holder);
		} else {
			holder = (ContactViewHolder) convertView.getTag();
		}

		bindData(position, holder, t);

		return convertView;
	}

	protected ContactViewHolder getViewHolder() {
		return new ContactViewHolder();
	}

	protected int getConvertViewId() {
		return R.layout.contact_list_item_layout;
	}

	protected void findViewById(View convertView, ContactViewHolder holder) {
		View view = convertView.findViewById(R.id.tv_contact_initials);
		if (view != null)
			holder.tvInitials = (TextView) view;
		view = null;

		view = convertView.findViewById(R.id.iv_contact_initals_line);
		if (view != null)
			holder.ivInitals = (ImageView) view;
		view = null;

		view = convertView.findViewById(R.id.tv_contact_name);
		if (view != null)
			holder.tvName = (TextView) view;
		view = null;

		view = convertView.findViewById(R.id.tv_contact_phone);
		if (view != null)
			holder.tvPhone = (TextView) view;
		view = null;

		view = convertView.findViewById(R.id.iv_contact_photo);
		if (view != null)
			holder.ivHead = (HeadImageView) view;
		view = null;

		view = convertView.findViewById(R.id.cb_contact_check);
		if (view != null)
			holder.cbCheck = (CheckBox) view;
		view = null;

		view = convertView.findViewById(R.id.iv_contact_call);
		if (view != null)
			holder.ivCall = (ImageView) view;
		view = null;
	}

	protected void bindData(int position, ContactViewHolder holder, T t) {
		// 设置首字母及分割线
		setInitialsTextView(position, holder, t);

		// 设置选择框及拨打矿
		setCallImageView(position, holder);

		// 设置姓名,电话,头像
		if (holder.tvName != null)
			holder.tvName.setText(users.get(position).getNickName());
		if (holder.tvPhone != null)
			holder.tvPhone.setText(users.get(position).getPhone());
		if (holder.ivHead != null)
			holder.ivHead.setSex(users.get(position).getSex());
		if (holder.ivHead != null)
			holder.ivHead.setImageUrl(users.get(position).getIcon());
	}

	protected class ContactViewHolder {
		public TextView tvInitials, tvName, tvPhone;
		public HeadImageView ivHead;
		public ImageView ivCall, ivInitals;
		public CheckBox cbCheck;
	}

	protected void setCallImageView(final int position, ContactViewHolder holder) {
		holder.cbCheck.setVisibility(View.GONE);
		holder.ivCall.setVisibility(View.VISIBLE);
		holder.ivCall.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				goCallActivity(users.get(position));
			}
		});
	}

	protected void goCallActivity(T t) {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.DIAL");
		intent.setData(Uri.parse("tel:" + t.getPhone()));
		mContext.startActivity(intent);
	}

	protected void setInitialsTextView(int position, ContactViewHolder holder, T t) {
		String currentSpell = ChineseUtil.converterToSpell(t.getNickName()).charAt(0) + "";
		String previewSpell = position > 0 ? ChineseUtil.converterToSpell(users.get(position - 1).getNickName()).charAt(0) + "" : "";
		if (currentSpell.equals(previewSpell)) {
			holder.tvInitials.setVisibility(View.GONE);
			holder.ivInitals.setVisibility(View.GONE);
		} else {
			holder.tvInitials.setVisibility(View.VISIBLE);
			holder.ivInitals.setVisibility(View.VISIBLE);
			holder.tvInitials.setText(currentSpell.toUpperCase(Locale.CHINESE));
		}
	}

	/**
	 * 获取首字母为当前传入字母的第一个联系人所在position
	 * 
	 * @author wangyang 2014-11-12 下午7:43:02
	 * @param s
	 * @return
	 */
	public int getFirstPosition(String s) {
		for (int i = 0; i < getCount(); i++) {
			if (users.get(i).getFirstSpell().equalsIgnoreCase(s))
				return i;
		}
		return -1;
	}

	public List<? extends BaseUser> getList() {
		return users;
	}

	public void setList(List<T> users) {
		this.users = users;
		if (users != null)
			Collections.sort(this.users);
	}

}