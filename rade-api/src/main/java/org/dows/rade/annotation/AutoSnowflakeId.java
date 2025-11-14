package org.dows.rade.annotation;

import java.lang.annotation.*;

/**
 * 自动填充雪花ID注解
 * 标记在字段上，表示该字段在插入时需要自动填充雪花ID
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface AutoSnowflakeId {
}