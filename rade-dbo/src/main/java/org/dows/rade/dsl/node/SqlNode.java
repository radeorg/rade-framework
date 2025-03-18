package org.dows.rade.dsl.node;

import org.dows.rade.dsl.engine.Context;

import java.util.Set;


public interface SqlNode {

    void apply(Context context);

    void applyParameter(Set<String> set);

}
