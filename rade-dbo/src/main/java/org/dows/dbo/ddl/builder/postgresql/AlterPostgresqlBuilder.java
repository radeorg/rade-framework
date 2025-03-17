package org.dows.dbo.ddl.builder.postgresql;

import org.dows.dbo.ddl.actuator.ExecuteAlter;
import org.dows.dbo.ddl.actuator.ibuilder.AlterBuilder;
import org.dows.dbo.ddl.actuator.sign.PostgreSqlSign;

public class AlterPostgresqlBuilder extends ExecuteAlter<AlterPostgresqlBuilder> implements PostgreSqlSign, AlterBuilder<AlterPostgresqlBuilder> {
    @Override
    public AlterPostgresqlBuilder alter() {
        return null;
    }

    @Override
    public AlterPostgresqlBuilder renameTo(String nweTableName) {
        return null;
    }

    @Override
    public AlterPostgresqlBuilder change() {
        return null;
    }

    @Override
    public AlterPostgresqlBuilder modify() {
        return null;
    }

    @Override
    public AlterPostgresqlBuilder drop() {
        return null;
    }

    @Override
    public AlterPostgresqlBuilder add() {
        return null;
    }

    @Override
    public AlterPostgresqlBuilder index() {
        return null;
    }

    @Override
    public AlterPostgresqlBuilder primaryKey() {
        return null;
    }

    @Override
    public AlterPostgresqlBuilder autokey(String key) {
        return null;
    }

    @Override
    public AlterPostgresqlBuilder notNull() {
        return null;
    }


}
