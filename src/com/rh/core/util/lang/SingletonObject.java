package com.rh.core.util.lang;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/***
 * 
 * 表示类型为单例的对象
 * @author yangjy
 * 
 */
@Documented
@Target({ TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface SingletonObject {

}
