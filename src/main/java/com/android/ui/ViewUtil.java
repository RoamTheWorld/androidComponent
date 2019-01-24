package com.android.ui;

import java.lang.reflect.Field;

import android.app.Activity;

import com.android.ui.annotation.View;

public class ViewUtil {
	public static void findViewByAnnotation(Object object, Object view) {
		Field[] fields = object.getClass().getDeclaredFields();
		for (Field field : fields) {
			View annot = field.getAnnotation(View.class);
			if (annot == null)
				continue;
			field.setAccessible(true);
			try {
				if (view instanceof Activity) {
					Activity activity = (Activity) view;
					field.set(object, activity.findViewById(annot.id()));
				}
				if (view instanceof android.view.View) {
					android.view.View view2 = (android.view.View) view;
					field.set(object, view2.findViewById(annot.id()));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
