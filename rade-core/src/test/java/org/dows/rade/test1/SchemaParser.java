package org.dows.rade.test1;

import java.util.ArrayList;
import java.util.List;

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
    private String basicType;
    private ObjectSchema complexType;
    private boolean isArray;
    
    public Field(String name) {
        this.name = name;
    }
    
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

public class SchemaParser {
    private String input;
    private int index = 0;
    
    public ObjectSchema parse(String input) {
        this.input = input;
        this.index = 0;
        return parseObject();
    }
    
    private ObjectSchema parseObject() {
        String name = parseUntil(':', '{');
        if (peek() == ':') {
            expect(':');
            expect('{');
        }
        
        ObjectSchema schema = new ObjectSchema(name);
        while (peek() != '}' && index < input.length()) {
            schema.addField(parseField());
            if (peek() == ',') {
                index++;
            }
        }
        if (peek() == '}') {
            index++;
        }
        return schema;
    }
    
    private Field parseField() {
        String name = parseUntil(':', '[');
        if (peek() == ':') {
            index++;
        }
        
        Field field = new Field(name);
        char next = peek();
        
        if (next == '[') {
            index++;
            parseArrayType(field);
        } else if (next == '{') {
            ObjectSchema schema = parseObject();
            field.setComplexType(schema);
            field.setArray(false);
        } else {
            String typeName = parseUntil(',', '}');
            field.setBasicType(typeName);
            field.setArray(false);
        }
        return field;
    }
    
    private void parseArrayType(Field field) {
        char next = peek();
        if (next == '{') {
            // 对象数组
            ObjectSchema schema = parseObject();
            field.setComplexType(schema);
            field.setArray(true);
        } else if (next == '[') {
            // 嵌套数组
            parseArrayType(field);
        } else {
            // 基本类型数组
            String typeName = parseUntil(']', ',');
            field.setBasicType(typeName);
            field.setArray(true);
        }
        
        if (peek() == ']') {
            index++;
        }
        
        // 处理可能的逗号（多个类型）
        if (peek() == ',') {
            index++;
        }
    }
    
    private String parseUntil(char... delimiters) {
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
        return input.substring(start);
    }
    
    private char peek() {
        return index < input.length() ? input.charAt(index) : '\0';
    }
    
    private void expect(char expected) {
        if (index >= input.length() || input.charAt(index) != expected) {
            throw new RuntimeException("Expected '" + expected + "' at position " + index);
        }
        index++;
    }
}

class Main {
    public static void main(String[] args) {
        // 测试嵌套对象
        String input1 = "amout:{p1:string,p2:integer,p3:{p4:string,p5:long}}";
        // 测试数组类型
        String input2 = "amout:{p1:[string],p2:integer,p3:[{p4:string,p5:long}]}";
        
        SchemaParser parser = new SchemaParser();
        
        System.out.println("===== 解析嵌套对象 =====");
        ObjectSchema schema1 = parser.parse(input1);
        printSchema(schema1, 0);
        
        System.out.println("\n===== 解析数组类型 =====");
        ObjectSchema schema2 = parser.parse(input2);
        printSchema(schema2, 0);
    }
    
    private static void printSchema(ObjectSchema schema, int indent) {
        String indentStr = "  ".repeat(indent);
        System.out.println(indentStr + "Object: " + 
                          (schema.getName() != null ? schema.getName() : "<anonymous>"));
        
        for (Field field : schema.getFields()) {
            System.out.print(indentStr + "- Field: " + field.getName() + ", Type: ");
            
            if (field.getBasicType() != null) {
                System.out.print(field.getBasicType());
            } else {
                System.out.print("<object>");
            }
            
            if (field.isArray()) {
                System.out.print("[]");
            }
            
            System.out.println();
            
            if (field.getComplexType() != null) {
                printSchema(field.getComplexType(), indent + 1);
            }
        }
    }
}