package com.android.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.master.R;
import com.android.utils.ButtonClickLock;
import com.android.utils.Constants;

import java.util.Set;

/**
 * @description Fragment基类,可在引用项目中继承该类并添加相应功能。
 * 
 * @author wangyang
 * @create 2014-2-24 下午06:21:33
 * @version 1.0.0
 */
public abstract class BaseFragment extends Fragment implements OnClickListener {

	private final String CLICK_LOCK = this.getClass().getSimpleName();

	protected SharedPreferences mSp;

	protected View view;

	protected LinearLayout linearLayout;
	protected FrameLayout frameLeft, frameRight;
	protected TextView tvLeft, tvContent, tvRight;

	protected  BaseFragment(){}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mSp = getActivity().getSharedPreferences(Constants.FILE_CACHE_NAME, Context.MODE_PRIVATE);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = setContentView(inflater);
		findViewById(view);
		init();
		init(getActivity());
		registerReceivers();
		setListeners();
		return view;
	}

	/**
	 * 设置布局文件
	 */
	protected View setContentView(LayoutInflater inflater) {
		return inflater.inflate(getContentViewId(), null);
	}

	/**
	 * 设置布局文件Id
	 */
	protected abstract int getContentViewId();
	public abstract void startActivity(Class clazz, boolean isfinsh);

	/**
	 * 查找控件
	 * 
	 * @param view
	 */
	protected abstract void findViewById(View view);

	/**
	 * 设置事件监听
	 */
	protected void setListeners() {
	}

	/**
	 * 初始化Fragement数据
	 */
	protected abstract void init();
	protected  void init(Context context){

	}

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

	@Override
	public void onClick(View v) {
		if(!isCanClick())
			return;
	}

	@Override
	public void onDestroy() {
		unRegisterReceivers();
		super.onDestroy();
	}

	protected void put(String key, boolean value) {
		Editor editor = mSp.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	protected void put(String key, float value) {
		Editor editor = mSp.edit();
		editor.putFloat(key, value);
		editor.commit();
	}

	protected void put(String key, String value) {
		Editor editor = mSp.edit();
		editor.putString(key, value);
		editor.commit();
	}

	protected void put(String key, int value) {
		Editor editor = mSp.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	protected void put(String key, long value) {
		Editor editor = mSp.edit();
		editor.putLong(key, value);
		editor.commit();
	}

	@SuppressLint("NewApi")
	protected void put(String key, Set<String> value) {
		Editor editor = mSp.edit();
		editor.putStringSet(key, value);
		editor.commit();
	}

	/**
	 * 初始化标题栏
	 * 
	 * @author 李应锋
	 * @create 2015-3-2
	 * 
	 * @param imgLeft
	 *            左侧图片ID
	 * @param imgRight
	 *            右侧图片ID
	 * @param left
	 *            左侧文字
	 * @param content
	 *            中间文字
	 * @param right
	 *            右侧文字
	 */
	protected void initBaseTitle(int imgLeft, int imgRight, String left, String content, String right) {
		this.initBaseTitle(Color.GRAY, imgLeft, imgRight, left, content, right, Color.BLACK, Color.BLACK, Color.BLACK);
	}

	/**
	 * 初始化标题栏
	 * 
	 * @author 李应锋
	 * @create 2015-3-2
	 * 
	 * @param bgColor
	 *            标题栏背景色
	 * @param imgLeft
	 *            左侧图片ID
	 * @param imgRight
	 *            右侧图片ID
	 * @param left
	 *            左侧文字
	 * @param content
	 *            中间文字
	 * @param right
	 *            右侧文字
	 */
	protected void initBaseTitle(int bgColor, int imgLeft, int imgRight, String left, String content, String right) {
		this.initBaseTitle(bgColor, imgLeft, imgRight, left, content, right, Color.BLACK, Color.BLACK, Color.BLACK);
	}

	/**
	 * 初始化标题栏
	 * 
	 * @author 李应锋
	 * @create 2015-3-2
	 * 
	 * @param bgColor
	 *            标题栏背景色
	 * @param imgLeft
	 *            左侧图片ID
	 * @param imgRight
	 *            右侧图片ID
	 * @param left
	 *            左侧文字
	 * @param content
	 *            中间文字
	 * @param right
	 *            右侧文字
	 * @param leftColor
	 *            左侧文字颜色
	 * @param contentColor
	 *            中间文字颜色
	 * @param rightColor
	 *            右侧文字颜色
	 */
	protected void initBaseTitle(int bgColor, int imgLeft, int imgRight, String left, String content, String right, int leftColor, int contentColor, int rightColor) {
		if (linearLayout == null)
			linearLayout = (LinearLayout) view.findViewById(R.id.title_linear);
		linearLayout.setBackgroundColor(bgColor);
		if (frameLeft == null)
			frameLeft = (FrameLayout) view.findViewById(R.id.title_frame_left);
		if (imgLeft > 0 || left != null) {
			frameLeft.setVisibility(View.VISIBLE);
			frameLeft.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					onClickTitleLeft(v);
				}
			});
		} else
			frameLeft.setVisibility(View.INVISIBLE);
		if (frameRight == null)
			frameRight = (FrameLayout) view.findViewById(R.id.title_frame_right);
		if (imgRight > 0 || right != null) {
			frameRight.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					onClickTitleRight(v);
				}
			});
		} else
			frameRight.setVisibility(View.INVISIBLE);

		if (tvLeft == null)
			tvLeft = (TextView) view.findViewById(R.id.title_tv_left);
		if (imgLeft > 0)
			tvLeft.setBackgroundResource(imgLeft);
		if (left != null)
			tvLeft.setText(left);
		tvLeft.setTextColor(leftColor);
		if (tvContent == null)
			tvContent = (TextView) view.findViewById(R.id.title_tv_content);
		if (content != null)
			tvContent.setText(content);
		tvContent.setTextColor(contentColor);
		if (tvRight == null)
			tvRight = (TextView) view.findViewById(R.id.title_tv_right);
		if (imgRight > 0)
			tvRight.setBackgroundResource(imgRight);
		if (right != null)
			tvRight.setText(right);
		tvRight.setTextColor(rightColor);
	}

	/** 标题栏左侧点击事件 */
	protected void onClickTitleLeft(View v) {
		// getActivity().finish();
	}

	/** 标题栏右侧点击事件 */
	protected void onClickTitleRight(View v) {
	}

	public boolean isCanClick() {
		return ButtonClickLock.isCanClick(CLICK_LOCK);
	}


	public void setOnClickListeners(OnClickListener listeners,View...views){
		if(listeners!=null){
			for (View view:views ) {
				view.setOnClickListener(listeners);
			}
		}

	}

	public void openActivity(Context context,Class<?> clazz){
		Intent intent=new Intent(context,clazz);
		startActivity(intent);
	}
}