package org.dows.rade.tree;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import lombok.Data;

import java.time.LocalDateTime;


@Data
public class DemoTree {

    @Id(keyType = KeyType.Auto)
    private Long DemoTreeId;

    private Long parentId;


    private Integer level;

    private String name;

    private String path;

    private LocalDateTime createTime;
    private LocalDateTime deleteTime;


}
