package org.dows.rade.ddl.actuator;

import org.dows.rade.ddl.actuator.ibuilder.DropBuilder;
import org.dows.rade.ddl.api.ddl.Drop;
import org.dows.rade.ddl.builder.DdlBuilder;

public abstract class ExecuteDrop<T extends DropBuilder<T>> extends DdlBuilder<T> implements Drop {

    public String dropTable(String table) {
        return current.drop().tableName(table).end();

    }

    public String dropDataBase(String database) {
        return current.drop().dataBase(database).end();
    }

}
