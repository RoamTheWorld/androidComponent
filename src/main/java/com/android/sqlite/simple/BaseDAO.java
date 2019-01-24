package com.android.sqlite.simple;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import android.content.Context;
import android.database.Cursor;

import com.android.sqlite.simple.DBHelper.OnUpGradeListener;
import com.android.sqlite.simple.annotation.ID;
import com.android.sqlite.simple.annotation.TableName;
import com.android.sqlite.simple.bean.Coloumn;
import com.android.sqlite.simple.bean.Table;
import com.android.utils.Log;
import com.android.utils.StringUtil;

/**
 * 数据库操作基类
 * 
 * @author wangyang
 * @2014-3-3 上午11:20:35
 * @param <M>
 */
public abstract class BaseDAO<M> implements IDAO<M> {

	private static final String TAG = "SQLite";

	private static final int VERSION = 1;

	/**
	 * DBHelper,继承自SQLiteOpenHelper类
	 */
	private DBHelper db;

	/**
	 * 是否自动提交
	 */
	private boolean autoCommit;

	/**
	 * 表结构的类
	 */
	private Table table;

	/**
	 * 生成SQL的Builder
	 */
	private SqlBuilder sqlBuilder;

	/**
	 * 是否需要检测数据库表是否存在
	 */
	private boolean isNeedCheckTableExit = false;

	public void setOnUpgradeListener(OnUpGradeListener listener) {
		db.setListener(listener);
	}

	public void setAutoCommit(boolean autoCommit) {
		this.autoCommit = autoCommit;
	}

	/**
	 * 根据上下文,建表语句创建BaseDao
	 * @param context
	 * @param version
	 * @param dbName
	 * @param autoCommit
	 * @param checkTable
	 * @param ddl
	 */
	public BaseDAO(Context context, int version, String dbName, boolean autoCommit, boolean checkTable, String... ddl) {
		db = new DBHelper(context, version, dbName, ddl);
		sqlBuilder = new com.android.sqlite.simple.SqlBuilder(db);

		this.autoCommit = autoCommit;
		this.isNeedCheckTableExit = checkTable;
		this.table = new Table();

	}

	public BaseDAO(Context context, int version, String dbName, boolean autoCommit, String... ddl) {
		this(context, version, dbName, autoCommit, false, ddl);

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
		this(context, VERSION, dbName, true, false, ddl);
	}

	/**
	 * 根据上下文,是否自动提交标志创建BaseDao
	 * 
	 * @author wangyang
	 * @2014-2-28 下午5:14:53
	 * @param context
	 * @param autoCommit
	 * 
	 */
	public BaseDAO(Context context, String dbName, boolean autoCommit) {
		db = new DBHelper(context, VERSION, dbName);
		this.autoCommit = autoCommit;
		this.table = new Table();
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
		this(context, VERSION, dbName, false, true);
	}

	/**
	 * 数据库操作类型
	 * 
	 * @author wangyang
	 * @2014-3-3 上午11:21:10
	 */
	private enum OperatorType {
		INSERT, UPDATE, DELETE, QUERY;
	}

	/**
	 * 开启事务
	 * 
	 * @author wangyang
	 * @2014-3-3 上午11:23:57
	 */
	public void beginTransaction() {
		this.autoCommit = false;
		db.beginTransaction();
	}

