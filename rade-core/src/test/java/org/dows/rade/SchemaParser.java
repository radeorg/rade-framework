package org.dows.rade;

import java.util.ArrayList;
import java.util.List;

// 定义数据结构类
class ObjectSchema {
    private String name;
    private List<Field> fields = new ArrayList<>();
    
    public ObjectSchema(String name) {
        this.name = name;
    }
    
    public String getName() { return name; }
    public List<Field> getFields() { return fields; }
    public void addField(Field field) { fields.add(field); }
}

class Field {
    private String name;
    private String basicType;     // 基本类型
    private ObjectSchema complexType; // 嵌套对象
    private boolean isArray;      // 是否为数组
    
    public Field(String name) {
        this.name = name;
    }
    
    // Getters and Setters
    public String getName() { return name; }
    public String getBasicType() { return basicType; }
    public ObjectSchema getComplexType() { return complexType; }
    public boolean isArray() { return isArray; }
    
    public void setBasicType(String basicType) { 
        this.basicType = basicType; 
    }
    
    public void setComplexType(ObjectSchema complexType) { 
        this.complexType = complexType; 
    }
    
    public void setArray(boolean isArray) { 
        this.isArray = isArray; 
    }
}

// 解析器实现
 class SchemaParser {
    private String input;
    private int index = 0;
    
    public ObjectSchema parse(String input) {
        this.input = input;
        this.index = 0;
        return parseObject();
    }
    
    private ObjectSchema parseObject() {
        String name = parseUntil(':');
        expect(':');
        expect('{');
        
        ObjectSchema schema = new ObjectSchema(name);
        while (peek() != '}') {
            schema.addField(parseField());
            if (peek() == ',') {
                index++; // 跳过逗号
            }
        }
        expect('}');
        return schema;
    }


    private Field parseField() {
        String name = parseUntil(':');
        expect(':');
        
        Field field = new Field(name);
        char next = peek();
        
        if (next == '[') { // 处理数组类型
            index++; // 跳过'['
            
            if (peek() == ']') {
                throw new RuntimeException("Empty array type not allowed");
            }
            
            // 尝试解析基本类型数组
            int start = index;
            while (index < input.length() && 
                  (Character.isLetterOrDigit(input.charAt(index)) || 
                   input.charAt(index) == '_')) {
                index++;
            }
            String possibleType = input.substring(start, index);
            
            if (peek() == ']') { // 基本类型数组
                index++; // 跳过']'
                field.setBasicType(possibleType);
                field.setArray(true);
            } else { // 对象数组
                index = start; // 回退位置
                ObjectSchema anonymousSchema = new ObjectSchema(null);
                while (peek() != ']') {
                    anonymousSchema.addField(parseField());
                    if (peek() == ',') {
                        index++; // 跳过逗号
                    }
                }
                index++; // 跳过']'
                field.setComplexType(anonymousSchema);
                field.setArray(true);
            }
        } else if (next == '{') { // 嵌套对象
            ObjectSchema schema = parseObject();
            field.setComplexType(schema);
            field.setArray(false);
        } else { // 基本类型
            String typeName = parseUntil(',', '}', ']');
            field.setBasicType(typeName);
            field.setArray(false);
        }
        
        return field;
    }
    
    // 辅助方法：解析直到遇到特定字符
    /*private String parseUntil(char... delimiters) {
        int start = index;
        while (index < input.length()) {
            char c = input.charAt(index);
            for (char d : delimiters) {
                if (c == d) {
                    return input.substring(start, index);
                }
            }
            index++;
        }
        throw new RuntimeException("Unexpected end of input");
    }*/

    private String parseUntil(char... delimiters) {
        int start = index;
        while (index < input.length()) {
            char c = input.charAt(index);
            skipWhitespace(); // 新增：跳过空白字符
            for (char d : delimiters) {
                if (c == d) {
                    return input.substring(start, index).trim(); // 去除前后空格
                }
            }
            index++;
        }
        throw new RuntimeException("Unexpected end of input");
    }
    
    // 辅助方法：检查并跳过特定字符
    /*private void expect(char expected) {
        if (index >= input.length() || input.charAt(index) != expected) {
            throw new RuntimeException("Expected '" + expected + "' at position " + index);
        }
        index++;
    }*/

    private void skipWhitespace() {
        while (index < input.length() && Character.isWhitespace(input.charAt(index))) {
            index++;
        }
    }

    private void expect(char expected) {
        skipWhitespace(); // 增加这一行
        if (index >= input.length() || input.charAt(index) != expected) {
            throw new RuntimeException("Expected '" + expected + "' at position " + index);
        }
        index++;
    }
    
    // 辅助方法：查看下一个字符
    private char peek() {
        return index < input.length() ? input.charAt(index) : '\0';
    }
}

// 使用示例
