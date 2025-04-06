//package org.dows.rade.feign.a;
//
//import cn.hutool.core.bean.BeanUtil;
//import cn.hutool.core.util.StrUtil;
//import cn.hutool.extra.spring.SpringUtil;
//import cn.hutool.json.JSONUtil;
//import lombok.Builder;
//import lombok.Data;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.dows.rade.exception.RadeException;
//import org.dows.rade.model.PojoSchema;
//
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Method;
//import java.net.URI;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
/// **
// * @description: </br>
// * @author: lait.zhang@gmail.com
// * @date: 10/11/2024 1:35 PM
// * @history: </br>
// * <author>      <time>      <version>    <desc>
// * 修改人姓名      修改时间        版本号       描述
// */
//@RequiredArgsConstructor
//@Slf4j
//public class FeignUtil {
//
//    private final static Map<String, Class<?>> CLASS_MAP = new ConcurrentHashMap<>();
//
//    /**
//     * 动态调用
//     *
//     * @param protocol    协议
//     * @param returnJson  返回值
//     * @param subjectName 主题名[class名，post,get,select,update,delete...]
//     * @param subjectFunc 方法名[]
//     * @param requestJson 请求值
//     */
//    public static Object invoke(String protocol, String returnJson,
//                                String subjectName, String subjectFunc, String requestJson, Object data) {
//        Object response = null;
//        if (StrUtil.equals(protocol, "bean")) {
//            response = FeignUtil.beanInvoking(returnJson, subjectName, subjectFunc, requestJson, data);
//        } else if (StrUtil.equals(protocol, "http")) {
//            response = FeignUtil.httpInvoking(returnJson, subjectName, subjectFunc, requestJson, data);
//        } else if (StrUtil.equals(protocol, "jdbc")) {
//            response = FeignUtil.jdbcInvoking(returnJson, subjectName, subjectFunc, requestJson, data);
//        } else {
//            throw new RadeException("不支持的调用方式");
//        }
//        // todo 转换
//        if (response != null) {
//            PojoSchema pojoSchema = JSONUtil.toBean(returnJson, PojoSchema.class);
//            if (StrUtil.equals(pojoSchema.getCollect(), "list")) {
//
//            } else if (StrUtil.equals(pojoSchema.getCollect(), "map")) {
//
//            } else if (StrUtil.equals(pojoSchema.getCollect(), "array")) {
//
//            } else {
//
//            }
//        }
//        return response;
//    }
//
//
//    /**
//     * bean调用
//     * returnJson, subjectName, subjectFunc, requestJson, data
//     *
//     * @param returnJson
//     * @param subjectName
//     * @param subjectFunc
//     * @param requestJson
//     * @param data
//     * @return
//     */
//    public static Object beanInvoking(String returnJson, String subjectName, String subjectFunc, String requestJson, Object data) {
//        log.info("调用bean接口");
//        if (StrUtil.isNotBlank(requestJson) && StrUtil.isNotBlank(subjectName) && StrUtil.isNotBlank(subjectFunc)) {
//            List<PojoSchema> pojoSchemas = JSONUtil.toList(requestJson, PojoSchema.class);
//            Class<?>[] classes = new Class[pojoSchemas.size()];
//            Object[] values = new Object[pojoSchemas.size()];
//
//            Class<?> beanClass = CLASS_MAP.get(subjectName);
//            try {
//                if (beanClass == null) {
//                    beanClass = Class.forName(subjectName);
//                    CLASS_MAP.put(subjectName, beanClass);
//                }
//                Object bean = SpringUtil.getBean(beanClass);
//                for (int i = 0; i < pojoSchemas.size(); i++) {
//                    Class<?> aClass = CLASS_MAP.get(pojoSchemas.get(i).getType());
//                    if (aClass == null) {
//                        classes[i] = Class.forName(pojoSchemas.get(i).getType());
//                        CLASS_MAP.put(pojoSchemas.get(i).getType(), classes[i]);
//                    } else {
//                        classes[i] = aClass;
//                    }
//                    // todo 普通值和json对象值需要对应处理，目前只支持一个集合类型参数，先简单实现
//                    if (StrUtil.equals(pojoSchemas.get(i).getCollect(), "list") && data instanceof List) {
//                        // 按集合来出来，构建集合对象
//                        List<?> value = BeanUtil.copyToList((Collection<?>) data, classes[i]);
//                        values[i] = value;
//                    } else {
//                        // 普通对象处理,创建并copy给入参类型对象
//                        Object value = BeanUtil.toBean(data, classes[i]);
//                        values[i] = value;
//                    }
//                }
//                Method method = bean.getClass().getMethod(subjectFunc, classes);
//                // 动态调用
//                return method.invoke(bean, values);
//            } catch (ClassNotFoundException | NoSuchMethodException |
//                     IllegalAccessException | InvocationTargetException e) {
//                throw new RuntimeException(e);
//            }
//        }
//        return null;
//    }
//
//    /**
//     * http调用
//     *
//     * @param returnJson
//     * @param subjectName
//     * @param subjectFunc
//     * @param requestJson
//     * @param data
//     */
//    public static Object httpInvoking(String returnJson, String subjectName, String subjectFunc, String requestJson, Object data) {
//        // todo 调用http接口
////            response = HttpUtil.post("http://localhost:8080/v1/task/oneVehicleTask", requestJson);
//        RadeFeignClient client = SpringUtil.getBean(RadeFeignClient.class);
//        try {
//            RequestMetadata requestMetadata = parseRequestMetaJson(requestJson, data);
//            if (StrUtil.equals(subjectName, "post")) {
//                /*Map<String, Object> map = new HashMap<>();
//                map.put("planDate", "2024-10-08 10:19:44");
//                map.put("vehicleNo", "1eoer");
//                List<Object> request = new ArrayList<>();
//                request.add(map);*/
//                // @PostMapping 中的requestBody 只能有一个对象入参，HttpServletRequest, HttpServletResponse除外
//                Object result = client.post(URI.create(subjectFunc), null, requestMetadata.getInputValues()[0]);
//                log.info("result:{}", result);
//            } else if (StrUtil.equals(subjectName, "get")) {
//                log.info("result:{}", "get");
//            }
//        } catch (ClassNotFoundException e) {
//            throw new RuntimeException(e);
//        }
//        return null;
//    }
//
//
//    public static RequestMetadata parseRequestMetaJson(String requestJson, Object bizData) throws ClassNotFoundException {
//        List<PojoSchema> pojoSchemas = JSONUtil.toList(requestJson, PojoSchema.class);
//        Class<?>[] classes = new Class[pojoSchemas.size()];
//        Object[] values = new Object[pojoSchemas.size()];
//
//        // todo 优化一个集合类型参数
//        List<Object> data = new ArrayList<>();
//
//        for (int i = 0; i < pojoSchemas.size(); i++) {
//            Class<?> aClass = CLASS_MAP.get(pojoSchemas.get(i).getType());
//            if (aClass == null) {
//                classes[i] = Class.forName(pojoSchemas.get(i).getType());
//                CLASS_MAP.put(pojoSchemas.get(i).getType(), classes[i]);
//            } else {
//                classes[i] = aClass;
//            }
//            // todo 普通值和json对象值需要对应处理，目前只支持一个集合类型参数，先简单实现
//            if (StrUtil.equals(pojoSchemas.get(i).getCollect(), "list")) {
//                // 集合类型处理
//                if (pojoSchemas.size() == 1 && isNotCollectionOrMap(bizData)) {
//                    data.add(bizData);
//                } else {
//                    data.addAll((Collection<?>) bizData);
//                }
//                // 按集合来出来，构建集合对象
//                List<?> value = BeanUtil.copyToList(data, classes[i]);
//                values[i] = value;
//            } else {
//                // 普通对象处理,创建并copy给入参类型对象
//                Object value = BeanUtil.toBean(bizData, classes[i]);
//                values[i] = value;
//            }
//        }
//        return RequestMetadata.builder()
//                .inputTypes(classes)
//                .inputValues(values)
//                .build();
//    }
//
//
//    public static boolean isNotCollectionOrMap(Object obj) {
//        boolean notCollection = !(obj instanceof Collection);
//        boolean notMap = !(obj instanceof Map);
//        // 如果既不是Collection也不是Map，返回true
//        return notCollection && notMap;
//    }
//
//    /**
//     * jdbc调用
//     *
//     * @param returnJson
//     * @param subjectName
//     * @param subjectFunc
//     * @param requestJson
//     * @param data
//     */
//    public static Object jdbcInvoking(String returnJson, String subjectName, String subjectFunc, String requestJson, Object data) {
//
//        log.info("调用jdbc接口");
////            response = JdbcClient.select()
//        return null;
//    }
//
//
//    @Builder
//    @Data
//    public static class RequestMetadata {
//        // 入参类型,http 时不需要关注
//        private Class<?>[] inputTypes;
//        private Object[] inputValues;
//    }
//
//}
//
