package com.android.sqlite.handler;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.database.Cursor;

import com.android.sqlite.DBHelper;
import com.android.sqlite.table.Column;
import com.android.sqlite.table.EntityTempCache;
import com.android.sqlite.table.Finder;
import com.android.sqlite.table.Foreign;
import com.android.sqlite.table.Id;
import com.android.sqlite.table.Model;
import com.android.sqlite.table.Table;

/**
 * 
 * @author wangyang
 * @param <T>
 *            该类继承自CursorHandler,并实现了其方法,分别处理cursor结果集返回一个对象与一个泛型集合
 */
@SuppressLint("UseSparseArrays")
public class BeanHandler<T> implements CursorHandler<T> {

	/**
	 * 
	 * @author wangyang
	 * @param cursor
	 * @param clazz
	 * @return 根据传入的Cursor结果集与Class通过反射与注解封装至Class的实例对象
	 */
	@Override
	public T handler(DBHelper db, Cursor cursor, Class<T> entityType, boolean autoCommit, boolean Recursion) {
		if (db == null || cursor == null)
			return null;
		Table table = Table.get(entityType, true, true, Recursion);
		T entity = null;
		if (cursor.moveToNext()) {
			try {
				entity = operatorByColumn(db, cursor, entityType, table);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				cursor.close();
				if (autoCommit)
					db.close();
				table.clearValue();
			}
		}
		return entity;
	}

	/**
	 * 
	 * @author wangyang
	 * @param cursor
	 * @param clazz
	 * @param autoCommit
	 * @return 根据传入的Cursor结果集与Class通过反射与注解封装至Class实例的泛型集合
	 */
	@Override
	public List<T> handler2List(DBHelper db, Cursor cursor, Class<T> entityType, boolean autoCommit, boolean Recursion) {
		if (db == null || cursor == null)
			return null;
		List<T> list = new ArrayList<T>();
		Table table = Table.get(entityType, true, true, Recursion);
		try {
			while (cursor.moveToNext()) {
				T entity = operatorByColumn(db, cursor, entityType, table);
				list.add(entity);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cursor.close();
			if (autoCommit)
				db.close();
			table.clearValue();
		}

		return list;
	}

	/**
	 * 将Cursor的一条数据封装至实体并返回
	 * 
	 * @author wangyang 2014-4-22 下午6:41:42
	 * @param db
	 * @param cursor
	 * @param entityType
	 * @param table
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	private T operatorByColumn(DBHelper db, Cursor cursor, Class<T> entityType, Table table) throws InstantiationException, IllegalAccessException {
		// 获取数据缓存Id
		Id id = table.getId();
		String idColumnName = id.getColumnName();
		int idIndex = cursor.getColumnIndex(idColumnName);
		String idStr = cursor.getString(idIndex);

		// 根据Id获取数据缓存对象,如果没有则设置值
		T entity = EntityTempCache.get(entityType, idStr);
		if (entity == null) {
			entity = entityType.newInstance();
			id.setValue2Entity(entity, cursor, idIndex);
			EntityTempCache.put(idStr, entity);
		} else {
			setLazyLoader(db, table, entity);
			return entity;
		}

		// 遍历所有字段并映射至字段集合,然后设置值
		int columnCount = cursor.getColumnCount();
		for (int i = 0; i < columnCount; i++) {
			String columnName = cursor.getColumnName(i);
			Column column = table.getAttributes().get(columnName);

			// 跳过column为空
			if (column == null)
				continue;

			// 跳过为主键字段
			if (column instanceof Id)
				continue;

			// 如果是外键,则设置db
			if (column instanceof Foreign) {
				Foreign foreign = (Foreign) column;
				foreign.setDb(db);
				foreign.setValue2Entity(entity, cursor, i);
				continue;
			}

			// 设置字段的值
			column.setValue2Entity(entity, cursor, i);
		}

		setLazyLoader(db, table, entity);
		return entity;
	}

	private void setLazyLoader(DBHelper db, Table table, T entity) {

		// 设置Finder懒加载的值
		for (Column column : table.getFinders().values()) {
			Finder finder = (Finder) column;
			finder.setDb(db);
			finder.setValue2Entity(entity, null, 0);
		}

		// 设置外键懒加载的db
		for (Column column : table.getAttributes().values()) {
			if (column instanceof Foreign) {
				Foreign foreign = (Foreign) column;
				foreign.setDb(db);
			}
		}
	}

	/**
	 * 
	 * 
	 * @author wangyang 2014-4-22 下午6:42:30
	 * @param cursor
	 * @param db
	 * @param autoCommit
	 * @return
	 */
	@Override
	public Model getModel(final Cursor cursor, DBHelper db, boolean autoCommit) {
		if (cursor == null)
			return null;
		Model result = null;
		if (cursor.moveToNext()) {
			result = operatorModel(cursor);
		}
		cursor.close();
		if (autoCommit)
			db.close();
		return result;
	}

	@Override
	public List<Model> getListModel(final Cursor cursor, DBHelper db, boolean autoCommit) {
		if (cursor == null)
			return null;
		List<Model> list = new ArrayList<Model>();
		while (cursor.moveToNext()) {
			Model result = operatorModel(cursor);
			list.add(result);
		}
		cursor.close();
		if (autoCommit)
			db.close();
		return list;
	}

	/**
	 * 将Cursor的一条数据封装至Model实体并返回
	 * 
	 * @author wangyang 2014-4-22 下午6:43:21
	 * @param cursor
	 * @return
	 */
	private Model operatorModel(final Cursor cursor) {
		Model result;
		result = new Model();
		int columnCount = cursor.getColumnCount();
		for (int i = 0; i < columnCount; i++) {
			result.add(cursor.getColumnName(i), cursor.getString(i));
		}
		return result;
	}
}
