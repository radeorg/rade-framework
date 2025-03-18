package org.dows.rade.ddl.builder.mysql;

import org.dows.rade.ddl.actuator.ExecuteTruncate;
import org.dows.rade.ddl.actuator.ibuilder.TruncateBuilder;
import org.dows.rade.ddl.actuator.sign.MysqlSign;

public class TruncateMysqlBuilder extends ExecuteTruncate<TruncateMysqlBuilder> implements MysqlSign, TruncateBuilder<TruncateMysqlBuilder> {

    public TruncateMysqlBuilder truncate() {
        ddl.append("TRUNCATE");
        return this.space();
    }

}
