package com.android.sqlite.annotation;

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
public @interface Table {
	/**
	 * 数据库表名,若value为空则取类名为数据库表名
	 */
	String name() default "";
	
	/**
	 *  建表后执行的SQL
	 */
	String execAfterTableCreated() default "";
}
