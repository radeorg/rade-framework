package org.dows.dbo.ddl.builder.mysql;

import org.dows.dbo.ddl.actuator.ExecuteTruncate;
import org.dows.dbo.ddl.actuator.ibuilder.TruncateBuilder;
import org.dows.dbo.ddl.actuator.sign.MysqlSign;

public class TruncateMysqlBuilder extends ExecuteTruncate<TruncateMysqlBuilder> implements MysqlSign, TruncateBuilder<TruncateMysqlBuilder> {

    public TruncateMysqlBuilder truncate() {
        ddl.append("TRUNCATE");
        return this.space();
    }

}
