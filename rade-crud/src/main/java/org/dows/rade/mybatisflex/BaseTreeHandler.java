package org.dows.rade.mybatisflex;

import com.mybatisflex.core.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.dows.rade.crud.BaseService;
import org.dows.rade.crud.BaseTreeEntity;
import org.dows.rade.tree.TreeException;
import org.dows.rade.tree.TreeExceptionStatusCode;
import org.dows.rade.tree.TreeNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 通用树状结构处理基类
 * 用于处理继承自BaseTreeEntity的实体类的增删改查操作
 *
 * @param <T> 实体类型，必须继承自BaseTreeEntity
 * @param <S> 实体服务类型，必须实现IService接口
 */
@Slf4j
public class BaseTreeHandler<T extends BaseTreeEntity<?>, S extends BaseService<T>> {

    /**
     * 实体服务类
     */
    protected final S baseService;

    /**
     * 构造函数
     *
     * @param baseService 实体服务类实例
     */
    public BaseTreeHandler(S baseService) {
        this.baseService = baseService;
    }

    /**
     * 保存实体，去重检查、自动处理路径信息
     *
     * @param entity 实体
     * @return 是否保存成功
     */
    public Long createNode(T entity) {
        // 去重检查
        isDuplicate(entity.getName(), entity.getCode());

        baseService.save(entity);

        // 如果是根节点
        if (entity.getPid() == null || entity.getPid() == 0) {
            setRootPath(entity);
        } else {
            // 查询父类并检查是否存在
            T parent = getByParentIdAndCheckIsExist(entity.getPid());

            // 设置路径信息
            setChildPath(entity, parent);
        }

        return entity.getId();
    }

    /**
     * 更新实体，去重检查、自动处理路径信息
     *
     * @param entity 实体
     * @return 是否更新成功
     */
    public boolean updateNode(T entity) {
        // 查询实体并检查是否存在
        T oldEntity = getByNodeIdAndCheckIsExist(entity.getId());

        // 检查是否重复
        isDuplicate(entity.getId(), entity.getName(), entity.getCode());

        // 如果父节点或当前节点code发生变化，当前节点及子节点需要更新路径信息
        boolean isPathChanged = !Objects.equals(oldEntity.getPid(), entity.getPid())
                || !Objects.equals(oldEntity.getCode(), entity.getCode());
        if (isPathChanged) {
            updatePath(entity);
        }

        return baseService.updateById(entity);
    }

    /**
     * 移动节点及子节点
     *
     * @param nodeId     要移动的节点ID
     * @param newParentId 新的父节点ID
     * @return 是否移动成功
     */
    public boolean moveNode(Long nodeId, Long newParentId) {
        // 查询实体并检查是否存在
        T entity = getByNodeIdAndCheckIsExist(nodeId);

        entity.setPid(newParentId);
        return updateNode(entity);
    }

    /**
     * 删除当前节点及其所有子孙节点
     *
     * @param nodeId 节点ID
     * @return 是否删除成功
     */
    public boolean deleteNodeWithAllDescendants(Long nodeId) {
        // 查询实体并检查是否存在
        T entity = getByNodeIdAndCheckIsExist(nodeId);

        // 查询所有子孙节点
        List<T> children = getAllDescendantsNode(nodeId);
        
        // 删除所有子孙节点
        if (!children.isEmpty()) {
            List<Long> childIds = children.stream()
                    .map(BaseTreeEntity::getId)
                    .collect(Collectors.toList());
            baseService.removeByIds(childIds);
        }

        // 删除当前节点
        return baseService.removeById(nodeId);
    }

    /**
     * 根据节点ID查询节点
     *
     * @param nodeId 节点ID
     * @return 节点实体
     */
    public T getNodeById(Long nodeId) {
        return baseService.getById(nodeId);
    }

    /**
     * 根据节点ID查询节点并检查是否存在
     * @param nodeId 节点ID
     * @return 节点实体
     */
    public T getByNodeIdAndCheckIsExist(Long nodeId) {
        T entity = getNodeById(nodeId);
        if (Objects.isNull(entity)) {
            throw new TreeException(TreeExceptionStatusCode.TREE_NOT_FOUND);
        }
        return entity;
    }

    /**
     * 获取子节点
     *
     * @param parentId 父ID
     * @return 子节点列表
     */
    public List<T> getChildrenNode(Long parentId) {
        QueryWrapper query = QueryWrapper.create().eq("pid", parentId).orderBy("seq");
        return baseService.list(query);
    }

    /**
     * 获取所有子孙节点
     *
     * @param parentId 父ID
     * @return 获取所有子孙节点
     */
    public List<T> getAllDescendantsNode(Long parentId) {
        List<T> result = new ArrayList<>();
        
        // 查询直接子节点
        List<T> children = getChildrenNode(parentId);
        if (children.isEmpty()) {
            return result;
        }

        result.addAll(children);

        // 递归查询每个子节点的子节点
        for (T child : children) {
            result.addAll(getAllDescendantsNode(child.getId()));
        }

        return result;
    }

