//package org.dows.rade.mybatisflex;
//
//import cn.hutool.core.util.StrUtil;
//import com.fasterxml.jackson.annotation.JsonIgnore;
//import io.swagger.v3.oas.annotations.media.Schema;
//import lombok.Data;
//
//@Data
//public class OrderBy {
//    @Schema(description = "排序字段")
//    private String field;
//
//    @Schema(description = "排序方式[asc,desc]")
//    private String order = "DESC";
//
//    @JsonIgnore
//    public String getOrderBy(/*Class<?> objectClass*/) {
//        // 校验order是否合法
//        if (!"asc".equalsIgnoreCase(order) && !"desc".equalsIgnoreCase(order)) {
//            throw new IllegalArgumentException("order must be asc or desc");
//        }
//        if (field == null || field.trim().isEmpty()) {
//            throw new IllegalArgumentException("field must not be null or empty");
//        }
//        // todo 校验field是否合法，确保field在objectClass对应的字段中存在
//        //Map<String, Object> annotationValueMap = AnnotationUtil.getAnnotationValueMap(objectClass, Ordering.class);
//        return StrUtil.toUnderlineCase(field) + " " + order;
//    }
//}
