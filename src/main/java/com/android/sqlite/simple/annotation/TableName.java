package com.android.sqlite.simple.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @author wangyang 该注解用于定义哪些类需要与数据库建立ORM映射
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TableName {
	String value() default "";// 如果有值,则取值作为表名,否则去类名为表名,默认为空
}
