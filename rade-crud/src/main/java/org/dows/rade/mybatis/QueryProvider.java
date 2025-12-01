package org.dows.rade.mybatis;

import com.mybatisflex.core.table.TableInfo;
import com.mybatisflex.core.table.TableInfoFactory;

import java.util.Map;

public class QueryProvider {

    public String queryByAppointColumn(Class<?> clazz, Map<String, String> kvMap) {
        TableInfo tableInfo = TableInfoFactory.ofEntityClass(clazz);
        String tableName = tableInfo.getTableName();
        String columns = String.join(",", tableInfo.getAllColumns());
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ")
                .append(columns)
                .append(" FROM ")
                .append(tableName)
                .append(" where deleted = 0");
        if (kvMap != null) {
            for (Map.Entry<String, String> entry : kvMap.entrySet()) {
                sql.append(" and ");
                sql.append(entry.getKey());
                sql.append(" = ");
                sql.append(entry.getValue());
            }
        }
        return sql.toString();
    }
}