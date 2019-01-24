package com.android.ui.widget;

import android.content.Context;
import android.view.View;
import android.view.WindowManager;

import com.android.BaseApplication;

import java.lang.reflect.Field;

/**
 * 自定义Toast
 * 
 * @author wangyang
 * @2014-2-25 上午11:26:29
 */
public class Toast {
	/** 全局唯一的Toast */
	private static android.widget.Toast mToast;
	
	private static android.widget.Toast mTextToast;
	
	private  static boolean isField = false;

	public static void show(String text) {
		show(BaseApplication.getInstance(),text);
	}

	/**
	 * 弹出Toast,该方法防止了重复弹出Toast的情况
	 * 
	 * @author wangyang
	 * @2014-2-25 上午11:27:36
	 * @param context
	 * @param text
	 */
	public static void show(Context context, String text, int length) {
		if (mTextToast == null) {
			mTextToast = android.widget.Toast.makeText(context, text, length);
		} else {
			mTextToast.setText(text);
			mTextToast.setDuration(length);
		}
		mTextToast.show();
	}

	public static void show(Context context, String text) {
		show(context, text, android.widget.Toast.LENGTH_SHORT);
	}
	
	public static void show(Context context, View view, int duration, int animId,int gravity,int xOffset,int yOffset) {
		initAnimationId(context,animId);
		mToast.setGravity(gravity, xOffset, yOffset);
		mToast.setView(view);
		mToast.setDuration(duration);
		mToast.show();
	}
	
	public static void show(Context context, View view, int gravity,int xOffset,int yOffset) {
		mToast.setGravity(gravity, xOffset, yOffset);
		mToast.setView(view);
		mToast.setDuration(android.widget.Toast.LENGTH_SHORT);
		mToast.show();
	}

	public static void initAnimationId(Context context, int animId) {
		if(mToast == null)
			mToast = new android.widget.Toast(context);
		if(!isField && animId>0){
			try {
				Object field = getField(mToast, "mTN");
				if (field != null) {
					Object mParams = getField(field, "mParams");
					if (mParams != null && mParams instanceof WindowManager.LayoutParams) {
						WindowManager.LayoutParams params = (WindowManager.LayoutParams) mParams;
						params.windowAnimations = animId;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			isField = true;
		}
	}

	/**
	 * 
	 *
	 * @author wangyang
	 * 2015-12-11 上午10:21:23
	 * @param object
	 * @param fieldName
	 * @return
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 */
	private static Object getField(Object object, String fieldName) throws NoSuchFieldException, IllegalAccessException {
		Field field = object.getClass().getDeclaredField(fieldName);
		if (field != null) {
			field.setAccessible(true);
			return field.get(object);
		}
		return null;
	}
	
}
