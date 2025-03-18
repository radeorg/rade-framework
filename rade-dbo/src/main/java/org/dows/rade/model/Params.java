package org.dows.rade.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * @description: </br>
 * @author: lait.zhang@gmail.com
 * @date: 12/30/2024 2:22 PM
 * @history: </br>
 * <author>      <time>      <version>    <desc>
 * 修改人姓名      修改时间        版本号       描述
 */
@Schema(name = "参数 对象", title = "Param Meta Object")
@Data
public class Params {

    private Column column;
    @Schema(name = "condition", title = "条件[>,<,=,>=,<=,like,rlike,llike,in,between,not in,not between,is null,is not null,is true,is false,is not true,is not false]")
    private String condition;
    @Schema(name = "value", title = "字段值")
    private Object value;
    @Schema(name = "logic", title = "逻辑[and,or],可不传,默认and")
    private String logic;


    public Object getValue() {
        if (condition.equals("in")) {
            if (value instanceof Collection<?> collection) {
                String joinedString;
                if (column.getType().equals("string")) {
                    joinedString = collection.stream().map(s -> "'" + s + "'").collect(Collectors.joining(","));
                } else {
                    joinedString = collection.stream().map(s -> s + "").collect(Collectors.joining(","));
                }
                return "(" + joinedString + ")";
            }
        }
        if (column.getType().equals("string")) {
            if (condition.equals("like")) {
                return "'%" + value + "%'";
            }
            if (condition.equals("rlike")) {
                return "'" + value + "%'";
            }
            if (condition.equals("llike")) {
                return "'%" + value + "'";
            }
        }
        if (condition.equals("between")) {
            String[] split = value.toString().split(",");
            if (column.getType().equals("string")) {
                return "'" + split[0] + "' AND '" + split[1] + "'";
            }
            return split[0] + " AND " + split[1];
        }
        if (column.getType().equals("string") && value != null) {
            return "'" + value + "'";
        } else {
            return value;
        }
    }


}
