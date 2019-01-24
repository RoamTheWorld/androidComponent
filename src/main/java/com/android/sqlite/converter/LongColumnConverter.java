package com.android.sqlite.converter;

import android.database.Cursor;
import android.text.TextUtils;

import com.android.sqlite.table.Column.ColumnType;

/**
 * Author: wangyang
 * Date: 13-11-4
 * Time: 下午10:51
 */
public class LongColumnConverter implements ColumnConverter<Long> {
    @Override
    public Long getFiledValue(final Cursor cursor, int index) {
        return cursor.isNull(index) ? null : cursor.getLong(index);
    }

    @Override
    public Long getFiledValue(String fieldStringValue) {
        if (TextUtils.isEmpty(fieldStringValue)) return null;
        return Long.valueOf(fieldStringValue);
    }

    @Override
    public Object fieldValue2ColumnValue(Long fieldValue) {
        return fieldValue;
    }

    @Override
    public ColumnType getColumnType() {
        return ColumnType.INTEGER;
    }
}
