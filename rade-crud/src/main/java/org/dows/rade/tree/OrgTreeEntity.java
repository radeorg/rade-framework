package org.dows.rade.tree;

import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dows.rade.crud.CrudEntity;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Table("org_tree")
public class OrgTreeEntity extends CrudEntity<OrgTreeEntity> {

    @Schema(title = "组织树ID")
    private Long orgTreeId;
    @Schema(title = "父ID")
    private Long parentId;
    @Schema(title = "组织实例ID")
    private Long orgInstanceId;
    @Schema(title = "组织名")
    private String name;
    @Schema(title = "组织编码")
    private String code;
    @Schema(title = "组织头像")
    private String avatar;
    @Schema(title = "ID路径")
    private String idPath;
    @Schema(title = "名称路径")
    private String namePath;
    @Schema(title = "层级")
    private Integer level;
    @Schema(title = "组织性质[0:内部,1:外部]")
    private Integer orgProperty;
    @Schema(title = "排序")
    private Integer sorted;
    @Schema(title = "版本号")
    private Integer revision;
    @Schema(title = "应用id")
    private String appId;
    @Schema(title = "时间戳")
    private LocalDateTime createTime;
    @Schema(title = "更新时间")
    private LocalDateTime updateTime;
    @Schema(title = "删除时间")
    private LocalDateTime deleteTime;
    @Schema(title = "创建者ID")
    private Long createId;
    @Schema(title = "更新者ID")
    private Long updateId;
}