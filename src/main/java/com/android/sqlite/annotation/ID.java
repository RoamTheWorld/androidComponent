package com.android.sqlite.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @author wangyang 该注解用于定义类中映射到数据库中为主键的字段
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ID {
	/**
	 *  是否为自增,默认为false
	 */
	boolean autoIncrement() default false;

}