    /**
     * 获取节点的路径（从根节点到当前节点）
     *
     * @param nodeId 节点ID
     * @return 路径节点列表，从根节点到当前节点
     */
    public List<T> getNodePath(Long nodeId) {
        // 查询实体并检查是否存在
        T node = getByNodeIdAndCheckIsExist(nodeId);

        List<T> path = new ArrayList<>();
        path.add(node);

        // 如果不是根节点，递归获取父节点
        if (node.getPid() != null && node.getPid() != 0) {
            path.addAll(0, getNodePath(node.getPid()));
        }

        return path;
    }

    /**
     * 获取树形结构
     *
     * @param parentId 父节点ID，传入0表示从根节点开始
     * @return 树形结构
     */
    public List<TreeNode<T>> getTree(Long parentId) {
        // 查询子节点
        List<T> children = getChildrenNode(parentId);
        if (children.isEmpty()) {
            return new ArrayList<>();
        }

        // 构建树形结构
        return children.stream().map(child -> {
            TreeNode<T> node = new TreeNode<>();
            node.setData(child);
            node.setChildren(getTree(child.getId()));
            return node;
        }).collect(Collectors.toList());
    }

    /**
     * 根据级别查询节点
     *
     * @param level 级别
     * @return 节点列表
     */
    public List<T> getNodesByLevel(Integer level) {
        return baseService.list(QueryWrapper.create().eq("level", level));
    }

    /**
     * 根据条件查询节点
     *
     * @param queryWrapper 查询条件
     * @return 节点列表
     */
    public List<T> queryNodes(QueryWrapper queryWrapper) {
        return baseService.list(queryWrapper);
    }

    /**
     * 获取树的最大深度
     *
     * @return 最大深度
     */
    public Integer getMaxDepth() {
        QueryWrapper query = QueryWrapper.create();
        query.select("MAX(level) as max_level");
        T result = baseService.getOne(query);
        return result != null ? result.getLevel() : 0;
    }

    /**
     * 获取树的节点总数
     *
     * @return 节点总数
     */
    public Long getNodeCount() {
        return baseService.count();
    }

    /**
     * 更新子节点的路径信息
     *
     * @param parent 父节点
     */
    private void updateChildrenPaths(T parent) {
        List<T> children = getChildrenNode(parent.getId());
        if (children.isEmpty()) {
            return;
        }

        for (T child : children) {
            // 更新子节点的路径信息
            setChildPath(child, parent);
            baseService.updateById(child);

            // 递归更新子节点的子节点
            updateChildrenPaths(child);
        }
    }

    /**
     * 父节点发生变化，需要更新路径信息
     * @param entity 实体
     */
    private void updatePath(T entity){
        // 如果是根节点
        if (entity.getPid() == null || entity.getPid() == 0) {
            setRootPath(entity);
        } else {
            // 获取新的父节点
            T parent = getByParentIdAndCheckIsExist(entity.getPid());

            // 设置新的路径信息
            setChildPath(entity, parent);
        }

        // 更新所有子节点的路径信息
        updateChildrenPaths(entity);
    }

    /**
     * 设置顶级节点路径
     * @param entity 实体
     */
    private void setRootPath(T entity){
        entity.setIdPath("/0/" + entity.getId());
        entity.setPath("/" + entity.getCode());
        entity.setLevel(1);
        baseService.updateById(entity);
    }

    /**
     * 设置子节点路径
     * @param child 子节点实体
     * @param parent 父节点实体
     */
    private void setChildPath(T child, T parent){
        child.setIdPath(parent.getIdPath() + "/" + child.getId());
        child.setPath(parent.getPath() + "/" + child.getCode());
        child.setLevel(parent.getLevel() + 1);
        baseService.updateById(child);
    }

    private void isDuplicate(String name, String code) {
        if (getByName(name) != null) {
            throw new TreeException(TreeExceptionStatusCode.TREE_NAME_HAS_EXIST);
        }
        if (getByCode(code) != null) {
            throw new TreeException(TreeExceptionStatusCode.TREE_CODE_HAS_EXIST);
        }
    }

    private void isDuplicate(Long id, String name, String code) {
        T nameEntity = getByName(name);
        if (Objects.nonNull(nameEntity) && !nameEntity.getId().equals(id)) {
            throw new TreeException(TreeExceptionStatusCode.TREE_NAME_HAS_EXIST);
        }
        T codeEntity = getByCode(code);
        if (Objects.nonNull(codeEntity) && !codeEntity.getId().equals(id)) {
            throw new TreeException(TreeExceptionStatusCode.TREE_CODE_HAS_EXIST);
        }
    }

    private T getByParentIdAndCheckIsExist(Long pid) {
        T entity = getNodeById(pid);
        if (Objects.isNull(entity)) {
            throw new TreeException(TreeExceptionStatusCode.TREE_PARENT_NOT_FOUND);
        }
        return entity;
    }

    private T getByName(String name) {
        return baseService.getOne(QueryWrapper.create()
                .eq("name", name));
    }

    private T getByCode(String code) {
        return baseService.getOne(QueryWrapper.create()
                .eq("code", code));
    }
}
