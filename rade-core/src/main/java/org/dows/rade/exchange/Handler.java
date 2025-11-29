package org.dows.rade.exchange;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Handler {
    // 多个处理器
    Class<?>[] value();
}
