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
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基础实体类
 */
//@Getter
//@Setter
public abstract class BaseEntity<T extends Model<T>> extends Model<T> implements Serializable {

    @Ignore
    @Column(ignore = true)
    @JsonIgnore
    private static Map<Class<?>, Field> tableIdFields = new ConcurrentHashMap<>();

    @Ignore
    @Column(ignore = true)
    @JsonIgnore
    @Getter
    @Setter
    private QueryWrapper queryWrapper;

    /*@Column(onInsertValue = "now()")
    @ColumnDefine(comment = "创建时间")
    protected Date createTime;
    @Column(onInsertValue = "now()", onUpdateValue = "now()")
    @ColumnDefine(comment = "更新时间")
    protected Date updateTime;
    @Column(onInsertValue = "now()")
    @ColumnDefine(comment = "创建时间")
    protected Date ct;*/

    public Long getId() {
        try {
            Field idField = tableIdFields.get(this.getClass());
            if (idField == null) {
                String entityName = this.getClass().getSimpleName().replace("Entity", "Id");
                entityName = entityName.substring(0, 1).toLowerCase() + entityName.substring(1);
                Field field = this.getClass().getField(entityName);
                tableIdFields.put(this.getClass(), field);
            }
            return (Long) idField.get(this);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        //throw new UnsupportedOperationException("Not supported yet.");
    }




}