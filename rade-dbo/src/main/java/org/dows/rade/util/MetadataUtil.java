package org.dows.rade.util;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.dows.rade.model.Column;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MetadataUtil {

    // 定义正则表达式
    static Pattern pattern = Pattern.compile("\\[(.*?)\\]");



    public static List<Column> dslJsonToBeanObject(String dslJson) {
        List<Column> columns = new ArrayList<>();
        JSONObject metaData = JSONUtil.parseObj(dslJson);
        JSONArray params = metaData.getJSONArray("params");
        for (Object param : params) {
            JSONObject jsonObject = (JSONObject) param;
            JSONObject column = jsonObject.getJSONObject("column");
            if (column != null) {
                Column bean = column.toBean(Column.class);
                columns.add(bean);
            }
        }
        return columns;
    }


    public static void toMetadataJsonObject(JSONObject metaData, JSONObject jsonObject) {

        String attrType = metaData.getStr("type");
        if (metaData.containsKey("attrs")) {
            JSONArray attrs = metaData.getJSONArray("attrs");
            if (attrType.equals("object")) {
                for (Object attr : attrs) {
                    toMetadataJsonObject((JSONObject) attr, jsonObject);
                }
            }
            // format to  array[string] ,array[object]
            if (attrType.contains("array")) {
                // 创建matcher对象
                Matcher matcher = pattern.matcher(attrType);
                boolean b = matcher.find();
                if (!b) {
                    throw new RuntimeException(attrType + " 格式错误, array[string|integer|object]");
                }
                String type = matcher.group(1);
                JSONArray objects = new JSONArray();
                jsonObject.set(metaData.getStr("name"), objects);
                JSONObject jo = new JSONObject();
                if (type.equals("object")) {
                    objects.set(jo);
                    for (Object ja : attrs) {
                        try {
                            toMetadataJsonObject((JSONObject) ja, jo);
                        } catch (Exception e) {
                            throw new RuntimeException(e.getMessage() + "\n" + ja + " cast to JSONObject failed");
                        }
                    }
                } else {
                    objects.set(type);
                }
            }
        }
        String name = metaData.getStr("name");
        // 为所有属性设置默认值
        switch (attrType) {
            case "integer":
                jsonObject.set(name, 0);
                break;
            case "string":
                jsonObject.set(name, attrType);
                break;
            case "decimal":
                jsonObject.set(name, attrType);
                break;
            case "json":
                jsonObject.set(name, attrType);
                break;
            case "long":
                jsonObject.set(name, attrType);
                break;
        }
    }

    public static List<Parameter> toParam(JSONObject metaData) {
        List<Parameter> methodParams = new ArrayList<>();
        String attrType = metaData.getStr("type");
        if (metaData.containsKey("attrs")) {
            JSONArray attrs = metaData.getJSONArray("attrs");
            if (attrType.equals("object")) {
                for (Object attr : attrs) {
                    Parameter parameter = new Parameter();
                    parameter.setName((String) ((JSONObject) attr).get("name"));
                    if (null == ((JSONObject) attr).get("required")) {
                        parameter.setRequired(false);
                    } else {
                        parameter.setRequired((Boolean) ((JSONObject) attr).get("required"));
                    }
                    parameter.setDescription((String) ((JSONObject) attr).get("comment"));
                    methodParams.add(parameter);
                }
            }
        } else {
            Parameter parameter = new Parameter();
            parameter.setName((String) (metaData).get("name"));
            if (null == metaData.get("required")) {
                parameter.setRequired(false);
            } else {
                parameter.setRequired((Boolean) metaData.get("required"));
            }
            parameter.setDescription((String) (metaData).get("comment"));
            methodParams.add(parameter);
        }
        return methodParams;
    }



    public static Schema toSchemaWithObjectKey(JSONObject metaData, Schema schema) {
        String attrType = metaData.getStr("type");
        if (metaData.containsKey("attrs")) {
            JSONArray attrs = metaData.getJSONArray("attrs");
            if (attrType.equals("object")) {
                Schema schema1 = new Schema();
                schema1.type("object").title((String) metaData.get("comment"));
                for (Object attr : attrs) {
                    schema.addProperty(metaData.getStr("name"), toSchemaWithObjectKey((JSONObject) attr, schema1));
                }
            }
            if (attrType.contains("array")) {
                // 创建matcher对象
                Matcher matcher = pattern.matcher(attrType);
                boolean b = matcher.find();
                if (!b) {
                    throw new RuntimeException(attrType + " 格式错误, array[string|integer|object]");
                }
                String type = matcher.group(1);
                Schema objects = new Schema();
                objects.type("object").title(metaData.getStr("comment"));
                if (type.equals("object")) {
                    Schema jo = new Schema();
                    for (Object ja : attrs) {
                        try {
                            jo = toSchemaWithObjectKey((JSONObject) ja, jo);
                        } catch (Exception e) {
                            throw new RuntimeException(e.getMessage() + "\n" + ja + " cast to JSONObject failed");
                        }
                    }
                    schema.addProperty(metaData.getStr("name"), new Schema().type("array").items(jo));
                } else {
                    objects.addProperty(metaData.getStr("name"), new Schema().type(type).title(metaData.getStr("comment")));
                    schema.addProperty(metaData.getStr("name"), new Schema().type("array").items(objects));
                }
            }
        }
        String name = metaData.getStr("name");
        // 为所有属性设置默认值
        switch (attrType) {
            case "long":
            case "decimal":
            case "integer":
                schema.addProperty(name, new Schema().type("integer").title((String) metaData.get("comment")));
                break;
            case "datetime":
            case "date":
            case "string":
                schema.addProperty(name, new Schema().type("string").title((String) metaData.get("comment")));
                break;
            case "object":
            case "json":
                schema.addProperty(name, new Schema().type("object").title((String)metaData.get("comment")));
                break;
            default:
                schema.addProperty(name, new Schema().type("string").title((String)metaData.get("comment")));
        }
        return schema;
    }

    public static Schema toSchema(JSONObject metaData, Schema schema) {
        String attrType = metaData.getStr("type");
        if (metaData.containsKey("attrs")) {
            JSONArray attrs = metaData.getJSONArray("attrs");
            if (attrType.equals("object")) {
                for (Object attr : attrs) {
                    schema = toSchemaWithObjectKey((JSONObject) attr, schema);
                }
            }
        }
        return schema;
    }
}

