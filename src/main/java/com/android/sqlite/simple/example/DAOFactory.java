package com.android.sqlite.simple.example;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import android.content.Context;

/**
 * @description 数据库操作工厂类。此类在引用项目中创建，根据不同的DBImpl实现类创建不同的提供示例方法。
 * 
 * @author wangyang
 * @create 2014-2-21 上午11:40:27
 * @version 1.0.0
 * @param <M>
 * @param <M>
 */
public class DAOFactory {
	/**
	 * 创建DBImpl子类
	 * 
	 * @param context
	 * @param clazz
	 * @return
	 */
	public static <M> DAOImpl<M> createDBImpl(Context context,
			Class<? extends DAOImpl<M>> clazz) {
		try {
			Constructor<? extends DAOImpl<M>> constructor = clazz
					.getConstructor(new Class[] { Context.class });
			return (DAOImpl<M>) constructor
					.newInstance(new Object[] { context });
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 创建ChatMessageImpl示例
	 * 
	 * @param context
	 * @return
	 */
	public static ChatMessageImpl createChatMessageImpl(Context context) {
		return (ChatMessageImpl) createDBImpl(context, ChatMessageImpl.class);
	}

}
