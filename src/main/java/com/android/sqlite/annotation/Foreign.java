package com.android.sqlite.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Foreign {
	
	/**
	 * 数据库字段名,若value为空则取属性名为数据库列名
	 */
    String column() default "";
    
    /**
	 * 外键表关联字段
	 */
    String foreign();
    
    /**
	 * 关联字段是否在父类
	 */
    boolean isSuperClass() default false;
}
