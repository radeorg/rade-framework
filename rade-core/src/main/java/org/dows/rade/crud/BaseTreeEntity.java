package org.dows.rade.crud;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.core.activerecord.Model;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 基础实体类
 */
public abstract class BaseTreeEntity<T extends Model<T>> extends BaseEntity<T> implements Serializable {

    @Getter
    @Setter
    @Column(value = "name")
    private String name;

    @Getter
    @Setter
    @Column(value = "code")
    private String code;

    @Getter
    @Setter
    @Column(value = "pid")
    private Long pid;

    @Getter
    @Setter
    @Column(value = "id_path")
    private String idPath;

    @Getter
    @Setter
    @Column(value = "path")
    private String path;

    @Getter
    @Setter
    @Column(value = "level")
    private Integer level;

    @Getter
    @Setter
    @Column(value = "seq")
    private Integer seq;
}