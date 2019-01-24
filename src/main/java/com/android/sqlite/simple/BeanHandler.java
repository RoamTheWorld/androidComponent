package com.android.sqlite.simple;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;

import com.android.utils.Log;
import com.android.utils.StringUtil;

/**
 * 
 * @author wangyang
 * @param <T>
 *            该类继承自CursorHandler,并实现了其方法,分别处理cursor结果集返回一个对象与一个泛型集合
 */
public class BeanHandler<T> implements CursorHandler<T> {

	/**
	 * 
	 * @author wangyang
	 * @param cursor
	 * @param clazz
	 * @return 根据传入的Cursor结果集与Class通过反射与注解封装至Class的实例对象
	 */
	@Override
	public T handler(DBHelper db, Cursor cursor, Class<?> clazz, boolean autoCommit) {
		return handler2List(db, cursor, clazz, autoCommit).get(0);
	}

	/**
	 * 
	 * @author wangyang
	 * @param clazz
	 * @param cursor
	 * @param index
	 * @return 通过字段类型,结果集与索引获取数据库的值并返回(目前只支持8种基本数据类型及其包装类与String)
	 */
	private Object setValue(Class<?> clazz, Cursor cursor, int index) {
		if (clazz == String.class || clazz == Character.class || clazz == char.class )
			return cursor.getString(index);
		
		if (clazz == Byte.class || clazz == byte.class)
			return cursor.getInt(index);
		
		if (clazz == Integer.class || clazz == int.class)
			return cursor.getInt(index);

		if (clazz == Double.class || clazz == double.class)
			return cursor.getDouble(index);

		if (clazz == Short.class || clazz == short.class)
			return cursor.getShort(index);

		if (clazz == Long.class || clazz == long.class)
			return cursor.getLong(index);

		if (clazz == Float.class || clazz == float.class)
			return cursor.getFloat(index);

		if (Boolean.class == clazz || boolean.class == clazz)
			return cursor.getInt(index) == 1 ? true : false;

		return null;
	}

	/**
	 * 
	 * @author wangyang
	 * @param cursor
	 * @param clazz
	 * @param autoCommit
	 * @return 根据传入的Cursor结果集与Class通过反射与注解封装至Class实例的泛型集合
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<T> handler2List(DBHelper db, Cursor cursor, Class<?> clazz, boolean autoCommit) {
		List<T> list = new ArrayList<T>();
		while (cursor.moveToNext()) {
			try {
				T t = null;
				Field[] fields = clazz.getDeclaredFields();
				for (Field field : fields) {
					field.setAccessible(true);
					com.android.sqlite.simple.annotation.Coloumn coloumn = field.getAnnotation(com.android.sqlite.simple.annotation.Coloumn.class);
					if (coloumn != null) {
						String coloumnName = StringUtil.isEmpty(coloumn.value()) ? field.getName() : coloumn.value();
						int index = cursor.getColumnIndex(coloumnName);
						if (index == -1)
							continue;
						// 设置field的值并注入到实例化的对象
						if (t == null)
							t = (T) clazz.newInstance();
						field.set(t, setValue(field.getType(), cursor, index));
					}
				}
				// 将实例对象添加至集合
				list.add(t);

			} catch (InstantiationException e) {
				Log.e("实例化对象错误!!!");
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				Log.e("非法的参数!!!");
				e.printStackTrace();
			}
		}
		cursor.close();
		if (autoCommit)
			db.close();
		return list;
	}
}
