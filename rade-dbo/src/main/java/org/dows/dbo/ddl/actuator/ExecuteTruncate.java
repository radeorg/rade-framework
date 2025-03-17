package org.dows.dbo.ddl.actuator;

import org.dows.dbo.ddl.actuator.ibuilder.TruncateBuilder;
import org.dows.dbo.ddl.api.ddl.Truncate;
import org.dows.dbo.ddl.builder.DdlBuilder;

public abstract class ExecuteTruncate<T extends TruncateBuilder<T>> extends DdlBuilder<T> implements Truncate {

    public String truncateTable(String table) {
        return current.truncate().tableName(table).end();
    }

}
