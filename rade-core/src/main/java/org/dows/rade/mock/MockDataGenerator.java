package org.dows.rade.mock;

import io.swagger.v3.oas.annotations.media.Schema;
import net.datafaker.Faker;
import org.apache.commons.lang3.RandomUtils;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 * 增强版Mock数据生成器：支持关键字匹配的动态处理器
 */
public class MockDataGenerator {
    private static final EasyRandom easyRandom;
    private static final Faker faker;
    
    // 精确字段名匹配的生成器
    private static final Map<String, Function<Field, Object>> EXACT_FIELD_GENERATORS = new HashMap<>();
    
    // 关键字匹配的生成器（按匹配优先级排序）
    private static final List<KeywordProcessor> KEYWORD_PROCESSORS = new ArrayList<>();
    
    // 数据类型生成器
    private static final Map<Class<?>, Function<Field, Object>> TYPE_GENERATORS = new HashMap<>();

    static {
        // 初始化EasyRandom配置
        EasyRandomParameters parameters = new EasyRandomParameters()
                .seed(System.currentTimeMillis())
                .stringLengthRange(5, 20)
                .collectionSizeRange(1, 3);
        easyRandom = new EasyRandom(parameters);
        
        // 初始化Faker
        faker = new Faker(Locale.CHINA);
        
        // 初始化精确字段名生成器
        initExactFieldGenerators();
        
        // 初始化关键字处理器
        initKeywordProcessors();
        
        // 初始化类型生成器
        initTypeGenerators();
    }

    /**
     * 初始化精确字段名匹配的生成器
     */
    private static void initExactFieldGenerators() {
        // 保留原有的精确字段名匹配逻辑，优先级最高
        EXACT_FIELD_GENERATORS.put("projectName", field -> faker.app().name());
        EXACT_FIELD_GENERATORS.put("userName", field -> faker.name().username());
        EXACT_FIELD_GENERATORS.put("taskProjectId", field -> generate19DigitsLong());
        EXACT_FIELD_GENERATORS.put("taskInstanceId", field -> generate19DigitsLong());
        EXACT_FIELD_GENERATORS.put("processCode", field -> "PROC-" + faker.code().isbn10());
        EXACT_FIELD_GENERATORS.put("projectIdenfifier", field -> "PROJ-" + faker.random().hex(6));
        EXACT_FIELD_GENERATORS.put("taskIdentifier", field -> "TASK-" + faker.random().hex(6));
    }

