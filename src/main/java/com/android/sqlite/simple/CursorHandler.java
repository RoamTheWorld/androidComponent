package com.android.sqlite.simple;

import java.util.List;

import android.database.Cursor;

/**
 * 
 * @author wangyang
 * @param <T>
 *            Describe 接收结果集并进行对应的处理,返回泛型
 */
public interface CursorHandler<T> {
	/**
	 * 
	 * @author wangyang
	 * @param cursor
	 * @return T 根据传入的Class,反射属性,并去Cursor获取值最后封装到泛型里,最后关闭游标与数据库
	 */
	T handler(DBHelper db,Cursor cursor, Class<?> clazz,boolean autoCommit);

	/**
	 * 
	 * @author wangyang
	 * @param cursor
	 * @param clazz
	 * @return List<T> 根据传入的Class,反射属性,并去Cursor获取值最后封装到泛型集合里,最后关闭游标与数据库
	 */
	List<T> handler2List(DBHelper db,Cursor cursor, Class<?> clazz,boolean autoCommit);
}
