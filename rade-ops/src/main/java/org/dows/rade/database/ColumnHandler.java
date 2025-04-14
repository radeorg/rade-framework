package org.dows.rade.database;

import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class ColumnHandler {

    // 数据库连接信息
    private static final String DB_URL = "jdbc:mysql://115.159.24.40:13306/rade?useUnicode=true&characterEncoding=UTF-8&serverTimezone=GMT%2b8";
    private static final String USER = "root";
    private static final String PASS = "radeorg123!";

    public static void main(String[] args) {

    }



    public static void addColumns(String schemaName, Map<String,String> columnsToAdd) {
        Connection conn = null;
        Statement stmt = null;
        try {
            // 注册JDBC驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
            // 打开连接
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            // 获取所有表名
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SHOW TABLES");
            StringBuilder alterTableDdl = new StringBuilder();
            // 遍历每张表并添加新字段
            while (rs.next()) {
                String tableName = rs.getString(1);
                List<String> columns = getColumns(conn, "rade", tableName);
                columnsToAdd.forEach((columnName, columnType) -> {
                    if (!columns.contains(columnName)) {
                        alterTableDdl.append("ALTER TABLE `%s` ADD COLUMN `%s` %s;".formatted(tableName, columnName, columnType) + "\n");
                    }
                });
            }
            String ddlSql = alterTableDdl.toString();
            stmt.executeUpdate(ddlSql);
            // 关闭资源
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            log.error("Exception occurred", e);
        } finally {
            // 关闭资源
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException se) {
                log.warn("Failed to close statement or connection", se);
            }
        }
    }
    /**
     * 检查指定表中是否存在指定字段
     *
     * @param conn       数据库连接
     * @param schemaName 库名
     * @param tableName  表名
     * @return 字段是否存在
     * @throws SQLException SQL异常
     */
    private static List<String> getColumns(Connection conn, String schemaName, String tableName) throws SQLException {
        String sql = String.format("SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = '%s' AND TABLE_NAME = '%s'", schemaName, tableName);
        List<String> columns = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String columnName = rs.getString(1);
                columns.add(columnName);
            }
        }
        return columns;
    }

    /**
     * 添加字段到指定表
     *
     * @param conn       数据库连接
     * @param tableName  表名
     * @param columnName 字段名
     * @param columnType 字段类型
     * @throws SQLException SQL异常
     */
    private static void addColumn(Connection conn, String tableName, String columnName, String columnType) throws SQLException {
        String sql = "ALTER TABLE %s ADD COLUMN %s %s".formatted(tableName, columnName, columnType);
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            log.info("Added column {} to table: {}", columnName, tableName);
        }
    }


    /**
     * 删除指定表中的字段
     *
     * @param conn        数据库连接
     * @param tableName   表名
     * @param columnName  字段名
     * @throws SQLException SQL异常
     */
    private static void dropColumn(Connection conn, String tableName, String columnName) throws SQLException {
        String sql = String.format("ALTER TABLE %s DROP COLUMN %s", tableName, columnName);
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            log.info("Dropped column {} from table: {}", columnName, tableName);
        }
    }
}
