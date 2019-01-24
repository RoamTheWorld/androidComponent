package com.android.sqlite.table;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.text.TextUtils;

import com.android.sqlite.DBHelper;
import com.android.sqlite.annotation.Foreign;
import com.android.sqlite.annotation.ID;

/**
 * 表结构的类
 * 
 * @author wangyang
 * @2014-3-7 上午11:51:04
 */
public class Table {

	/**
	 * 表名
	 */
	private String tableName;

	/**
	 * 保存Table的key
	 */
	private Class<?> tableKey;

	/**
	 * 主键
	 */
	private Id id;

	/**
	 * 字段集合
	 */
	private Map<String, Column> attributes;

	/**
	 * 延迟加载集合
	 */
	private Map<String, Finder> finders;

	/**
	 * 是否迭代父类字段
	 */
	private boolean isRecursion;

	public boolean isRecursion() {
		return isRecursion;
	}

	public void setRecursion(boolean isRecursion) {
		this.isRecursion = isRecursion;
	}

	/**
	 * 表结构集合 key=Class,value=表结构对象
	 */
	public static final Map<Class<?>, Table> tableMap = new ConcurrentHashMap<Class<?>, Table>();

	public Table(Class<?> entityType) {
		this(entityType, false, false, false);
	}

	/**
	 * 
	 * @param entityType
	 *            表结构映射的Class
	 * @param addId
	 *            是否添加主键
	 * @param addColumn
	 *            是否添加列
	 * @param isRecursion
	 *            是否迭代添加父类字段
	 */
	public Table(Class<?> entityType, boolean addId, boolean addColumn, boolean isRecursion) {
		this.isRecursion = isRecursion;
		this.tableKey = entityType;
		this.tableName = getTableName(entityType);
		attributes = new ConcurrentHashMap<String, Column>();
		finders = new ConcurrentHashMap<String, Finder>();
		if (addId || addColumn)
			addColumns(tableKey, addId, addColumn);
		tableMap.put(entityType, this);
	}

	/**
	 * 根据Class获取表名
	 * 
	 * @author wangyang 2014-4-16 下午6:11:02
	 * @param entityType
	 * @return
	 */
	public static String getTableName(Class<?> entityType) {
		String tableName = entityType.getSimpleName();
		com.android.sqlite.annotation.Table table = entityType.getAnnotation(com.android.sqlite.annotation.Table.class);
		if (table != null && !TextUtils.isEmpty(table.name()))
			tableName = table.name();
		return tableName;
	}

	public String getTableName() {
		return tableName;
	}

	public Id getId() {
		return id;
	}

	/**
	 * 根据字段名,获取列对象
	 * 
	 * @author wangyang 2014-7-16 下午1:51:09
	 * @param columnName
	 * @return
	 */
	public Column getColumnByColumnName(String columnName) {
		if (id.getColumnName().equals(columnName))
			return id;
		else
			return attributes.get(columnName);
	}

	public Map<String, Column> getAttributes() {
		return attributes;
	}

	public Map<String, Finder> getFinders() {
		return finders;
	}

	/**
	 * 获取表结构对象
	 * 
	 * @author wangyang 2014-7-16 下午1:51:31
	 * @param entityType
	 *            表结构映射的Class
	 * @param addId
	 *            是否添加主键
	 * @param addColumn
	 *            是否添加列
	 * @param isRecursion
	 *            是否迭代添加父类字段
	 * @return
	 */
	public static synchronized Table get(Class<?> entityType, boolean addId, boolean addColumn, boolean isRecursion) {
		Table table = tableMap.get(entityType);
		if (table == null)
			table = new Table(entityType, addId, addColumn, isRecursion);
		if (table.id == null || table.getAttributes().size() == 0)
			table.addColumns(entityType, table.id == null && addId, (table.getAttributes().size() == 0 || table.finders.size() == 0) && addColumn);
		return table;
	}

	/**
	 * 删除表结构
	 * 
	 * @author wangyang 2014-7-16 下午1:52:03
	 * @param entityType
	 */
	public static synchronized void remove(Class<?> entityType) {
		tableMap.remove(entityType);
	}

