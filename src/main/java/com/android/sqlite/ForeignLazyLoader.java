package com.android.sqlite;

import java.io.Serializable;
import java.util.List;

import com.android.sqlite.sql.ModelSelector;
import com.android.sqlite.sql.Selector;
import com.android.sqlite.sql.WhereBuilder;
import com.android.sqlite.table.Column;
import com.android.sqlite.table.Foreign;
import com.android.sqlite.table.Model;
import com.android.utils.StringUtil;

/**
 * 外键表的延迟加载类,关联字段在主键表
 * 
 * @author wangyang 2014-7-15 下午3:39:40
 * @param <T>
 */
public class ForeignLazyLoader<T> implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 外键列Foreign
	 */
	private transient Foreign foreignColumn;

	/**
	 * 关联外键列的值
	 */
	private Object columnValue;

	/**
	 * 主键表的值
	 */
	private Object value;

	/**
	 * 关联外键表字段名
	 */
	private String columnName;

	/**
	 * 外键表Class
	 */
	private Class<?> entityType;

	/**
	 * 是否迭代生成父类字段
	 */
	private boolean isRecursion;

	/**
	 * @param columnName
	 *            关联外键表字段名
	 * @param columnValue
	 *            关联外键列的值
	 * @param entityType
	 *            外键表Class
	 * @param isRecursion
	 *            是否迭代生成父类字段
	 */
	public ForeignLazyLoader(String columnName, Object columnValue, Class<?> entityType, boolean isRecursion) {
		this.entityType = entityType;
		this.isRecursion = isRecursion;
		this.columnName = columnName;
		this.foreignColumn = (Foreign) Column.getColumnOrId(entityType, columnName, isRecursion);
		this.columnValue = columnValue;
	}

	/**
	 * 
	 * @param columnName
	 *            关联外键表字段名
	 * @param columnValue
	 *            关联外键列的值
	 * @param entityType
	 *            外键表Class
	 */
	public ForeignLazyLoader(String columnName, Object columnValue, Class<?> entityType) {
		this(columnName, columnValue, entityType, true);
	}

	/**
	 * 
	 * @param entityType
	 *            外键表Class
	 * @param columnName
	 *            关联外键表字段名
	 * @param value
	 *            主键表的值
	 */
	public ForeignLazyLoader(Class<?> entityType, String columnName, Object value) {
		this(entityType, columnName, value, true);
	}

	/**
	 * 
	 * @param entityType
	 *            外键表Class
	 * @param columnName
	 *            关联外键表字段名
	 * @param value
	 *            主键表的值
	 * @param isRecursion
	 *            是否迭代生成父类字段
	 */
	public ForeignLazyLoader(Class<?> entityType, String columnName, Object value, boolean isRecursion) {
		this.entityType = entityType;
		this.columnName = columnName;
		this.isRecursion = isRecursion;
		this.foreignColumn = (Foreign) Column.getColumnOrId(entityType, columnName, isRecursion);
		this.value = value;
		this.columnValue = null;
	}

	/**
	 * 
	 * @param foreignColumn
	 *            外键列Foreign
	 * @param columnValue
	 *            关联外键列的值
	 */
	public ForeignLazyLoader(Foreign foreignColumn, Object columnValue, boolean isRecursion) {
		this.entityType = foreignColumn.getForeignOrFinderEntityType();
		this.foreignColumn = foreignColumn;
		this.columnValue = Column.convert2ColumnValue(columnValue);
		this.columnName = foreignColumn.getColumnName();
		this.isRecursion = isRecursion;
		this.value = null;
	}

	/**
	 * 获取外键列Foreign
	 * 
	 * @author wangyang 2014-7-15 下午3:59:55
	 * @return
	 */
	public Foreign getForeign() {
		// 如果foreignColumn为null,则根据条件获取foreignColumn
		if (foreignColumn == null) {
			if (entityType == null)
				return null;
			if (StringUtil.isEmpty(columnName))
				return null;
			foreignColumn = (Foreign) Column.getColumnOrId(entityType, columnName, isRecursion);
		}
		return foreignColumn;
	}

	/**
	 * 延迟加载外键表列表
	 * 
	 * @author wangyang 2014-7-15 下午3:36:56
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<T> getAll2List(String... express) {
		List<T> entities = null;
		if (getForeign() != null && getForeign().getDb() != null) {
			Selector selector = Selector.from(getForeign().getForeignOrFinderEntityType()).where(getForeign().getForeignColumnName(), "=", columnValue);
			setExpress2Selector(selector, express);
			entities = (List<T>) getForeign().getDb().list(selector);
		}
		return entities;
	}

	private void setExpress2Selector(Selector selector, String... express) {
		if (express != null) {
			for (String str : express)
				selector.expr(str);
		}
	}

	private void setExpress2ModelSelector(ModelSelector selector, String... express) {
		if (express != null) {
			for (String str : express)
				selector.expr(str);
		}
	}

	/**
	 * 延迟加载外键表对象
	 * 
	 * @author wangyang 2014-7-15 下午3:36:56
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public T getObject(String... express) {
		T entity = null;
		if (getForeign() != null && getForeign().getDb() != null) {
			Selector selector = Selector.from(getForeign().getForeignOrFinderEntityType()).where(getForeign().getForeignColumnName(), "=", columnValue);
			setExpress2Selector(selector, express);
			entity = (T) getForeign().getDb().query(selector);
		}
		return entity;
	}

	/**
	 * 获取外键表列对象数量
	 * 
	 * @author wangyang 2014-7-15 下午3:38:50
	 * @return
	 */
	public int getCount(String... express) {
		int count = 0;
		if (getForeign() != null && getForeign().getDb() != null) {
			ModelSelector selector = ModelSelector.from(getForeign().getForeignOrFinderEntityType()).select("count(*) as count").and(WhereBuilder.b(getForeign().getForeignColumnName(), "=", columnValue));
			setExpress2ModelSelector(selector, express);
			Model model = getForeign().getDb().query(selector);
			count = model.getInt("count");
		}
		return count;
	}

	public void setColumnValue(Object value) {
		this.columnValue = Column.convert2ColumnValue(value);
	}

	public Object getColumnValue() {
		return columnValue;
	}

	public void setDB(DBHelper db) {
		getForeign().setDb(db);
	}

	public DBHelper getDb() {
		return getForeign().getDb();
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
}
