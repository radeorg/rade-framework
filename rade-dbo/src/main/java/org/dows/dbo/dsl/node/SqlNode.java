package org.dows.dbo.dsl.node;

import org.dows.dbo.dsl.engine.Context;

import java.util.Set;


public interface SqlNode {

    void apply(Context context);

    void applyParameter(Set<String> set);

}
