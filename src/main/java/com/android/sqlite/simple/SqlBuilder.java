package com.android.sqlite.simple;

import java.util.Collection;
import java.util.Comparator;
import java.util.concurrent.ConcurrentSkipListSet;

import android.database.Cursor;
import android.text.TextUtils;

import com.android.sqlite.table.Column;
import com.android.sqlite.table.Foreign;
import com.android.sqlite.table.Id;
import com.android.sqlite.table.Table;
import com.android.utils.Log;

/**
 * 根据泛型生成Sql的工具类
 * 
 * @author wangyang 2014-4-23 上午11:29:33
 */
public class SqlBuilder {
	/**
	 * 表结构
	 */
	private Table table;

	/**
	 * 是否迭代父类字段
	 */
	private boolean isRecursion;

	public SqlBuilder(DBHelper db) {
		super();
	}

	public Table getTable() {
		return table;
	}

	private String getTableName(Class<?> entityType) {
		table = Table.get(entityType, false, false, isRecursion);
		return table == null ? null : table.getTableName();
	}

	/**
	 * 生成表
	 *
	 * @author wangyang
	 * 2014-7-16 上午11:32:33
	 * @param db
	 * @param entityType
	 */
	public void CreateTableIfNotExists(DBHelper db, Class<?> entityType) {
		if (entityType == null)
			return;

		// 判断表是否存在
		if (tableIsExist(db, entityType))
			return;

		// 生成表结构
		table = Table.get(entityType, true, true, isRecursion);

		if (table.getId() == null || table.getAttributes().size() == 0)
			table.addColumns(entityType, table.getId() == null, table.getAttributes().size() == 0);

		Id id = table.getId();
		Collection<Column> columns = table.getAttributes().values();

		// 根据表结构生成建表Sql并执行
		StringBuilder sqlBuffer = new StringBuilder();
		sqlBuffer.append("CREATE TABLE IF NOT EXISTS ");
		sqlBuffer.append(table.getTableName());
		sqlBuffer.append(" ( ");

		if (id != null && id.isAutoIncrement()) {
			sqlBuffer.append("\"").append(id.getColumnName()).append("\"  ").append("INTEGER PRIMARY KEY AUTOINCREMENT,");
		} else {
			sqlBuffer.append("\"").append(id.getColumnName()).append("\"  ").append(id.getColumnType()).append(" PRIMARY KEY,");
		}

		for (Column column : columns) {

			sqlBuffer.append("\"").append(column.getColumnName()).append("\"  ");
			sqlBuffer.append(column.getColumnType());

			if (column instanceof Foreign) {
				sqlBuffer.append(",");
				continue;
			}

			if (column.isUnique())
				sqlBuffer.append(" UNIQUE");

			if (column.isNotNull())
				sqlBuffer.append(" NOT NULL");

			String check = column.getCheck();
			if (!TextUtils.isEmpty(check)) {
				sqlBuffer.append(" CHECK(").append(check).append(")");
			}
			sqlBuffer.append(",");
		}

		sqlBuffer.deleteCharAt(sqlBuffer.length() - 1);
		sqlBuffer.append(" )");
		Log.e(sqlBuffer.toString());
		db.execute(sqlBuffer.toString());

		String sql = table.getExecAfterTableCreated();
		Log.e("ExecAfterTableCreated=" + sql);
		if (!TextUtils.isEmpty(sql))
			db.execute(sql);

		createTables.add(entityType);
	}

	/**
	 * 已经创建好表的缓存
	 */
	private ConcurrentSkipListSet<Class<?>> createTables = new ConcurrentSkipListSet<Class<?>>(new Comparator<Class<?>>() {
		@Override
		public int compare(Class<?> object1, Class<?> object2) {
			return object1.hashCode() - object2.hashCode();
		}
	});

	/**
	 * 判断表是否存在
	 *
	 * @author wangyang
	 * 2014-7-16 上午11:34:04
	 * @param db
	 * @param entityType
	 * @return
	 */
	public boolean tableIsExist(DBHelper db, Class<?> entityType) {
		boolean result = false;

		// 判断缓存是否存在
		if (createTables.contains(entityType)) {
			result = true;
			return result;
		}

		// 查询数据库是否已经创建
		Cursor cursor = db.rawQuery("SELECT * FROM sqlite_master WHERE type ='table' AND name ='" + getTableName(entityType) + "'");
		if (cursor != null && cursor.moveToNext()) {
			result = true;
			createTables.add(entityType);
		}

		cursor.close();
		return result;
	}
}
