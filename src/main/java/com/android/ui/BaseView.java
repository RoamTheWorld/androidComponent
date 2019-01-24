package com.android.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.android.utils.Constants;

/**
 * 单Activity 模式下 界面显示 BaseView 其模拟Activity 有生命周期方法 所有需要显示的界面都必须继承它
 * 分别有初始化View需要的参数,以及初始化的抽象方法。
 * 
 * @author wangyang
 * @2014-2-25 上午10:39:06
 */
public abstract class BaseView implements OnClickListener, OnItemSelectedListener, OnItemClickListener, OnCheckedChangeListener, OnScrollListener {

	// -----------------------------数据-------------------------------

	protected static int mCurrentView;

	protected Context mContext;// 上下文

	protected Bundle mBundle;// 传递参数的Bundle

	protected ViewGroup mShowView;// 界面布局

	private SharedPreferences mSp;

	protected Editor mEditor;

	/**
	 * @author wangyang
	 * @2013-11-14 上午9:55:22
	 * @param mContext
	 */
	public BaseView(Context mContext) {
		this(mContext, null);
	}

	/**
	 * 
	 * @author wangyang
	 * @2013-11-14 上午9:54:52
	 * @param mContext
	 * @param mBundle
	 */
	public BaseView(Context mContext, Bundle mBundle) {
		this.mContext = mContext;
		this.mBundle = mBundle;
		Looper.getMainLooper();
		mSp = mContext.getSharedPreferences(Constants.FILE_CACHE_NAME, Context.MODE_PRIVATE);
		mEditor = mSp.edit();
		mShowView = setContentView();
		findViewById();
		init();
		setListeners();
		registerReceivers();
	}

	/**
	 * 
	 * @author wangyang
	 * @2013-11-14 上午10:04:47 初始化布局,数据及控件
	 */
	protected  ViewGroup setContentView(){
		return (ViewGroup) View.inflate(mContext, getContentViewId(), null);
	}
	
	/**
	 * 设置控件Id
	 */
	protected abstract int getContentViewId();

	/**
	 * 查找控件
	 */
	protected abstract void findViewById();

	/**
	 * 设置事件监听
	 */
	protected void setListeners() {
	}

	/**
	 * 初始化Fragement数据
	 */
	protected abstract void init();

	/**
	 * 注册广播接收者
	 */
	protected void registerReceivers() {
	};

	/**
	 * 取消广播接收
	 */
	protected void unRegisterReceivers() {
	};

	/**
	 * 
	 * @author wangyang
	 * @2013-11-14 上午10:06:00 View的生命周期的开始
	 */
	public void onStart() {
		onResume();
	};

	/**
	 * 
	 * @author wangyang
	 * @2013-11-20 下午2:12:09 当View回去焦点的生命周期
	 */
	public void onResume() {
	};

	/**
	 * 
	 * @author wangyang
	 * @2013-11-14 上午9:38:24
	 * @param resId
	 * @return 根据id获取对应的View
	 */
	protected View findViewById(int resId) {
		return mShowView.findViewById(resId);
	}

	/**
	 * 
	 * @author wangyang
	 * @2013-11-14 上午9:38:15
	 * @return 获取布局对象
	 */
	public View getView() {
		// 如果布局参数为空,则设置布局参数后返回
		if (mShowView.getLayoutParams() == null)
			mShowView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		return mShowView;
	}

	public String getString(int resId) {
		return mContext.getString(resId);
	}

	/**
	 * 
	 * @author wangyang
	 * @2013-11-14 上午10:04:27
	 * @return 返回view唯一的ID
	 */
	public abstract Integer getId();

	/**
	 * 
	 * @author wangyang
	 * @2013-11-14 上午10:07:25
	 * @param keyCode
	 * @param event
	 * @return View的OnkeyDown
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return new View(mContext).onKeyDown(keyCode, event);
	};

	/**
	 * 
	 * @author wangyang
	 * @2013-11-14 上午10:09:29
	 * @param v
	 *            onClick点击事件
	 */
	@Override
	public void onClick(View v) {}

	/**
	 * 
	 * @author xiezhijie
	 * @2013-11-14 下午3:27:52
	 * @param event
	 * @return
	 */
	public boolean onTouchEvent(MotionEvent event) {
		return new View(mContext).onTouchEvent(event);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
	}

	/**
	 * 
	 * @author wangyang
	 * @2013-11-14 上午10:09:49
	 * @param mBundle
	 *            设置Bundle
	 */
	public void setBundle(Bundle mBundle) {
		this.mBundle = mBundle;
	}

	/**
	 * 
	 * @author wangyang
	 * @2013-11-14 上午10:06:21 View生命周期的结束
	 */
	public void onStop() {}

}
