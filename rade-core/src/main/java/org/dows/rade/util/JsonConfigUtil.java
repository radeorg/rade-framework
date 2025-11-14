package org.dows.rade.util;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.Data;

@Data
public class JsonConfigUtil {
    private Class<?> clazz;
    private Object properties;


    /**
     * 将配置类对象转换为json字符串
     *
     * @param object
     * @return
     */
    public static String toJsonConfig(Object object) {
        /*JsonConfigUtil jsonConfig = new JsonConfigUtil();
        jsonConfig.setClazz(object.getClass());
        jsonConfig.setProperties(object);*/
        JSONObject jsonObject = new JSONObject();
        //{"com.hina.cloud.eaglee.endpoint.request.setting.TaskRuleSetting":"{\"ruleName\":\"150\",\"ruleDesc\":\"150\",\"timeoutThreshold\":150,\"minRunTimes\":3,\"enableTimeoutAlarm\":true,\"alarmLevel\":\"警告\",\"exceptionType\":\"业务类\",\"retryInterval\":6000,\"projectCode\":11986638487424,\"retryTimes\":3,\"taskCardinalCount\":3,\"autoProcess\":true}"}
        jsonObject.set(object.getClass().getName(), JSONUtil.toJsonStr(object));
        return jsonObject.toJSONString(0);
    }


    /**
     * 将json字符串转换为配置类对象
     *
     * @param jsonConfig
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T fromJsonConfig(String jsonConfig, Class<T> clazz) {
        /*JSONObject jsonObject = JSONUtil.parseObj(jsonConfig);
        return jsonObject.get(clazz.getName(), clazz);*/

        try {
            JSONObject jsonObject = JSONUtil.parseObj(jsonConfig);
            // 检查是否是嵌套JSON字符串
            Object value = jsonObject.get(clazz.getName());
            if (value instanceof String) {
                // 如果是字符串，再次解析
                return JSONUtil.toBean((String) value, clazz);
            } else if (value instanceof JSONObject) {
                // 如果是JSONObject，直接转换
                return ((JSONObject) value).toBean(clazz);
            }
            return jsonObject.get(clazz.getName(), clazz);
        } catch (Exception e) {
            // 如果解析失败，尝试直接转换整个字符串
            try {
                return JSONUtil.toBean(jsonConfig, clazz);
            } catch (Exception ex) {
                throw new RuntimeException("Failed to convert JSON config to " + clazz.getName(), ex);
            }
        }
    }
}
