package org.dows.rade.exchange;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface UriParam {
    String value() default "";
}
