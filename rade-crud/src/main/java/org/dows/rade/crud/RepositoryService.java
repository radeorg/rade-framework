package org.dows.rade.crud;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryCondition;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class RepositoryService<T, M extends BaseMapper<T>, S extends ServiceImpl<M, T>> {

    private S service;
    private Class<T> entityClass;
    ;

    public boolean save(Object object) {
        T obj = entityClass.cast(object);
        return service.save(obj);
    }


    public boolean saveBatch(Collection<Object> entities) {
        return false;
        //return service.saveBatch();
    }


    public boolean saveBatch(Collection<T> entities, int batchSize) {
        return service.saveBatch(entities, batchSize);
    }


    public boolean saveOrUpdate(T entity) {
        return service.saveOrUpdate(entity);
    }


    public boolean saveOrUpdateBatch(Collection<T> entities) {
        return service.saveOrUpdateBatch(entities);
    }


    public boolean saveOrUpdateBatch(Collection<T> entities, int batchSize) {
        return service.saveOrUpdateBatch(entities, batchSize);
    }


    public boolean remove(QueryWrapper query) {
        return service.remove(query);
    }


    public boolean remove(QueryCondition condition) {
        return service.remove(condition);
    }


    public boolean removeById(T entity) {
        return service.removeById(entity);
    }


    public boolean removeById(Serializable id) {
        return service.removeById(id);
    }


    public boolean removeByIds(Collection<? extends Serializable> ids) {
        return service.removeByIds(ids);
    }


    public boolean removeByMap(Map<String, Object> query) {
        return service.removeByMap(query);
    }


    public boolean updateById(T entity) {
        return service.updateById(entity);
    }


    public boolean updateById(T entity, boolean ignoreNulls) {
        return service.updateById(entity, ignoreNulls);
    }


    public boolean update(T entity, Map<String, Object> query) {
        return service.update(entity, query);
    }


    public boolean update(T entity, QueryWrapper query) {
        return service.update(entity, query);
    }


    public boolean update(T entity, QueryCondition condition) {
        return service.update(entity, condition);
    }


    public boolean updateBatch(Collection<T> entities) {
        return service.updateBatch(entities);
    }


    public boolean updateBatch(Collection<T> entities, boolean ignoreNulls) {
        return service.updateBatch(entities, ignoreNulls);
    }


    public boolean updateBatch(Collection<T> entities, int batchSize) {
        return service.updateBatch(entities, batchSize);
    }


    public boolean updateBatch(Collection<T> entities, int batchSize, boolean ignoreNulls) {
        return service.updateBatch(entities, batchSize, ignoreNulls);
    }


    public T getById(Serializable id) {
        return service.getById(id);
    }


    public T getOneByEntityId(T entity) {
        return service.getOneByEntityId(entity);
    }


    public Optional<T> getByEntityIdOpt(T entity) {
        return service.getByEntityIdOpt(entity);
    }


    public Optional<T> getByIdOpt(Serializable id) {
        return service.getByIdOpt(id);
    }


    public T getOne(QueryWrapper query) {
        return service.getOne(query);
    }


    public Optional<T> getOneOpt(QueryWrapper query) {
        return service.getOneOpt(query);
    }


    public <R> R getOneAs(QueryWrapper query, Class<R> asType) {
        return service.getOneAs(query, asType);
    }


    public <R> Optional<R> getOneAsOpt(QueryWrapper query, Class<R> asType) {
        return service.getOneAsOpt(query, asType);
    }


    public T getOne(QueryCondition condition) {
        return service.getOne(condition);
    }


    public Optional<T> getOneOpt(QueryCondition condition) {
        return service.getOneOpt(condition);
    }


    public Object getObj(QueryWrapper query) {
        return service.getObj(query);
    }


    public Optional<Object> getObjOpt(QueryWrapper query) {
        return service.getObjOpt(query);
    }


    public <R> R getObjAs(QueryWrapper query, Class<R> asType) {
        return service.getObjAs(query, asType);
    }


    public <R> Optional<R> getObjAsOpt(QueryWrapper query, Class<R> asType) {
        return service.getObjAsOpt(query, asType);
    }


    public List<Object> objList(QueryWrapper query) {
        return service.objList(query);
    }


    public <R> List<R> objListAs(QueryWrapper query, Class<R> asType) {
        return service.objListAs(query, asType);
    }


    public List<T> list() {
        return service.list();
    }


    public List<T> list(QueryWrapper query) {
        return service.list(query);
    }


    public List<T> list(QueryCondition condition) {
        return service.list(condition);
    }


    public <R> List<R> listAs(QueryWrapper query, Class<R> asType) {
        return service.listAs(query, asType);
    }


    public List<T> listByIds(Collection<? extends Serializable> ids) {
        return service.listByIds(ids);
    }


    public List<T> listByMap(Map<String, Object> query) {
        return service.listByMap(query);
    }


    public boolean exists(QueryWrapper query) {
        return service.exists(query);
    }


    public boolean exists(QueryCondition condition) {
        return service.exists(condition);
    }


    public long count() {
        return service.count();
    }


    public long count(QueryWrapper query) {
        return service.count(query);
    }


    public long count(QueryCondition condition) {
        return service.count(condition);
    }


    public Page<T> page(Page<T> page) {
        return service.page(page);
    }


    public Page<T> page(Page<T> page, QueryWrapper query) {
        return service.page(page, query);
    }


    public Page<T> page(Page<T> page, QueryCondition condition) {
        return service.page(page, condition);
    }


    public <R> Page<R> pageAs(Page<R> page, QueryWrapper query, Class<R> asType) {
        return service.pageAs(page, query, asType);
    }


    public QueryWrapper query() {
        return service.query();
    }


    public QueryChain<T> queryChain() {
        return service.queryChain();
    }


    public UpdateChain<T> updateChain() {
        return service.updateChain();
    }
}
