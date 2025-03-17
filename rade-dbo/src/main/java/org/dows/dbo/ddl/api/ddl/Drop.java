package org.dows.dbo.ddl.api.ddl;

public interface Drop {
    String dropTable(String table);

    String dropDataBase(String database);
}
