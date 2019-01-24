package com.android.sqlite.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @author wangyang 该注解用于定义类的哪些属性为对应的数据库字段
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Column {
	/**
	 * 数据库字段名,若value为空则取属性名为数据库列名
	 */
	String name() default "";
	
	/**
	 * 默认值
	 */
	String defaultValue() default "";
	
	/**
	 * 是否为唯一约束
	 */
	boolean isUnique() default false;
	
	/**
	 * 允许为空
	 */
	boolean isNotNull() default false;
	
	/**
	 * 校验约束
	 */
	String check() default "";
	
	/**
	 * 是否在查询时忽略
	 *
	 * @author wangyang
	 * 2015-1-27 下午9:17:24
	 * @return
	 */
	boolean ignoreQuery() default false;
}
