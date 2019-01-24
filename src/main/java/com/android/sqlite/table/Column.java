package com.android.sqlite.table;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.text.TextUtils;

import com.android.sqlite.FinderLazyLoader;
import com.android.sqlite.ForeignLazyLoader;
import com.android.sqlite.converter.ColumnConverter;
import com.android.sqlite.converter.ColumnConverterFactory;

/**
 * 数据列
 * 
 * @author wangyang 2014-7-16 上午11:35:09
 */
@SuppressWarnings("rawtypes")
public class Column implements Serializable {

	private static final long serialVersionUID = -8878695840873552312L;

	/**
	 * 默认值
	 */
	private final Object defaultValue;

	/**
	 * 数据值
	 */
	protected Object value;

	/**
	 * 当前字段
	 */
	protected transient final Field columnField;

	/**
	 * 列转换类
	 */
	protected final ColumnConverter converter;

	/**
	 * 字段注解
	 */
	protected com.android.sqlite.annotation.Column column;

	public Column(Field field) {
		super();
		this.columnField = field;
		this.columnField.setAccessible(true);
		column = columnField.getAnnotation(com.android.sqlite.annotation.Column.class);
		this.converter = ColumnConverterFactory.getColumnConverter(columnField.getType());
		if (converter != null)
			this.defaultValue = converter.getFiledValue(getDefaultStringValue());
		else
			this.defaultValue = null;
		value = null;
	}

	/**
	 * 获取当前表字段名
	 * 
	 * @author wangyang 2014-4-21 下午4:02:34
	 * @return
	 */
	public String getColumnName() {
		String columnName = columnField.getName();
		if (column != null && !TextUtils.isEmpty(column.name()))
			columnName = column.name();
		return columnName;
	}

	/**
	 * 获取数据库字段类型
	 * 
	 * @author wangyang 2014-4-21 下午4:03:00
	 * @return
	 */
	public ColumnType getColumnType() {
		return converter.getColumnType();
	}

	public boolean ignoreQuery() {
		return column == null ? false : column.ignoreQuery();
	}

	/**
	 * 获取字段类型
	 * 
	 * @author wangyang 2014-4-22 上午11:46:49
	 * @return
	 */
	public Class<?> getType() {
		return columnField == null ? null : columnField.getType();
	}

	/**
	 * 获取默认值
	 * 
	 * @author wangyang 2014-4-21 下午4:02:52
	 * @return
	 */
	public Object getDefaultValue() {
		return defaultValue;
	}

	/**
	 * 获取字段默认值
	 * 
	 * @author wangyang 2014-4-14 下午3:36:51
	 * @return
	 */
	private String getDefaultStringValue() {
		return column == null ? null : TextUtils.isEmpty(column.defaultValue()) ? null : column.defaultValue();
	}

