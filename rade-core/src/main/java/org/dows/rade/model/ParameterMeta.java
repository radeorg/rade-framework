package org.dows.rade.model;

import lombok.Data;

@Data
// 参数元数据内部类
public class ParameterMeta {
    private String name;
    private String dataType;       // 实际数据类型
    private String collectionType;
    private String genericType;// 集合类型（如List/Set等），非集合时为null

    public ParameterMeta(){

    }
    public ParameterMeta(String dataType, String collectionType) {
        this.dataType = dataType;
        this.collectionType = collectionType;
    }

    public String getDataType() {
        return dataType;
    }

    public String getCollectionType() {
        return collectionType;
    }

    public boolean isCollection() {
        return collectionType != null;
    }

    @Override
    public String toString() {
        return isCollection() ?
                "{collectionType=" + collectionType + ", dataType=" + dataType + "}" :
                "{dataType=" + dataType + "}";
    }
}