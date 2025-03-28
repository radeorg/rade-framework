package org.dows.rade.util;

import org.dows.rade.model.ParameterMeta;
import org.dows.rade.model.UriSignature;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MethodSignatureParser {
    // 匹配JVM方法描述符
    private static final Pattern METHOD_SIGNATURE_PATTERN = 
        Pattern.compile("^\\((.*?)\\)(.*?)$");
    
    // 匹配类描述符（包括泛型）
    private static final Pattern CLASS_DESCRIPTOR_PATTERN = 
        Pattern.compile("L([^;<]+)(<.*>)?;");
    
    // 匹配集合类型
    private static final Pattern COLLECTION_PATTERN = 
        Pattern.compile("Ljava/util/(List|Set|Collection|Queue|Deque)<(.*)>;");
    
    // 匹配数组类型
    private static final Pattern ARRAY_PATTERN = 
        Pattern.compile("^\\[+([^;]+).*");
    
    public static UriSignature parse(String signature) {
        UriSignature result = new UriSignature();
        
        Matcher methodMatcher = METHOD_SIGNATURE_PATTERN.matcher(signature);
        if (!methodMatcher.matches()) {
            throw new IllegalArgumentException("Invalid method signature format");
        }
        
        String paramsPart = methodMatcher.group(1);
        String returnPart = methodMatcher.group(2);
        
        // 解析参数类型
        parseParameterDescriptors(paramsPart, result.getInputs());
        
        // 解析返回类型
        result.setOutput(parseReturnTypeDescriptor(returnPart));
        
        return result;
    }
    
    private static void parseParameterDescriptors(String descriptors, List<ParameterMeta> output) {
        int index = 0;
        while (index < descriptors.length()) {
            char c = descriptors.charAt(index);
            
            if (c == 'L') {
                // 处理对象类型描述符
                Matcher matcher = CLASS_DESCRIPTOR_PATTERN.matcher(descriptors.substring(index));
                if (matcher.find()) {
                    String descriptor = descriptors.substring(index, index + matcher.end());
                    
                    // 检查是否是集合类型
                    Matcher collectionMatcher = COLLECTION_PATTERN.matcher(descriptor);
                    if (collectionMatcher.matches()) {
                        // 集合类型
                        String collectionType = "java.util." + collectionMatcher.group(1);
                        String genericType = extractDataType(collectionMatcher.group(2));
                        output.add(new ParameterMeta(genericType, collectionType));
                    } else {
                        // 普通对象类型
                        String className = extractClassName(descriptor);
                        output.add(new ParameterMeta(className, null));
                    }
                    index += matcher.end();
                } else {
                    throw new IllegalArgumentException("Invalid class descriptor at position " + index);
                }
            } else if (c == '[') {
                // 处理数组类型
                Matcher arrayMatcher = ARRAY_PATTERN.matcher(descriptors.substring(index));
                if (arrayMatcher.find()) {
                    String arrayPrefix = descriptors.substring(index, index + arrayMatcher.start(1));
                    int dimension = arrayPrefix.length(); // 数组维度
                    
                    String elementDescriptor = descriptors.substring(index + dimension);
                    char elementType = elementDescriptor.charAt(0);
                    
                    if (elementType == 'L') {
                        Matcher classMatcher = CLASS_DESCRIPTOR_PATTERN.matcher(elementDescriptor);
                        if (classMatcher.find()) {
                            String className = extractClassName(elementDescriptor.substring(0, classMatcher.end()));
                            output.add(new ParameterMeta(className, dimension + "-dimension array"));
                            index += dimension + classMatcher.end();
                        }
                    } else {
                        String primitiveType = getPrimitiveType(elementType);
                        if (primitiveType != null) {
                            output.add(new ParameterMeta(primitiveType, dimension + "-dimension array"));
                            index += dimension + 1;
                        }
                    }
                }
            } else {
                // 处理基本类型
                String primitiveType = getPrimitiveType(c);
                if (primitiveType != null) {
                    output.add(new ParameterMeta(primitiveType, null));
                    index++;
                } else {
                    throw new IllegalArgumentException("Unknown type descriptor: " + c);
                }
            }
        }
    }
    
    private static ParameterMeta parseReturnTypeDescriptor(String descriptor) {
        if (descriptor.startsWith("L")) {
            Matcher collectionMatcher = COLLECTION_PATTERN.matcher(descriptor);
            if (collectionMatcher.matches()) {
                // 集合返回类型
                String collectionType = "java.util." + collectionMatcher.group(1);
                String genericType = extractDataType(collectionMatcher.group(2));
                return new ParameterMeta(genericType, collectionType);
            } else {
                // 普通对象返回类型
                String className = extractClassName(descriptor);
                return new ParameterMeta(className, null);
            }
        } else if (descriptor.startsWith("[")) {
            // 数组返回类型
            Matcher arrayMatcher = ARRAY_PATTERN.matcher(descriptor);
            if (arrayMatcher.find()) {
                String arrayPrefix = descriptor.substring(0, arrayMatcher.start(1));
                int dimension = arrayPrefix.length();
                
                String elementDescriptor = descriptor.substring(dimension);
                char elementType = elementDescriptor.charAt(0);
                
                if (elementType == 'L') {
                    Matcher classMatcher = CLASS_DESCRIPTOR_PATTERN.matcher(elementDescriptor);
                    if (classMatcher.find()) {
                        String className = extractClassName(elementDescriptor.substring(0, classMatcher.end()));
                        return new ParameterMeta(className, dimension + "-dimension array");
                    }
                } else {
                    String primitiveType = getPrimitiveType(elementType);
                    if (primitiveType != null) {
                        return new ParameterMeta(primitiveType, dimension + "-dimension array");
                    }
                }
            }
        } else {
            // 基本类型或void返回
            String type = descriptor.equals("V") ? "void" : getPrimitiveType(descriptor.charAt(0));
            return new ParameterMeta(type, null);
        }
        
        throw new IllegalArgumentException("Unsupported return type descriptor: " + descriptor);
    }
    
    private static String extractDataType(String descriptor) {
        // 处理泛型参数中的数据类型
        if (descriptor.startsWith("L")) {
            return extractClassName(descriptor);
        } else {
            char c = descriptor.charAt(0);
            if (c == '[') {
                return "array"; // 简化处理，实际可以递归解析
            } else {
                String primitiveType = getPrimitiveType(c);
                return primitiveType != null ? primitiveType : "unknown";
            }
        }
    }
    
    private static String extractClassName(String descriptor) {
        if (descriptor.startsWith("L")) {
            descriptor = descriptor.substring(1, descriptor.length() - 1);
        }
        
        // 处理泛型
        if (descriptor.contains("<")) {
            return descriptor.substring(0, descriptor.indexOf('<')).replace('/', '.');
        }
        return descriptor.replace('/', '.');
    }
    
    private static String getPrimitiveType(char descriptor) {
        switch (descriptor) {
            case 'B': return "byte";
            case 'C': return "char";
            case 'D': return "double";
            case 'F': return "float";
            case 'I': return "int";
            case 'J': return "long";
            case 'S': return "short";
            case 'Z': return "boolean";
            case 'V': return "void";
            default: return null;
        }
    }
}