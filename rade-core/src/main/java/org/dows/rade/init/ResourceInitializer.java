package org.dows.rade.init;

import java.util.List;

public interface ResourceInitializer<T extends InitializableResource> {

    default void init(List<T> initializableResources) {
        throw new UnsupportedOperationException("not implement class!");
    }
}
