//package org.dows.rade.crud;
//
//import com.mybatisflex.core.dialect.OperateType;
//
//public class AppIdSqlProcessor implements ISqlProcessor {
//    @Override
//    public String process(String sql, OperateType operateType) {
//        // 只处理 SELECT 查询
//        if (operateType == OperateType.SELECT) {
//            // 检查 SQL 是否已经包含 app_id 条件
//            if (!sql.toLowerCase().contains("where app_id")) {
//                if (sql.toLowerCase().contains("where")) {
//                    // 如果已有 WHERE 条件，则追加 AND app_id = 1
//                    sql = sql.replaceAll("(?i)where", "WHERE app_id = 1 AND");
//                } else {
//                    // 如果没有 WHERE 条件，则添加 WHERE app_id = 1
//                    sql = sql + " WHERE app_id = 1";
//                }
//            }
//        }
//        return sql;
//    }
//}