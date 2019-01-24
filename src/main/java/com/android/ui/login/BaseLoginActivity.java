package com.android.ui.login;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.master.R;
import com.android.ui.BaseActivity;
import com.android.ui.widget.LoadingDialog;
import com.android.ui.widget.Toast;
import com.android.utils.Constants;
import com.android.utils.NetWorkUtil;
import com.android.utils.StringUtil;

public abstract class BaseLoginActivity extends BaseActivity {
	/**
	 * 登录帐号输入框,登录密码输入框
	 */
	protected EditText etLoginName, etLoginPassword;

	protected TextView lostPwd, register;
	/**
	 * 登录帐号
	 */
	protected String loginName;
	/**
	 * 登录密码
	 */
	protected String password;

	/**
	 * 是否正在登录
	 */
	protected boolean isOtherActivity;

	@Override
	protected void findViewById() {
		etLoginName = (EditText) findViewById(R.id.et_login_name);
		etLoginPassword = (EditText) findViewById(R.id.et_login_password);
		lostPwd = (TextView) findViewById(R.id.tv_lost_pwd);
		register = (TextView) findViewById(R.id.tv_register);
	}

	@Override
	protected int getContentViewId() {
		return R.layout.login_activity;
	}

	protected void init() {
		isOtherActivity = getIntent().getBooleanExtra(Constants.IS_OTHER_ACTIVITY, false);

		loginName = mSp.getString(Constants.CURRENT_USER_LOGIN_NAME, "");
		password = mSp.getString(Constants.CURRENT_USER_PASSWORD, "");
		etLoginName.setText(loginName);
		etLoginPassword.setText(password);

		if (mSp.getBoolean(Constants.AUTO_LOGIN, true) && !StringUtil.isEmpty(password)) {
			checkLoginDataInput();
			doLogin(null);
		}
	}

	/**
	 * 登录
	 * 
	 * @param view
	 *            登录按钮
	 */
	public void doLogin(View view) {
		if (!checkLoginDataInput()) {
			return;
		}

		if (!NetWorkUtil.isConnectInternet(this)) {
			Toast.show(this, "无网络或信号过弱!");
			return;
		}
		LoadingDialog.showLoadingDialog(this, "正在登录...");
		sendLoginRequest();
	}

	protected void sendLoginRequest() {
	}

	/**
	 * 检测登录数据输入
	 */
	private boolean checkLoginDataInput() {
		loginName = etLoginName.getText().toString();
		if (StringUtil.isEmpty(loginName)) {
			Toast.show(this, "请输入登录帐号");
			return false;
		}

		password = etLoginPassword.getText().toString();
		if (StringUtil.isEmpty(password, 6)) {
			Toast.show(this, "请输入登录密码,并且不少于6位");
			return false;
		}
		return true;
	}

}
