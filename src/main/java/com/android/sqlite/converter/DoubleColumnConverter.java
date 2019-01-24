package com.android.sqlite.converter;

import android.database.Cursor;
import android.text.TextUtils;

import com.android.sqlite.table.Column.ColumnType;

/**
 * Author: wangyang
 * Date: 13-11-4
 * Time: 下午10:51
 */
public class DoubleColumnConverter implements ColumnConverter<Double> {
    @Override
    public Double getFiledValue(final Cursor cursor, int index) {
        return cursor.isNull(index) ? null : cursor.getDouble(index);
    }

    @Override
    public Double getFiledValue(String fieldStringValue) {
        if (TextUtils.isEmpty(fieldStringValue)) return null;
        return Double.valueOf(fieldStringValue);
    }

    @Override
    public Object fieldValue2ColumnValue(Double fieldValue) {
        return fieldValue;
    }

    @Override
    public ColumnType getColumnType() {
        return ColumnType.REAL;
    }
}