	/**
	 * 提交事物
	 * 
	 * @author wangyang
	 * @2014-3-3 上午11:24:36
	 */
	public void setTransactionSuccessful() {
		db.commit();
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
	 * 关闭数据库
	 * 
	 * @author wangyang
	 * @2014-3-3 上午11:24:56
	 */
	public void close() {
		db.close();
	}

	/**
	 * 检测数据库表，无则创建对应的表
	 * 
	 * @param isCheck
	 */
	public void checkDBTable() {
		try {
			if (isNeedCheckTableExit) {
				sqlBuilder.CreateTableIfNotExists(db, (Class<?>) getInstance());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setNeedCheckTableExit(boolean isNeedCheckTableExit) {
		this.isNeedCheckTableExit = isNeedCheckTableExit;
		checkDBTable();
	}

	/**
	 * 根据传入的泛型对象做Insert操作
	 * 
	 * @author wangyang
	 * @param m
	 * @return
	 * @Describe
	 */
	@Override
	public synchronized boolean insert(M m) {
		M temp = queryForOnlyKey(m);
		if (temp != null) {
			return update(m);
		}
		boolean isInsert = false;
		try {
			// 生成sql
			String sql = generateInsertSql(m);
			if (!StringUtil.isEmpty(sql)) {
				Log.i(TAG, sql);
				db.execute(sql);
				isInsert = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (autoCommit)
				db.close();
		}
		return isInsert;
	}

	/**
	 * 根据传入的对象做update操作
	 * 
	 * @author wangyang
	 * @param m
	 * @return
	 * @Describe
	 */
	@Override
	public synchronized boolean update(M m) {
		boolean isUpdate = false;
		M temp = queryForOnlyKey(m);
		if (temp != null) {
			try {
				// 生成sql
				String sql = generateUpdateSql(m);
				if (!StringUtil.isEmpty(sql)) {
					Log.i(TAG, sql + "");
					db.execute(sql);
					isUpdate = true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (autoCommit)
					db.close();
			}
		}
		return isUpdate;
	}

	/**
	 * 根据传入的操作做delete操作
	 * 
	 * @author wangyang
	 * @param m
	 * @return
	 * @Describe
	 */
	@Override
	public synchronized boolean delete(M m) {
		boolean isDelete = false;
		try {
			// 生成sql
			String sql = generateDeleteSql(m);
			if (!StringUtil.isEmpty(sql)) {
				Log.i(TAG, sql);
				db.execute(sql);
				isDelete = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (autoCommit)
				db.close();
		}
		return isDelete;
	}

	/**
	 * 删除表中所有数据
	 * 
	 * @author wangyang
	 * @param m
	 * @return
	 * @Describe
	 */
	@Override
	public synchronized boolean deleteAll() {
		boolean isDelete = false;
		try {
			// 生成sql
			String sql = generateDeleteSql(null);
			if (!StringUtil.isEmpty(sql)) {
				Log.i(TAG, sql);
				db.execute(sql);
				isDelete = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (autoCommit)
				db.close();
		}
		return isDelete;
	}

	/**
	 * 根据泛型对象参数,查询泛型集合
	 * 
	 * @author wangyang
	 * @param m
	 * @return
	 * @Describe
	 */
	@Override
	public synchronized List<M> list(M m) {
		try {
			String sql = generateQuerySql(m);
			Log.i(TAG, sql + "");
			Cursor cursor = db.rawQuery(sql);
			BeanHandler<M> handler = new BeanHandler<M>();
			return handler.handler2List(db, cursor, getInstance().getClass(), autoCommit);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 根据泛型对象参数,查询泛型集合
	 * 
	 * @author wangyang
	 * @param m
	 * @return
	 * @Describe
	 */
	public synchronized List<M> listForOnlyKey(M m) {
		try {
			String sql = generateQuerySqlForOnlyKey(m);
			Log.i(TAG, sql + "");
			Cursor cursor = db.rawQuery(sql);
			BeanHandler<M> handler = new BeanHandler<M>();
			return handler.handler2List(db, cursor, getInstance().getClass(), autoCommit);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 查询所有泛型集合
	 * 
	 * @author wangyang
	 * @return
	 * @Describe
	 */
	public synchronized List<M> list() {
		try {
			String sql = generateQuerySql(null);
			Log.i(TAG, sql + "");
			Cursor cursor = db.rawQuery(sql);
			BeanHandler<M> handler = new BeanHandler<M>();
			return handler.handler2List(db, cursor, getInstance().getClass(), autoCommit);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 根据Sql与参数查询所有泛型集合
	 * 
	 * @author wangyang
	 * @param sql
	 * @param args
	 * @return
	 * @Describe
	 */
	@Override
	public synchronized List<M> listBySql(String sql, String... args) {
		Log.i(TAG, sql + "");
		Cursor cursor = db.rawQuery(sql, args);
		BeanHandler<M> handler = new BeanHandler<M>();
		return handler.handler2List(db, cursor, getInstance().getClass(), autoCommit);
	}

	/**
	 * 根据SQL及参数查询泛型对象
	 * 
	 * @author wangyang
	 * @param sql
	 * @param args
	 * @return
	 * @Describe
	 */
	public synchronized M query(String sql, String... args) {
		Log.i(TAG, sql + "");
		List<M> list = listBySql(sql, args);
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	/**
	 * 查询数据表中第一个泛型对象
	 * 
	 * @author wangyang
	 * @return
	 * @Describe
	 */
	public synchronized M query() {
		List<M> list = list();
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	/**
	 * 根据泛型对象,查询数据表中第一个泛型对象
	 * 
	 * @author wangyang
	 * @param m
	 * @return
	 * @Describe
	 */
	public synchronized M query(M m) {
		List<M> list = list(m);
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	/**
	 * 根据sql查询泛型集合
	 * 
	 * @author wangyang
	 * @param sql
	 * @return
	 * @Describe
	 */
	public synchronized List<M> list(String sql) {
		return list(sql);
	}

	/**
	 * 根据泛型对象,查询数据表中第一个泛型对象
	 * 
	 * @author wangyang
	 * @param m
	 * @return
	 * @Describe
	 */
	public synchronized M queryForOnlyKey(M m) {
		List<M> list = listForOnlyKey(m);
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	/**
	 * 根据传入的泛型,获取该类的TableName注解的表名参数,如果注解参数为空,则以类名作为表名
	 * 
	 * @author wangyang
	 * @return String
	 */
	private String getTableName() {
		M m = getInstance();
		TableName tableName = m.getClass().getAnnotation(TableName.class);
		return tableName.value() != null && !"".equals(tableName.value()) ? tableName.value() : m.getClass().getSimpleName();
	}

	/**
	 * 获取类的泛型参数的Class并实例化对象
	 * 
	 * @author wangyang
	 * @return M
	 */
	@SuppressWarnings("unchecked")
	private M getInstance() {
		Class<?> clazz = getClass();

		// 获取泛型参数的Type
		ParameterizedType pt = null;
		if (getClass() == BaseDAO.class)
			pt = (ParameterizedType) clazz.getGenericInterfaces()[0];
		else
			pt = (ParameterizedType) clazz.getGenericSuperclass();

		// 获取ParameterizedType的参数,并转换为Class
		Type type = pt.getActualTypeArguments()[0];
		Class<?> clazz1 = (Class<?>) type;
		try {
			return (M) clazz1.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 生成InsertSql,
	 * 
	 * @author wangyang
	 * @2014-3-3 上午11:26:04
	 * @param m
	 * @return
	 * @throws Exception
	 */
	private String generateInsertSql(M m) throws Exception {
		if (m == null)
			return null;

		// 生成要操作的表结构实体
		generateBeanObject(m, true, false, OperatorType.INSERT);

		if (table.getKeyAttburite().size() > 0 || table.getAttributes().size() > 0) {
			String sql = "insert into " + getTableName() + "( ";

			// 遍历字段名
			sql = generateInsertColoumns(sql, table.getKeyAttburite(), false);
			sql = generateInsertColoumns(sql, table.getAttributes(), false);

			// 过滤掉多余的逗号
			sql = sql.substring(0, sql.lastIndexOf(",")) + ") values( ";

			// 遍历字段值
			sql = generateInsertColoumns(sql, table.getKeyAttburite(), true);
			sql = generateInsertColoumns(sql, table.getAttributes(), true);

			// 过滤掉多余的逗号
			sql = sql.substring(0, sql.lastIndexOf(",")) + ") ";

			// 清空beanObject参数集合
			table.clear();

			return sql;
		}
		return null;
	}

	/**
	 * 根据泛型对象生成Update SQL语句
	 * 
	 * @author wangyang
	 * @param m
	 * @return
	 * @throws Exception
	 * @Describe
	 */
	private String generateUpdateSql(M m) throws Exception {
		if (m == null)
			return null;

		// 根据泛型生成BeanObject数据库结构类
		generateBeanObject(m, true, false, OperatorType.UPDATE);

		if (table.getAttributes().size() > 0) {
			String sql = "Update  " + getTableName() + " set ";

			// 遍历修改字段
			sql = generateColoumns(sql, table.getAttributes(), false);

			// 过滤掉多余的逗号
			sql = sql.substring(0, sql.lastIndexOf(",")) + " where 1=1 ";

			// 遍历主键
			sql = generateColoumns(sql, table.getKeyAttburite(), true);

			// 清空beanObject参数集合
			table.clear();

			return sql;
		}

		return null;
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
	private String generateColoumns(String sql, List<Coloumn> list, boolean isWhereClause) {
		// 遍历BeanColoumn集合
		for (Coloumn coloumn : list) {
			String arg = null;

			// 字段为Number类型且值不为空且成员变量不为默认值
			if (coloumn.isNumber()) {
				if (coloumn.getConvertValue() != null) {
					arg = coloumn.getColoumnName() + " = "
							+ (coloumn.getValue() == null || "null".equals(coloumn.getValue()) ? "" : coloumn.getValue());
				}
			} else {
				arg = coloumn.getColoumnName() + " ='" + (coloumn.getValue() == null || "null".equals(coloumn.getValue()) ? "" : coloumn.getValue())
						+ "' ";
			}

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
	private String generateInsertColoumns(String sql, List<Coloumn> list, boolean isValue) {
		for (Coloumn coloumn : list) {
			// 判断是字段名还是字段值操作
			if (isValue) {
				sql += " '" + (coloumn.getValue() == null || "null".equals(coloumn.getValue()) ? "" : coloumn.getValue()) + "', ";
			} else {
				sql += coloumn.getColoumnName() + ", ";
			}
		}
		return sql;
	}

	/**
	 * 根据泛型对象生成Delete SQL语句
	 * 
	 * @author wangyang
	 * @param m
	 * @return
	 * @throws Exception
	 * @Describe
	 */
	private String generateDeleteSql(M m) throws Exception {
		if (m == null)
			return "DELETE FROM  " + getTableName() + " WHERE 1=1 ";

		// 根据泛型生成BeanObject数据库结构类
		generateBeanObject(m, true, true, OperatorType.DELETE);

		if (table.getKeyAttburite().size() > 0) {
			String sql = "DELETE FROM  " + getTableName() + " WHERE 1=1 ";

			// 遍历除主键生成where条件
			sql = generateColoumns(sql, table.getKeyAttburite(), true);

			// 清空beanObject参数集合
			table.clear();

			return sql;
		}
		return null;
	}

	/**
	 * 根据泛型对象生成QUERY SQL语句
	 * 
	 * @author wangyang
	 * @param m
	 * @return
	 * @throws Exception
	 * @Describe
	 */
	private String generateQuerySql(M m) throws Exception {
		String sql = "SELECT * FROM  " + getTableName() + " where 1=1";
		if (m == null)
			return sql;

		generateBeanObject(m, true, false, OperatorType.QUERY);

		if (table.getKeyAttburite().size() > 0 || table.getAttributes().size() > 0) {
			// 生成where条件
			sql = generateColoumns(sql, table.getAttributes(), true);

			// 清空beanObject参数集合
			table.clear();

			return sql;
		}
		return null;
	}

	/**
	 * 根据泛型对象生成QUERY SQL语句
	 * 
	 * @author wangyang
	 * @param m
	 * @return
	 * @throws Exception
	 * @Describe
	 */
	private String generateQuerySqlForOnlyKey(M m) throws Exception {
		String sql = "SELECT * FROM  " + getTableName() + " where 1=1";
		if (m == null)
			return sql;

		generateBeanObject(m, true, true, OperatorType.QUERY);

		if (table.getKeyAttburite().size() > 0 || table.getAttributes().size() > 0) {
			// 生成where条件
			sql = generateColoumns(sql, table.getAttributes(), true);

			// 清空beanObject参数集合
			table.clear();

			return sql;
		}
		return null;
	}

	/**
	 * 根据泛型对象生成表结构实体
	 * 
	 * @author wangyang
	 * @2014-3-3 上午11:40:40
	 * @param m
	 * @param isNullValue
	 * @param isOnlyKey
	 * @param type
	 * @throws Exception
	 */
	private void generateBeanObject(M m, boolean isNullValue, boolean isOnlyKey, OperatorType type) throws Exception {
		// 反射得到字段集合,并遍历
		Field[] fields = m.getClass().getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);

			// 只生成主键
			if (isOnlyKey) {
				ID id = field.getAnnotation(ID.class);
				if (id == null)
					continue;
			}

			// 过滤掉空值
			if (isNullValue) {
				Object value = field.get(m);
				if (value == null)
					continue;
			}

			// 获取有Coloumn注解的字段
			com.android.sqlite.simple.annotation.Coloumn coloumn = field.getAnnotation(com.android.sqlite.simple.annotation.Coloumn.class);
			if (coloumn != null) {

				// 生成字段名
				String coloumnName = StringUtil.isEmpty(coloumn.value()) ? field.getName() : coloumn.value();

				// 得到主键注解
				ID id = field.getAnnotation(ID.class);

				switch (type) {
				// 生成主键不为自增的列,及其他列
				case INSERT:
					if (id != null && !id.autoIncrement()) {
						table.getKeyAttburite().add(new Coloumn(coloumnName, field.getType(), field.get(m)));
					} else {
						table.getAttributes().add(new Coloumn(coloumnName, field.getType(), field.get(m)));
					}
					continue;

					// 主键如果不自增,也可以做为修改列
				case UPDATE:
					if (id != null) {
						table.getKeyAttburite().add(new Coloumn(coloumnName, field.getType(), field.get(m)));
						if (!id.autoIncrement()) {
							table.getAttributes().add(new Coloumn(coloumnName, field.getType(), field.get(m)));
						}
					} else {
						table.getAttributes().add(new Coloumn(coloumnName, field.getType(), field.get(m)));
					}
					continue;

					// 只生成主键
				case DELETE:
					if (id != null)
						table.getKeyAttburite().add(new Coloumn(coloumnName, field.getType(), field.get(m)));
					continue;
					// 全部可添加为查询条件
				case QUERY:
					table.getAttributes().add(new Coloumn(coloumnName, field.getType(), field.get(m)));
					continue;
				}
			}
		}
	}

	/**
	 * // * 通过传入的对象,利用反射与注解拼接where子句 // * // * @author wangyang // * @param m //
	 * * @param isUpdate // * @return String //
	 */
	// private String setWhereClause(M m) {
	// String sql = " where 1=1 ";
	// Field[] fields = m.getClass().getDeclaredFields();
	// for (int i = 0; i < fields.length; i++) {
	// // 根据有值的列生成Sql
	// Coloumn coloumn = fields[i].getAnnotation(Coloumn.class);
	// if (coloumn != null) {
	// try {
	// // 获取属性值,如果不为空则添加至集合
	// fields[i].setAccessible(true);
	// Object object = fields[i].get(m);
	// if (object != null && !"".equals(object.toString())) {
	// String coloumnName = StringUtil.isEmpty(coloumn.value()) ?
	// fields[i].getName() : coloumn.value();
	// sql += " and " + coloumnName + "=" + object.toString();
	// }
	// } catch (IllegalArgumentException e) {
	// Log.i("非法参数!!!");
	// e.printStackTrace();
	// } catch (Exception e) {
	// Log.i("没有权限,非法获取数据!!!");
	// e.printStackTrace();
	// }
	// }
	// }
	// return sql;
	// }

	// /**
	// *
	// * @author wangyang
	// * @param m
	// * @param isUpdate
	// * @return String[] 通过传入的对象,利用反射与注解拼接where子句的参数值
	// */
	// private String[] setWhereClauseArg(M m, boolean isUpdate) {
	// List<String> list = new ArrayList<String>();
	// // 通过反射获得传入对象的属性
	// Field[] fields = m.getClass().getDeclaredFields();
	// // 根据注解拼接SQL子句的参数值
	// for (int i = 0; i < fields.length; i++) {
	// Coloumn coloumn = fields[i].getAnnotation(Coloumn.class);
	// // 如果是执行UPDATE操作,只取主键作为whereclause条件
	// if (isUpdate) {
	// ID id = fields[i].getAnnotation(ID.class);
	// if (id != null)
	// operationWhereClauseArg(m, list, fields, i, coloumn);
	// } else {
	// operationWhereClauseArg(m, list, fields, i, coloumn);
	// }
	// }
	// return list.size() > 0 ? list.toArray(new String[] {}) : null;
	// }

	// /**
	// * 通过传入的对象,利用反射与注解将参数和值,以键值对形式保存至Contentvalues
	// *
	// * @author wangyang
	// * @param m
	// * @return ContentValues
	// */
	// private ContentValues setContentValue(M m) {
	// ContentValues values = new ContentValues();
	// try {
	// Field[] fields = m.getClass().getDeclaredFields();
	// for (int i = 0; i < fields.length; i++) {
	// fields[i].setAccessible(true);
	// // 获取属性值,如果不为空则添加至集合
	// Object object = fields[i].get(m);
	// if (object == null || "".equals(object.toString()))
	// continue;
	// Coloumn coloumn = fields[i].getAnnotation(Coloumn.class);
	// if (coloumn != null) {
	// ID id = fields[i].getAnnotation(ID.class);
	// if (id != null) {
	// // 判断主键是否为自增,是否需要添加
	// if (id.autoIncrement())
	// continue;
	// }
	// String key = StringUtil.isEmpty(coloumn.value()) ? fields[i].getName() :
	// coloumn.value();
	// values.put(key, object.toString());
	// }
	// }
	// } catch (IllegalArgumentException e) {
	// Log.i("非法参数!!!");
	// e.printStackTrace();
	// } catch (Exception e) {
	// Log.i("没有权限,非法获取数据!!!");
	// e.printStackTrace();
	// }
	// return values.size() > 0 ? values : null;
	// }

	// /**
	// *
	// * @author wangyang
	// * @param m
	// * @return String[] 通过传入的对象,利用反射与注解获取要增,改,查询的字段,并以数组返回
	// */
	// private String[] setColoumn(M m, boolean isQuery) {
	// List<String> list = new ArrayList<String>();
	// Field[] fields = m.getClass().getDeclaredFields();
	// for (int i = 0; i < fields.length; i++) {
	// Coloumn coloumn = fields[i].getAnnotation(Coloumn.class);
	// if (coloumn != null) {
	// // 如果不为查询,则判断主键是否为自增,是否需要添加
	// if (!isQuery) {
	// ID id = fields[i].getAnnotation(ID.class);
	// if (id != null) {
	// if (id.autoIncrement())
	// continue;
	// }
	// }
	// list.add(fields[i].getName());
	// }
	// }
	// return list.size() > 0 ? list.toArray(new String[] {}) : null;
	// }
}
