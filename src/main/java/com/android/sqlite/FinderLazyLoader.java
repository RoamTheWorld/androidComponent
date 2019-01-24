package com.android.sqlite;

import java.io.Serializable;
import java.util.List;

import com.android.sqlite.sql.ModelSelector;
import com.android.sqlite.sql.Selector;
import com.android.sqlite.sql.WhereBuilder;
import com.android.sqlite.table.Column;
import com.android.sqlite.table.Finder;
import com.android.sqlite.table.Model;
import com.android.utils.StringUtil;

/**
 * 外键表的延迟加载类,关联字段在外键表
 * 
 * @author wangyang 2014-7-15 下午3:21:46
 * @param <T>
 */
public class FinderLazyLoader<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 外键列Finder
	 */
	private transient Finder finderColumn;

	/**
	 * 关联主键表的值
	 */
	private Object finderValue;

	/**
	 * 外键表的值
	 */
	private Object value;

	/**
	 * 关联主键表字段名
	 */
	private String columnName;

	/**
	 * 主键表Class
	 */
	private Class<?> entityType;

	/**
	 * 是否迭代生成父类字段
	 */
	private boolean isRecursion;

	/**
	 * 
	 * @param entityType
	 *            主键表Class
	 * @param columnName
	 *            关联主键表字段名
	 * @param findValue
	 *            关联主键表的值
	 * @param value
	 *            外键表的值
	 * @param isRecursion
	 *            是否迭代生成父类字段
	 */
	public FinderLazyLoader(Class<?> entityType, String columnName, Object findValue, Object value, boolean isRecursion) {
		this.finderColumn = (Finder) Column.getFinderOrId(entityType, columnName, isRecursion);
		this.finderValue = Column.convert2ColumnValue(findValue);
		this.isRecursion = isRecursion;
		this.columnName = columnName;
		this.entityType = entityType;
		this.value = value;
	}

	/**
	 * 
	 * @param entityType
	 *            主键表Class
	 * @param columnName
	 *            关联主键表字段名
	 * @param value
	 *            外键表的值
	 */
	public FinderLazyLoader(Class<?> entityType, String columnName, Object value) {
		this(entityType, columnName, null, value, true);
	}

	/**
	 * 
	 * @param entityType
	 *            主键表Class
	 * @param columnName
	 *            关联主键表字段名
	 * @param value
	 *            外键表的值
	 */
	public FinderLazyLoader(Class<?> entityType, String columnName, Object value, boolean isRecursion) {
		this(entityType, columnName, null, value, isRecursion);
	}

	/**
	 * 
	 * @param entityType
	 *            主键表Class
	 * @param columnName
	 *            关联主键表字段名
	 * @param findValue
	 *            关联主键表的值
	 * @param value
	 *            外键表的值
	 */
	public FinderLazyLoader(Class<?> entityType, String columnName, Object findValue, Object value) {
		this(entityType, columnName, findValue, value, true);
	}

	/**
	 * 
	 * @param finderColumn
	 *            外键列Finder
	 * @param findValue
	 *            关联主键表的值
	 */
	public FinderLazyLoader(Finder finderColumn, Object findValue, boolean isRecursion) {
		this.finderColumn = finderColumn;
		this.finderValue = Column.convert2ColumnValue(findValue);
		this.columnName = finderColumn.getColumnName();
		this.isRecursion = isRecursion;
		this.entityType = finderColumn.getForeignOrFinderEntityType();
		this.value = null;
	}

	/**
	 * 获取外键列Finder
	 * 
	 * @author wangyang 2014-7-15 下午3:34:36
	 * @return
	 */
	private Finder getFinder() {
		// 如果finderColumn为null,则根据条件获取finderColumn
		if (finderColumn == null) {
			if (entityType == null)
				return null;
			if (StringUtil.isEmpty(columnName))
				return null;
			finderColumn = (Finder) Column.getFinderOrId(entityType, columnName, isRecursion);
		}
		return finderColumn;
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
		if (getFinder() != null && getFinder().getDb() != null) {
			// 分别查询不为多对多,以及多对多关系的数据
			if (StringUtil.isEmpty(getFinder().getMany2Many())) {
				Selector selector = Selector.from(getFinder().getForeignOrFinderEntityType()).where(getFinder().getTargetColumnName(), "=", finderValue);
				setExpress2Selector(selector, express);
				entities = (List<T>) getFinder().getDb().list(selector);
			} else {
				if (!getFinder().IsInverse()) {
					ModelSelector subSelector = Selector.from(getFinder().getMany2Many()).select(getFinder().getFinder().getTargetColumnName()).where(getFinder().getTargetColumnName(), "=", finderValue);
					String expr = getFinder().getColumnName() + " in (" + subSelector.toString() + ")";
					Selector selector = Selector.from(getFinder().getForeignOrFinderEntityType()).expr(expr);
					setExpress2Selector(selector, express);
					entities = (List<T>) getFinder().getDb().list(selector);
				}
			}
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
		if (getFinder() != null && getFinder().getDb() != null) {
			// 分别查询不为多对多,以及多对多关系的数据
			if (StringUtil.isEmpty(getFinder().getMany2Many())) {
				Selector selector = Selector.from(getFinder().getForeignOrFinderEntityType()).where(getFinder().getTargetColumnName(), "=", finderValue);
				setExpress2Selector(selector, express);
				entity = (T) getFinder().getDb().query(selector);
			} else {
				if (!getFinder().IsInverse()) {
					ModelSelector subSelector = Selector.from(getFinder().getMany2Many()).select(getFinder().getFinder().getTargetColumnName()).where(getFinder().getTargetColumnName(), "=", finderValue);
					String expr = getFinder().getColumnName() + " = " + subSelector.toString();
					Selector selector = Selector.from(getFinder().getForeignOrFinderEntityType()).expr(expr);
					setExpress2Selector(selector, express);
					entity = (T) getFinder().getDb().query(selector);
				}
			}
		}
		return entity;
	}

	/**
	 * 延迟加载外键表列表,并按照自己要求排序
	 * 
	 * @author wangyang 2014-7-15 下午3:38:03
	 * @param order
	 * @param desc
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public T getObject(String order, boolean desc, String... express) {
		T entity = null;
		if (getFinder() != null && getFinder().getDb() != null) {
			// 分别查询不为多对多,以及多对多关系的数据
			if (StringUtil.isEmpty(getFinder().getMany2Many())) {
				Selector selector = Selector.from(getFinder().getForeignOrFinderEntityType()).where(getFinder().getTargetColumnName(), "=", finderValue).orderBy(order, desc);
				setExpress2Selector(selector, express);
				entity = (T) getFinder().getDb().query(selector);
			} else {
				if (!getFinder().IsInverse()) {
					ModelSelector subSelector = Selector.from(getFinder().getMany2Many()).select(getFinder().getFinder().getTargetColumnName()).where(getFinder().getTargetColumnName(), "=", finderValue);
					String expr = getFinder().getColumnName() + " in (" + subSelector.toString() + ")";
					Selector selector = Selector.from(getFinder().getForeignOrFinderEntityType()).expr(expr).orderBy(expr, desc);
					setExpress2Selector(selector, express);
					entity = (T) getFinder().getDb().query(selector);
				}
			}
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
		if (getFinder() != null && getFinder().getDb() != null) {
			// 分别查询不为多对多,以及多对多关系的数量
			if (StringUtil.isEmpty(getFinder().getMany2Many())) {
				ModelSelector selector = ModelSelector.from(getFinder().getForeignOrFinderEntityType()).select("count(*) as count").and(WhereBuilder.b(getFinder().getTargetColumnName(), "=", finderValue));
				setExpress2ModelSelector(selector, express);
				Model model = getFinder().getDb().query(selector);
				count = model.getInt("count");
			} else {
				if (!getFinder().IsInverse()) {
					ModelSelector subSelector = Selector.from(getFinder().getMany2Many()).select(getFinder().getFinder().getTargetColumnName()).where(getFinder().getTargetColumnName(), "=", finderValue);
					String expr = getFinder().getColumnName() + " in (" + subSelector.toString() + ")";
					ModelSelector selector = Selector.from(getFinder().getForeignOrFinderEntityType()).select("count(*) as count").expr(expr);
					setExpress2ModelSelector(selector, express);
					count = getFinder().getDb().query(selector).getInt("count");
				}
			}
		}
		return count;
	}

	public Object getColumnValue() {
		return finderValue;
	}

	public void setFinderValue(Object finderValue) {
		this.finderValue = finderValue;
	}

	public Object getValue() {
		return value;
	}

	public DBHelper getDb() {
		return getFinder().getDb();
	}

	public void setValue(Object value) {
		this.value = value;
	}
}
