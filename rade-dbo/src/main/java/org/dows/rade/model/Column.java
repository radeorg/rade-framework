package org.dows.rade.model;

import cn.hutool.core.util.StrUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @description: </br>
 * @author: lait.zhang@gmail.com
 * @date: 12/30/2024 2:22 PM
 * @history: </br>
 * <author>      <time>      <version>    <desc>
 * 修改人姓名      修改时间        版本号       描述
 */
@Schema(name = "Column 对象", title = "Column Meta Object")
@Data
public class Column {

    @Schema(name = "alias", title = "字段别名")
    private String alias;
    @Schema(name = "length", title = "字段长度")
    private int length;

    @Schema(name = "table", title = "表名")
    private String table;
    @Schema(name = "name", title = "字段名")
    private String name;
    @Schema(name = "type", title = "字段类型[]")
    private String type;
    @Schema(name = "comment", title = "字段说明")
    private String comment;
    @Schema(name = "func", title = "字段函数[count,sum,avg,max,min,distinct]")
    private String func;
    @Schema(name = "groupBy", title = "该字段是否分组[true,false]")
    private Boolean groupBy = false;
    @Schema(name = "orderBy", title = "字段是否排序:[desc,asc]")
    private String orderBy;
    @Schema(name = "having", title = "过滤条件:当前字段函数(name)的alias [>,<,=,<=,>] value")
    private String having;
    @Schema(name = "pk", title = "是否是主键")
    private boolean pk;
    @Schema(name = "deleted", title = "是否逻辑删除")
    private Boolean deleted;
    @Schema(name = "format", title = "格式化模板['yyyy-MM-dd'|'yyyy-MM-dd HH:mm:ss']")
    private String format;


    public String getAlias(){
        if(StrUtil.isBlank(this.alias)){
            return StrUtil.toCamelCase(this.name);
        }
        return StrUtil.toCamelCase(alias);
    }
}