    /**
     * 初始化关键字处理器
     */
    private static void initKeywordProcessors() {
        // 添加关键字处理器，按照优先级排序（更具体的关键字应放在前面）
        
        // ID相关关键字处理器
        KEYWORD_PROCESSORS.add(new KeywordProcessor("id", field -> {
            if (field.getType() == Long.class || field.getType() == long.class) {
                return generate19DigitsLong();
            } else if (field.getType() == String.class) {
                return "ID-" + faker.random().hex(8);
            }
            return null;
        }));
        
        // Name相关关键字处理器
        KEYWORD_PROCESSORS.add(new KeywordProcessor("name", field -> {
            if (field.getType() == String.class) {
                // 根据不同的name类型生成不同的中文名称
                String fieldName = field.getName().toLowerCase();
                if (fieldName.contains("user")) {
                    return faker.name().fullName(); // DataFaker配置了Locale.CHINA，会生成中文姓名
                } else if (fieldName.contains("project")) {
                    // 生成中文项目名称
                    return faker.app().name() + "项目";
                } else if (fieldName.contains("task")) {
                    // 生成中文任务名称
                    return "任务-" + faker.lorem().word();
                } else if (fieldName.contains("role")) {
                    // 生成中文角色名称
                    return faker.company().profession();
                } else {
                    // 生成通用中文名称
                    return faker.name().fullName();
                }
            }
            return null;
        }));
        /*KEYWORD_PROCESSORS.add(new KeywordProcessor("name", field -> {
            if (field.getType() == String.class) {
                // 根据不同的name类型生成不同的名称
                String fieldName = field.getName().toLowerCase();
                if (fieldName.contains("user")) {
                    return faker.name().fullName();
                } else if (fieldName.contains("project")) {
                    return faker.app().name();
                } else if (fieldName.contains("task")) {
                    return "任务" + faker.lorem().word();
                } else if (fieldName.contains("role")) {
                    return faker.company().profession();
                } else {
                    return faker.lorem().word();
                }
            }
            return null;
        }));*/
        
        // Code相关关键字处理器
        KEYWORD_PROCESSORS.add(new KeywordProcessor("code", field -> {
            if (field.getType() == String.class) {
                return faker.code().asin();
            }
            return null;
        }));
        
        // Description相关关键字处理器
        KEYWORD_PROCESSORS.add(new KeywordProcessor("desc", field -> {
            if (field.getType() == String.class) {
                return faker.lorem().sentence();
            }
            return null;
        }));
        
        // Email相关关键字处理器
        KEYWORD_PROCESSORS.add(new KeywordProcessor("email", field -> {
            if (field.getType() == String.class) {
                return faker.internet().emailAddress();
            }
            return null;
        }));
        
        // Phone相关关键字处理器
        KEYWORD_PROCESSORS.add(new KeywordProcessor("phone", field -> {
            if (field.getType() == String.class) {
                return faker.phoneNumber().phoneNumber();
            }
            return null;
        }));
        
        // Time/Date相关关键字处理器
        KEYWORD_PROCESSORS.add(new KeywordProcessor("time", field -> {
            if (field.getType() == LocalDateTime.class) {
                return LocalDateTime.now();
            } else if (field.getType() == Date.class) {
                return new Date();
            } else if (field.getType() == LocalDate.class) {
                return LocalDate.now();
            } else if (field.getType() == LocalTime.class) {
                return LocalTime.now();
            }
            return null;
        }));
        KEYWORD_PROCESSORS.add(new KeywordProcessor("date", field -> {
            if (field.getType() == LocalDate.class) {
                return LocalDate.now();
            } else if (field.getType() == Date.class) {
                return new Date();
            } else if (field.getType() == LocalDateTime.class) {
                return LocalDateTime.now();
            }
            return null;
        }));
        
        // Status/State相关关键字处理器
        KEYWORD_PROCESSORS.add(new KeywordProcessor("status", field -> {
            if (field.getType() == Integer.class || field.getType() == int.class) {
                return RandomUtils.nextInt(0, 3); // 0, 1, 2 状态码
            }
            return null;
        }));
        KEYWORD_PROCESSORS.add(new KeywordProcessor("state", field -> {
            if (field.getType() == Integer.class || field.getType() == int.class) {
                return RandomUtils.nextInt(0, 2); // 0或1状态
            }
            return null;
        }));
        
        // Count/Number相关关键字处理器
        KEYWORD_PROCESSORS.add(new KeywordProcessor("count", field -> {
            if (field.getType() == Integer.class || field.getType() == int.class) {
                return faker.number().randomDigitNotZero();
            }
            return null;
        }));
        
        // URL相关关键字处理器
        KEYWORD_PROCESSORS.add(new KeywordProcessor("url", field -> {
            if (field.getType() == String.class) {
                return faker.internet().url();
            }
            return null;
        }));
        
        // Address相关关键字处理器
        KEYWORD_PROCESSORS.add(new KeywordProcessor("address", field -> {
            if (field.getType() == String.class) {
                return faker.address().fullAddress();
            }
            return null;
        }));
    }

    /**
     * 初始化基于数据类型的生成器
     */
    private static void initTypeGenerators() {
        // 字符串类型
        TYPE_GENERATORS.put(String.class, field -> faker.lorem().characters(5, 20));
        
        // 数字类型
        TYPE_GENERATORS.put(Integer.class, field -> faker.number().randomDigitNotZero());
        TYPE_GENERATORS.put(int.class, field -> faker.number().randomDigitNotZero());
        TYPE_GENERATORS.put(Long.class, field -> generate19DigitsLong());
        TYPE_GENERATORS.put(long.class, field -> generate19DigitsLong());
        TYPE_GENERATORS.put(Double.class, field -> faker.number().randomDouble(2, 1, 1000));
        TYPE_GENERATORS.put(double.class, field -> faker.number().randomDouble(2, 1, 1000));
        TYPE_GENERATORS.put(BigDecimal.class, field -> new BigDecimal(faker.number().randomDouble(2, 1, 1000)));
        TYPE_GENERATORS.put(BigInteger.class, field -> BigInteger.valueOf(generate19DigitsLong()));
        
        // 布尔类型
        TYPE_GENERATORS.put(Boolean.class, field -> faker.bool().bool());
        TYPE_GENERATORS.put(boolean.class, field -> faker.bool().bool());
        
        // 日期时间类型
        TYPE_GENERATORS.put(Date.class, field -> new Date());
        TYPE_GENERATORS.put(LocalDateTime.class, field -> LocalDateTime.now());
        TYPE_GENERATORS.put(LocalDate.class, field -> LocalDate.now());
        TYPE_GENERATORS.put(LocalTime.class, field -> LocalTime.now());
    }

