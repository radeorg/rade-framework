package org.dows.rade.ddl.builder.postgresql;

import org.dows.rade.ddl.actuator.ExecuteTruncate;
import org.dows.rade.ddl.actuator.ibuilder.TruncateBuilder;
import org.dows.rade.ddl.actuator.sign.PostgreSqlSign;

public class TruncatePostgresqlBuilder extends ExecuteTruncate<TruncatePostgresqlBuilder> implements PostgreSqlSign, TruncateBuilder<TruncatePostgresqlBuilder> {
    @Override
    public TruncatePostgresqlBuilder truncate() {
        return null;
    }
}
