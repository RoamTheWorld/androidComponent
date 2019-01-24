package com.android.sqlite.handler;

import java.util.List;

import android.database.Cursor;

import com.android.sqlite.DBHelper;
import com.android.sqlite.table.Model;

/**
 * 
 * @author wangyang
 * @param <T>
 *            Describe 接收结果集并进行对应的处理,返回泛型
 */
public interface CursorHandler<T> {
	/**
	 * 根据传入的Class,反射属性,并去Cursor获取值最后封装到泛型里,最后关闭游标与数据库
	 * 
	 * @author wangyang
	 * @param cursor
	 * @return T 根据传入的Class,反射属性,并去Cursor获取值最后封装到泛型里,最后关闭游标与数据库
	 */
	T handler(DBHelper db,Cursor cursor, Class<T> clazz,boolean autoCommit,boolean Recursion);

	/**
	 * 根据传入的Class,反射属性,并去Cursor获取值最后封装到泛型集合里,最后关闭游标与数据库
	 * 
	 * @author wangyang
	 * @param cursor
	 * @param clazz
	 * @return List<T> 
	 */
	List<T> handler2List(DBHelper db,Cursor cursor, Class<T> clazz,boolean autoCommit,boolean Recursion);
	
	/**
	 * 根据传入的Class,反射属性,并去Cursor获取值最后封装到Model里,最后关闭游标与数据库
	 *
	 * @author wangyang
	 * 2014-4-22 下午6:47:15
	 * @param cursor
	 * @param db
	 * @param autoCommit
	 * @return
	 */
	Model getModel(Cursor cursor, DBHelper db, boolean autoCommit);
	
	/**
	 * 根据传入的Class,反射属性,并去Cursor获取值最后封装到Model集合里,最后关闭游标与数据库
	 *
	 * @author wangyang
	 * 2014-4-22 下午6:47:20
	 * @param cursor
	 * @param db
	 * @param autoCommit
	 * @return
	 */
	List<Model> getListModel(Cursor cursor, DBHelper db, boolean autoCommit);
}
