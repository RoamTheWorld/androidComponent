package com.android.sqlite;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import android.content.Context;
import com.android.sqlite.DBHelper.OnUpGradeListener;
import com.android.sqlite.sql.Selector;

/**
 * 泛型DAO基类
 * 
 * @author wangyang
 * @2014-3-3 上午11:20:35
 * @param <M>
 */
@SuppressWarnings("unchecked")
public abstract class BaseDAO<M> implements IDAO<M> {

	/**
	 * 数据库版本
	 */
	private static final int VERSION = 1;

	/**
	 * DBHelper,继承自SQLiteOpenHelper类
	 */
	private DBHelper db;

	/**
	 * 泛型实例
	 */
	private M m;

	/**
	 * 泛型Class
	 */
	private Class<M> clazz;

	/**
	 * 设置数据库变化的监听
	 * 
	 * @author wangyang 2014-4-30 下午2:58:37
	 * @param listener
	 */
	public void setOnUpgradeListener(OnUpGradeListener listener) {
		db.setListener(listener);
	}

	/**
	 * 根据上下文,建表语句创建BaseDao
	 * 
	 * @author wangyang
	 * @2014-2-28 下午5:14:49
	 * @param context
	 * @param ddl
	 */
	public BaseDAO(Class<M> clazz, Context context, int version, String dbName, OnUpGradeListener listener, String... ddl) {
		db = DBHelper.getInstance(context, version, dbName, listener, ddl);
		this.clazz = clazz;
	}

	/**
	 * 根据上下文,建表语句,是否自动提交标志创建BaseDao
	 * 
	 * @author wangyang
	 * @2014-2-28 下午5:14:53
	 * @param context
	 * @param autoCommit
	 * @param ddl
	 */
	public BaseDAO(Context context, String dbName, String... ddl) {
		this(null, context, VERSION, dbName, null, ddl);
	}

	/**
	 * 根据上下文创建BaseDao
	 * 
	 * @author wangyang
	 * @2014-2-28 下午5:14:53
	 * @param context
	 * 
	 */
	public BaseDAO(Context context, String dbName) {
		this(null, context, VERSION, dbName);
	}

	/**
	 * 
	 * @param clazz
	 * @param context
	 * @param dbName
	 */
	public BaseDAO(Class<M> clazz, Context context, String dbName) {
		this(clazz, context, VERSION, dbName);
	}

	public BaseDAO(Class<M> clazz, Context context, int version, String dbName, String... ddl) {
		this(clazz, context, version, dbName, null, ddl);
	}

	/**
	 * 获得数据库名
	 *
	 * @author wangyang
	 * 2014-7-21 下午6:04:49
	 * @return
	 */
	public String getDatabaseName() {
		return db.getDatabaseName();
	}

	/**
	 * 自动关闭数据库
	 *
	 * @author wangyang
	 * 2014-7-21 下午3:58:27
	 */
	public void setAutoClose() {
		db.setAutoClose();
	}

	/**
	 * 开启事务
	 * 
	 * @author wangyang
	 * @2014-3-3 上午11:23:57
	 */
	public void beginTransaction() {
		db.beginTransaction();
	}

	/**
	 * 提交事物
	 * 
	 * @author wangyang
	 * @2014-3-3 上午11:24:36
	 */
	public void setTransactionSuccessful() {
		db.setTransactionSuccessful();
	}

	/**
	 * 结束事物
	 * 
	 * @author wangyang
	 * @2014-3-3 上午11:24:48
	 */
	public void endTransaction() {
		db.endTransaction();
	}

	/**
	 * 取消内部事务
	 * 
	 * @author wangyang 2014-5-21 下午12:32:58
	 */
	public void cancelInnerTransaction() {
		db.setInnerTransaction(false);
	}

	/**
	 * 关闭数据库
	 * 
	 * @author wangyang
	 * @2014-3-3 上午11:24:56
	 */
	public void close() {
		db.close();
	}

	public void setGenerateRecursion(boolean isGenerateRecursion) {
		db.setGenerateRecursion(isGenerateRecursion);
	}

	public boolean isGenerateRecursion() {
		return db.isGenerateRecursion();
	}

	public void setRecursion(boolean Recursion) {
		db.setRecursion(Recursion);
	}

	public boolean isRecursion() {
		return db.isRecursion();
	}

