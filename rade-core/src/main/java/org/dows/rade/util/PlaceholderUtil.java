package org.dows.rade.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import org.springframework.core.env.Environment;
import org.springframework.util.PropertyPlaceholderHelper;

import java.io.Serializable;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaceholderUtil {
    // escapeCharacters 设置为 null 表示不转义，即保留 ${} 中的内容
    static PropertyPlaceholderHelper helper = new PropertyPlaceholderHelper("${", "}", ": ",null,false);
    public static String resolve(String expression) {
        String s = extractPlaceholder(expression);
        if(StrUtil.isNotBlank(s)){
            String[] split = s.split(": ");
            if(split.length == 2){
                // 环境变量取值,如果取到则返回环境变量值，否则返回默认值
                String envValue = getEnvValue(split[0]);
                if(StrUtil.isBlank(envValue)){
                    return split[1];
                }
                return envValue;
            } else {
                return getEnvValue(s);
            }
        }
        return null;
    }

    private static String getEnvValue(String key) {
        // 1. 先查系统环境变量（系统环境变量通常是大写+下划线，这里兼容原key名查询）
        String systemEnv = System.getenv(key);
        if (systemEnv != null) {
            return systemEnv;
        }
        // spring 获取
        Environment env = SpringUtil.getApplicationContext().getEnvironment();
        systemEnv = env.getProperty(key);
        if (systemEnv != null) {
            return systemEnv;
        }
        // 2. 再查JVM参数（-Dkey=value）
        return System.getProperty(key);
    }

    private static String extractPlaceholder(String input) {
        // 返回 ${} 中的内容
        return helper.replacePlaceholders(input, key -> key);
    }



    public static String regExtractPlaceholder(String input) {
        Pattern pattern = Pattern.compile("\\$\\{(.*?)\\}");
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }


    public static void main(String[] args) {
        String input = "${hina.eaglee.dolphin.host: http://10.0.20.25:12345}${hina.eaglee.dolphin.host: http://10.0.20.25:12345}";
        String placeholder = extractPlaceholder(input);
        System.out.println(placeholder); // 输出: hina.eaglee.dolphin.host:http://10.0.20.25:12345
    }

    public static String replace(String template, Map<String, ? extends Serializable> params) {
        if (StrUtil.isBlank(template) || params == null || params.isEmpty()) {
            return template;
        }
        
        String result = template;
        for (Map.Entry<String, ? extends Serializable> entry : params.entrySet()) {
            String placeholder = "${" + entry.getKey() + "}";
            String value = entry.getValue() != null ? entry.getValue().toString() : "";
            result = result.replace(placeholder, value);
        }
        return result;
    }
}