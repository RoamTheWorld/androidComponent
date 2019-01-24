package com.android.sqlite.table;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.text.TextUtils;

import com.android.sqlite.DBHelper;
import com.android.sqlite.ForeignLazyLoader;
import com.android.sqlite.converter.ColumnConverter;
import com.android.sqlite.converter.ColumnConverterFactory;
import com.android.utils.Log;
import com.android.utils.StringUtil;

/**
 * 外键类,关联字段在主键表,继承自Column
 * 
 * @author wangyang 2014-4-22 下午3:01:31
 */
@SuppressWarnings("rawtypes")
public class Foreign extends Column {

	private static final long serialVersionUID = -2207790804603399982L;

	/**
	 * 操作数据库工具
	 */
	private DBHelper db;

	/**
	 * 外键注解
	 */
	private com.android.sqlite.annotation.Foreign foregin;

	/**
	 * 主键列实际的值
	 */
	private Object columnValue;

	/**
	 * 外键表实际的列
	 */
	private Column targetColumn;

	/**
	 * 转换列值的转换工具
	 */
	private final ColumnConverter foreignConverter;

	/**
	 * 是否迭代父类字段
	 */
	private boolean isRecursion;

	protected Foreign(Field field, boolean isRecursion) {
		super(field);
		foregin = field.getAnnotation(com.android.sqlite.annotation.Foreign.class);
		targetColumn = getColumnOrId(getForeignOrFinderEntityType(), getForeignColumnName(), isRecursion);
		foreignConverter = ColumnConverterFactory.getColumnConverter(targetColumn.getType());
		this.isRecursion = isRecursion;
	}

	public String getForeignColumnName() {
		return foregin.foreign();
	}

	@Override
	public String getColumnName() {
		String columnName = columnField.getName();
		if (foregin != null && !TextUtils.isEmpty(foregin.column()))
			columnName = foregin.column();
		return columnName;
	}

	/**
	 * 外键列关联字段是否在父类
	 * 
	 * @author wangyang 2014-7-16 上午11:48:18
	 * @return
	 */
	public boolean isSuperClass() {
		return foregin.isSuperClass();
	}

	@Override
	public void setValue2Entity(Object entity, Cursor cursor, int index) {
		Object filedValue = foreignConverter.getFiledValue(cursor, index);
		if (filedValue == null)
			return;

		try {
			Object value = null;
			if (Column.lazyLoaderTypes.contains(getType())) {
				value = new ForeignLazyLoader(this, filedValue, isRecursion);
			} else if (Column.listTypes.contains(getType())) {
				value = new ForeignLazyLoader(this, filedValue, isRecursion).getAll2List();
			} else {
				value = new ForeignLazyLoader(this, filedValue, isRecursion).getObject();
			}
			if (value != null)
				this.columnField.set(entity, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据字段值与字段名保存至数据库,并返回关联字段值
	 * 
	 * @author wangyang 2014-7-16 下午12:03:45
	 * @param entity
	 * @return
	 */
	@Override
	public Object getColumnValue(Object entity) {
		value = getFieldValue(entity);

		columnValue = null;
		if (value == null)
			return columnValue;

		try {
			if (Column.lazyLoaderTypes.contains(getType())) {
				ForeignLazyLoader foreignLazyLoader = (ForeignLazyLoader) value;
				columnValue = foreignLazyLoader.getColumnValue();
				if (StringUtil.isEmpty(columnValue))
					columnValue = getColumnValueAndSaveDb(foreignLazyLoader.getValue());
			} else if (Column.listTypes.contains(getType())) {
				List<?> foreignEntities = (List<?>) value;
				if (foreignEntities.size() > 0)
					columnValue = getColumnValueAndSaveDb(foreignEntities);
			} else {
				columnValue = getColumnValueAndSaveDb(value);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return columnValue;
	}

	/**
	 * 根据字段值与字段名保存至数据库,并返回关联字段值
	 * 
	 * @author wangyang 2014-4-22 下午2:06:14
	 * @param fieldValue
	 * @param columnValue
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Object getColumnValueAndSaveDb(Object fieldValue) {
		if (fieldValue == null)
			return null;

		List<Object> list = new ArrayList<Object>();
		if (fieldValue instanceof List)
			list = (List<Object>) fieldValue;
		else
			list.add(fieldValue);

		if (db != null && targetColumn instanceof Column)
			for (Object object : list) 
				saveOrUpdate(getForeignOrFinderEntityType(), object);

		return targetColumn == null || list.isEmpty() ? null : targetColumn.getColumnValue(list.get(0));
	}

	private void saveOrUpdate(Class<?> clazz, Object fieldValue) {
		Log.e("dbhelper  foregin", clazz.getSimpleName() + "====" + fieldValue.toString());
		if (db.query(clazz, fieldValue, null, null) == null)
			db.saveOrUpdateWithInnerTransaction(clazz, fieldValue, null, null);
	}

	/**
	 * 根据实体,与数据库操作对象,获取字段值
	 * 
	 * @author wangyang 2014-7-16 下午12:02:19
	 * @param entity
	 * @param db
	 * @return
	 */
	public Object getFieldObjectValue(Object entity, DBHelper db) {
		Object fieldValue = getFieldValue(entity);
		if (fieldValue == null) {
			entity = db.query(entity);
			fieldValue = getFieldValue(entity);
		}

		if (Column.lazyLoaderTypes.contains(getType()) || Column.listTypes.contains(getType())) {
			ForeignLazyLoader foreignLazyLoader = (ForeignLazyLoader) fieldValue;
			foreignLazyLoader.setDB(db);
			fieldValue = foreignLazyLoader.getObject();
		}
		return fieldValue;

	}

	@Override
	public ColumnType getColumnType() {
		return foreignConverter.getColumnType();
	}

	@Override
	public void setValue(Object value) {
		this.columnValue = value;
		this.value = value;
	}

	@Override
	public Object getValue() {
		return columnValue;
	}

	@Override
	public Object getDefaultValue() {
		return null;
	}

	public DBHelper getDb() {
		return db;
	}

	public Column getTargetColumn() {
		return targetColumn;
	}

	public void setDb(DBHelper db) {
		this.db = db;
	}
}
