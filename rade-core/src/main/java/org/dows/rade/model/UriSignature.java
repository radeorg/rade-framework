package org.dows.rade.model;

import lombok.Data;
import org.dows.rade.init.InitializableResource;

import java.util.ArrayList;
import java.util.List;

@Data
public class UriSignature implements InitializableResource {

    private String appId;
    private String tag;
    private String javaMethod;
    private String httpMethod;
    private String uri;
    private String summary;
    private String description;
    // 参数列表
    private List<ParameterMeta> inputs = new ArrayList<>();
    // 返回类型元数据
    private ParameterMeta output;

    @Override
    public String toString() {
        return "MethodSignature{\n" +
                "parameters=" + inputs + ",\n" +
                "returnType=" + output + "\n" +
                '}';
    }
}