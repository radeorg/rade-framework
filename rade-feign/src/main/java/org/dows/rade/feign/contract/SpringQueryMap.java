package org.dows.rade.feign.contract;

import feign.QueryMap;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface SpringQueryMap {

    /**
     * @return alias for {@link #encoded()}.
     * @see QueryMap#encoded()
     */
    @AliasFor("encoded")
    boolean value() default false;

    /**
     * @return Specifies whether parameter names and values are already encoded.
     * @see QueryMap#encoded()
     */
    @AliasFor("value")
    boolean encoded() default false;

}