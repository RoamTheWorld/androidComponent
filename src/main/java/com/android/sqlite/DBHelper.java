package com.android.sqlite;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.android.sqlite.handler.BeanHandler;
import com.android.sqlite.sql.ModelSelector;
import com.android.sqlite.sql.Selector;
import com.android.sqlite.sql.SqlBuilder;
import com.android.sqlite.table.Column;
import com.android.sqlite.table.EntityTempCache;
import com.android.sqlite.table.Finder;
import com.android.sqlite.table.Foreign;
import com.android.sqlite.table.Model;
import com.android.sqlite.table.Table;
import com.android.utils.Log;
import com.android.utils.StringUtil;

/**
 * 继承自SQLiteOpenHelper的一个帮助类。
 * 
 * @author wangyang 2014-7-3 下午4:40:51
 */
@SuppressWarnings("unchecked")
public class DBHelper extends SQLiteOpenHelper {

	private final static String TAG = "DBHelper";

	/**
	 * String[] mSql_ddl 一个类成员变量,主要用于类初始化时赋值, 并在onCreate方法中循环执行SQL。
	 */
	private String[] mSql_ddl;

	/**
	 * 上下文
	 */
	private Context context;
	/**
	 * 保存当前线程数据库
	 */
	private static ConcurrentHashMap<String, DBHelper> connections = new ConcurrentHashMap<String, DBHelper>();

	/**
	 * 数据库
	 */
	private SQLiteDatabase db;

	/**
	 * 数据库变化监听
	 */
	private OnUpGradeListener listener;

	/**
	 * 生成SQL的Builder
	 */
	private SqlBuilder sqlBuilder;

	/**
	 * 是否开启事务
	 */
	private boolean isTransaction;
	/**
	 * 是否开启事务
	 */
	private boolean transaction;

	/**
	 * 是否开启事务
	 */
	private boolean innerTransaction;
	/**
	 * 是否正在事务
	 */
	private boolean isTransacting = false;

	/**
	 * 是否迭代插入父类表
	 */
	private boolean isRecursion;

	/**
	 * 是否迭代生成父类字段
	 */
	private boolean isGenerateRecursion;

	public boolean isRecursion() {
		return isRecursion;
	}

	public boolean isGenerateRecursion() {
		return isGenerateRecursion;
	}

	public void setRecursion(boolean isRecursion) {
		this.isRecursion = isRecursion;
	}

	public void setInnerTransaction(boolean innerTransaction) {
		this.innerTransaction = innerTransaction;
	}

	public void setGenerateRecursion(boolean isGenerateRecursion) {
		this.isGenerateRecursion = isGenerateRecursion;
		if (sqlBuilder != null)
			sqlBuilder.setRecursion(isGenerateRecursion);
	}

