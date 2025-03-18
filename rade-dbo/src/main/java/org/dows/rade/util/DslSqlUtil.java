package org.dows.rade.util;

import cn.hutool.core.util.StrUtil;
import org.dows.rade.model.Params;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DslSqlUtil {




    public static String getLimitSql(Map<String, Object> params) {
        // 使用更为安全的类型转换
        Long pageNo = (params.get("pageNo") != null) ? Long.valueOf(params.get("pageNo").toString()) : null;
        Long pageSize = (params.get("pageSize") != null) ? Long.valueOf(params.get("pageSize").toString()) : null;

        // 检查pageNo和pageSize是否为正数
        if (pageNo == null || pageNo <= 0 || pageSize == null || pageSize <= 0) {
            return null;
        }

        // 计算偏移量
        Long offset = (pageNo - 1) * pageSize;

        // 使用String.format生成LIMIT子句
        return String.format(" %d,%d", offset, pageSize);
    }

    public static String getOrderBySql(List<Params> params) {
        // 用于存储ORDER BY子句的每一项
        List<String> orderByItems = new ArrayList<>();

        for (Params param : params) {
            if (param != null && StrUtil.isNotBlank(param.getColumn().getOrderBy())) {
                String column = param.getColumn().getName();
                String order = param.getColumn().getOrderBy();
                // 将字段名和排序方向添加到列表中
                orderByItems.add(column + " " + order);
            }
        }

        // 将所有排序项组合成一个字符串，并用逗号分隔
        String orderByClause = String.join(", ", orderByItems);

        // 如果没有排序项，返回空字符串或适当的值
        if (orderByItems.isEmpty()) {
            return "";
        }

        // 返回完整的ORDER BY SQL子句
        return orderByClause;
    }


    public static String getGroupBySql(List<Params> params) {

        List<String> groupByItems = new ArrayList<>();

        for (Params param : params) {
            if (param.getColumn().getGroupBy()) {
                String column = param.getColumn().getName();
                // 将字段名添加到列表中
                groupByItems.add(column);
            }
        }

        String groupByClause = String.join(", ", groupByItems);

        if (groupByItems.isEmpty()) {
            return "";
        }

        return groupByClause;

    }

    public static String getHavingSql(List<Params> params) {
        // 如果没有参数，返回空字符串
        if (params == null || params.isEmpty()) {
            return "";
        }

        List<String> havingConditions = new ArrayList<>();

        for (Params param : params) {
            // 检查聚合函数和条件是否不为空
            if (StrUtil.isNotBlank(param.getColumn().getHaving())) {
//                String havingCondition = param.getColumn().getAlias() + "("
//                        + param.getColumn() + ") "
//                        + param.getCondition() + " "
//                        + param.getValue();
                String havingCondition = param.getColumn().getAlias()+param.getColumn().getHaving();
                havingConditions.add(havingCondition);
            }
        }

        // 将所有条件组合成一个字符串，用 'AND' 连接
        String havingClause = String.join(" AND ", havingConditions);

        // 如果没有条件，返回空字符串
        if (havingConditions.isEmpty()) {
            return "";
        }

        // 将条件放在 'HAVING' 关键字后面，并返回
        return  havingClause;
    }

    /*public static void buildApiDsl(SelectDsl dslInfo, Map<String, Object> params, List<Params> realParamList) {
        dslInfo.setLimit(getLimitSql(params));
        dslInfo.setGroupBy(getGroupBySql(realParamList));
        dslInfo.setOrderBy(getOrderBySql(realParamList));
        dslInfo.setHaving(getHavingSql(realParamList));
    }*/
}
