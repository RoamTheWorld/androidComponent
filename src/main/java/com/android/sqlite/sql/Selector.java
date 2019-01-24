package com.android.sqlite.sql;

import java.util.ArrayList;
import java.util.List;

import com.android.sqlite.table.Table;

/**
 * Selector查询条件,类似Sql操作模式
 *
 * @author wangyang
 * 2014-7-16 上午11:16:25
 */
public class Selector {
	
	/**
	 * 表映射的Class
	 */
    protected Class<?> entityType;
    
    /**
     * 表名
     */
    protected String tableName;
    
    /**
     * where条件
     */
    protected WhereBuilder whereBuilder;
    
    /**
     * order by 条件
     */
    protected List<OrderBy> orderByList;
    
    protected int limit = 0;
    protected int offset = 0;
    
    /**
     * @param entityType 表映射的Class
     */
    private Selector(Class<?> entityType) {
        this.entityType = entityType;
        this.tableName = Table.getTableName(entityType);
    }
    
    /**
     * @param tableName 表名
     */
    private Selector(String tableName) {
        this.tableName = tableName;
    }
    
    /**
     * 根据Class生成Selector查询条件
     * 
     * @author wangyang
     * 2014-7-16 上午11:17:38
     * @param entityType
     * @return
     */
    public static Selector from(Class<?> entityType) {
        return new Selector(entityType);
    }
    
    /**
     * 根据表名生成Selector查询条件
     * 
     * @author wangyang
     * 2014-7-16 上午11:17:38
     * @param entityType
     * @return
     */
    public static Selector from(String tableName) {
        return new Selector(tableName);
    }
    
    /**
     * 添加where条件
     *
     * @author wangyang
     * 2014-7-16 上午11:18:24
     * @param whereBuilder
     * @return
     */
    public Selector where(WhereBuilder whereBuilder) {
        this.whereBuilder = whereBuilder;
        return this;
    }
    
    /**
     * 添加where条件
     *
     * @author wangyang
     * 2014-7-16 上午11:18:38
     * @param columnName    列名
     * @param op			操作符
     * @param value			数据值
     * @return
     */
    public Selector where(String columnName, String op, Object value) {
        this.whereBuilder = WhereBuilder.b(columnName, op, value);
        return this;
    }
    
    /**
     * 添加where条件
     *
     * @author wangyang
     * 2014-7-16 上午11:19:05
     * @param columnName    列名
     * @param op			操作符
     * @param value			数据值
     * @return
     */
    public Selector and(String columnName, String op, Object value) {
        this.whereBuilder.and(columnName, op, value);
        return this;
    }
    
    /**
     * 添加where语句的and条件
     *
     * @author wangyang
     * 2014-7-16 上午11:19:24
     * @param where
     * @return
     */
    public Selector and(WhereBuilder where) {
        this.whereBuilder.expr("AND (" + where.toString() + ")");
        return this;
    }
    
    /**
     * 添加where语句的or条件
     *
     * @author wangyang
     * 2014-7-16 上午11:19:41
     * @param columnName    列名
     * @param op			操作符
     * @param value			数据值
     * @return
     */
    public Selector or(String columnName, String op, Object value) {
        this.whereBuilder.or(columnName, op, value);
        return this;
    }
    
    /**
     * 添加where语句的or条件
     *
     * @author wangyang
     * 2014-7-16 上午11:20:06
     * @param where
     * @return
     */
    public Selector or(WhereBuilder where) {
        this.whereBuilder.expr("OR (" + where.toString() + ")");
        return this;
    }
    
    /**
     * 添加where条件表达式
     *
     * @author wangyang
     * 2014-7-16 上午11:20:16
     * @param expr
     * @return
     */
    public Selector expr(String expr) {
        if (this.whereBuilder == null) {
            this.whereBuilder = WhereBuilder.b();
        }
        this.whereBuilder.expr(expr);
        return this;
    }
    
    /**
     * 添加where条件表达式
     *
     * @author wangyang
     * 2014-7-16 上午11:20:35
     * @param columnName    列名
     * @param op			操作符
     * @param value			数据值
     * @return
     */
    public Selector expr(String columnName, String op, Object value) {
        if (this.whereBuilder == null) {
            this.whereBuilder = WhereBuilder.b();
        }
        this.whereBuilder.expr(columnName, op, value);
        return this;
    }
    
    /**
     * 添加groupBy条件,并返回对应的ModelSelector查询条件
     *
     * @author wangyang
     * 2014-7-16 上午11:21:13
     * @param columnName
     * @return
     */
    public ModelSelector groupBy(String columnName) {
        return new ModelSelector(this, columnName);
    }
    
    /**
     * 添加查询的数据列,并返回对应的ModelSelector查询条件
     *
     * @author wangyang
     * 2014-7-16 上午11:22:57
     * @param columnExpressions
     * @return
     */
    public ModelSelector select(String... columnExpressions) {
        return new ModelSelector(this, columnExpressions);
    }
    
    /**
     * 添加order by 条件
     *
     * @author wangyang
     * 2014-7-16 上午11:24:03
     * @param columnName
     * @return
     */
    public Selector orderBy(String columnName) {
        if (orderByList == null) {
            orderByList = new ArrayList<OrderBy>(2);
        }
        orderByList.add(new OrderBy(columnName));
        return this;
    }
    
    /**
     * 添加order by条件,并设置排序方式
     *
     * @author wangyang
     * 2014-7-16 上午11:24:15
     * @param columnName
     * @param desc
     * @return
     */
    public Selector orderBy(String columnName, boolean desc) {
        if (orderByList == null) {
            orderByList = new ArrayList<OrderBy>(2);
        }
        orderByList.add(new OrderBy(columnName, desc));
        return this;
    }

    public Selector limit(int limit) {
        this.limit = limit;
        return this;
    }

    public Selector offset(int offset) {
        this.offset = offset;
        return this;
    }
    
    /**
     * 将Selector转换为sql
     *
     * @author wangyang
     * 2014-7-16 上午11:25:13
     * @return
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        
        //设置查询表名
        result.append("SELECT ");
        result.append("*");
        result.append(" FROM ").append(tableName);
        
        //设置where条件
        if (whereBuilder != null && whereBuilder.getWhereItemSize() > 0) {
            result.append(" WHERE ").append(whereBuilder.toString());
        }
        
        //设置order by 条件
        if (orderByList != null) {
            for (int i = 0; i < orderByList.size(); i++) {
                if(i==0)
                    result.append(" ORDER BY ").append(orderByList.get(i).toString());
                else
                    result.append(" , ").append(orderByList.get(i).toString());
            }
        }
        if (limit > 0) {
            result.append(" LIMIT ").append(limit);
            result.append(" OFFSET ").append(offset);
        }
        return result.toString();
    }
    
    /**
     * 获取操作表的Class
     *
     * @author wangyang
     * 2014-7-16 上午11:26:05
     * @return
     */
    public Class<?> getEntityType() {
        return entityType;
    }
    
    /**
     * 查询OrderBy条件
     *
     * Copyright: 版权所有 (c) 2014 Company: 北京开拓明天科技有限公司
     *
     * @author wangyang
     * 2014-7-16 上午11:26:14
     */
    protected class OrderBy {
    	/**
    	 * 字段名
    	 */
        private String columnName;
        /**
         * 排序方式
         */
        private boolean desc;

        public OrderBy(String columnName) {
            this.columnName = columnName;
        }

        public OrderBy(String columnName, boolean desc) {
            this.columnName = columnName;
            this.desc = desc;
        }

        @Override
        public String toString() {
            return columnName + (desc ? " DESC" : " ASC");
        }
    }
}