	/**
	 * 获取当前DBHelper
	 * 
	 * @author wangyang 2014-4-30 下午2:59:07
	 * @return
	 */
	public DBHelper getDb() {
		return db;
	}

	@Override
	public boolean saveOrUpdate(M m) {
		return db.saveOrUpdate(m);
	}

	@Override
	public boolean saveOrUpdate(List<M> m) {
		return db.saveOrUpdate(m);
	}

	@Override
	public boolean delete(M m) {
		return db.delete(m);
	}

	public boolean delete(Class<?> clazz) {
		return db.delete(clazz);
	}

	@Override
	public boolean delete(List<M> m) {
		return db.delete(m);
	}

	@Override
	public List<M> listBySql(String sql, String... args) {
		return (List<M>) db.listBySql(sql, getInstance().getClass(), args);
	}

	@Override
	public List<M> list(M m) {
		return db.list(m);
	}

	@Override
	public M queryBySql(String sql, String... args) {
		return (M) db.queryBySql(sql, getInstance().getClass(), args);
	}

	public M queryBySql(Class<?> clazz, String sql, String... args) {
		return (M) db.queryBySql(sql, clazz, args);
	}

	@Override
	public M query(M m) {
		return (M) db.query(m);
	}

	public M query() {
		return (M) db.query(getInstance().getClass(), null, null);
	}

	public List<M> list() {
		return (List<M>) db.list(getInstance().getClass(), null, null);
	}

	public List<M> list(Selector selector) {
		return (List<M>) db.listBySql(selector.toString(), getInstance().getClass());
	}

	public M query(Selector selector) {
		return (M) db.queryBySql(selector.toString(), getInstance().getClass());
	}

	public List<M> list(Class<?> clazz, int id) {
		return (List<M>) db.list(clazz, id);
	}

	public List<M> list(Class<?> clazz, String[] columns, Object[] values) {
		return (List<M>) db.list(clazz, columns, values);
	}

	public M query(Class<?> clazz, int id) {
		return (M) db.query(clazz, id);
	}

	public M query(Class<?> clazz, String[] columns, Object[] values) {
		return (M) db.query(clazz, columns, values);
	}

	public boolean delete(Class<?> clazz, String column, Object value) {
		return db.delete(clazz, column, value);
	}

	public boolean delete(Class<?> clazz, Object id) {
		return db.delete(clazz, id);
	}

	protected Class<M> getActualTypeClass() {
		if (clazz != null)
			return clazz;
		Class<?> clazz1 = getClass();

		// 获取泛型参数的Type
		ParameterizedType pt = null;
		if (clazz1 == BaseDAO.class)
			pt = (ParameterizedType) clazz1.getGenericInterfaces()[0];
		else
			pt = (ParameterizedType) clazz1.getGenericSuperclass();

		// 获取ParameterizedType的参数,并转换为Class
		Type type = pt.getActualTypeArguments()[0];
		clazz = (Class<M>) type;
		return clazz;
	}

	/**
	 * 获取类的泛型参数的Class并实例化对象
	 * 
	 * @author wangyang
	 * @return M
	 */
	private M getInstance() {
		if (m != null)
			return m;
		try {
			return m = getActualTypeClass().newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static <M> BaseDAO<M> getInstance(Class<M> clazz, Context context, String dbName) {
		return new BaseDAOImpl<M>(clazz, context, dbName);
	}

	public static <M> BaseDAO<M> getInstance(Class<M> clazz, Context context, int version, String dbName) {
		return new BaseDAOImpl<M>(clazz, context, version, dbName);
	}

	public static <M> BaseDAO<M> getInstance(Class<M> clazz, Context context, int version, String dbName, OnUpGradeListener listener) {
		return new BaseDAOImpl<M>(clazz, context, version, dbName, listener);
	}

	private static class BaseDAOImpl<M> extends BaseDAO<M> {

		public BaseDAOImpl(Class<M> clazz, Context context, int version, String dbName, OnUpGradeListener listener, String... ddl) {
			super(clazz, context, version, dbName, listener, ddl);
		}

		public BaseDAOImpl(Class<M> clazz, Context context, int version, String dbName, String... ddl) {
			super(clazz, context, version, dbName, ddl);
		}

		public BaseDAOImpl(Class<M> clazz, Context context, String dbName) {
			super(clazz, context, dbName);
		}

		public BaseDAOImpl(Context context, String dbName, String... ddl) {
			super(context, dbName, ddl);
		}
	}
}
