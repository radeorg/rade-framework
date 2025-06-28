package org.dows.rade.tree;

import lombok.Data;

import java.util.List;

/**
 * 树节点，用于构建树形结构
 *
 * @param <T> 实体类型
 */
@Data
public class TreeNode<T> {
    /**
     * 节点数据
     */
    private T data;

    /**
     * 子节点列表
     */
    private List<TreeNode<T>> children;

    /**
     * 是否是叶子节点
     */
    public boolean isLeaf() {
        return children == null || children.isEmpty();
    }
}
