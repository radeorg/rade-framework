package org.dows.rade.mybatisflex;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Ordering {
    String value() default "";
    String orderBy() default "DESC";
}
