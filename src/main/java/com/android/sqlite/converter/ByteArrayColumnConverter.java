package com.android.sqlite.converter;

import android.database.Cursor;

import com.android.sqlite.table.Column.ColumnType;

/**
 * Author: wangyang
 * Date: 13-11-4
 * Time: 下午10:51
 */
public class ByteArrayColumnConverter implements ColumnConverter<byte[]> {
    @Override
    public byte[] getFiledValue(final Cursor cursor, int index) {
        return cursor.isNull(index) ? null : cursor.getBlob(index);
    }

    @Override
    public byte[] getFiledValue(String fieldStringValue) {
        return null;
    }

    @Override
    public Object fieldValue2ColumnValue(byte[] fieldValue) {
        return fieldValue;
    }

    @Override
    public ColumnType getColumnType() {
        return ColumnType.BLOB;
    }
}
