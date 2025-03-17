package org.dows.dbo.dsl.node;

import org.dows.dbo.dsl.engine.Context;

import java.util.Set;

public class IfSqlNode implements SqlNode {

    String test;
    SqlNode contents;

    public IfSqlNode(String test, SqlNode contents) {
        this.test = test;
        this.contents = contents;
    }

    @Override
    public void apply(Context context) {
        Boolean value = context.getOgnlBooleanValue(test);
        if (value) {
            //标签类SqlNode先拼接空格，和前面的内容隔开
            context.appendSql(" ");
            contents.apply(context);
        }
    }

    @Override
    public void applyParameter(Set<String> set) {
        contents.applyParameter(set);
    }
}
