package org.dows.rade.core.id;

public interface IdGenerable {
    long next(String key);

    default void init(){
        throw new UnsupportedOperationException("请配置对象的实现方式");
    }
}
