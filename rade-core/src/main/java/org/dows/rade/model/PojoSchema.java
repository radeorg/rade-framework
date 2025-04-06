package org.dows.rade.model;

import lombok.Data;

/**
 * @description: </br>
 * @author: lait.zhang@gmail.com
 * @date: 9/30/2024 9:58 AM
 * @history: </br>
 * <author>      <time>      <version>    <desc>
 * 修改人姓名      修改时间        版本号       描述
 */
@Data
public class PojoSchema {
    // 字段索引
    private int index;
    // 字段名称
    private String name;
    // 集合类型[array,list, set,map]
    private String collect;
    // 字段类型[内置8大类型，自定义数据类型]
    private String type;
    // 字段值
    private Object value;

}

