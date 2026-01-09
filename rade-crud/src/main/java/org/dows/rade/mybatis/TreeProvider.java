package org.dows.rade.mybatis;

import com.mybatisflex.core.table.TableInfo;
import com.mybatisflex.core.table.TableInfoFactory;
import org.apache.ibatis.jdbc.SQL;
import org.dows.rade.tree.DemoTree;
import org.dows.rade.tree.OrgTreeEntity;
import org.dows.rade.util.PlaceholderUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 根据pid 查询，该树下所有子节点信息
 * WITH RECURSIVE cte AS (
 * SELECT *, 0 as level
 * FROM departments
 * -- WHERE pid IS NULL or pid = 0
 * WHERE pid = 3
 * UNION ALL
 * SELECT d.*, c.level + 1
 * FROM departments d
 * INNER JOIN cte c ON d.pid = c.sequence
 * )
 * SELECT *
 * FROM cte
 * ORDER BY sequence;
 * <p>
 * <p>
 * 根据ID查询，当前树节点及所有子节点信息
 * WITH RECURSIVE cte AS (
 * SELECT *, 0 as level
 * FROM departments
 * -- WHERE pid IS NULL or pid = 0
 * WHERE sequence = 2
 * UNION ALL
 * SELECT d.*, c.level + 1
 * FROM departments d
 * INNER JOIN cte c ON d.pid = c.sequence
 * )
 * SELECT *
 * FROM cte -- where level = 1
 * ORDER BY sequence;
 * <p>
 * 根据pid 查询，该树下所有子节点信息 并根据树的层级过滤
 * WITH RECURSIVE cte AS (
 * SELECT *, 0 as level
 * FROM departments
 * -- WHERE pid IS NULL or pid = 0
 * WHERE pid = 0
 * UNION ALL
 * SELECT d.*, c.level + 1
 * FROM departments d
 * INNER JOIN cte c ON d.pid = c.sequence
 * )
 * SELECT *
 * FROM cte where level = 1
 * ORDER BY sequence;
 */
public class TreeProvider {


    /**
     * WITH RECURSIVE cte AS (
     * SELECT *, 0 as level FROM departments
     * WHERE pid = 3
     * UNION ALL
     * SELECT d.*, c.level + 1
     * FROM departments d
     * INNER JOIN cte c ON d.pid = c.sequence
     * )
     * SELECT *
     * FROM cte
     * ORDER BY sequence;
     * 根据pid 查询，该树下所有子节点信息
     *
     * @param clazz
     * @param pid
     * @return
     */

