package org.dows.rade.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

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
public class MethodMetadata {
    private String table;
    private String title;
    private String methodName;
    private String declaringClass;
    private ParameterMetadata returnType;
    private List<ParameterMetadata> parameters;
}
