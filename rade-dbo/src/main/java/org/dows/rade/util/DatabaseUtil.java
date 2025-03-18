package org.dows.rade.util;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/**
 * @description: </br>
 * @author: lait.zhang@gmail.com
 * @date: 12/27/2024 7:12 PM
 * @history: </br>
 * <author>      <time>      <version>    <desc>
 * 修改人姓名      修改时间        版本号       描述
 */
@Slf4j
public class DatabaseUtil {

    /**
     * 动态快照数据库
     * @param ddl
     * @param dml
     * @throws SQLException
     */
    public static void snapshotDatabase(Connection connection,String ddl, String dml) throws SQLException {
        // 执行ddl
        connection.createStatement().execute(ddl);
        // 执行dml
        connection.createStatement().execute(dml);
        //connection.close();
    }


    /**
     * 在当前数据库源下创建应用对应的数据库
     * @param connection
     * @param appId
     * @throws SQLException
     */
    public static void createDatabase(Connection connection, String appId) throws SQLException {
        Statement statement = connection.createStatement();
        // 创建数据库的SQL语句
        String createDatabaseSQL = String.format("CREATE DATABASE IF NOT EXISTS %s", appId);
        statement.executeUpdate(createDatabaseSQL);
        //connection.close();
        log.info("数据库 :{} 创建成功", appId);
    }