    /*public String listTreeByPid(Class<?> clazz, Long pid) {
        // 1. 通过实体类获取表结构元数据（表名、字段名、主键等）
        TableInfo tableInfo = TableInfoFactory.ofEntityClass(clazz);
        String tableName = tableInfo.getTableName();

        String columns = String.join(",d.", tableInfo.getAllColumns());

        // 3. 关键配置（可根据业务灵活调整）
        boolean enableDeletedFilter = true; // 是否启用逻辑删除过滤（默认启用）
        boolean enableRootLevelCheck = true; // 是否启用根节点层级校验（默认启用）
        int maxRecursiveLevel = 20; // 最大递归层级（防死循环）
        String sortColumn = "path"; // 排序字段（对齐优化后的 path 排序）

        // 4. 构建递归 SQL

        sql.append("WITH RECURSIVE cte AS (")
                // 递归起始条件：根节点查询（pid = 传入值）
                .append("SELECT ")
                .append(columns) // 所有业务字段
                .append(", d.level - 1 AS level_num ") // 基于表中 level 字段计算层级（根节点 level=1 → level_num=0）
                .append("FROM ")
                .append(tableName).append(" AS d ")
                .append("WHERE d.pid = ").append(pid) // 传入的父节点 ID
                // 逻辑删除过滤（必加）
                .append(enableDeletedFilter ? " AND d.deleted = 0 " : " ")
                // 根节点层级校验（可选，数据规范时启用）
                .append(enableRootLevelCheck ? " AND d.level = 1 " : " ")
                // 递归关联部分
                .append("UNION ALL ")
                .append("SELECT ")
                .append(columns) // 子节点业务字段
                .append(", d.level - 1 AS level_num ") // 复用表中 level 字段，无需累加
                .append("FROM ")
                .append(tableName).append(" AS d ")
                .append("INNER JOIN cte c ON d.pid = c.").append(primaryKey) // 子节点 pid = 父节点主键
                .append(" WHERE ")
                // 子节点逻辑删除过滤
                .append(enableDeletedFilter ? "d.deleted = 0 " : " ")
                // 防递归死循环（限制最大层级）
                .append("AND c.level_num < ").append(maxRecursiveLevel)
                .append(") ")
                // 最终查询：排序优化（path 字段排序，树形结构最规整）
                .append("SELECT ")
                // 显式指定查询字段（避免重复字段，level_num 是递归新增字段）
                .append(allColumns.stream().map(ColumnInfo::getColumn).collect(Collectors.joining(",")))
                .append(", level_num ")
                .append("FROM cte ")
                .append("ORDER BY ").append(sortColumn).append(" ASC"); // 按 path 升序排序

        return sql.toString();
    }*/
    public String listTreeByPid(Class<?> clazz, Long pid) {
        // 1. 通过实体类获取 TableInfo（核心方法）
        TableInfo tableInfo = TableInfoFactory.ofEntityClass(clazz);
        String tableName = tableInfo.getTableName();
        String columns = String.join(",d.", tableInfo.getAllColumns());

        String sqlTemplate = """
                WITH RECURSIVE cte AS (
                    SELECT d.${columns}, d.level - 1 AS level_num
                    FROM ${tableName} AS d WHERE pid = ${pid}
                    UNION ALL
                    SELECT d.${columns}, c.level + 1 AS level_num
                    FROM ${tableName} AS d INNER JOIN cte c ON d.pid = c.${tableName}_id
                )
                SELECT * FROM cte where deleted = 0 ORDER BY ${tableName}_id
                """;
        return PlaceholderUtil.replace(sqlTemplate, Map.of(
                "columns", columns,
                "tableName", tableName,
                "pid", pid
        ));
    }


    /**
     * WITH RECURSIVE cte AS (
     * SELECT *, 0 as level
     * FROM departments
     * WHERE pid = 0
     * UNION ALL
     * SELECT d.*, c.level + 1
     * FROM departments d
     * INNER JOIN cte c ON d.pid = c.sequence
     * )
     * SELECT *
     * FROM cte where level = 1
     * ORDER BY sequence;
     * <p>
     * 根据pid 查询，该树下所有子节点信息 并根据树的层级过滤
     *
     * @param clazz
     * @param pid
     * @return
     */
    public String listTreeByPidAndLevel(Class<?> clazz, Long pid, Integer level) {
        TableInfo tableInfo = TableInfoFactory.ofEntityClass(clazz);
        String tableName = tableInfo.getTableName();
        String columns = String.join(",d.", tableInfo.getAllColumns());
        StringBuilder sql = new StringBuilder();
        String sqlTemplate = """
                WITH RECURSIVE cte AS (
                    SELECT d.${columns}, d.level - 1 AS level_num
                    FROM ${tableName} AS d WHERE pid = ${pid}
                    UNION ALL
                    SELECT d.${columns}, c.level + 1
                    FROM ${tableName} AS d INNER JOIN cte c ON d.pid = c.${tableName}_id
                )
                SELECT * FROM cte where deleted = 0 and level = ${level} ORDER BY ${tableName}_id
                """;
        return PlaceholderUtil.replace(sqlTemplate, Map.of(
                "columns", columns,
                "tableName", tableName,
                "parentId", pid,
                "level", level
        ));
    }


