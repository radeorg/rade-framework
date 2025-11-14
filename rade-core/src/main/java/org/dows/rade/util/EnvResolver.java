package org.dows.rade.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 环境变量解析工具类：从环境变量获取值，不存在则使用默认值（支持默认值含特殊字符）
 */
public class EnvResolver {

    // 正则表达式：匹配 ${key:'default'} 格式，捕获key和默认值
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\$\\{([^:]+):'([^']+)'\\}");

    /**
     * 解析占位符表达式，获取值
     * @param expression 表达式，格式如 ${key:'default'}（默认值用单引号包裹，支持含冒号等特殊字符）
     * @return 环境变量中的值，或默认值
     * @throws IllegalArgumentException 表达式格式错误时抛出
     */
    public static String resolve(String expression) {
        // 验证表达式格式
        if (expression == null || !expression.startsWith("${") || !expression.endsWith("}")) {
            throw new IllegalArgumentException("无效的表达式格式，必须为 ${key:'default'}");
        }

        // 匹配正则，提取key和默认值
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(expression);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("表达式格式错误，正确格式：${key:'default'}（默认值需用单引号包裹）");
        }

        String key = matcher.group(1).trim();
        String defaultValue = matcher.group(2);

        // 从环境变量获取值（优先系统环境变量，再查JVM参数）
        String envValue = getEnvValue(key);

        // 环境变量存在则返回，否则返回默认值
        return envValue != null ? envValue : defaultValue;
    }

    /**
     * 获取环境变量的值（支持系统环境变量和JVM参数 -Dkey=value）
     * @param key 键名
     * @return 环境变量的值，不存在则返回null
     */
    private static String getEnvValue(String key) {
        // 1. 先查系统环境变量（系统环境变量通常是大写+下划线，这里兼容原key名查询）
        String systemEnv = System.getenv(key);
        if (systemEnv != null) {
            return systemEnv;
        }

        // 2. 再查JVM参数（-Dkey=value）
        return System.getProperty(key);
    }


    // 测试示例
    public static void main(String[] args) {
        // 测试1：环境变量不存在，使用默认值（含冒号）
        String expr1 = "${hina.eaglee.dolphin.host:'http://10.0.20.25:12345'}";
        System.out.println(resolve(expr1)); // 输出：http://10.0.20.25:12345

        // 测试2：设置环境变量后，优先使用环境变量
        System.setProperty("hina.eaglee.dolphin.host", "http://custom:6789");
        System.out.println(resolve(expr1)); // 输出：http://custom:6789

        // 测试3：默认值含多个冒号
        String expr2 = "${test.key:'http://a:8080/path:sub'}";
        System.out.println(resolve(expr2)); // 输出：http://a:8080/path:sub
    }
}