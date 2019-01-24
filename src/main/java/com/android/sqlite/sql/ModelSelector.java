package com.android.sqlite.sql;

import android.text.TextUtils;

/**
 * Model对象的查询Selector,类似Sql语句操作
 *
 * @author wangyang
 * 2014-7-16 上午10:32:30
 */
public class ModelSelector {
	
	/**
	 * 查询列的表达式
	 */
    private String[] columnExpressions;
    
    /**
	 * 分组查询列名
	 */
    private String groupByColumnName;
    
    /**
	 * having条件
	 */
    private WhereBuilder having;
    
    /**
     * Selector查询条件
     */
    private Selector selector;
    
    /**
     * @param entityType 数据库表映射的Class
     */
    private ModelSelector(Class<?> entityType) {
        selector = Selector.from(entityType);
    }
    
    /**
     * @param tableName 数据库表名
     */
    private ModelSelector(String tableName) {
        selector = Selector.from(tableName);
    }
    
    /**
     * @param selector           Selector查询条件
     * @param groupByColumnName  分组查询列名
     */
    protected ModelSelector(Selector selector, String groupByColumnName) {
        this.selector = selector;
        this.groupByColumnName = groupByColumnName;
    }
    
    /**
     * @param selector           Selector查询条件
     * @param columnExpressions  查询列数据
     */
    protected ModelSelector(Selector selector, String[] columnExpressions) {
        this.selector = selector;
        this.columnExpressions = columnExpressions;
    }
    
    /**
     * 根据表Class生成Model查询条件
     *
     * @author wangyang
     * 2014-7-16 上午10:40:03
     * @param entityType
     * @return
     */
    public static ModelSelector from(Class<?> entityType) {
        return new ModelSelector(entityType);
    }
    
    /**
     * 根据表Class生成Model查询条件
     *
     * @author wangyang
     * 2014-7-16 上午10:40:03
     * @param entityType
     * @return
     */
    public static ModelSelector from(String tableName) {
        return new ModelSelector(tableName);
    }
    
    /**
     * 添加Where条件
     *
     * @author wangyang
     * 2014-7-16 上午10:43:00
     * @param whereBuilder
     * @return
     */
    public ModelSelector where(WhereBuilder whereBuilder) {
        selector.where(whereBuilder);
        return this;
    }
    
    /**
     * 添加查询条件
     *
     * @author wangyang
     * 2014-7-16 上午10:43:14
     * @param columnName    列名
     * @param op			操作符
     * @param value			数据值
     * @return
     */
    public ModelSelector where(String columnName, String op, Object value) {
        selector.where(columnName, op, value);
        return this;
    }
    
    /**
     * 添加where子句的and条件
     *
     * @author wangyang
     * 2014-7-16 上午10:43:48
     * @param columnName    列名
     * @param op			操作符
     * @param value			数据值
     * @return
     */
    public ModelSelector and(String columnName, String op, Object value) {
        selector.and(columnName, op, value);
        return this;
    }
    
    /**
     * 将where条件添加至where子句的and条件
     *
     * @author wangyang
     * 2014-7-16 上午10:43:00
     * @param whereBuilder
     * @return
     */
    public ModelSelector and(WhereBuilder where) {
        selector.and(where);
        return this;
    }
    
    /**
     * 添加where子句的or条件
     *
     * @author wangyang
     * 2014-7-16 上午10:43:48
     * @param columnName    列名
     * @param op			操作符
     * @param value			数据值
     * @return
     */
    public ModelSelector or(String columnName, String op, Object value) {
        selector.or(columnName, op, value);
        return this;
    }
    
    /**
     * 将where条件添加至where子句的or条件
     *
     * @author wangyang
     * 2014-7-16 上午10:43:00
     * @param whereBuilder
     * @return
     */
    public ModelSelector or(WhereBuilder where) {
        selector.or(where);
        return this;
    }
    
    /**
     * 添加where条件表达式
     *
     * @author wangyang
     * 2014-7-16 上午10:46:28
     * @param expr
     * @return
     */
    public ModelSelector expr(String expr) {
        selector.expr(expr);
        return this;
    }
    
