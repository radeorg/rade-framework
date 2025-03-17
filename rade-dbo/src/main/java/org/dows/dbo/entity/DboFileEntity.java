package org.dows.dbo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;
import org.dows.rade.core.crud.BaseEntity;

import java.util.*;

/**
 * 文件
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
@Schema(name = "DboFileEntity", title = "文件")
public class DboFileEntity extends BaseEntity<DboFileEntity> {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "主键")
    private Long dboFileId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "父ID")
    private Long pid;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "文件名(含后缀,如:.java)")
    private String fileName;

    @Schema(title = "路径")
    private String path;

    @Schema(title = "磁盘盘符[windows:c:")
    private String drive;

    @Schema(title = "文件类型[0:目录,1:文件]")
    private Integer fileType;

    @Schema(title = "版本号")
    private Integer ver;

    @Schema(title = "时间戳")
    private Date dt;

}