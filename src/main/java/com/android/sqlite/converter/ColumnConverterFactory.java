package com.android.sqlite.converter;

import java.util.Date;

import android.support.v4.util.LruCache;

import com.android.sqlite.table.Column.ColumnType;

/**
 * @author wangyang 下午5:01:25
 */
@SuppressWarnings("rawtypes")
public class ColumnConverterFactory {

	private ColumnConverterFactory() {
	}

	public static ColumnConverter getColumnConverter(Class<?> columnType) {
		return columnType_columnConverter_map.get(columnType.getCanonicalName());
	}

	public static ColumnType getColumnType(Class columnType) {
		ColumnConverter converter = getColumnConverter(columnType);
		if (converter != null) {
			return converter.getColumnType();
		}
		return ColumnType.TEXT;
	}

	public static boolean isSupportColumnConverter(Class<?> columnType) {
		return getColumnConverter(columnType) != null;
	}

	private static final LruCache<String, ColumnConverter> columnType_columnConverter_map;

	static {
		columnType_columnConverter_map = new LruCache<String, ColumnConverter>(1024 * 1024);

		BooleanColumnConverter booleanColumnConverter = new BooleanColumnConverter();
		columnType_columnConverter_map.put(boolean.class.getCanonicalName(), booleanColumnConverter);
		columnType_columnConverter_map.put(Boolean.class.getCanonicalName(), booleanColumnConverter);

		ByteArrayColumnConverter byteArrayColumnConverter = new ByteArrayColumnConverter();
		columnType_columnConverter_map.put(byte[].class.getCanonicalName(), byteArrayColumnConverter);

		ByteColumnConverter byteColumnConverter = new ByteColumnConverter();
		columnType_columnConverter_map.put(byte.class.getCanonicalName(), byteColumnConverter);
		columnType_columnConverter_map.put(Byte.class.getCanonicalName(), byteColumnConverter);

		CharColumnConverter charColumnConverter = new CharColumnConverter();
		columnType_columnConverter_map.put(char.class.getCanonicalName(), charColumnConverter);
		columnType_columnConverter_map.put(Character.class.getCanonicalName(), charColumnConverter);

		DateColumnConverter dateColumnConverter = new DateColumnConverter();
		columnType_columnConverter_map.put(Date.class.getCanonicalName(), dateColumnConverter);

		DoubleColumnConverter doubleColumnConverter = new DoubleColumnConverter();
		columnType_columnConverter_map.put(double.class.getCanonicalName(), doubleColumnConverter);
		columnType_columnConverter_map.put(Double.class.getCanonicalName(), doubleColumnConverter);

		FloatColumnConverter floatColumnConverter = new FloatColumnConverter();
		columnType_columnConverter_map.put(float.class.getCanonicalName(), floatColumnConverter);
		columnType_columnConverter_map.put(Float.class.getCanonicalName(), floatColumnConverter);

		IntegerColumnConverter integerColumnConverter = new IntegerColumnConverter();
		columnType_columnConverter_map.put(int.class.getCanonicalName(), integerColumnConverter);
		columnType_columnConverter_map.put(Integer.class.getCanonicalName(), integerColumnConverter);

		LongColumnConverter longColumnConverter = new LongColumnConverter();
		columnType_columnConverter_map.put(long.class.getCanonicalName(), longColumnConverter);
		columnType_columnConverter_map.put(Long.class.getCanonicalName(), longColumnConverter);

		ShortColumnConverter shortColumnConverter = new ShortColumnConverter();
		columnType_columnConverter_map.put(short.class.getCanonicalName(), shortColumnConverter);
		columnType_columnConverter_map.put(Short.class.getCanonicalName(), shortColumnConverter);

		SqlDateColumnConverter sqlDateColumnConverter = new SqlDateColumnConverter();
		columnType_columnConverter_map.put(java.sql.Date.class.getCanonicalName(), sqlDateColumnConverter);

		StringColumnConverter stringColumnConverter = new StringColumnConverter();
		columnType_columnConverter_map.put(String.class.getCanonicalName(), stringColumnConverter);
	}
}
