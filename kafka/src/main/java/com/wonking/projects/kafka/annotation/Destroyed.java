package com.wonking.projects.kafka.annotation;

import java.lang.annotation.*;

/**
 * Created by kewangk on 2018/1/19.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Destroyed {
    String cb() default "";
}
