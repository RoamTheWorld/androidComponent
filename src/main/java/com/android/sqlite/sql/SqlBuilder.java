package com.android.sqlite.sql;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListSet;

import android.database.Cursor;
import android.text.TextUtils;

import com.android.sqlite.DBHelper;
import com.android.sqlite.table.Column;
import com.android.sqlite.table.Finder;
import com.android.sqlite.table.Foreign;
import com.android.sqlite.table.Id;
import com.android.sqlite.table.Table;
import com.android.utils.Log;
import com.android.utils.StringUtil;

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
	 * 数据库操作对象
	 */
	private DBHelper db;

	/**
	 * 是否迭代父类字段
	 */
	private boolean isRecursion;

	public SqlBuilder(DBHelper db) {
		super();
		this.db = db;
		isRecursion = true;
	}

	public boolean isRecursion() {
		return isRecursion;
	}

	public void setRecursion(boolean isRecursion) {
		this.isRecursion = isRecursion;
	}

	/**
	 * 根据泛型对象生成表结构实体
	 * 
	 * @author wangyang 2014-4-23 上午10:20:39
	 * @param entity
	 * @param m
	 * @param isOnlyKey
	 * @param type
	 * @throws IllegalAccessException
	 */
	public void generateTable(Class<?> clazz, Object entity, boolean isOnlyKey, boolean isAddForegin) throws IllegalAccessException {
		table = Table.get(clazz, true, true, isRecursion);
		if (entity == null)
			return;

		if (isOnlyKey)
			table.generateTableValueOnlyId(entity);
		else
			table.generateTableValue(entity, isAddForegin ? db : null);
	}

	public Table getTable() {
		return table;
	}

	private String getTableName(Class<?> entityType) {
		table = Table.get(entityType, false, false, isRecursion);
		return table == null ? null : table.getTableName();
	}

	/**
	 * 生成InsertSql,
	 * 
	 * @author wangyang
	 * @param entity
	 * @2014-3-3 上午11:26:04
	 * @param m
	 * @return
	 * @throws IllegalAccessException
	 */
	public String generateInsertSql(Class<?> clazz, Object entity, Object[] columns, Object[] foregineValues, String suffix) throws IllegalAccessException {
		if (entity == null || clazz == null || clazz == Object.class)
			return null;

		// 生成要操作的表结构实体
		generateTable(clazz, entity, false, true);

		String tableName = getTableName(clazz);
		if (TextUtils.isEmpty(tableName) || (table.getId() == null && table.getAttributes().size() == 0))
			return null;

		String sql = suffix + " into " + tableName + "( ";

		// 遍历字段名
		sql = generateInsertId(sql, table.getId(), false);
		sql = generateInsertColoumns(sql, table.getAttributes(), false);
		// 遍历外键名
		if (columns != null && foregineValues != null && columns.length == foregineValues.length)
			for (Object column : columns)
				sql += column + ", ";
		// 过滤掉多余的逗号
		sql = sql.substring(0, sql.lastIndexOf(",")) + ") values( ";

		// 遍历字段值
		sql = generateInsertId(sql, table.getId(), true);
		sql = generateInsertColoumns(sql, table.getAttributes(), true);

		// 遍历外键值
		if (columns != null && foregineValues != null && columns.length == foregineValues.length)
			for (Object foregineValue : foregineValues)
				sql += " '" + foregineValue + "', ";

		// 过滤掉多余的逗号
		sql = sql.substring(0, sql.lastIndexOf(",")) + ") ";
		table.clearValue();
		return sql;
	}

	private String generateId(String sql, Id id, boolean isWhereClause) {
		// 如果指为空,则跳过
		if (id.getValue() == null || TextUtils.isEmpty(id.getValue().toString()))
			return sql;

		String arg = id.getColumnName() + " ='" + id.getValue() + "' ";

		// 拼接SQL
		if (arg != null) {
			sql = generateSqlByIsWhereClause(sql, isWhereClause, arg);
		}
		return sql;
	}

	/**
	 * 根据泛型对象生成Update SQL语句
	 * 
	 * @author wangyang
	 * @param entity
	 * @param m
	 * @return
	 * @throws IllegalAccessException
	 * @Describe
	 */
	public String generateUpdateSql(Class<?> clazz, Object entity, Object[] columnNames, Object[] values) throws IllegalAccessException {
		if (clazz == null) {
			if (entity == null)
				return null;
			else
				clazz = entity.getClass();
		}

		// 根据泛型生成BeanObject数据库结构类
		generateTable(clazz, entity, false, false);

		String tableName = getTableName(clazz);
		if (TextUtils.isEmpty(tableName) || table.getAttributes().size() == 0)
			return null;

		String sql = "Update  " + tableName + " set ";

		// 遍历修改字段
		sql = generateColoumns(sql, table.getAttributes(), false, false);

		if (!sql.contains(","))
			return null;

		// 过滤掉多余的逗号
		sql = sql.substring(0, sql.lastIndexOf(",")) + " where 1=1 ";

		// 遍历主键
		sql = generateId(sql, table.getId(), true);

		// 遍历值
		if (columnNames != null && values != null && columnNames.length == values.length)
			for (int i = 0; i < columnNames.length; i++) {
				if (StringUtil.isEmpty(columnNames[i]) || StringUtil.isEmpty(values[i]))
					continue;
				sql += "and " + columnNames[i] + "='" + values[i] + "'";
			}
		table.clearValue();
		return sql;
	}

	/**
	 * 根据sql,BeanColoumn集合,及是否为where条件拼接sql
	 * 
	 * @author wangyang
	 * @2014-3-3 上午11:32:11
	 * @param sql
	 * @param list
	 * @param isWhereClause
	 * @return
	 */
	private String generateColoumns(String sql, Map<String, ? extends Column> map, boolean isQuery, boolean isWhereClause) {
		// 遍历BeanColoumn集合
		for (com.android.sqlite.table.Column coloumn : map.values()) {
			if (isQuery && coloumn.ignoreQuery())
				continue;
			// 如果指为空,则跳过
			Object value = coloumn.getValue();
			if (value == null || TextUtils.isEmpty(value.toString()))
				continue;

			String arg = coloumn.getColumnName() + " ='" + value + "' ";

			// 拼接SQL
			if (arg != null) {
				sql = generateSqlByIsWhereClause(sql, isWhereClause, arg);
			}
		}
		return sql;
	}

	/**
	 * 根据是否为where条件拼接SQL
	 * 
	 * @author wangyang
	 * @2014-3-3 上午11:38:31
	 * @param sql
	 * @param isWhereClause
	 * @param arg
	 * @return
	 */
	private String generateSqlByIsWhereClause(String sql, boolean isWhereClause, String arg) {
		if (isWhereClause)
			sql += " and " + arg;
		else
			sql += arg + ", ";
		return sql;
	}

	/**
	 * 根据字段集合,生成插入SQL
	 * 
	 * @author wangyang
	 * @2014-3-3 上午11:39:02
	 * @param sql
	 * @param list
	 * @param isValue
	 * @return
	 */
	private String generateInsertId(String sql, Id id, boolean isValue) {
		// 跳过空值
		if (id.getValue() == null || TextUtils.isEmpty(id.getValue().toString()))
			return sql;

		// 判断是字段名还是字段值操作
		if (isValue)
			sql += " '" + id.getValue() + "', ";
		else
			sql += id.getColumnName() + ", ";

		return sql;
	}

	/**
	 * 根据字段集合,生成插入SQL
	 * 
	 * @author wangyang
	 * @2014-3-3 上午11:39:02
	 * @param sql
	 * @param list
	 * @param isValue
	 * @return
	 */
	private String generateInsertColoumns(String sql, Map<String, ? extends Column> map, boolean isValue) {
		for (Column coloumn : map.values()) {

			// 跳过空值
			Object value = coloumn.getValue();
			if (value == null || TextUtils.isEmpty(value.toString()))
				continue;

			// 判断是字段名还是字段值操作
			if (isValue)
				sql += " '" + value + "', ";
			else
				sql += coloumn.getColumnName() + ", ";
		}
		return sql;
	}

	/**
	 * 根据泛型对象生成Delete SQL语句
	 * 
	 * @author wangyang
	 * @param m
	 * @return
	 * @throws IllegalAccessException
	 * @Describe
	 */
	public String generateDeleteSql(Class<?> clazz, Object entity, String columnName, Object value) throws IllegalAccessException {
		if (clazz == null) {
			if (entity == null)
				return null;
			else
				clazz = entity.getClass();
		}

		String sql = generateDeleteSql(clazz, columnName, value);
		// 根据泛型生成BeanObject数据库结构类
		generateTable(clazz, entity, true, true);

		if (table.getId() == null && table.getAttributes().size() == 0)
			return sql;

		// 遍历除主键生成where条件
		sql = generateId(sql, table.getId(), true);
		sql = generateColoumns(sql, table.getAttributes(), false, true);

		table.clearValue();
		return sql;
	}

	public String generateDeleteSql(Object object, String column, Object value) {
		String tableName = object.toString();
		if (object instanceof Class) {
			tableName = getTableName((Class<?>) object);

			if (StringUtil.isEmpty(column))
				column = table.getId().getColumnName();
		}
		String sql = "DELETE  FROM " + tableName + " WHERE 1=1 ";

		if (!StringUtil.isEmpty(column) && !StringUtil.isEmpty(value))
			sql += "and " + column + "='" + value + "'";
		return sql;
	}

	/**
	 * 根据泛型对象生成QUERY SQL语句
	 * 
	 * @author wangyang
	 * @param m
	 * @return
	 * @throws IllegalAccessException
	 * @Describe
	 */
	public String generateQuerySql(Class<?> clazz, Object entity, String[] columnNames, Object[] values) throws IllegalAccessException {
		if (clazz == null) {
			if (entity == null)
				return null;
			else
				clazz = entity.getClass();
		}

		generateTable(clazz, entity, false, false);

		String tableName = getTableName(clazz);
		if (TextUtils.isEmpty(tableName))
			return null;

		String sql = "SELECT * FROM  " + tableName + " where 1=1";

		// 遍历值
		if (columnNames != null && values != null && columnNames.length == values.length)
			for (int i = 0; i < columnNames.length; i++) {
				if (StringUtil.isEmpty(columnNames[i]) || StringUtil.isEmpty(values[i]))
					continue;
				sql += " and " + columnNames[i] + "='" + values[i] + "'";
			}

		if (table.getId() == null && table.getAttributes().size() == 0)
			return sql;

		// 生成where条件
		sql = generateColoumns(sql, table.getAttributes(), true, true);
		sql = generateId(sql, table.getId(), true);
		table.clearValue();
		return sql;
	}

	/**
	 * 生成表
	 * 
	 * @author wangyang 2014-7-16 上午11:32:33
	 * @param db
	 * @param entityType
	 */
	public void CreateTableIfNotExists(Class<?> entityType) {
		if (entityType == null)
			return;

		// 判断表是否存在
		if (tableIsExist(entityType))
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

		createTables.add(table.getTableName());
	}

	/**
	 * 已经创建好表的缓存
	 */
	public static ConcurrentSkipListSet<String> createTables = new ConcurrentSkipListSet<String>();

	/**
	 * 判断表是否存在
	 * 
	 * @author wangyang 2014-7-16 上午11:34:04
	 * @param db
	 * @param entityType
	 * @return
	 */
	public boolean tableIsExist(Object object) {
		boolean result = false;

		String tableName = object.toString();
		if (object instanceof Class)
			tableName = getTableName((Class<?>) object);

		// 判断缓存是否存在
		if (createTables.contains(tableName)) {
			result = true;
			return result;
		}
		String sql = "SELECT * FROM sqlite_master WHERE type ='table' AND name ='" + tableName + "'";

		// 查询数据库是否已经创建
		Cursor cursor = db.rawQuery(sql);
		if (cursor != null && cursor.moveToNext()) {
			result = true;
			createTables.add(tableName);
		}

		cursor.close();
		return result;
	}

	/**
	 * 根据表名,Finder集合生成中间表
	 * 
	 * @author wangyang 2014-8-8 上午11:59:20
	 * @param db
	 * @param tableName
	 * @param finders
	 */
	public static void CreateTableIfNotExists(DBHelper db, String tableName, Finder... finders) {
		if (StringUtil.isEmpty(tableName))
			return;

		// 判断表是否存在
		if (new SqlBuilder(db).tableIsExist(tableName))
			return;

		// 根据表结构生成建表Sql并执行
		StringBuilder sqlBuffer = new StringBuilder();
		sqlBuffer.append("CREATE TABLE IF NOT EXISTS ");
		sqlBuffer.append(tableName);
		sqlBuffer.append(" ( ");
		sqlBuffer.append("\"").append("id").append("\"  ").append("INTEGER PRIMARY KEY AUTOINCREMENT,");

		for (Finder finder : finders) {
			sqlBuffer.append("\"").append(finder.getTargetColumnName()).append("\"  ");
			sqlBuffer.append(finder.getColumnType());
			sqlBuffer.append(" NOT NULL");
			sqlBuffer.append(",");
		}

		sqlBuffer.deleteCharAt(sqlBuffer.length() - 1);
		sqlBuffer.append(" )");
		Log.e(sqlBuffer.toString());
		db.execute(sqlBuffer.toString());

		createTables.add(tableName);
	}

	/**
	 * 根据表名,字段名,字段值 生成SQL
	 * 
	 * @author wangyang 2014-8-12 下午2:27:05
	 * @param tableName
	 * @param columns
	 * @param values
	 * @return
	 */
	public static String generateInsertSql(String tableName, String[] columns, Object[] values) {
		String sql = "insert into " + tableName;
		sql += " (";
		for (int i = 0; i < columns.length; i++) {
			sql += " " + columns[i] + ",";
		}
		sql = sql.substring(0, sql.lastIndexOf(",")) + ") values( ";
		for (int i = 0; i < values.length; i++) {
			sql += " '" + values[i] + "',";
		}
		sql = sql.substring(0, sql.lastIndexOf(",")) + ") ";

		return sql;
	}
}
