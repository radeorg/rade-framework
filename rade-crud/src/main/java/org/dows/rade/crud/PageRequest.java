package org.dows.rade.crud;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class PageRequest {

    @Min(value = 1, message = "页码必须大于0")
    @Schema(description = "页码", example = "1")
    private Integer pageNum = 1;

    @Min(value = 1, message = "每页大小必须大于0")
    @Max(value = 100, message = "每页大小不能超过100")
    @Schema(description = "每页大小", example = "10")
    private Integer pageSize = 10;


    @Schema(description = "排序字段", example = "startTime:DESC,state:ASC")
    private String orderBys;

    @JsonIgnore
    public String getOrderBys() {
        return OrderByBuilder.build(this.orderBys, this.getClass());
    }
    @JsonIgnore
    public Integer getOffset() {
        return (this.pageNum - 1) * this.pageSize;
    }
}