	/**
	 * 数据库变化监听类
	 * 
	 * Copyright: 版权所有 (c) 2014 Company: 北京开拓明天科技有限公司
	 * 
	 * @author wangyang 2014-4-30 下午3:01:30
	 */
	public interface OnUpGradeListener {
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion, String... sqls);
	}

	/**
	 * 设置监听
	 * 
	 * @author wangyang 2014-4-30 下午3:01:49
	 * @param listener
	 */
	public void setListener(OnUpGradeListener listener) {
		this.listener = listener;
	}

	/**
	 * 
	 * @param context
	 *            上下文对象
	 * @param dbName
	 *            数据库名称
	 * @param version
	 *            数据库版本
	 * @param mSql_ddl
	 *            创建数据库的Sql
	 * @param listener
	 *            数据库变化监听
	 * @param autoClose
	 *            () 自动关闭数据库
	 * @param isTransaction
	 *            是否开启事务
	 * @param isRecursion
	 *            是否迭代父类
	 */
	private DBHelper(Context context, String dbName, int version, OnUpGradeListener listener, boolean isRecursion, boolean isGenerateRecursion, String... mSql_ddl) {
		super(context, dbName, null, version);
		this.context = context;
		this.mSql_ddl = mSql_ddl;
		this.listener = listener;
		this.isRecursion = isRecursion;
		this.isTransaction = false;
		this.isGenerateRecursion = isGenerateRecursion;
		sqlBuilder = new SqlBuilder(this);
		setGenerateRecursion(isGenerateRecursion);
	}

	public synchronized static DBHelper getInstance(Context context, String dbName, int version, OnUpGradeListener listener, boolean isRecursion, boolean isGenerateRecursion, String... mSql_ddl) {
		DBHelper helper;
		if (connections.containsKey(dbName)) {
			helper = connections.get(dbName);
		} else {
			helper = new DBHelper(context, dbName, version, listener, isRecursion, isGenerateRecursion, mSql_ddl);
			connections.put(dbName, helper);
		}
		return helper;
	}

	public static DBHelper getInstance(Context context, int version, String dbName, OnUpGradeListener listener, String[] ddl) {
		return getInstance(context, dbName, version, listener, false, true, ddl);
	}

	public static DBHelper getInstance(Context context, int version, String dbName) {
		return getInstance(context, dbName, version, null, false, true);
	}

	public static DBHelper getInstance(Context context, int version, String dbName, String... mSql_ddl) {
		return getInstance(context, dbName, version, null, false, true, mSql_ddl);
	}

	public static DBHelper getInstance(Context context, int version, String dbName, boolean isRecursion) {
		return getInstance(context, dbName, version, null, isRecursion, true);
	}

	public static DBHelper getInstance(Context context, String dbName) {
		return getInstance(context, 1, dbName);
	}

	/**
	 * 
	 * @auther wangyang
	 * @param db
	 *            类初始化后建立数据库后调用的方法,如果成员变量mSql_ddl数组不为空,
	 *            则循环执行SQL,此变量主要用于初始化数据库时候执行一些DLL建表SQL。
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		init_ddlSql(db);
	}

	/**
	 * 
	 * @author wangyang
	 * @param db
	 *            如果ddlSql数组不为空,则遍历数组,并依次执行
	 */
	private void init_ddlSql(SQLiteDatabase db) {
		if (mSql_ddl != null && mSql_ddl.length > 0) {
			for (int i = 0; i < mSql_ddl.length; i++) {
				if (!TextUtils.isEmpty(mSql_ddl[i]))
					db.execSQL(mSql_ddl[i]);
			}
		}
	}

	/**
	 * 
	 * @auther wangyang
	 * @param db
	 *            数据库
	 * @param oldVersion
	 *            老版本号
	 * @param newVersion
	 *            新版本号 数据库的版本号变化时运行此方法,主要用于数据库版本更换时的数据移植。
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (listener != null)
			listener.onUpgrade(db, oldVersion, newVersion, mSql_ddl);
	}

	/**
	 * 获取数据库
	 * 
	 * @author wangyang
	 * @2014-3-6 下午5:09:12
	 */
	private synchronized void getDatabase() {
		if (db == null || !db.isOpen()) {
			db = getWritableDatabase();
		}
	}

	/**
	 * 
	 * @auther wangyang
	 * @param sql
	 *            要执行的SQL
	 * @param args
	 *            可变Object参数 此方法用于执行自己写的SQL,参数为可选参数,
	 *            如果没有传入参数则直接执行SQL,否则加上参数一起运行。
	 */
	public synchronized void execute(String sql, Object... args) {
		getDatabase();
		if (args == null)
			db.execSQL(sql);
		else
			db.execSQL(sql, args);
	}

	/**
	 * 开启内部事务,并且设置当前事务正在执行中,避免重复开启
	 * 
	 * @author wangyang 2014-4-30 下午3:03:26
	 */
	public void beginInnerTransaction() {
		if (!innerTransaction && !isTransacting) {
			getDatabase();
			isTransacting = true;
			innerTransaction = true;
			db.beginTransaction();
		}
	}

	/**
	 * 提交内部事务
	 * 
	 * @author wangyang 2014-4-30 下午3:03:36
	 */
	public void setInnerTransactionSuccessful() {
		if (innerTransaction && isTransacting)
			db.setTransactionSuccessful();
	}

	/**
	 * 结束内部事务,并且更新当前事务状态
	 * 
	 * @author wangyang 2014-4-30 下午3:03:43
	 */
	public void endInnerTransaction() {
		if (innerTransaction && isTransacting) {
			isTransacting = false;
			innerTransaction = false;
			db.endTransaction();
		}
	}

	/**
	 * 开启内部事务,并且设置当前事务正在执行中,避免重复开启
	 * 
	 * @author wangyang 2014-4-30 下午3:03:26
	 */
	public void beginOuterTransaction() {
		if (!transaction && !isTransacting) {
			getDatabase();
			isTransacting = true;
			transaction = true;
			db.beginTransaction();
		}
	}

	/**
	 * 提交内部事务
	 * 
	 * @author wangyang 2014-4-30 下午3:03:36
	 */
	public void setOuterTransactionSuccessful() {
		if (transaction && isTransacting)
			db.setTransactionSuccessful();
	}

	/**
	 * 结束内部事务,并且更新当前事务状态
	 * 
	 * @author wangyang 2014-4-30 下午3:03:43
	 */
	public void endOuterTransaction() {
		if (transaction && isTransacting) {
			isTransacting = false;
			transaction = false;
			db.endTransaction();
		}
	}

	/**
	 * 开启事务,并且更新当前事务状态为执行中,避免重复开启
	 * 
	 * @author wangyang 2014-4-30 下午3:03:26
	 */
	public void beginTransaction() {
		if (!isTransaction && !isTransacting) {
			isTransaction = true;
			isTransacting = true;
			getDatabase();
			db.beginTransaction();
		}
	}

	/**
	 * 提交事务
	 * 
	 * @author wangyang 2014-4-30 下午3:03:36
	 */
	public void setTransactionSuccessful() {
		if (isTransaction && isTransacting)
			db.setTransactionSuccessful();
	}

	/**
	 * 结束事务,并且设置当前事务正在执行中,避免重复开启
	 * 
	 * @author wangyang 2014-4-30 下午3:03:43
	 */
	public void endTransaction() {
		if (isTransaction && isTransacting) {
			db.endTransaction();
			isTransacting = false;
			isTransaction = false;
		}
	}

	/**
	 * 关闭数据库
	 */
	@Override
	public void close() {
		if (db != null) {
			if (db.isOpen()) {
				db.close();
			}
			db = null;
		}
	}

	/**
	 * 关闭数据库,并且情况缓存
	 * 
	 * @author wangyang 2014-7-3 下午4:46:11
	 */
	public static void closeDbIfOpen(String dbName) {
		closeDb(dbName);
	}

	private static void closeDb(String dbName) {
		DBHelper helper = connections.get(dbName);
		if (helper != null && helper.db != null) {
			if (helper.db.isOpen()) {
				helper.db.close();
			}
			helper.db = null;
		}
	}

	public static void clearCache() {
		EntityTempCache.clear();
		Table.tableMap.clear();
		SqlBuilder.createTables.clear();
	}

	public static void closeDbAndCacheIfOpen(String dbName) {
		closeDb(dbName);
		clearCache();
	}

	/**
	 * 判断是否为自动关闭数据库
	 * 
	 * @author wangyang 2014-7-3 下午4:46:28
	 * @return
	 */
	private boolean autoClose() {
		return !isTransacting;
	}

	/**
	 * 设置为自动关闭
	 * 
	 * @author wangyang 2014-7-21 下午3:58:00
	 */
	public void setAutoClose() {
		isTransacting = false;
	}

	/**
	 * 
	 * @author wangyang
	 * @param sql
	 * @param selectionArgs
	 * @return Cursor 根据sql与查询条件获得Cursor结果集
	 */
	public synchronized Cursor rawQuery(String sql, String... selectionArgs) {
		getDatabase();
		return db.rawQuery(sql, selectionArgs);
	}

	public boolean saveOrUpdate(Class<?> clazz, Object entity) {
		return saveOrUpdate(clazz, entity, null, null);
	}

	public boolean saveOrUpdate(Object entity) {
		return saveOrUpdate(entity.getClass(), entity, null, null);
	}

	public boolean saveOrUpdate(List<?> list) {
		return saveOrUpdate(list.get(0).getClass(), list, null, null);
	}

	public boolean saveOrUpdate(Class<?> clazz, List<?> list) {
		return saveOrUpdate(clazz, list, null, null);
	}

	/**
	 * 对传入实体进行保存或者修改操作,具体根据是否存在id值操作,如果设置了事务,则进入事务
	 * 
	 * @author wangyang 2014-4-30 下午3:04:04
	 * @param entity
	 * @return
	 */
	public synchronized boolean saveOrUpdate(Class<?> clazz, Object entity, String column, Object foreginValue) {
		if (entity == null)
			return false;

		boolean result = true;
		try {
			beginOuterTransaction();
			if (entity instanceof List) {
				List<?> list = (List<?>) entity;
				if (list == null || list.size() == 0)
					return true;
				for (Object object : list) {
					if (!saveOrUpdateWithInnerTransaction(clazz, object, column, foreginValue)) {
						result = false;
						break;
					}
				}
			} else {
				result = saveOrUpdateWithInnerTransaction(clazz, entity, column, foreginValue);
			}
			setOuterTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			endOuterTransaction();
			if (autoClose())
				close();
		}

		return result;
	}

	/**
	 * 对传入集合进行保存或者修改操作,具体根据是否存在id值操作,如果设置了事务,则进入事务
	 * 
	 * @author wangyang 2014-4-30 下午3:07:23
	 * @param list
	 * @return
	 */
	public boolean saveOrUpdateWithInnerTransaction(Class<?> clazz, Object entity, String columnName, Object foreginValue) {
		boolean result = true;
		if (entity == null)
			return false;
		try {
			beginInnerTransaction();
			// 初始化字段名,与字段值集合
			List<String> columns = new ArrayList<String>();
			List<Object> foreginValues = new ArrayList<Object>();

			// 如果字段名与值都不为空,则添加至集合
			if (!StringUtil.isEmpty(columnName) && !StringUtil.isEmpty(foreginValue)) {
				columns.add(columnName);
				foreginValues.add(foreginValue);
			}

			// 判断是否需要迭代插入父类表
			if (isRecursion && clazz.getSuperclass() != Object.class) {
				// 创建父类表
				sqlBuilder.CreateTableIfNotExists(clazz.getSuperclass());

				/*
				 * Object superEntity = entity; if
				 * (sqlBuilder.getTable().getId().getColumnValue(entity) ==
				 * null) superEntity = query(clazz.getSuperclass(), superEntity,
				 * null, null); if (superEntity == null) superEntity = entity;
				 */

				// 插入数据至父类表,如果结果为true则将于子类表关联的字段添加至字段与值的集合,否则返回false
				if (saveOrUpdateWithInnerTransaction(clazz.getSuperclass(), entity, null, null))
					generateSuperClassForegin(clazz, entity, columns, foreginValues);
				else
					return false;
			}

			// 创建表
			sqlBuilder.CreateTableIfNotExists(clazz);

			// 如果插入失败,返回false,否则插入当前表的外键表数据
			if (!saveOrUpdateWithOutTransaction(clazz, entity, columns.toArray(new String[] {}), foreginValues.toArray())) {
				result = false;
			} else {
				/*
				 * if (isRecursion && clazz.getSuperclass() != Object.class) {
				 * Table table = Table.get(clazz, false, true, isRecursion); if
				 * (table != null && table.getFinders().size() > 0) { for
				 * (Finder finder : table.getFinders().values()) { if
				 * (finder.isSuperClass()) { if
				 * (!saveOrUpdateWithInnerTransaction(clazz.getSuperclass(),
				 * entity, null, null)) return false; } } } }
				 */

				if (!saveOrUpdateFindersWithOutTransaction(clazz, entity) && (transaction || innerTransaction || isTransaction))
					result = false;
			}

			if (result)
				setInnerTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			endInnerTransaction();
			if (autoClose())
				close();
		}
		return result;
	}

	private void generateSuperClassForegin(Class<?> clazz, Object entity, List<String> columns, List<Object> foreginValues) {
		Foreign foreign = null;

		// 获取clazz对应的表结构
		Table table = Table.get(clazz, false, true, isGenerateRecursion);

		// 遍历字段,取得父类外键,
		for (Column col : table.getAttributes().values()) {
			if (col instanceof Foreign) {
				if (((Foreign) col).isSuperClass()) {
					foreign = (Foreign) col;
					break;
				}
			}
		}

		// 如果外键不为null,则向字段名与值集合中添加
		if (foreign != null) {
			// 获取外键在父类表对应的字段,并获取值
			Column column = Table.get(clazz.getSuperclass(), true, true, isGenerateRecursion).getColumnByColumnName(foreign.getForeignColumnName());
			Object value = column.getColumnValue(entity);

			// 如果值为null,则去数据库查询
			if (value == null)
				value = column.getColumnValue(query(clazz.getSuperclass(), entity, null, null));

			// 将字段名与字段值添加至集合
			columns.add(foreign.getColumnName());
			foreginValues.add(value);
		}
	}

	/**
	 * 对传入实体进行保存或者修改操作,具体根据是否存在id值操作,没有事务
	 * 
	 * @author wangyang 2014-4-30 下午3:04:38
	 * @param entity
	 * @return
	 */
	public boolean saveOrUpdateWithOutTransaction(Class<?> clazz, Object entity, String[] columnNames, Object[] foreginValues) {
		// 生成表结构,并获取Id的值
		Table table = Table.get(clazz, true, false, isGenerateRecursion);
		table.generateTableValueOnlyId(entity);

		// 如果主键值为非自增,则进入replace方法
		boolean isUpdate = true;
		if (!table.getId().isAutoIncrement())
			return replace(clazz, entity, columnNames, foreginValues);

		// 如果主键为自增,并且值为null,进行insert操作,值不为null进行update操作
		if (table.getId().getColumnValue(entity) == null || TextUtils.isEmpty(table.getId().getColumnValue(entity).toString()))
			isUpdate = false;

		if (isUpdate)
			return update(clazz, entity, null, null);
		else
			return insert(clazz, entity, columnNames, foreginValues);
	}

	/**
	 * 插入实体的外键表
	 * 
	 * @author wangyang 2014-7-3 下午4:47:21
	 * @param clazz
	 * @param entity
	 * @return
	 */
	private boolean saveOrUpdateFindersWithOutTransaction(Class<?> clazz, Object entity) {
		boolean isSave = true;

		// 生成表结构,如果没有外键字段,则直接返回
		Table table = Table.get(clazz, false, true, isGenerateRecursion);
		if (table.getFinders().size() == 0)
			return isSave;

		// 遍历所有外键字段
		for (Finder finder : table.getFinders().values()) {
			// 如果外键值为null则跳过
			if (finder.getFieldValue(entity) == null)
				continue;

			// 获取当前表的外键字段,并且获得字段值
			Column column = sqlBuilder.getTable().getColumnByColumnName(finder.getColumnName());
			Object finderValue = column.getColumnValue(entity);

			// 如果外键字段值为null,则去数据库查询
			if (finderValue == null)
				finderValue = column.getColumnValue(query(clazz, entity, null, null));

			// 如果插入失败,直接返回
			if (!finder.saveFinder2Db(entity, finderValue, this)) {
				isSave = false;
				break;
			}
		}
		return isSave;
	}

	/**
	 * 将传入实体插入数据库
	 * 
	 * @author wangyang 2014-4-30 下午3:07:51
	 * @param entity
	 * @return
	 */
	public boolean insert(Class<?> clazz, Object entity, String[] columnNames, Object[] foreginValues) {
		boolean isInsert = true;
		try {
			// 查询实体是否存在,如果存在则做修改操作
			Object queryEntity = query(clazz, entity, columnNames, foreginValues);
			if (queryEntity != null)
				return update(clazz, entity, new String[] { sqlBuilder.getTable().getId().getColumnName() }, new Object[] { sqlBuilder.getTable().getId().getColumnValue(queryEntity) });

			// 生成sql
			String sql = sqlBuilder.generateInsertSql(clazz, entity, columnNames, foreginValues, "insert");
			if (!TextUtils.isEmpty(sql)) {
				Log.e(TAG, sql);
				execute(sql);
			}
		} catch (Exception e) {
			e.printStackTrace();
			isInsert = false;
		} finally {
			if (autoClose())
				close();
		}
		return isInsert;
	}

	/**
	 * 将传入实体替换至数据库
	 * 
	 * @author wangyang 2014-4-30 下午3:08:58
	 * @param entity
	 * @return
	 */
	public boolean replace(Class<?> clazz, Object entity, Object[] columnNames, Object[] foreginValues) {
		boolean isReplace = true;
		try {
			// 生成sql
			String sql = sqlBuilder.generateInsertSql(clazz, entity, columnNames, foreginValues, "replace");
			if (!TextUtils.isEmpty(sql)) {
				Log.e(TAG, sql);
				execute(sql);
			}
		} catch (Exception e) {
			e.printStackTrace();
			isReplace = false;
		} finally {
			if (autoClose())
				close();
		}
		return isReplace;
	}

	public boolean update(Class<?> clazz, String sql) {
		if (TextUtils.isEmpty(sql))
			return false;
		try {
			sqlBuilder.CreateTableIfNotExists(clazz);
			beginInnerTransaction();
			Log.e(TAG, sql);
			execute(sql);
			EntityTempCache.remove(clazz);
			setInnerTransactionSuccessful();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			endInnerTransaction();
			if (autoClose())
				close();
		}
		return false;
	}

	/**
	 * 将传入实体生成Sql并做修改操作
	 * 
	 * @author wangyang 2014-4-30 下午3:09:11
	 * @param entity
	 * @return
	 */
	public boolean update(Class<?> clazz, Object entity, Object[] columnNames, Object[] foreginValues) {
		boolean isUpdate = true;
		try {
			// 生成sql
			String sql = sqlBuilder.generateUpdateSql(clazz, entity, columnNames, foreginValues);
			if (!TextUtils.isEmpty(sql)) {
				Log.e(TAG, sql);
				execute(sql);

				// 获取Id值,清除缓存
				Object idValue = sqlBuilder.getTable().getId().getColumnValue(entity);
				if (idValue == null) {
					for (int i = 0; i < foreginValues.length; i++) {
						if (sqlBuilder.getTable().getId().getColumnName().equals(columnNames[i].toString()))
							idValue = foreginValues[i];
					}
				}
				EntityTempCache.remove(clazz, idValue.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
			isUpdate = false;
		} finally {
			if (autoClose())
				close();
		}
		return isUpdate;
	}

	public boolean update(Object entity) {
		return update(entity.getClass(), entity, null, null);
	}

	/**
	 * 根据传入的实体,删除数据库的数据
	 * 
	 * @author wangyang 2014-4-30 下午3:21:10
	 * @param entity
	 * @return
	 */
	public boolean delete(Object entity) {
		if (entity == null)
			return false;

		boolean result = true;
		try {
			beginOuterTransaction();
			if (entity instanceof List) {
				List<?> list = (List<?>) entity;
				if (list == null || list.size() == 0)
					return false;
				for (Object object : list) {
					if (!deleteWithInnerTransaction(object.getClass(), object, null, null)) {
						result = false;
						break;
					}
				}
			} else {
				result = deleteWithInnerTransaction(entity.getClass(), entity, null, null);
			}
			setOuterTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			endOuterTransaction();
			if (autoClose())
				close();
		}
		return result;
	}

	/**
	 * 根据传入的实体集合,删除数据库的数据,如果设置了事务,则进入事务
	 * 
	 * @author wangyang 2014-4-30 下午3:22:10
	 * @param list
	 * @return
	 */
	public boolean deleteWithInnerTransaction(Class<?> clazz, Object entity, String columnName, Object value) {
		boolean result = true;
		try {
			sqlBuilder.CreateTableIfNotExists(clazz);
			beginInnerTransaction();

			/*
			 * 如果存在外键表,先删除外键表数据
			 */

			// 生成表结构,并判断是否存在外键
			Table table = Table.get(clazz, true, true, isGenerateRecursion);
			if (table.getFinders() != null && table.getFinders().size() > 0) {

				// 遍历外键集合
				for (Finder finder : table.getFinders().values()) {

					// 如果外键列的值为空,则查询数据库
					if (table.getColumnByColumnName(finder.getColumnName()).getColumnValue(entity) == null)
						entity = query(clazz, entity, new String[] { columnName }, new Object[] { value });

					// 获取外键列名与值
					String finderColumn = finder.getTargetColumnName();
					Object finderValue = table.getColumnByColumnName(finder.getColumnName()).getColumnValue(entity);

					if (!StringUtil.isEmpty(finder.getMany2Many()) && !finder.IsInverse()) {
						deleteWithOutTransaction(finder.getMany2Many(), finderColumn, finderValue);
					} else {
						if (!deleteWithInnerTransaction(finder.getForeignOrFinderEntityType(), null, finderColumn, finderValue))
							return false;
					}
				}
			}

			// 删除关联父类的表
			if (isRecursion && clazz.getSuperclass() != Object.class) {
				// 生成表结构
				table = Table.get(clazz, true, true, isGenerateRecursion);
				if (table.getAttributes().size() > 0) {
					// 遍历字段集合
					for (Column column : table.getAttributes().values()) {
						if (column instanceof Foreign) {
							Foreign foreign = (Foreign) column;
							// 如果外键字段是父类字段则一同删除父类表数据
							if (foreign.isSuperClass()) {
								Object fieldValue;
								if (entity == null)
									fieldValue = list(clazz, new String[] { columnName }, new Object[] { value });
								else
									fieldValue = foreign.getFieldObjectValue(entity, this);

								if (fieldValue instanceof List)
									return delete(fieldValue);
								else {
									if (!deleteWithInnerTransaction(clazz.getSuperclass(), fieldValue, null, null))
										return false;
								}
							}
						}
					}
				}
			}
			result = deleteWithOutTransaction(clazz, entity, columnName, value);
			setInnerTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			endInnerTransaction();
			if (autoClose())
				close();
		}
		return result;
	}

	public <T> T query(Class<T> clazz, int id) {
		return query(clazz, new String[] { Table.get(clazz, true, false, isGenerateRecursion).getId().getColumnName() }, new Object[] { id });
	}

	/**
	 * 根据Class与条件查询对象
	 * 
	 * @author wangyang 2014-7-3 下午6:10:36
	 * @param clazz
	 * @param columns
	 * @param values
	 * @return
	 */
	public <T> T query(Class<T> clazz, String[] columns, Object[] values) {
		try {
			// 创建表
			sqlBuilder.CreateTableIfNotExists(clazz);

			// 生成Sql
			String sql = sqlBuilder.generateQuerySql(clazz, null, columns, values);
			Log.e(TAG, sql);

			// 执行Sql,并封装结果集
			Cursor cursor = rawQuery(sql);
			BeanHandler<T> handler = new BeanHandler<T>();
			return handler.handler(this, cursor, clazz, autoClose(), isGenerateRecursion);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 根据Class与条件查询对象集合
	 * 
	 * @author wangyang 2014-7-3 下午6:11:17
	 * @param clazz
	 * @param columns
	 * @param values
	 * @return
	 */
	public <T> List<T> list(Class<T> clazz, String[] columns, Object[] values) {
		try {
			// 创建表
			sqlBuilder.CreateTableIfNotExists(clazz);

			// 生成Sql
			String sql = sqlBuilder.generateQuerySql(clazz, null, columns, values);
			Log.e(TAG, sql);

			// 执行Sql,并封装结果集
			Cursor cursor = rawQuery(sql);
			BeanHandler<T> handler = new BeanHandler<T>();
			return handler.handler2List(this, cursor, clazz, autoClose(), isGenerateRecursion);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 根据class与主键值查询集合
	 * 
	 * @author wangyang 2014-7-3 下午6:12:23
	 * @param clazz
	 * @param id
	 * @return
	 */
	public <T> List<T> list(Class<T> clazz, int id) {
		return list(clazz, new String[] { Table.get(clazz, true, false, isGenerateRecursion).getId().getColumnName() }, new Object[] { id });
	}

	public boolean delete(Class<?> clazz) {
		return delete(clazz, null, null);
	}

	public boolean delete(Class<?> clazz, Object value) {
		return delete(clazz, Table.get(clazz, true, false, isGenerateRecursion).getId().getColumnName(), value);
	}

	/**
	 * 根据Class与条件删除数据
	 * 
	 * @author wangyang 2014-7-3 下午6:13:03
	 * @param clazz
	 * @param column
	 * @param value
	 * @return
	 */
	public boolean delete(Class<?> clazz, String column, Object value) {
		boolean result = true;
		try {
			sqlBuilder.CreateTableIfNotExists(clazz);
			beginInnerTransaction();
			if (!deleteWithOutTransaction(clazz, column, value))
				result = false;
			setInnerTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			endInnerTransaction();
			if (autoClose())
				close();
		}
		return result;
	}

	/**
	 * 根据传入的实体集合,删除数据库的数据
	 * 
	 * @author wangyang 2014-4-30 下午3:22:51
	 * @param entity
	 * @return
	 */
	private boolean deleteWithOutTransaction(Class<?> clazz, Object entity, String columnName, Object value) {
		boolean isDelete = true;
		try {

			// 生成sql
			String sql = sqlBuilder.generateDeleteSql(clazz, entity, columnName, value);
			if (!TextUtils.isEmpty(sql)) {
				Log.e(TAG, sql);
				execute(sql);

				// 得到主键值,清空缓存
				if (entity == null)
					EntityTempCache.remove(clazz);
				else {
					Object idValue = sqlBuilder.getTable().getId().getColumnValue(entity);
					if (idValue == null) {
						if (sqlBuilder.getTable().getId().getColumnName().equals(columnName))
							idValue = value;
					}
					if (!StringUtil.isEmpty(idValue))
						EntityTempCache.remove(clazz, idValue.toString());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			isDelete = false;
		}
		return isDelete;
	}

	private boolean deleteWithOutTransaction(Object object, String column, Object id) {
		boolean isDelete = false;
		try {
			// 生成sql
			String sql = sqlBuilder.generateDeleteSql(object, column, id);
			if (!TextUtils.isEmpty(sql)) {
				Log.e(TAG, sql);

				// 执行Sql,清除缓存
				execute(sql);
				if (object instanceof Class)
					EntityTempCache.remove((Class<?>) object, id + "");
				isDelete = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isDelete;
	}

	/**
	 * 根据传入的实体作为查询条件,得到实体集合
	 * 
	 * @author wangyang 2014-4-30 下午3:22:58
	 * @param entity
	 * @return
	 */
	public <T> List<T> list(Class<T> clazz, Object entity, String[] columns, Object[] values) {
		try {
			// 创建表
			sqlBuilder.CreateTableIfNotExists(clazz);

			// 生成Sql
			String sql = sqlBuilder.generateQuerySql(clazz, entity, columns, values);
			Log.e(TAG, sql);

			// 执行Sql,封装结果集并返回
			Cursor cursor = rawQuery(sql);
			BeanHandler<T> handler = new BeanHandler<T>();
			return handler.handler2List(this, cursor, clazz, autoClose(), isGenerateRecursion);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 根据传入的sql,泛型Class以及参数作为查询条件,得到实体集合
	 * 
	 * @author wangyang 2014-4-30 下午3:24:09
	 * @param sql
	 * @param clazz
	 * @param args
	 * @return
	 */
	public <T> List<T> listBySql(String sql, Class<T> clazz, String... args) {
		Log.e(TAG, sql);

		// 创建表
		sqlBuilder.CreateTableIfNotExists(clazz);

		// 执行Sql,封装结果集并返回
		Cursor cursor = rawQuery(sql, args);
		BeanHandler<T> handler = new BeanHandler<T>();
		return handler.handler2List(this, cursor, clazz, autoClose(), isGenerateRecursion);
	}

	/**
	 * 根据传入的实体作为查询条件,得到实体
	 * 
	 * @author wangyang 2014-4-30 下午3:26:24
	 * @param entity
	 * @return
	 */
	public <T> T query(Class<T> clazz, Object entity, String[] columns, Object[] values) {
		try {
			// 创建表
			sqlBuilder.CreateTableIfNotExists(clazz);
			String sql = sqlBuilder.generateQuerySql(clazz, entity, columns, values);
			Log.e(TAG, sql);

			// 执行Sql,封装结果集并返回
			Cursor cursor = rawQuery(sql);
			BeanHandler<T> handler = new BeanHandler<T>();
			return handler.handler(this, cursor, clazz, autoClose(), isGenerateRecursion);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 根据实体作为查询条件,查询对象
	 * 
	 * @author wangyang 2014-7-3 下午6:27:04
	 * @param entity
	 * @return
	 */
	public <T> T query(Object entity) {
		return (T) query(entity.getClass(), entity, null, null);
	}

	/**
	 * 根据Class作为查询条件,查询对象
	 * 
	 * @author wangyang 2014-7-3 下午6:27:27
	 * @param clazz
	 * @return
	 */
	public <T> T query(Class<T> clazz) {
		sqlBuilder.CreateTableIfNotExists(clazz);
		return (T) queryBySql("SELECT * FROM " + Table.getTableName(clazz), clazz);
	}

	/**
	 * 根据Class作为查询条件,查询对象集合
	 * 
	 * @author wangyang 2014-7-3 下午6:27:41
	 * @param clazz
	 * @return
	 */
	public <T> List<T> list(Class<T> clazz) {
		sqlBuilder.CreateTableIfNotExists(clazz);
		return listBySql("SELECT * FROM " + Table.getTableName(clazz), clazz);
	}

	/**
	 * 根据实体作为查询条件,查询对象集合
	 * 
	 * @author wangyang 2014-7-3 下午6:27:54
	 * @param entity
	 * @return
	 */
	public <T> List<T> list(Object entity) {
		return (List<T>) list(entity.getClass(), entity, null, null);
	}

	/**
	 * 根据Selector作为查询条件,查询对象
	 * 
	 * @author wangyang 2014-7-3 下午6:28:10
	 * @param selector
	 * @return
	 */
	public Object query(Selector selector) {
		return queryBySql(selector.toString(), selector.getEntityType());
	}

	/**
	 * 根据Selector作为查询条件,查询对象集合
	 * 
	 * @author wangyang 2014-7-3 下午6:28:31
	 * @param selector
	 * @return
	 */
	public List<? extends Object> list(Selector selector) {
		return listBySql(selector.toString(), selector.getEntityType());
	}

	/**
	 * 根据传入的sql,泛型Class以及参数作为查询条件,得到实体
	 * 
	 * @author wangyang 2014-4-30 下午3:30:40
	 * @param sql
	 * @param object
	 * @param args
	 * @return
	 */
	public Object queryBySql(String sql, Object object, String... args) {
		try {
			Log.e(TAG, sql);
			Class<Object> clazz = (Class<Object>) object;
			sqlBuilder.CreateTableIfNotExists(clazz);
			Cursor cursor = rawQuery(sql, args);
			BeanHandler<Object> handler = new BeanHandler<Object>();
			return handler.handler(this, cursor, clazz, autoClose(), isGenerateRecursion);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 根据表名,字段名与字段值插入数据库
	 * 
	 * @author wangyang 2014-8-12 下午2:24:36
	 * @param tableName
	 * @param columnNames
	 * @param values
	 */
	public void insertByTableNameAndColumns(String tableName, String[] columnNames, Object[] values) {
		if (columnNames == null || columnNames.length == 0 || values == null || values.length != columnNames.length)
			return;
		if (columnNames.length != values.length)
			return;

		// 如果已经存在关联关系,则不插入
		ModelSelector selector = ModelSelector.from(tableName).select("COUNT(1) as count").where("1", "=", 1);
		for (int i = 0; i < columnNames.length; i++) {
			selector.and(columnNames[i], "=", values[i]);
		}
		Model model = query(selector);

		if (model.getInt("count") == 0) {
			String sql = SqlBuilder.generateInsertSql(tableName, columnNames, values);
			Log.e(TAG, sql);
			execute(sql);
		}
	}

	/**
	 * 根据传入的ModelSelector作为查询条件,得到Model集合
	 * 
	 * @author wangyang 2014-4-30 下午3:30:53
	 * @param selector
	 * @return
	 */
	public List<Model> list(ModelSelector selector) {
		return list(selector.getEntityType(), selector.toString());
	}

	/**
	 * 根据传入的ModelSelector作为查询条件,得到Model对象
	 * 
	 * @author wangyang 2014-4-30 下午3:31:48
	 * @param selector
	 * @return
	 */
	public Model query(ModelSelector selector) {
		return query(selector.getEntityType(), selector.toString());
	}

	/**
	 * 根据传入的Sql语句作为查询条件,得到Model对象
	 * 
	 * @author wangyang 2014-4-30 下午3:31:55
	 * @param sql
	 * @return
	 */
	public Model query(Class<?> clazz, String sql) {
		sqlBuilder.CreateTableIfNotExists(clazz);
		Log.e(TAG, sql);
		Cursor cursor = rawQuery(sql);
		BeanHandler<Model> handler = new BeanHandler<Model>();
		return handler.getModel(cursor, this, autoClose());
	}

	/**
	 * 根据传入的Sql语句作为查询条件,得到Model集合
	 * 
	 * @author wangyang 2014-4-30 下午3:31:59
	 * @param sql
	 * @return
	 */
	public List<Model> list(Class<?> clazz, String sql) {
		sqlBuilder.CreateTableIfNotExists(clazz);
		Log.e(TAG, sql);
		Cursor cursor = rawQuery(sql);
		BeanHandler<Model> handler = new BeanHandler<Model>();
		return handler.getListModel(cursor, this, autoClose());
	}

	public Context getContext() {
		return context;
	}
}
