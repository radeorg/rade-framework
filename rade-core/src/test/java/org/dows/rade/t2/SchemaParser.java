package org.dows.rade.t2;

import java.util.*;

class SchemaNode {
    private final String name;
    private final Map<String, Field> fields = new LinkedHashMap<>();
    
    public SchemaNode(String name) {
        this.name = name;
    }
    
    public String getName() { return name; }
    public Map<String, Field> getFields() { return fields; }
    
    public void addField(String name, Field field) {
        fields.put(name, field);
    }
}

class Field {
    private final String name;
    private String primitiveType;
    private SchemaNode complexType;
    private boolean isArray;
    
    public Field(String name) {
        this.name = name;
    }
    
    public String getName() { return name; }
    public String getPrimitiveType() { return primitiveType; }
    public SchemaNode getComplexType() { return complexType; }
    public boolean isArray() { return isArray; }
    public boolean isPrimitive() { return primitiveType != null; }
    
    public void setPrimitiveType(String type) {
        this.primitiveType = type;
    }
    
    public void setComplexType(SchemaNode node) {
        this.complexType = node;
    }
    
    public void setArray(boolean array) {
        this.isArray = array;
    }
}

public class SchemaParser {
    public SchemaNode parse(String schemaStr) {
        // 提取主对象名和内容
        String[] parts = schemaStr.split(":", 2);
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid schema format");
        }
        
        String objectName = parts[0].trim();
        String content = parts[1].trim();
        
        // 确保内容用花括号包裹
        if (!content.startsWith("{") || !content.endsWith("}")) {
            throw new IllegalArgumentException("Schema content must be wrapped in curly braces");
        }
        
        content = content.substring(1, content.length() - 1);
        SchemaNode root = new SchemaNode(capitalize(objectName));
        parseFields(content, root);
        return root;
    }
    
    private void parseFields(String content, SchemaNode node) {
        // 使用栈来处理嵌套结构
        Stack<Character> stack = new Stack<>();
        StringBuilder current = new StringBuilder();
        List<String> tokens = new ArrayList<>();
        
        // 分割字段，同时处理嵌套结构
        for (char c : content.toCharArray()) {
            if (c == '{' || c == '[') {
                stack.push(c);
            } else if (c == '}' || c == ']') {
                if (stack.isEmpty() || (c == '}' && stack.peek() != '{') || (c == ']' && stack.peek() != '[')) {
                    throw new IllegalArgumentException("Mismatched brackets");
                }
                stack.pop();
            }
            
            if (c == ',' && stack.isEmpty()) {
                tokens.add(current.toString().trim());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        
        // 添加最后一个token
        if (!current.toString().trim().isEmpty()) {
            tokens.add(current.toString().trim());
        }
        
        // 解析每个字段
        for (String token : tokens) {
            parseField(token, node);
        }
    }
    
    private void parseField(String token, SchemaNode node) {
        // 分割字段名和类型定义
        int colonIndex = token.indexOf(':');
        if (colonIndex == -1) {
            throw new IllegalArgumentException("Invalid field format: " + token);
        }
        
        String fieldName = token.substring(0, colonIndex).trim();
        String typeDef = token.substring(colonIndex + 1).trim();
        
        Field field = new Field(fieldName);
        
        // 处理数组类型
        if (typeDef.startsWith("[")) {
            field.setArray(true);
            typeDef = typeDef.substring(1, typeDef.length() - 1).trim(); // 移除方括号
        }
        
        // 处理嵌套对象
        if (typeDef.startsWith("{")) {
            if (!typeDef.endsWith("}")) {
                throw new IllegalArgumentException("Mismatched curly braces for field: " + fieldName);
            }
            
            String nestedContent = typeDef.substring(1, typeDef.length() - 1).trim();
            SchemaNode nestedNode = new SchemaNode(capitalize(fieldName));
            parseFields(nestedContent, nestedNode);
            field.setComplexType(nestedNode);
        } 
        // 处理基本类型
        else {
            field.setPrimitiveType(mapType(typeDef));
        }
        
        node.addField(fieldName, field);
    }
    
    private String mapType(String type) {
        switch (type.toLowerCase()) {
            case "string": return "String";
            case "integer": return "Integer";
            case "int": return "Integer";
            case "long": return "Long";
            case "double": return "Double";
            case "float": return "Float";
            case "bool": return "Boolean";
            case "boolean": return "Boolean";
            default: return type; // 自定义类型
        }
    }
    
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}

class JavaCodeGenerator {
    private final StringBuilder output = new StringBuilder();
    private int indentLevel = 0;
    
    public String generate(SchemaNode root) {
        output.setLength(0);
        generateClass(root);
        return output.toString();
    }
    
    private void generateClass(SchemaNode node) {
        // 生成类头
        addLine("public class " + node.getName() + " {");
        indentLevel++;
        
        // 生成字段
        for (Field field : node.getFields().values()) {
            generateField(field);
        }
        
        // 生成嵌套类
        for (Field field : node.getFields().values()) {
            if (field.getComplexType() != null) {
                generateClass(field.getComplexType());
            }
        }
        
        indentLevel--;
        addLine("}");
    }
    
    private void generateField(Field field) {
        String typeName;
        
        if (field.isPrimitive()) {
            typeName = field.getPrimitiveType();
        } else {
            typeName = field.getComplexType().getName();
        }
        
        if (field.isArray()) {
            typeName = "List<" + typeName + ">";
        }
        
        addLine("private " + typeName + " " + field.getName() + ";");
    }
    
    private void addLine(String text) {
        for (int i = 0; i < indentLevel; i++) {
            output.append("    ");
        }
        output.append(text).append("\n");
    }
}


class User {
    String dd;
    List<String> d2;
    D3 d3;
    List<D4> d4;
    class D3{
        String d31;
        Long d32;
    }

    class D4{
        String d41;
        Long d42;
    }
}
 class Main {
    public static void main(String[] args) {
        // 测试用例
        String schema1 = "class://org.dows.uim.UserRest?method=dd&in=amout:[{p1:string,p2:integer,p3:{p4:string,p5:long}}]&out=user:{d1:string,d2:[string],d3:{d31:string,d32:long},d4:[{d41:string,d42:long}]}";
        String schema2 = "http://hioas.com/uim/add?in=amout:[{p1:string,p2:integer,p3:{p4:string,p5:long}}]&out=user:{d1:string,d2:[string],d3:{d31:string,d32:long},d4:[{d41:string,d42:long}]}";
        String schema3 = "jdbc://mysql/db/tb?method=insert&in=amout:[{p1:string,p2:integer,p3:{p4:string,p5:long}}]&out=user:{d1:string,d2:[string],d3:{d31:string,d32:long},d4:[{d41:string,d42:long}]}";


        String schema4 = "amout:{pkg:,in@amount";
        String schema5 = "amout:{p1:[string],p2:integer,p3:[{p4:string,p5:long}]}";
        String schema6 = "amout:{pkg:org.dows.aa,properties:{p1:[string],p2:integer,p3:[{p4:string,p5:long}]}}";

        SchemaParser parser = new SchemaParser();
        JavaCodeGenerator generator = new JavaCodeGenerator();
        
        System.out.println("===== 嵌套对象解析 =====");
        SchemaNode node1 = parser.parse(schema1);
        System.out.println(generator.generate(node1));
        
        System.out.println("\n===== 数组对象解析 =====");
        SchemaNode node2 = parser.parse(schema2);
        System.out.println(generator.generate(node2));
    }
}