package org.dows.rade;

public class MainClass {
    public static void main(String[] args) {
        // 测试嵌套对象
        String input1 = "amout:{p1:string,p2:integer,p3:{p4:string,p5:long}}";
        // 测试数组类型
        String input2 = "amout:{p1:[string],p2:integer,p3:[p4:string,p5:long]}";
        
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