    /**
     * WITH RECURSIVE cte AS (
     * SELECT *, 0 as level
     * FROM departments
     * WHERE sequence = 2
     * UNION ALL
     * SELECT d.*, c.level + 1
     * FROM departments d
     * INNER JOIN cte c ON d.pid = c.sequence
     * )
     * SELECT *
     * FROM cte -- where level = 1
     * ORDER BY sequence;
     * 根据ID查询，当前树节点及所有子节点信息
     *
     * @param clazz
     * @param id
     * @return
     */
    public String listTreeById(Class<?> clazz, Long id) {
        TableInfo tableInfo = TableInfoFactory.ofEntityClass(clazz);
        String tableName = tableInfo.getTableName();
        String columns = String.join(",d.", tableInfo.getAllColumns());
        StringBuilder sql = new StringBuilder();
        sql.append("WITH RECURSIVE cte AS (SELECT ")
                .append("d." + columns)
                .append(",0 as level FROM ")
                .append(tableName)
                .append(" AS d WHERE " + tableName + "_id = ")
                .append(id)
                .append(" UNION ALL SELECT ")
                .append("d." + columns)
                .append(", c.level + 1 FROM ")
                .append(tableName)
                .append(" AS d INNER JOIN cte c ON d.pid = c." + tableName + "_id) ")
                .append("SELECT * FROM cte where deleted = 0 ")
                .append("ORDER BY " + tableName + "_id");
        return sql.toString();
    }

    public String listTreeByAppointColumn1(Class<?> clazz, Map<String, Object> kvMap) {
        TableInfo tableInfo = TableInfoFactory.ofEntityClass(clazz);
        String tableName = tableInfo.getTableName();
        String columns = String.join(",d.", tableInfo.getAllColumns());
        String condition = "";
        if (kvMap != null) {
            for (Map.Entry<String, Object> entry : kvMap.entrySet()) {
                condition += " and ";
                condition += entry.getKey();
                condition += "=";
                condition += entry.getValue();
            }
            condition.replaceFirst(" and ", "");
            condition = "WHERE " + condition;
        }
        StringBuilder sql = new StringBuilder();
        sql.append("WITH RECURSIVE cte AS (SELECT ")
                .append("d." + columns)
                .append(",0 as level FROM ")
                .append(tableName)
                .append(" AS d ")
                .append(condition)
                .append(" UNION ALL SELECT ")
                .append("d." + columns)
                .append(", c.level + 1 FROM ")
                .append(tableName)
                .append(" AS d INNER JOIN cte c ON d.parent_id = c." + tableName + "_id) ")
                .append("SELECT * FROM cte where delete_time != null ")
                .append("ORDER BY " + tableName + "_id");
        return sql.toString();
    }


    public String listTreeByAppointColumn2(Class<?> clazz, Map<String, Object> kvMap) {
        TableInfo tableInfo = TableInfoFactory.ofEntityClass(clazz);
        String tableName = tableInfo.getTableName();

        // 1. 列名统一加别名前缀
        String columns = Arrays.stream(tableInfo.getAllColumns())
                .filter(c -> !"level".equalsIgnoreCase(c))
                .map(c -> "d." + c)
                .collect(Collectors.joining(","));

        // 2. 条件片段收集到 List，避免多余 and
        List<String> conditions = new ArrayList<>();
        if (kvMap != null) {
            kvMap.forEach((k, v) -> conditions.add(k + "=" + v));
        }

        // 3. 组装最终 SQL
        StringBuilder sql = new StringBuilder()
                .append("WITH RECURSIVE cte AS ( ")
                .append("SELECT ").append(columns).append(", 0 AS level ")
                .append("FROM ").append(tableName).append(" AS d ");

        if (!conditions.isEmpty()) {
            sql.append("WHERE ").append(String.join(" AND ", conditions));
        }

        sql.append(" UNION ALL ")
                .append("SELECT ").append(columns).append(", c.level + 1 ")
                .append("FROM ").append(tableName).append(" AS d ")
                .append("JOIN cte c ON d.parent_id = c.").append(tableName).append("_id) ")
                .append("SELECT * FROM cte WHERE delete_time IS NULL ")
                .append("ORDER BY ").append(tableName).append("_id");

        return sql.toString();
    }