    /**
     * 生成响应对象的Mock数据
     */
    public static <T> T generateMock(Class<T> responseType, Object request) {
        try {
            // 1. 生成基础Mock对象
            T mockResponse = easyRandom.nextObject(responseType);

            // 2. 若有请求对象，尝试关联相同字段名的值
            if (request != null) {
                copySameFields(request, mockResponse);
            }

            // 3. 根据@Schema注解、关键字匹配和数据类型自定义生成规则
            processSchemaAndTypeFields(mockResponse);

            return mockResponse;
        } catch (Exception e) {
            throw new RuntimeException("生成Mock数据失败：" + e.getMessage(), e);
        }
    }

    /**
     * 根据@Schema注解和数据类型处理字段值
     */
    private static void processSchemaAndTypeFields(Object response) throws IllegalAccessException {
        if (response == null) return;
        
        Field[] fields = response.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            
            // 处理当前字段
            Object fieldValue = processField(field, response);
            if (fieldValue != null) {
                field.set(response, fieldValue);
            }
        }
    }

    /**
     * 处理单个字段，按照优先级应用不同的生成策略
     */
    private static Object processField(Field field, Object response) throws IllegalAccessException {
        String fieldName = field.getName().toLowerCase(); // 转为小写以实现大小写不敏感的匹配
        
        // 1. 优先使用@Schema注解中的example值
        Schema schema = field.getAnnotation(Schema.class);
        if (schema != null && !schema.example().isEmpty() && !schema.example().equals("")) {
            Object exampleValue = convertExampleToValue(schema.example(), field.getType());
            if (exampleValue != null) {
                return exampleValue;
            }
        }
        
        // 2. 使用精确字段名匹配的生成器（保留原有逻辑）
        if (EXACT_FIELD_GENERATORS.containsKey(field.getName())) {
            return EXACT_FIELD_GENERATORS.get(field.getName()).apply(field);
        }
        
        // 3. 使用关键字匹配的生成器（新增功能）
        for (KeywordProcessor processor : KEYWORD_PROCESSORS) {
            if (processor.matches(fieldName)) {
                Object value = processor.generate(field);
                if (value != null) {
                    return value;
                }
            }
        }
        
        // 4. 使用基于数据类型的生成器
        if (TYPE_GENERATORS.containsKey(field.getType())) {
            return TYPE_GENERATORS.get(field.getType()).apply(field);
        }
        
        // 5. 处理集合类型
        if (Collection.class.isAssignableFrom(field.getType())) {
            return processCollectionField(field);
        }
        
        // 6. 处理Map类型
        if (Map.class.isAssignableFrom(field.getType())) {
            return processMapField(field);
        }
        
        // 7. 处理数组类型

        Class<?> fieldType = field.getType();
        if (fieldType.isArray()) {
            return processArrayField(field);
        }
        
        // 8. 处理嵌套对象
        if (!fieldType.isPrimitive() && fieldType.getPackageName().startsWith("org.dows.eaglee")) {
            Object nestedObject = easyRandom.nextObject(fieldType);
            processSchemaAndTypeFields(nestedObject);
            return nestedObject;
        }
        
        return null;
    }

    /**
     * 生成19位的Long类型数字
     */
    private static long generate19DigitsLong() {
        // 第一位生成1-9之间的数字
        StringBuilder sb = new StringBuilder(String.valueOf(faker.number().numberBetween(1, 10)));
        // 后面18位生成0-9之间的数字
        for (int i = 0; i < 18; i++) {
            sb.append(faker.number().randomDigit());
        }
        
        // 转换为Long（注意：如果生成的数字超过Long最大值，会返回Long.MAX_VALUE）
        String longStr = sb.toString();
        if (longStr.length() > 19 || (longStr.length() == 19 && longStr.compareTo("9223372036854775807") > 0)) {
            return Long.MAX_VALUE;
        }
        
        return Long.parseLong(longStr);
    }

    /**
     * 将@Schema注解中的example值转换为对应的Java类型
     */
    private static Object convertExampleToValue(String example, Class<?> fieldType) {
        try {
            if (fieldType == String.class) {
                return example;
            } else if (fieldType == Integer.class || fieldType == int.class) {
                return Integer.parseInt(example);
            } else if (fieldType == Long.class || fieldType == long.class) {
                return Long.parseLong(example);
            } else if (fieldType == Double.class || fieldType == double.class) {
                return Double.parseDouble(example);
            } else if (fieldType == Boolean.class || fieldType == boolean.class) {
                return Boolean.parseBoolean(example);
            } else if (fieldType == BigDecimal.class) {
                return new BigDecimal(example);
            }
        } catch (Exception e) {
            // 转换失败时忽略
        }
        return null;
    }

    /**
     * 处理集合类型字段
     */
    private static Collection<?> processCollectionField(Field field) throws IllegalAccessException {
        Type genericType = field.getGenericType();
        if (!(genericType instanceof ParameterizedType)) {
            return null;
        }
        
        ParameterizedType parameterizedType = (ParameterizedType) genericType;
        Type elementType = parameterizedType.getActualTypeArguments()[0];
        
        try {
            Class<?> elementClass = (Class<?>) elementType;
            int size = RandomUtils.nextInt(1, 4);
            
            if (List.class.isAssignableFrom(field.getType())) {
                List<Object> list = new ArrayList<>(size);
                for (int i = 0; i < size; i++) {
                    Object element = easyRandom.nextObject(elementClass);
                    processSchemaAndTypeFields(element);
                    list.add(element);
                }
                return list;
            } else if (Set.class.isAssignableFrom(field.getType())) {
                Set<Object> set = new HashSet<>(size);
                for (int i = 0; i < size; i++) {
                    Object element = easyRandom.nextObject(elementClass);
                    processSchemaAndTypeFields(element);
                    set.add(element);
                }
                return set;
            }
        } catch (Exception e) {
            // 处理失败时返回null
        }
        
        return null;
    }

    /**
     * 处理Map类型字段
     */
    private static Map<?, ?> processMapField(Field field) {
        int size = RandomUtils.nextInt(1, 4);
        Map<String, Object> map = new HashMap<>(size);
        
        for (int i = 0; i < size; i++) {
            map.put("key" + i, faker.lorem().word());
        }
        
        return map;
    }

    /**
     * 处理数组类型字段
     */
    private static Object processArrayField(Field field) {
        Class<?> componentType = field.getType().getComponentType();
        int length = RandomUtils.nextInt(1, 4);
        
        try {
            if (componentType == String.class) {
                String[] array = new String[length];
                for (int i = 0; i < length; i++) {
                    array[i] = faker.lorem().word();
                }
                return array;
            } else if (componentType == Integer.class || componentType == int.class) {
                int[] array = new int[length];
                for (int i = 0; i < length; i++) {
                    array[i] = faker.number().randomDigitNotZero();
                }
                return array;
            } else if (componentType == Long.class || componentType == long.class) {
                long[] array = new long[length];
                for (int i = 0; i < length; i++) {
                    array[i] = generate19DigitsLong();
                }
                return array;
            }
        } catch (Exception e) {
            // 处理失败时返回null
        }
        
        return null;
    }

    /**
     * 复制请求对象和响应对象中相同字段名的值
     */
    private static void copySameFields(Object request, Object response) throws IllegalAccessException {
        Field[] requestFields = request.getClass().getDeclaredFields();
        Field[] responseFields = response.getClass().getDeclaredFields();

        for (Field reqField : requestFields) {
            reqField.setAccessible(true);
            Object reqValue = reqField.get(request);
            if (reqValue == null) continue;

            for (Field resField : responseFields) {
                if (resField.getName().equals(reqField.getName()) 
                        && resField.getType().equals(reqField.getType())) {
                    resField.setAccessible(true);
                    resField.set(response, reqValue);
                    break;
                }
            }
        }
    }

    /**
     * 关键字处理器类
     */
    private static class KeywordProcessor {
        private final String keyword;
        private final Function<Field, Object> generator;

        public KeywordProcessor(String keyword, Function<Field, Object> generator) {
            this.keyword = keyword.toLowerCase(); // 转为小写以实现大小写不敏感的匹配
            this.generator = generator;
        }

        /**
         * 判断字段名是否包含关键字
         */
        public boolean matches(String fieldName) {
            return fieldName.contains(keyword);
        }

        /**
         * 生成字段值
         */
        public Object generate(Field field) {
            return generator.apply(field);
        }
    }

    // 函数式接口
    @FunctionalInterface
    private interface Function<T, R> {
        R apply(T t);
    }
}