package org.dows.rade.feign.register;

import java.lang.annotation.*;

/**
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FeignPlusClient {

    String name() default "";

    /**
     * @return Target url
     */
    String url() default "";

    /**
     * @return port
     */
    String port() default "80";
}
