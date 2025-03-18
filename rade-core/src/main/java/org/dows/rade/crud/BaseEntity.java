package org.dows.rade.crud;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.core.activerecord.Model;
import com.mybatisflex.core.query.QueryWrapper;
import com.tangzc.mybatisflex.autotable.annotation.ColumnDefine;
import lombok.Getter;
import lombok.Setter;
import org.dromara.autotable.annotation.Ignore;

import java.io.Serializable;
import java.util.Date;

/**
 * 基础实体类
 */
@Getter
@Setter
public abstract class BaseEntity<T extends Model<T>> extends Model<T> implements Serializable {

    /*@Column(onInsertValue = "now()")
    @ColumnDefine(comment = "创建时间")
    protected Date createTime;

    @Column(onInsertValue = "now()", onUpdateValue = "now()")
    @ColumnDefine(comment = "更新时间")
    protected Date updateTime;*/

    public Long getId(){
        throw new UnsupportedOperationException("Not supported yet.");
    }


    @Column(onInsertValue = "now()")
    @ColumnDefine(comment = "创建时间")
    protected Date ct;

    @Ignore
    @Column(ignore = true)
    @JsonIgnore
    private QueryWrapper queryWrapper;
}