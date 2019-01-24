package com.android.sqlite.simple.annotation;

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
public @interface Coloumn {
	String value() default "";// 若value为空则取属性名为数据库列名
	/*
	 * boolean isWhereClause() default false;//该属性是否为where条件,默认为false boolean
	 * isOrderBy() default false;//该属性是否是orderBy条件,默认为false boolean isGroupBy()
	 * default false;//该属性是否为GroupBy条件,默认为false
	 */
}
