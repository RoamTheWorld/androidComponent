package com.android.sqlite.converter;

import android.database.Cursor;

import com.android.sqlite.table.Column.ColumnType;

/**
 * Author: wangyang
 * Date: 13-11-4
 * Time: 下午10:51
 */
public class StringColumnConverter implements ColumnConverter<String> {
    @Override
    public String getFiledValue(final Cursor cursor, int index) {
        return cursor.isNull(index) ? null : cursor.getString(index);
    }

    @Override
    public String getFiledValue(String fieldStringValue) {
        return fieldStringValue;
    }

    @Override
    public Object fieldValue2ColumnValue(String fieldValue) {
        return fieldValue;
    }

    @Override
    public ColumnType getColumnType() {
        return ColumnType.TEXT;
    }
}
