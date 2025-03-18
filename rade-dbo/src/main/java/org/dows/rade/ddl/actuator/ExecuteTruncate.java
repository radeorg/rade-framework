package org.dows.rade.ddl.actuator;

import org.dows.rade.ddl.actuator.ibuilder.TruncateBuilder;
import org.dows.rade.ddl.api.ddl.Truncate;
import org.dows.rade.ddl.builder.DdlBuilder;

public abstract class ExecuteTruncate<T extends TruncateBuilder<T>> extends DdlBuilder<T> implements Truncate {

    public String truncateTable(String table) {
        return current.truncate().tableName(table).end();
    }

}
