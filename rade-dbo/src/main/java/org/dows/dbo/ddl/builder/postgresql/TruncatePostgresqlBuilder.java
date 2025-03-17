package org.dows.dbo.ddl.builder.postgresql;

import org.dows.dbo.ddl.actuator.ExecuteTruncate;
import org.dows.dbo.ddl.actuator.ibuilder.TruncateBuilder;
import org.dows.dbo.ddl.actuator.sign.PostgreSqlSign;

public class TruncatePostgresqlBuilder extends ExecuteTruncate<TruncatePostgresqlBuilder> implements PostgreSqlSign, TruncateBuilder<TruncatePostgresqlBuilder> {
    @Override
    public TruncatePostgresqlBuilder truncate() {
        return null;
    }
}
