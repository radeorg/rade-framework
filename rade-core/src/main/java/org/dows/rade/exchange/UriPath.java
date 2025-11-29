package org.dows.rade.exchange;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface UriPath {
    String value() default "";
    // 默认值
    String defValue() default "";
}
