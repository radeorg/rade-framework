package org.dows.rade.exchange;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface UriBody {
    String value() default "";
    String defValue() default "";
}
