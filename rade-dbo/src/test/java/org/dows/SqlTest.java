package org.dows;

import org.dows.rade.dsl.engine.DynamicSqlEngine;

import java.util.HashMap;
import java.util.Map;

/**
 * @description: </br>
 * @author: lait.zhang@gmail.com
 * @date: 12/30/2024 11:14 AM
 * @history: </br>
 * <author>      <time>      <version>    <desc>
 * 修改人姓名      修改时间        版本号       描述
 */
public class SqlTest {


    public static void main(String[] args) {
        String slq = """
                ddd:
                    select * from aa where 1 = 1
                    if $dd!=null
                       and dd = $dd
                    if 
                
                """;
        DynamicSqlEngine engine = new DynamicSqlEngine();
        String sql = ("<root>select <if test='minId != null'>id > ${minId} #{minId} <if test='maxId != null'> and id &lt; ${maxId} #{maxId}</if> </if></root>");
        Map<String, Object> map = new HashMap<>();
        map.put("minId", 100);
        map.put("maxId", 500);
        engine.parse(sql, map);
    }
}