	/**
	 * 根据实体获取数据库的值
	 * 
	 * @author wangyang 2014-4-14 下午3:37:01
	 * @param entity
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Object getColumnValue(Object entity) {
		if (entity == null)
			return null;
		value = converter.fieldValue2ColumnValue(getFieldValue(entity));
		return value;
	}

	/**
	 * 根据字段值转换为数据库的值
	 * 
	 * @author wangyang 2014-4-16 下午5:19:30
	 * @param value
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Object convert2ColumnValue(Object value) {
		if (value == null)
			return null;
		ColumnConverter converter = ColumnConverterFactory.getColumnConverter(value.getClass());
		if (converter == null)
			return value;
		else
			return converter.fieldValue2ColumnValue(value);
	}

	/**
	 * 获取Vlaue的值
	 * 
	 * @author wangyang 2014-4-16 下午4:11:17
	 * @param entity
	 * @throws IllegalAccessException
	 */
	public Object getFieldValue(Object entity) {
		try {
			if (entity == null)
				value = null;
			else
				value = columnField.get(entity);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}

	/**
	 * 将数据库值设置到实体
	 * 
	 * @author wangyang 2014-7-16 上午11:36:02
	 * @param entity
	 * @param cursor
	 * @param index
	 */
	public void setValue2Entity(Object entity, Cursor cursor, int index) {
		Object value = converter.getFiledValue(cursor, index);
		if (value == null && defaultValue == null)
			return;
		try {
			this.columnField.set(entity, value == null ? defaultValue : value);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取外键类型或延迟加载类型
	 * 
	 * @author wangyang 2014-4-22 上午10:42:39
	 * @return
	 */
	public Class<?> getForeignOrFinderEntityType() {
		if (lazyLoaderTypes.contains(getType()) || listTypes.contains(getType()))
			return (Class<?>) ((ParameterizedType) columnField.getGenericType()).getActualTypeArguments()[0];
		return getType();
	}

	/**
	 * 根据实体类型,最字段名称获取字段,如果主键存在则获取主键
	 * 
	 * @author wangyang 2014-4-22 上午10:43:10
	 * @param entityType
	 * @param columnName
	 * @return
	 */
	public synchronized static Column getColumnOrId(Class<?> entityType, String columnName, boolean isRecursion) {
		Column column = getId(entityType, columnName, isRecursion);
		if (column == null)
			column = getAttributeColumn(entityType, columnName, isRecursion);
		return column;
	}

	/**
	 * 根据实体类型,最字段名称获取字段,如果主键存在则获取主键
	 * 
	 * @author wangyang 2014-4-22 上午10:43:10
	 * @param entityType
	 * @param columnName
	 * @return
	 */
	public synchronized static Column getFinderOrId(Class<?> entityType, String columnName, boolean isRecursion) {
		Column column = getId(entityType, columnName, isRecursion);
		if (column == null)
			column = getFinderColumn(entityType, columnName, isRecursion);
		return column;
	}

	/**
	 * 根据传入的Class 中间表名获得关联表的Finder
	 * 
	 * @author wangyang 2014-8-8 上午11:21:27
	 * @param entityType
	 * @param many2manyName
	 * @param isRecursion
	 * @return
	 */
	public synchronized static Finder getMany2ManyFinder(Class<?> entityType, String many2manyName, boolean isRecursion) {
		Finder finder = null;
		Table table = Table.get(entityType, false, true, isRecursion);
		if (table.getFinders() != null && table.getFinders().size() > 0) {
			for (Finder f : table.getFinders().values()) {
				if (many2manyName.equals(f.getMany2Many())) {
					finder = f;
					break;
				}
			}
		}
		return finder;
	}

	/**
	 * 根据实体类型,获取主键,如果不存在表,则创建并添加字段集合
	 * 
	 * @author wangyang 2014-4-22 上午10:45:00
	 * @param entityType
	 * @param columnName
	 * @return
	 */
	private synchronized static Column getFinderColumn(Class<?> entityType, String columnName, boolean isRecursion) {
		Table table = Table.get(entityType, false, true, isRecursion);
		return table.getFinders().get(columnName);
	}

	/**
	 * 根据实体类型,获取主键,如果不存在表,则创建并添加字段集合
	 * 
	 * @author wangyang 2014-4-22 上午10:45:00
	 * @param entityType
	 * @param columnName
	 * @return
	 */
	private synchronized static Column getAttributeColumn(Class<?> entityType, String columnName, boolean isRecursion) {
		Table table = Table.get(entityType, false, true, isRecursion);
		return table.getAttributes().get(columnName);
	}

	/**
	 * 根据实体类型,获取主键,如果不存在表,则创建并添加主键集合
	 * 
	 * @author wangyang 2014-4-22 上午10:45:43
	 * @param entityType
	 * @param columnName
	 * @return
	 */
	public synchronized static Id getId(Class<?> entityType, String columnName, boolean isRecursion) {
		Table table = Table.get(entityType, true, false, isRecursion);
		return columnName.equals(table.getId().getColumnName()) ? table.getId() : null;
	}

	public Field getColumnField() {
		return columnField;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public boolean isUnique() {
		return column == null ? null : column.isUnique();
	}

	public boolean isNotNull() {
		return column == null ? null : column.isNotNull();
	}

	public String getCheck() {
		return column == null ? null : column.check();
	}

	/**
	 * 延迟加载的Clas与集合Class
	 */
	public static final List<Class<?>> lazyLoaderTypes = new ArrayList<Class<?>>();
	public static final List<Class<?>> listTypes = new ArrayList<Class<?>>();

	static {
		listTypes.add(List.class);
		listTypes.add(ArrayList.class);
		lazyLoaderTypes.add(FinderLazyLoader.class);
		lazyLoaderTypes.add(ForeignLazyLoader.class);
	}

	/**
	 * 数据库的四种字段类型
	 * 
	 * Copyright: 版权所有 (c) 2014 Company: 北京开拓明天科技有限公司
	 * 
	 * @author wangyang 下午3:51:33
	 */
	public enum ColumnType {
		INTEGER("INTEGER"), REAL("REAL"), TEXT("TEXT"), BLOB("BLOB");

		private String value;

		ColumnType(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return value;
		}
	}

	@Override
	public String toString() {
		return "Column [value=" + value + "]";
	}
}
