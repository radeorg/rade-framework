package org.dows.rade;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public class NonStandardJsonToJava {

    public static void main(String[] args) {
        String nonStandardJson = "amout:{p1:string,p2:integer,p3:{p4:string,p5:long}}";
        Map<String, Object> dataModel = parseNonStandardJson(nonStandardJson);

        try {
            generateJavaClass(dataModel);
        } catch (Exception e) {
        }

        System.out.println("Generated class: Amout");
    }

    private static Map<String, Object> parseNonStandardJson(String json) {
        Map<String, Object> dataModel = new HashMap<>();
        String className = StringUtils.capitalize(StringUtils.substringBefore(json, ":").trim());
        String body = StringUtils.strip(json.substring(json.indexOf("{") + 1, json.lastIndexOf("}")), "{} \t\n\r\f");

        Map<String, Object> fields = new HashMap<>();
        String[] fieldParts = body.split(",");

        for (String part : fieldParts) {
            String fieldName = StringUtils.trim(part.split(":")[0]);
            String fieldType = StringUtils.trim(part.split(":")[1]);

            if (fieldType.contains("{")) {
                Map<String, Object> nestedDataModel = parseNonStandardJson(fieldName + ":" + fieldType);
                fields.put(fieldName, nestedDataModel);
            } else {
                fields.put(fieldName, convertFieldType(fieldType));
            }
        }

        dataModel.put("className", className);
        dataModel.put("fields", fields);
        return dataModel;
    }

    private static String convertFieldType(String fieldType) {
        switch (fieldType.toLowerCase()) {
            case "string":
                return "String";
            case "integer":
                return "int";
            case "long":
                return "long";
            default:
                return fieldType; // Assuming it's a nested class
        }
    }

    private static void generateJavaClass(Map<String, Object> dataModel) throws IOException, TemplateException {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_34);
        cfg.setDirectoryForTemplateLoading(new File("."));
        cfg.setDefaultEncoding("UTF-8");

        Template template = cfg.getTemplate("pojo.ftl");

        StringWriter out = new StringWriter();
        template.process(dataModel, out);

        String generatedClass = out.toString();
        writeClassToFile((String) dataModel.get("className"), generatedClass);
    }

    private static void writeClassToFile(String className, String content) {
        File file = new File("./" + className + ".java");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}