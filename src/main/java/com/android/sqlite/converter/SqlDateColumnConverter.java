package com.android.sqlite.converter;

import android.database.Cursor;
import android.text.TextUtils;

import com.android.sqlite.table.Column.ColumnType;

/**
 * Author: wangyang
 * Date: 13-11-4
 * Time: 下午10:51
 */
public class SqlDateColumnConverter implements ColumnConverter<java.sql.Date> {
    @Override
    public java.sql.Date getFiledValue(final Cursor cursor, int index) {
        return cursor.isNull(index) ? null : new java.sql.Date(cursor.getLong(index));
    }

    @Override
    public java.sql.Date getFiledValue(String fieldStringValue) {
        if (TextUtils.isEmpty(fieldStringValue)) return null;
        return new java.sql.Date(Long.valueOf(fieldStringValue));
    }

    @Override
    public Object fieldValue2ColumnValue(java.sql.Date fieldValue) {
        if (fieldValue == null) return null;
        return fieldValue.getTime();
    }

    @Override
    public ColumnType getColumnType() {
        return ColumnType.INTEGER;
    }
}
