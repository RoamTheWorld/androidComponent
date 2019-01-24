package com.android.sqlite.table;

import java.lang.reflect.Field;

import com.android.sqlite.annotation.ID;

/**
 * 表结构主键,继承自Column
 * 
 * @author wangyang 2014-7-16 下午12:04:05
 */
public class Id extends Column {
	private static final long serialVersionUID = 8105495248275339141L;
	/**
	 * 是否自增
	 */
	private boolean isAutoIncrement;

	public Id(Field field) {
		super(field);
		ID id = columnField.getAnnotation(ID.class);
		if (id != null) {
			isAutoIncrement = id.autoIncrement();
		}
	}

	public boolean isAutoIncrement() {
		return isAutoIncrement;
	}

	/**
	 * 如果数据值为0,则返回null(鉴于Number的基本类型默认值为0,所以过滤掉了0)
	 * 
	 * @author wangyang 2014-7-16 下午12:04:38
	 * @return
	 */
	@Override
	public Object getValue() {
		if (getColumnType() == ColumnType.INTEGER && super.getValue() != null)
			return Integer.parseInt(super.getValue().toString()) == 0 ? null : super.getValue();
		return super.getValue();
	}

	@Override
	public Object getColumnValue(Object entity) {
		Object value = super.getColumnValue(entity);
		if (value == null || Integer.parseInt(value.toString()) == 0)
			value = null;
		return value;
	}
}