    public static List<String> getTableNames(Connection connection) throws SQLException {
        List<String> tableNames = new ArrayList<>();
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SHOW TABLES")) {

            while (resultSet.next()) {
                tableNames.add(resultSet.getString(1));
            }
        }
        return tableNames;
    }

    public static boolean doesTableHaveAppIdColumn(Connection connection, String tableName) throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SHOW COLUMNS FROM " + tableName)) {

            while (resultSet.next()) {
                if ("app_id".equals(resultSet.getString("Field"))) {
                    return true;
                }
            }
        }
        return false;
    }

    public static int countAppIdRecords(Connection connection, String tableName, String appIdToSearch) throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) AS count FROM " + tableName + " WHERE app_id = '" + appIdToSearch + "'")) {

            if (resultSet.next()) {
                return resultSet.getInt("count");
            }
        }
        return 0;
    }

    public static String generateInsertStatement(Connection connection, String tableName, String appIdToSearch, String newAppId) throws SQLException {
        // 查询表结构获取列名
        List<TableMatedata> columnNames = DatabaseUtil.getColumnNames(connection, tableName);

        StringBuilder insertStatement = new StringBuilder("INSERT INTO " + tableName + " (");
        for (int i = 0; i < columnNames.size(); i++) {
            if (i > 0) {
                insertStatement.append(", ");
            }
            insertStatement.append("`").append(columnNames.get(i).column_name()).append("`");
        }
        insertStatement.append(") VALUES ");

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(String.format("SELECT * FROM `%s` WHERE app_id = '%s'" , tableName, appIdToSearch))) {
            boolean firstRow = true;
            while (resultSet.next()) {
                if (!firstRow) {
                    insertStatement.append(", ");
                }
                insertStatement.append("(");
                DatabaseUtil.fillValue(newAppId, columnNames, insertStatement, resultSet);
                insertStatement.append(")");
                firstRow = false;
            }
            if(firstRow){
                return "";
            }
        }

        return insertStatement.toString();
    }

    public static void fillValue(String newAppId, List<TableMatedata> columnNames,
                                  StringBuilder insertStatement, ResultSet resultSet) throws SQLException {
        for (int i = 0, n = columnNames.size(); i < n; i++) {
            if (i > 0) {
                insertStatement.append(", ");
            }
            TableMatedata tableMatedata = columnNames.get(i);
            String columnName = tableMatedata.column_name();
            String dataType = tableMatedata.data_type();

            Object value = resultSet.getObject(columnName);
            if (dataType.equalsIgnoreCase("varchar") ||
                    dataType.equalsIgnoreCase("text") ||
                    dataType.equalsIgnoreCase("char") ||
                    dataType.equalsIgnoreCase("longtext") ||
                    dataType.equalsIgnoreCase("mediumtext") ||
                    dataType.equalsIgnoreCase("tinytext") ||
                    dataType.equalsIgnoreCase("datetime") ||
                    dataType.equalsIgnoreCase("date")) {

                if (columnName.equalsIgnoreCase("app_id")) {
                    insertStatement.append("'").append(newAppId).append("'");
                } else {
                    if (value == null) {
                        insertStatement.append((String) null);
                    } else {
                        insertStatement.append("'").append(DatabaseUtil.escapeSpecialChars(value.toString())).append("'");
                    }
                }

            } else if (dataType.equalsIgnoreCase("int") ||
                    dataType.equalsIgnoreCase("bigint") ||
                    dataType.equalsIgnoreCase("tinyint")||
                    dataType.equalsIgnoreCase("smallint") ||
                    dataType.equalsIgnoreCase("mediumint") ||
                    dataType.equalsIgnoreCase("decimal") ||
                    dataType.equalsIgnoreCase("float") ||
                    dataType.equalsIgnoreCase("double")) {

                if (value == null) {
                    // or zero
                    insertStatement.append((String) null);
                } else {
                    insertStatement.append(value);
                }
            }
        }
    }


    public static String escapeSpecialChars(String input) {
        if (input == null) {
            return null;
        }

        StringBuilder escaped = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c == '\'') {
                escaped.append("''");
            } else if (c == '"') {
                escaped.append("\\\"");
            } else if (c == '\\') {
                escaped.append("\\\\");
            } else if (c == '%') {
                escaped.append("\\%");
            } else if (c == '_') {
                escaped.append("\\_");
            } else {
                escaped.append(c);
            }
        }

        return escaped.toString();
    }


    public static String generateInsertStatementForAll(Connection connection, String tableName, String newAppId) throws SQLException {
        // 查询表结构获取列名
        List<TableMatedata> columnNames = DatabaseUtil.getColumnNames(connection, tableName);

        Map<String, Integer> IDS = new HashMap<>();
        StringBuilder insertStatement = new StringBuilder("INSERT INTO " + tableName + " (");
        for (int i = 0; i < columnNames.size(); i++) {
            if (i > 0) {
                insertStatement.append(", ");
            }
            String columnName = columnNames.get(i).column_name();
            // 定位是第几个
            if (columnName.equals("app_id")) {
                IDS.put("app_id", i);
            }
            insertStatement.append("`").append(columnName).append("`");
        }
        insertStatement.append(") VALUES ");

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(String.format("SELECT * FROM %s", tableName))) {

            boolean firstRow = true;
            while (resultSet.next()) {
                if (!firstRow) {
                    insertStatement.append(", ");
                }
                insertStatement.append("(");
                DatabaseUtil.fillValue(newAppId, columnNames, insertStatement, resultSet);
                insertStatement.append(")");
                firstRow = false;
            }
            if(firstRow){
                return "";
            }
        }
        return insertStatement.toString();
    }

    public static List<TableMatedata> getColumnNames(Connection connection, String tableName) throws SQLException {
        List<TableMatedata> columnNames = new ArrayList<>();
        String dbname = connection.getCatalog();
        String sql = String.format("""
                SELECT
                    column_name,data_type,character_maximum_length,is_nullable,column_default,column_comment
                FROM
                    information_schema.columns
                WHERE
                    table_schema = '%s'
                    AND table_name = '%s';
                """, dbname, tableName);

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)
                /*ResultSet resultSet = statement.executeQuery("SHOW COLUMNS FROM " + tableName)*/) {
            while (resultSet.next()) {
                TableMatedata tableMatedata = new TableMatedata(resultSet.getString("column_name"),
                        resultSet.getString("data_type"),
                        resultSet.getString("character_maximum_length"),
                        resultSet.getString("is_nullable"),
                        resultSet.getString("column_default"),
                        resultSet.getString("column_comment")
                );
                /*tableMatedata.column_name(resultSet.getString("column_name"));
                tableMatedata.setData_type(resultSet.getString("data_type"));
                tableMatedata.setCharacter_maximum_length(resultSet.getString("character_maximum_length"));
                tableMatedata.setIs_nullable(resultSet.getString("is_nullable"));
                tableMatedata.setColumn_default(resultSet.getString("column_default"));
                tableMatedata.setColumn_comment(resultSet.getString("column_comment"));*/
                //columnNames.add(resultSet.getString("Field"));
                columnNames.add(tableMatedata);
            }
        }
        return columnNames;
    }

    public static String generateDdl(Connection connection, String fromAppId, String toAppId, List<String> tableNames, Map<String, Set<String>> indexMap) throws SQLException {
        StringBuilder ddl = new StringBuilder("create database if not exists " + toAppId + ";\nuse " + toAppId + ";\n");
        for (String tableName : tableNames) {
            // 获取表的创建语句
            String createTableStatement = DatabaseUtil.getCreateTableStatement(connection, fromAppId, tableName,indexMap);
            ddl.append(createTableStatement);
        }
        return ddl.toString();
    }

    public static String getCreateTableStatement(Connection connection, String fromAppId, String tableName, Map<String, Set<String>> indexMap) throws SQLException {
        StringBuilder createTableStmt = new StringBuilder("DROP TABLE IF EXISTS `" + tableName + "`;\nCREATE TABLE ");
        createTableStmt.append(tableName).append(" (");
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SHOW COLUMNS FROM " + tableName)) {

            boolean firstColumn = true;
            while (resultSet.next()) {
                if (!firstColumn) {
                    createTableStmt.append(", ");
                }
                String columnName = resultSet.getString("Field");
                String dataType = resultSet.getString("Type");
                String nullable = resultSet.getString("Null");
                String key = resultSet.getString("Key");
                String defaultValue = resultSet.getString("Default");
                String extra = resultSet.getString("Extra");

                createTableStmt.append("`").append(columnName).append("` ");
                createTableStmt.append(dataType);
                if ("NO".equals(nullable)) {
                    createTableStmt.append(" NOT NULL");
                }
                if ("PRI".equals(key)) {
                    createTableStmt.append(" PRIMARY KEY");
                }
                if (defaultValue != null) {
                    if (columnName.equals("dt")) {
                        createTableStmt.append(" DEFAULT ").append("current_timestamp()");
                    } else {
                        createTableStmt.append(" DEFAULT ").append(defaultValue);
                    }
                    if (columnName.equals("deleted")) {
                        createTableStmt.append(" DEFAULT ").append("0");
                    }
                }

                if ("auto_increment".equals(extra)) {
                    createTableStmt.append(" AUTO_INCREMENT");
                }
                firstColumn = false;
            }
            // 追加索引
            Set<String> indexes = indexMap.get(fromAppId + "." + tableName);
            if (indexes != null) {
                List<String> list = indexes.stream().toList();
                createTableStmt.append(" ,");
                for (int i = 0; i < list.size(); i++) {
                    createTableStmt.append(list.get(i));
                    if (i < list.size() - 1) {
                        createTableStmt.append(",");
                    }
                }
            }
            createTableStmt.append(String.format(") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='%s'", " "))
                    .append(";\n");
        }
        return createTableStmt.toString();
    }


    public static void fetchIndex(Connection connection, String database, String fromAppId,Map<String,Set<String>> indexMap) {
        String sql = String.format("""
                SELECT
                    s.TABLE_NAME,
                    s.index_name,
                    s.INDEX_COMMENT,
                    s.NON_UNIQUE,
                    s.index_type,
                    CONCAT(
                        -- 根据是否唯一和索引类型来构建索引字符串
                        CASE
                            WHEN s.INDEX_NAME = 'PRIMARY' THEN 'PRIMARY KEY'
                            WHEN s.NON_UNIQUE = 0 AND s.index_type = 'BTREE' THEN 'UNIQUE KEY'
                            WHEN s.NON_UNIQUE = 1 AND s.index_type = 'BTREE' THEN 'KEY'
                            WHEN s.NON_UNIQUE = 1 AND s.index_type = 'FULLTEXT' THEN 'FULLTEXT KEY'
                            ELSE 'OTHER'
                        END,
                        ' `', s.index_name, '` (', GROUP_CONCAT('`', s.column_name SEPARATOR '`, '),'`', ')',
                        -- 根据索引类型添加索引方法
                        CASE s.index_type
                             WHEN 'BTREE' THEN ' USING BTREE'
                             WHEN 'HASH' THEN ' USING HASH'
                            -- WHEN 'FULLTEXT' THEN ' USING FULLTEXT'
                            ELSE ''
                        END
                    ) AS index_definition
                FROM
                    information_schema.statistics as s
                WHERE
                    s.table_schema = '%s'
                    AND s.index_name!= 'PRIMARY'
                GROUP BY
                    s.table_name,
                    s.index_name;
                
                """, database);

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            Set<String> indexString = new HashSet<>();
            while (resultSet.next()) {

                String tableName = resultSet.getString("TABLE_NAME");
                /*String indexName = resultSet.getString("index_name");
                String indexComment = resultSet.getString("index_comment");
                String indexType = resultSet.getString("index_type");
                String nonUnique = resultSet.getString("NON_UNIQUE");*/
                String indexDefinition = resultSet.getString("index_definition");
                /*if (nonUnique.equals("0")) {
                    indexDefinition = indexDefinition.replace("KEY", "UNIQUE KEY");
                }*/
                Set<String> indexes = indexMap.computeIfAbsent(fromAppId + "." + tableName, k -> new HashSet<>());
                //Set<String> indexes = INDEX_MAP.computeIfAbsent(fromAppId + "." + tableName, k -> new HashSet<>());
                indexes.add(indexDefinition);
            }
        } catch (Exception e) {
            log.error("获取索引失败", e);
        }
    }

    public record TableMatedata(String column_name, String data_type, String character_maximum_length,
                                String is_nullable, String column_default, String column_comment) {
        //private String column_name, data_type, character_maximum_length, is_nullable, column_default, column_comment;
    }
}