	/**
	 * 获取表执行Sql
	 * 
	 * @author wangyang 2014-7-16 下午1:52:12
	 * @return
	 */
	public String getExecAfterTableCreated() {
		com.android.sqlite.annotation.Table table = tableKey.getAnnotation(com.android.sqlite.annotation.Table.class);
		if (table != null) {
			return table.execAfterTableCreated();
		}
		return null;
	}

	/**
	 * 将实体Class的字段添加至集合
	 * 
	 * @author wangyang 2014-4-21 下午5:23:37
	 * @param addId
	 *            是否添加主键
	 * @param addColumn
	 *            是否添加列
	 */
	public void addColumns(Class<?> clazz, boolean addId, boolean addColumn) {
		if (clazz == Object.class)
			return;

		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			// 添加主键
			ID idAnot = field.getAnnotation(ID.class);
			if (idAnot != null) {
				if (addId && id == null)
					id = new Id(field);
				continue;
			}

			if (!addColumn)
				continue;

			// 添加外键
			Foreign foreignAnnot = field.getAnnotation(Foreign.class);
			if (foreignAnnot != null) {
				com.android.sqlite.table.Foreign foreign = new com.android.sqlite.table.Foreign(field, isRecursion);
				if (!attributes.containsKey(foreign.getColumnName()))
					attributes.put(foreign.getColumnName(), foreign);
				continue;
			}

			// 添加外键
			com.android.sqlite.annotation.Finder finderAnnot = field.getAnnotation(com.android.sqlite.annotation.Finder.class);
			if (finderAnnot != null) {
				Finder finder = new Finder(field, clazz, isRecursion);
				if (!finders.containsKey(finder.getColumnName()))
					finders.put(finder.getColumnName(), finder);
				continue;
			}

			// 添加列
			com.android.sqlite.annotation.Column annot = field.getAnnotation(com.android.sqlite.annotation.Column.class);
			if (annot != null) {
				Column column = new Column(field);
				if (!attributes.containsKey(column.getColumnName()))
					attributes.put(column.getColumnName(), column);
			}
		}

		// 迭代父类字段
		if (isRecursion)
			addColumns(clazz.getSuperclass(), addId, addColumn);
	}

	/**
	 * 根据实体,生成主键值
	 * 
	 * @author wangyang 2014-7-16 下午1:54:06
	 * @param entity
	 */
	private void generateIds(Object entity) {
		if (entity == null)
			return;
		synchronized (tableMap) {
			if (id == null)
				addColumns(entity.getClass(), true, false);
			id.getColumnValue(entity);
		}
	}

	/**
	 * 根据主键生成,列的值
	 * 
	 * @author wangyang 2014-7-16 下午1:54:31
	 * @param entity
	 * @param db
	 */
	private void generateColumns(Object entity, DBHelper db) {
		if (entity == null)
			return;
		synchronized (tableMap) {
			if (attributes.size() == 0)
				addColumns(entity.getClass(), false, true);
			for (Map.Entry<String, Column> entry : attributes.entrySet()) {
				if (entry.getValue() instanceof com.android.sqlite.table.Foreign) {
					com.android.sqlite.table.Foreign foreign = (com.android.sqlite.table.Foreign) entry.getValue();
					foreign.setDb(db);
				}
				entry.getValue().getColumnValue(entity);
			}
		}
	}

	public void generateTableValue(Object entity, DBHelper db) {
		generateIds(entity);
		generateColumns(entity, db);
	}

	public void generateTableValueOnlyId(Object entity) {
		generateIds(entity);
	}

	public void generateTableValueOnlyColumns(Object entity, DBHelper db) {
		generateColumns(entity, db);
	}

	/**
	 * 清空字段值
	 * 
	 * @author wangyang 2014-7-16 上午11:32:07
	 */
	public void clearValue() {
		if (id != null)
			id.setValue(null);
		if (attributes != null) {
			for (Column column : attributes.values())
				column.setValue(null);
		}
		if (finders != null) {
			for (Finder finder : finders.values())
				finder.setValue(null);
		}

	}
}
