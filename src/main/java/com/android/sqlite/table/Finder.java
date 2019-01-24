package com.android.sqlite.table;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.text.TextUtils;

import com.android.sqlite.DBHelper;
import com.android.sqlite.FinderLazyLoader;
import com.android.sqlite.sql.SqlBuilder;
import com.android.utils.StringUtil;

/**
 * Finder外键列,关联字段在外键表
 * 
 * @author wangyang 2014-7-16 上午11:42:35
 */
@SuppressWarnings("rawtypes")
public class Finder extends Column {

	private static final long serialVersionUID = -54664345511430911L;

	/**
	 * 数据库操作对象
	 */
	private DBHelper db;

	/**
	 * 外键列注解
	 */
	private com.android.sqlite.annotation.Finder finderAnnot;

	/**
	 * 多对多时,另一个主键表的Finder
	 */
	private Finder finder;

	/**
	 * 是否迭代父类字段
	 */
	private boolean isRecursion;

	/**
	 * 当前表名对应的Class
	 */
	private Class<?> clazz;

	public Finder(Field field, Class<?> clazz, boolean isRecursion) {
		super(field);
		finderAnnot = columnField.getAnnotation(com.android.sqlite.annotation.Finder.class);
		if (!StringUtil.isEmpty(getMany2Many()) && !IsInverse())
			finder = Column.getMany2ManyFinder(getForeignOrFinderEntityType(), getMany2Many(), isRecursion);
		this.isRecursion = isRecursion;
		this.clazz = clazz;
	}

	/**
	 * 获取外键表的关联字段名
	 * 
	 * @author wangyang 2014-7-16 上午11:48:03
	 * @return
	 */
	public String getTargetColumnName() {
		return finderAnnot.targetColumn();
	}

	public boolean IsInverse() {
		return finderAnnot.inverse();
	}

	/**
	 * 外键列关联字段是否在父类
	 * 
	 * @author wangyang 2014-7-16 上午11:48:18
	 * @return
	 */
	public boolean isSuperClass() {
		return finderAnnot.isSuperClass();
	}

	/**
	 * 是否为多对多关系
	 * 
	 * @author wangyang 2014-8-8 上午10:09:34
	 * @return
	 */
	public String getMany2Many() {
		return finderAnnot.many2many();
	}

	@Override
	public String getColumnName() {
		String columnName = columnField.getName();
		if (finderAnnot != null && !TextUtils.isEmpty(finderAnnot.column()))
			columnName = finderAnnot.column();
		return columnName;
	}

	@Override
	public void setValue2Entity(Object entity, Cursor cursor, int index) {
		Object value = null;
		// 获得关联字段值
		Object finderValue = Column.getColumnOrId(entity.getClass(), getColumnName(), isRecursion).getColumnValue(entity);

		if (finderValue == null)
			return;

		// 判断关联字段类型
		try {
			// 如果为延迟加载则将延迟加载类设置为值
			if (Column.lazyLoaderTypes.contains(getType())) {
				value = new FinderLazyLoader(this, finderValue, isRecursion);
				// 如果为泛型集合则直接查询所有关联数据
			} else if (Column.listTypes.contains(getType())) {
				value = new FinderLazyLoader(this, finderValue, isRecursion).getAll2List();
				// 如果为其他,则设置关联对象数据
			} else {
				value = new FinderLazyLoader(this, finderValue, isRecursion).getObject();
			}
			if (value != null)
				this.columnField.set(entity, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据实体,关联字段值,数据库操作对象,保存外键表数据
	 * 
	 * @author wangyang 2014-5-4 下午6:20:15
	 * @param entity
	 * @param finderValue
	 * @param finderValue
	 * @param dbHelper
	 * @return
	 */
	public boolean saveFinder2Db(Object entity, Object finderValue, DBHelper db) {
		this.db = db;
		boolean result = true;

		// 获取外键列字段值
		value = getFieldValue(entity);
		if (value == null)
			return result;
		try {
			// 当前列如果为延迟加载对象,则保存外键表的数据值
			if (Column.lazyLoaderTypes.contains(getType())) {
				FinderLazyLoader loader = (FinderLazyLoader) value;
				loader.setFinderValue(finderValue);
				result = saveDb(loader.getValue(), finderValue);
				// 当前列如果为泛型集合,则保存外键表的数据值
			} else if (Column.listTypes.contains(getType())) {
				List<?> foreignEntities = (List<?>) value;
				if (foreignEntities.size() > 0)
					result = saveDb(foreignEntities, finderValue);
				// 如果为其他,则保存外键列的值
			} else {
				result = saveDb(value, finderValue);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 根据字段值与字段名保存至数据库
	 * 
	 * @author wangyang 2014-4-22 下午2:06:14
	 * @param fieldValue
	 * @param columnValue
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private boolean saveDb(Object fieldValue, Object finderValue) {
		boolean result = true;
		if (fieldValue == null)
			return result;
		
		List<Object> list = new ArrayList<Object>();
		if (db != null) {
			if (fieldValue instanceof List) 
				list = (List<Object>) fieldValue;
			else 
				list.add(fieldValue);
			
			for (Object object : list) {
				// 如果不为多对多关系,则直接插入,否则插入完毕后,插入关联关系中间表
				if (finder == null) {
					result = db.saveOrUpdateWithInnerTransaction(getForeignOrFinderEntityType(), object, getTargetColumnName(), finderValue);
				} else {
					result = db.saveOrUpdateWithInnerTransaction(getForeignOrFinderEntityType(), object, null, null);

					// 插入关联关系中间表
					SqlBuilder.CreateTableIfNotExists(db, getMany2Many(), finder, this);
					saveMiddleTable(object, finderValue, finder);
				}
			}
			
		}

		this.db = null;
		return result;
	}

	/**
	 * 插入中间表
	 * 
	 * @author wangyang 2014-8-12 下午2:19:27
	 * @param fieldValue
	 *            另一张多对多表关联对象
	 * @param finderValue
	 *            当前表关联字段值
	 * @param finder
	 *            当前表的finder
	 */
	private void saveMiddleTable(Object fieldValue, Object finderValue, Finder finder) {
		String[] columnNames = { getTargetColumnName(), finder.getTargetColumnName() };
		Object[] values = new Object[2];
		values[0] = finderValue;

		// 生成另一张表关联字段值
		Column column = Table.tableMap.get(getForeignOrFinderEntityType()).getColumnByColumnName(finder.getColumnName());
		values[1] = column.getColumnValue(fieldValue);

		if (StringUtil.isEmpty(values[1])) {
			fieldValue = db.query(fieldValue);
			values[1] = column.getColumnValue(fieldValue);
		}

		// 将两张表关联字段值插入中间表
		db.insertByTableNameAndColumns(getMany2Many(), columnNames, values);
	}

	@Override
	public ColumnType getColumnType() {
		return getColumnOrId(clazz, getColumnName(), isRecursion).getColumnType();
	}

	@Override
	public Object getColumnValue(Object entity) {
		return null;
	}

	@Override
	public Object getDefaultValue() {
		return null;
	}

	public DBHelper getDb() {
		return db;
	}

	public void setDb(DBHelper db) {
		this.db = db;
	}

	public Finder getFinder() {
		return finder;
	}
}
