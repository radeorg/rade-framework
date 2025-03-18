package org.dows.rade.ddl.api.ddl;

public interface Drop {
    String dropTable(String table);

    String dropDataBase(String database);
}
