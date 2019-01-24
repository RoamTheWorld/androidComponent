package com.android.sqlite.converter;

import android.database.Cursor;

import com.android.sqlite.table.Column.ColumnType;

/**
 * 数据列转换接口
 *
 * @author wangyang
 * 下午3:44:52
 */
public interface ColumnConverter<T>{
	/**
	 * 获取列数据库的值
	 *
	 * @author wangyang
	 * 2014-7-16 上午10:27:23
	 * @param cursor
	 * @param index
	 * @return
	 */
	T getFiledValue(final Cursor cursor, int index);
	
	/**
	 * 获取数据列的实际值
	 *
	 * @author wangyang
	 * 2014-7-16 上午10:27:59
	 * @param fieldStringValue
	 * @return
	 */
    T getFiledValue(String fieldStringValue);
    
    /**
     * 将属性值转换为数据库字段值
     *
     * @author wangyang
     * 2014-7-16 上午10:28:51
     * @param fieldValue
     * @return
     */
    Object fieldValue2ColumnValue(T fieldValue);
    
    /**
     * 数据列在数据库的数据类型
     *
     * @author wangyang
     * 2014-7-16 上午10:28:55
     * @return
     */
    ColumnType getColumnType();
}
