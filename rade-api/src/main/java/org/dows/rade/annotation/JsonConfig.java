package org.dows.rade.annotation;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JsonConfig {

    String value() default "";

    Class<?> clazz() default Object.class;
}
