package com.android.sqlite.simple.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @author wangyang
 * @2013-10-9 下午4:48:43
 * 用于反射解析类的字段是否是List或者Date类型,如果是List则需要给包名赋值
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface BeanType {
	boolean isList() default false;
	String packageName() default "";
	boolean isDate() default false; 
}
