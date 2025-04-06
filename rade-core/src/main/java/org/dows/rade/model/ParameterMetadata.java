package org.dows.rade.model;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * @description: </br>
 * @author: lait.zhang@gmail.com
 * @date: 9/27/2024 10:30 AM
 * @history: </br>
 * <author>      <time>      <version>    <desc>
 * 修改人姓名      修改时间        版本号       描述
 */
@Builder
@Data
public class ParameterMetadata {
    private String name;
    private String type;
    private Map<String,PropertyMetadata> properties;

}