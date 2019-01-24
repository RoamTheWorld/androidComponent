package com.android.sqlite.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Finder {
	
	/**
	 * 数据库字段名,若value为空则取属性名为数据库列名
	 */
	String column();
	
	/**
	 * 外键表关联字段
	 */
	String targetColumn();
	
	/**
	 * 多对多关联时的中间表名
	 */
	String many2many() default "";
	
	/**
	 * 控制反转,用于多对多,默认为true代表,交由对方表维持多对多关系
	 */
	boolean inverse() default true;
	
	/**
	 * 关联字段是否在父类
	 */
	boolean isSuperClass() default false;
}
