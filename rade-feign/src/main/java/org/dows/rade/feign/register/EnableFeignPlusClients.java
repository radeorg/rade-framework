package org.dows.rade.feign.register;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Function:
 * <p>
 * <p>
 * Date: 2020/7/25 02:10
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(FeignPlusClientsRegistrar.class)
public @interface EnableFeignPlusClients {

    String[] value() default {};

    /**
     * Base packages to scan for annotated components.
     *
     * @return base packages
     */
    String[] basePackages() default {};
}
