package com.android;

import android.app.Activity;
import android.app.Application;

import com.android.ui.BaseActivity;
import com.android.ui.contact.BaseUser;
import com.android.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseApplication extends Application {

	public abstract BaseUser getCurUser();

	private static List<Activity> activities;

	private static BaseApplication INSTANCE;

	public static BaseApplication getInstance(){
		return INSTANCE;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Constants.FILE_ROOT_DIRECTORY = getPackageName();
		INSTANCE = this;
	}

	public List<Activity> getActivities() {
		if (activities == null)
			activities = new ArrayList<Activity>();
		return activities;
	}

	public void addActivity(Activity activity) {
		getActivities().add(activity);
	}

	public boolean removeActivity(Activity activity) {
		return getActivities().remove(activity);
	}

	public void clearAllActivity() {
		if (activities == null)
			return;
		for (Activity activity : activities)
			activity.finish();
	}

	public static void clearActivitys() {
		if (activities == null)
			return;
		for (Activity activity : activities)
			activity.finish();
	}

	public <T extends BaseActivity>T findActivity(Class<? extends BaseActivity> clazz){
		for (Activity activity : activities){
			if(activity instanceof BaseActivity && clazz == activity.getClass())
				return (T) activity;
		}
		return null;
	}

}
