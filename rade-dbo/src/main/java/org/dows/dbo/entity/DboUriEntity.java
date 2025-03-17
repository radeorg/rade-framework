package org.dows.dbo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;
import org.dows.rade.core.crud.BaseEntity;

import java.util.*;

/**
 * 资源
 * @author lait
 * @email lait.zhang@gmail.com
 * @since 2024年12月30日 上午10:57:20
 */
@SuppressWarnings("serial")
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "DboUriEntity", title = "资源")
public class DboUriEntity extends BaseEntity<DboUriEntity> {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "主键")
    private Long dboUriId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "主键")
    private Long dboFileId;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "HTTP方法[post,put,get,delete]")
    private String httpMethod;

    @Schema(title = "资源标识")
    private String uri;

    @Schema(title = "描述")
    private String description;

    @Schema(title = "函数ID集合(逗号分割，一个uri可能由多个dml函数构成)")
    private String funcIds;

    @Schema(title = "输出对象(对表output的记录json序列化)")
    private String outputJson;

    @Schema(title = "输入对象(对表input的记录json序列化)")
    private String inputsJson;

    @Schema(title = "版本号")
    private Integer ver;

    @Schema(title = "时间戳")
    private Date dt;

}