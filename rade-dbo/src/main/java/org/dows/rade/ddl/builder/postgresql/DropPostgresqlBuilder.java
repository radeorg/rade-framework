package org.dows.rade.ddl.builder.postgresql;

import org.dows.rade.ddl.actuator.ExecuteDrop;
import org.dows.rade.ddl.actuator.ibuilder.DropBuilder;
import org.dows.rade.ddl.actuator.sign.PostgreSqlSign;

public class DropPostgresqlBuilder extends ExecuteDrop<DropPostgresqlBuilder> implements PostgreSqlSign, DropBuilder<DropPostgresqlBuilder> {
    @Override
    public DropPostgresqlBuilder drop() {
        return null;
    }

    @Override
    public DropPostgresqlBuilder dataBase(String dataBase) {
        return null;
    }
}
