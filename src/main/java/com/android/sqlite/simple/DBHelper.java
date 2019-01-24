package com.android.sqlite.simple;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.android.utils.StringUtil;

/**
 * 
 * @author wangyang DBHelper 继承自SQLiteOpenHelper的一个帮助类。
 */
public class DBHelper extends SQLiteOpenHelper {

	/**
	 * String[] mSql_ddl 一个类成员变量,主要用于类初始化时赋值, 并在onCreate方法中循环执行SQL。
	 */
	private String[] mSql_ddl;

	private static ThreadLocal<SQLiteDatabase> local = new ThreadLocal<SQLiteDatabase>();

	private SQLiteDatabase db;

	private OnUpGradeListener listener;

	public interface OnUpGradeListener {
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion, String... sqls);
	}

	public void setListener(OnUpGradeListener listener) {
		this.listener = listener;
	}

	/**
	 * 
	 * @param context
	 *            参数上下文对象
	 * @param dbName
	 *            参数数据库名称
	 * @param mSql_ddl
	 *            参数初始化数据库时需要执行的SQL数组
	 */
	public DBHelper(Context context, int version, String dbName, String... mSql_ddl) {
		super(context, dbName, null, version);
		this.mSql_ddl = mSql_ddl;
	}

	/**
	 * 
	 * @param context
	 *            参数上下文对象
	 * @param dbName
	 *            参数数据库名称
	 */
	public DBHelper(Context context, int version, String dbName) {
		super(context, dbName, null, version);
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
				if (!StringUtil.isEmpty(mSql_ddl[i]))
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
		db = local.get();
		if (db == null || !db.isOpen()) {
			db = getWritableDatabase();
			local.set(db);
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
	public void execute(String sql, Object... args) {
		getDatabase();
		if (args == null)
			db.execSQL(sql);
		else
			db.execSQL(sql, args);
	}

	/**
	 * 
	 * @auther wangyang
	 * @param table
	 *            插入的表名
	 * @param nullColumnHack
	 *            需要以null填充的字段名
	 * @param values
	 *            一个参数集合,以Map形式保存
	 * @return 返回行号 此方法接收以上三个参数进行插入操作。
	 */
	public long insert(String table, String nullColumnHack, ContentValues values) {
		getDatabase();
		return db.insert(table, nullColumnHack, values);
	}

	/**
	 * 
	 * @auther wangyang
	 * @param table
	 *            表名
	 * @param columns
	 *            需要查询的字段名数组
	 * @param selection
	 *            where条件
	 * @param selectionArgs
	 *            where条件参数数组
	 * @param groupBy
	 *            groupBy子句
	 * @param having
	 *            having子句
	 * @param orderBy
	 *            orderby子句
	 * @return 返回一个Cursor 此方法接收以上参数来查询数据库并返回一个表示结果集的Cursor。
	 */
	public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
		getDatabase();
		return db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
	}

	/**
	 * 
	 * @auther wangyang
	 * @param table
	 *            表名
	 * @param values
	 *            以map形式保存要修改的参数集合
	 * @param whereClause
	 *            where子句
	 * @param whereArgs
	 *            where子句参数
	 * @return 返回行号 此方法接收以上参数修改数据库表,并返回行号。
	 */
	public int update(String table, ContentValues values, String whereClause, String[] whereArgs) {
		getDatabase();
		return db.update(table, values, whereClause, whereArgs);
	}

	/**
	 * 
	 * @auther wangyang
	 * @param table
	 *            表名
	 * @param whereClause
	 *            where子句
	 * @param whereArgs
	 *            where子句参数
	 * @return 返回行号 此方法接收以上参数删除数据库的表并返回行号。
	 */
	public int delete(String table, String whereClause, String[] whereArgs) {
		getDatabase();
		return db.delete(table, whereClause, whereArgs);
	}

	public void beginTransaction() {
		getDatabase();
		db.beginTransaction();
	}

	public void commit() {
		db.setTransactionSuccessful();
	}

	public void endTransaction() {
		db.endTransaction();
	}

	public void close() {
		if (db != null) {
			db.close();
			db = null;
		}
	}

	/**
	 * 通过路径获取非databases目录下的只读数据库
	 * 
	 * @param path
	 * @return SQLiteDatabase
	 */
	public static SQLiteDatabase openReadOnlyDatabase(String path) {
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
		return db;
	}

	/**
	 * 通过路径获取非databases目录下的数据库
	 * 
	 * @param path
	 * @return SQLiteDatabase
	 */
	public static SQLiteDatabase openReadWriteDatabase(String path) {
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READWRITE);
		return db;
	}

	/**
	 * 
	 * @auther wangyang
	 * @param table
	 *            插入的表名
	 * @param nullColumnHack
	 *            需要以null填充的字段名
	 * @param values
	 *            一个参数集合,以Map形式保存
	 * @param path
	 *            数据库的路径
	 * @return 返回行号 此方法接收以上三个参数进行插入操作。
	 */
	public static long insert(String table, String nullColumnHack, ContentValues values, String path) {
		SQLiteDatabase db = openReadWriteDatabase(path);
		return db.insert(table, nullColumnHack, values);
	}

	/**
	 * 
	 * @auther wangyang
	 * @param table
	 *            表名
	 * @param columns
	 *            需要查询的字段名数组
	 * @param selection
	 *            where条件
	 * @param selectionArgs
	 *            where条件参数数组
	 * @param groupBy
	 *            groupBy子句
	 * @param having
	 *            having子句
	 * @param orderBy
	 *            orderby子句
	 * @return 返回一个Cursor 此方法接收以上参数来查询数据库并返回一个表示结果集的Cursor。
	 */
	public static Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having,
			String orderBy, String path) {
		return openReadOnlyDatabase(path).query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
	}

	/**
	 * 
	 * @auther wangyang
	 * @param table
	 *            表名
	 * @param values
	 *            以map形式保存要修改的参数集合
	 * @param whereClause
	 *            where子句
	 * @param whereArgs
	 *            where子句参数
	 * @return 返回行号 此方法接收以上参数修改数据库表,并返回行号。
	 */
	public static int update(String table, ContentValues values, String whereClause, String[] whereArgs, String path) {
		return openReadWriteDatabase(path).update(table, values, whereClause, whereArgs);
	}

	/**
	 * 
	 * @auther wangyang
	 * @param table
	 *            表名
	 * @param whereClause
	 *            where子句
	 * @param whereArgs
	 *            where子句参数
	 * @return 返回行号 此方法接收以上参数删除数据库的表并返回行号。
	 */
	public static int delete(String table, String whereClause, String[] whereArgs, String path) {
		return openReadWriteDatabase(path).delete(table, whereClause, whereArgs);
	}

	/**
	 * 
	 * @author wangyang
	 * @param sql
	 * @param selectionArgs
	 * @return Cursor 根据sql与查询条件获得Cursor结果集
	 */
	public Cursor rawQuery(String sql, String... selectionArgs) {
		getDatabase();
		return db.rawQuery(sql, selectionArgs);
	}

	/**
	 * 
	 * @author wangyang
	 * @param path
	 * @param sql
	 * @param selectionArgs
	 * @return
	 */
	public static Cursor rawQuery(String path, String sql, String... selectionArgs) {
		return openReadOnlyDatabase(path).rawQuery(sql, selectionArgs);
	}
}
