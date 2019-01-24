package com.android.sqlite.simple.example;

import android.content.Context;

import com.android.sqlite.simple.BaseDAO;

/**
 * @description 此类在引用项目中创建，数据库版本，数据库名称及表创建语句在常量类中定义，实现类集成此类。
 * 
 * @author wangyang
 * @create 2014-2-21 上午11:40:27
 * @version 1.0.0
 * @param <M>
 * @param <M>
 */
public abstract class DAOImpl<M> extends BaseDAO<M> {
	protected Context context;

	public DAOImpl(Context context) {
		super(context, DataBase.VERSION, DataBase.DBNAME, true, DataBase.mSql_ddl);
		this.context = context;
	}
}