    /**
     * 添加where条件表达式
     *
     * @author wangyang
     * 2014-7-16 上午10:46:43
     * @param columnName    列名
     * @param op			操作符
     * @param value			数据值
     * @return
     */
    public ModelSelector expr(String columnName, String op, Object value) {
        selector.expr(columnName, op, value);
        return this;
    }
    
    /**
     * 添加分组查询列名
     *
     * @author wangyang
     * 2014-7-16 上午10:47:04
     * @param columnName
     * @return
     */
    public ModelSelector groupBy(String columnName) {
        this.groupByColumnName = columnName;
        return this;
    }
    
    /**
     * 添加groupBy子句的having条件
     *
     * @author wangyang
     * 2014-7-16 上午10:47:25
     * @param whereBuilder
     * @return
     */
    public ModelSelector having(WhereBuilder whereBuilder) {
        this.having = whereBuilder;
        return this;
    }
    
    /**
     * 添查询列的数据
     *
     * @author wangyang
     * 2014-7-16 上午10:47:49
     * @param columnExpressions
     * @return
     */
    public ModelSelector select(String... columnExpressions) {
        this.columnExpressions = columnExpressions;
        return this;
    }
    
    /**
     * 添加order by 条件
     *
     * @author wangyang
     * 2014-7-16 上午10:48:58
     * @param columnName
     * @return
     */
    public ModelSelector orderBy(String columnName) {
        selector.orderBy(columnName);
        return this;
    }
    
    /**
     * 添加order by 条件以及排序方式
     *
     * @author wangyang
     * 2014-7-16 上午10:49:08
     * @param columnName
     * @param desc
     * @return
     */
    public ModelSelector orderBy(String columnName, boolean desc) {
        selector.orderBy(columnName, desc);
        return this;
    }
    
    /**
     * 设置一次查询的数据条数
     *
     * @author wangyang
     * 2014-7-16 上午10:51:10
     * @param limit
     * @return
     */
    public ModelSelector limit(int limit) {
        selector.limit(limit);
        return this;
    }
    
    /**
     * 设置从那条后开始查询(不包括跳过那条)
     *
     * @author wangyang
     * 2014-7-16 上午10:51:41
     * @param offset
     * @return
     */
    public ModelSelector offset(int offset) {
        selector.offset(offset);
        return this;
    }
    
    /**
     * 获得操作的表的Class
     *
     * @author wangyang
     * 2014-7-16 上午10:52:18
     * @return
     */
    public Class<?> getEntityType() {
        return selector.getEntityType();
    }
    
    /**
     * 将Model查询条件生成sql语句
     *
     * @author wangyang
     * 2014-7-16 上午10:52:35
     * @return
     */
    @Override
    public String toString() {
        StringBuffer result = new StringBuffer();
        
        //设置要查询的列
        result.append("SELECT ");
        //设置查询的列
        if (columnExpressions != null && columnExpressions.length > 0) {
            for (int i = 0; i < columnExpressions.length; i++) {
                result.append(columnExpressions[i]);
                result.append(",");
            }
            result.deleteCharAt(result.length() - 1);
        //设置分组查询的列
        } else {
            if (!TextUtils.isEmpty(groupByColumnName)) {
                result.append(groupByColumnName);
            } else {
                result.append("*");
            }
        }
        
        //设置表名
        result.append(" FROM ").append(selector.tableName);
        
        //设置where条件
        if (selector.whereBuilder != null && selector.whereBuilder.getWhereItemSize() > 0) {
            result.append(" WHERE ").append(selector.whereBuilder.toString());
        }
        
        //设置分组查询条件
        if (!TextUtils.isEmpty(groupByColumnName)) {
            result.append(" GROUP BY ").append(groupByColumnName);
            if (having != null && having.getWhereItemSize() > 0) {
                result.append(" HAVING ").append(having.toString());
            }
        }
        
        //设置order by 条件
        if (selector.orderByList != null) {
            for (int i = 0; i < selector.orderByList.size(); i++) {
                if(i==0)
                    result.append(" ORDER BY ").append(selector.orderByList.get(i).toString());
                else
                    result.append(" ,").append(selector.orderByList.get(i).toString());
            }
        }
        
        //设置limit与offeset
        if (selector.limit > 0) {
            result.append(" LIMIT ").append(selector.limit);
            result.append(" OFFSET ").append(selector.offset);
        }
        return result.toString();
    }
}
