package com.android.utils.version;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.android.master.R;
import com.android.utils.StringUtil;

public class VersionDialog {
	static AlertDialog alertDialog;

	/**
	 * 
	 * @param context
	 *            上下文对象
	 * @param msg
	 *            提醒消息
	 * @param listenerLeft
	 *            左按钮监听 默认消失对话框
	 * @param listenerRight
	 *            右按钮监听 默认消失对话框
	 */
	public static void showDialog(Context context, String msg, OnClickListener listenerLeft, OnClickListener listenerRight,boolean outClick) {
		showDialog(context, null, msg, null, null, listenerLeft, listenerRight, false,outClick);
	}
	
	/**
	 * 
	 * @param context
	 *            上下文对象
	 * @param msg
	 *            提醒消息
	 * @param listenerLeft
	 *            左按钮监听 默认消失对话框
	 * @param listenerRight
	 *            右按钮监听 默认消失对话框
	 */
	public static void showDialog(Context context,String title, String msg, OnClickListener listenerLeft, OnClickListener listenerRight,boolean outClick) {
		showDialog(context, title, msg, null, null, listenerLeft, listenerRight, false,outClick);
	}


	/**
	 * 
	 * @param context
	 *            (必传)上下文对象
	 * @param title
	 *            提示标题 默认提示
	 * @param msg
	 *            提醒消息 默认 "有新版本，是否更新?"
	 * @param left
	 *            左按钮 文字 默认取消
	 * @param right
	 *            右按钮文字 默认确定
	 * @param listenerLeft
	 *            左按钮监听 默认消失对话框
	 * @param listenerRight
	 *            右按钮监听 默认消失对话框
	 * @param isMust
	 *            是否必须确定
	 * @param outClick
	 *            对话框外部点击是否关闭对话框
	 */
	public static void showDialog(Context context, String title, String msg, String left, String right, OnClickListener listenerLeft, OnClickListener listenerRight, boolean isMust,boolean outClick) {
		alertDialog = new AlertDialog.Builder(context).create();
		alertDialog.show();
		alertDialog.setCancelable(outClick);
		Window window = alertDialog.getWindow();
		window.setContentView(R.layout.dialog_layout);
		TextView titleTextView = (TextView) window.findViewById(R.id.dialog_layout_title);
		TextView content = (TextView) window.findViewById(R.id.dialog_layout_content);
		content.setText(StringUtil.isEmpty(msg) ? "有新版本，是否更新?" : msg);
		Button negative = (Button) window.findViewById(R.id.dialog_layout_negative);
		Button Positive = (Button) window.findViewById(R.id.dialog_layout_Positive);
		if (!StringUtil.isEmpty(title))
			titleTextView.setText(title);
		if (!StringUtil.isEmpty(left))
			negative.setText(left);
		if (!StringUtil.isEmpty(right))
			Positive.setText(right);
		if (listenerLeft != null)
			negative.setOnClickListener(listenerLeft);
		else
			negative.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					alertDialog.dismiss();
				}
			});
		if (listenerRight != null)
			Positive.setOnClickListener(listenerRight);
		else
			Positive.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					alertDialog.dismiss();
				}
			});
		if (isMust) {
			alertDialog.setCanceledOnTouchOutside(false);
			alertDialog.setCancelable(false);
			View view = (View) window.findViewById(R.id.dialog_layout_line);
			negative.setVisibility(View.GONE);
			view.setVisibility(View.GONE);
		}
	}

	public static void dismiss() {
		if (alertDialog != null)
			alertDialog.dismiss();
		alertDialog = null;
	}
}
