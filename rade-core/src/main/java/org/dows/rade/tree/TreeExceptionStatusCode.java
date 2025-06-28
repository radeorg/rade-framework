
package org.dows.rade.tree;

import lombok.Getter;
import org.dows.rade.status.StatusCode;

@Getter
public enum TreeExceptionStatusCode implements StatusCode {
    TREE_PARENT_NOT_FOUND("TREE000001", "父节点不存在"),
    TREE_NOT_FOUND("TREE000002", "实体不存在"),
    TREE_NAME_HAS_EXIST("TREE000003", "名称已存在"),
    TREE_CODE_HAS_EXIST("TREE000004", "CODE已存在"),
    TREE_REPEAT_OPERATE("TREE000005", "不能重复操作");

    private final String code;
    private final String describe;

    TreeExceptionStatusCode(String code, String describe) {
        this.code = code;
        this.describe = describe;
    }
}
