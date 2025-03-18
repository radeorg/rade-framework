package org.dows.rade.ddl.api.ddl;

import org.dows.rade.ddl.annotation.NotCamel;
import org.dows.rade.ddl.model.Column;

import java.util.List;

public interface Create {
    String createTable(String tableName, @NotCamel List<Column> columnList);

    //String creteTable(Class<? extends CrudEntity> entityClass);

    //String creteTableAndSnapshot(Class<? extends CrudEntity> entityClass, String sql);

}
