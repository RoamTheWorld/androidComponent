package com.android.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.android.master.R;

/**
 * @description 加载弹出框
 * 
 * @author wangyang
 * @create 2014-2-18 上午10:43:40
 * @version 1.0.0
 */
public class LoadingDialog extends Dialog {
	/**
	 * 加载提示
	 */
	private TextView tvText;
	/**
	 * 加载提示内容
	 */
	private String title;

	public LoadingDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	public LoadingDialog(Context context, int theme, String title) {
		super(context, theme);
		this.title = title;
	}

	public LoadingDialog(Context context) {
		super(context);
	}

	public LoadingDialog(Context context, int theme) {
		super(context, theme);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.loading_dialog);
		tvText = (TextView) findViewById(R.id.tv_loading_text);
		tvText.setText(title + "");
	}

	private static LoadingDialog mLoadingDialog;

	public static void showLoadingDialog(Context context, String title) {
		showLoadingDialog(context, title, null);
	}

	public static void showLoadingDialog(Context context, String title, OnCancelListener listener) {
		if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
			mLoadingDialog.tvText.setText(title + "");
		} else {
			mLoadingDialog = new LoadingDialog(context, R.style.loading_dialog, title);
			mLoadingDialog.setCanceledOnTouchOutside(false);
		}
		if (listener != null) {
			mLoadingDialog.setOnCancelListener(listener);
		}
		if (mLoadingDialog != null && !mLoadingDialog.isShowing())
			mLoadingDialog.show();
	}

	public static void dismissLoadingDialog() {
		if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
			mLoadingDialog.cancel();
		}
	}
}