    /**
     * @param clazz       实体类
     * @param kvMap       锚点查询条件（只过滤根节点）
     * @param depth    要递归的最大深度；传 null 表示不限制
     */
    public String listTreeByAppointColumn(Class<?> clazz,
                                          Map<String, Object> kvMap,
                                          Integer depth) {

        TableInfo tableInfo = TableInfoFactory.ofEntityClass(clazz);
        String tableName = tableInfo.getTableName();

        /* 1. 去掉实体里可能重名的 level 列 */
        String columns = Arrays.stream(tableInfo.getAllColumns())
                //.filter(c -> !"level".equalsIgnoreCase(c))
                .map(c -> "d." + c)
                .collect(Collectors.joining(","));

        /* 2. 锚点条件（只影响根节点） */
        List<String> anchorConds = new ArrayList<>();
        if (kvMap != null) {
            kvMap.forEach((k, v) -> anchorConds.add(k + "=" + toSqlLiteral(v)));
        }
        /* 3. 组装 SQL */
        StringBuilder sql = new StringBuilder()
                .append("WITH RECURSIVE cte AS ( ")
                .append("SELECT ").append(columns).append(", 0 AS depth ")
                .append("FROM ").append(tableName).append(" AS d ");

        if (!anchorConds.isEmpty()) {
            sql.append("WHERE ").append(String.join(" AND ", anchorConds));
        }

        /* 4. 递归成员：同时限制深度 */
        sql.append(" UNION ALL ")
                .append("SELECT ").append(columns).append(", c.depth + 1 ")
                .append("FROM ").append(tableName).append(" AS d ")
                .append("JOIN cte c ON d.parent_id = c.").append(tableName).append("_id ");

        if (depth != null) {
            sql.append("WHERE c.depth < ").append(depth);   // 关键：控制深度
        }

        sql.append(") ")
                .append("SELECT * FROM cte ")
                .append("WHERE delete_time IS NULL ")
                .append("ORDER BY ").append(tableName).append("_id");

        return sql.toString();
    }



    /**
     * 把值按类型转成 SQL 字面量
     */
    private static String toSqlLiteral(Object v) {
        if (v == null) {
            return "NULL";
        }
        // 字符串、日期、时间等需要加单引号
        if (v instanceof String || v instanceof java.util.Date) {
            // 简单转义单引号，防止 SQL 注入
            String s = v.toString().replace("'", "''");
            return "'" + s + "'";
        }
        // 数字、布尔直接 toString
        return v.toString();
    }

    public static void main(String[] args) {
        TreeProvider treeProvider = new TreeProvider();
        String s = treeProvider.listTreeByAppointColumn(OrgTreeEntity.class, Map.of("name", "技术组"),1);
        System.out.printf(s);
    }

   /* public String selectTree(Class<?> clazz) {
        TableInfo tableInfo = TableInfoFactory.ofEntityClass(clazz);
        String tableName = tableInfo.getTableName();
        String columns = String.join(",d.", tableInfo.getAllColumns());
        return new SQL() {{
            SELECT("sequence,name,parent_id,level");
            FROM(tableInfo.getTableName());
            if (fieldList.size() > 0) {
                // 拼接查询条件
                for (TableFieldInfo tableFieldInfo : fieldList) {
                    if (tableFieldInfo.isSelect()) {
                        WHERE(tableFieldInfo.getColumn() + " = #{" + tableFieldInfo.getField().getName() + "}");
                    }
                }
            }
        }}.toString();
    }*/

    /**
     * WITH RECURSIVE cte AS (
     * SELECT *, 0 as level
     * FROM departments
     * -- WHERE parent_id IS NULL or parent_id = 0
     * WHERE sequence = 2
     * UNION ALL
     * SELECT d.*, c.level + 1
     * FROM departments d
     * INNER JOIN cte c ON d.parent_id = c.sequence
     * )
     * SELECT *
     * FROM cte
     * ORDER BY sequence;
     *
     * @param params
     * @return
     */
    public String getTreeData(Map<String, Object> params) {
        String tableName = (String) params.get("tableName");
        String idColumn = (String) params.get("idColumn");
        String parentColumn = (String) params.get("parentColumn");
        Long rootId = (Long) params.get("rootId");

        // 构建动态 SQL
        SQL sql = new SQL()
                .SELECT("*")
                .FROM(tableName)
                .WHERE(idColumn + " = #{rootId}")
                //.UNION_ALL()
                .SELECT("*")
                .FROM(tableName + " t1")
                .INNER_JOIN(tableName + " t2 ON t1." + idColumn + " = t2." + parentColumn)
                .WHERE("t1." + idColumn + " = #{rootId}");

        return sql.toString();
    }


   /* public static void main(String[] args) {
        TreeProvider treeProvider = new TreeProvider();
        String treeById = treeProvider.listTreeByPidAndLevel(OrgTree.class, 1L, 1);
        System.out.println(treeById);
    }*/

}