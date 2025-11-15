package org.dows.rade.crud;

import cn.hutool.core.util.TypeUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryCondition;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.update.UpdateChain;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.dows.rade.util.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
//public abstract class CrudRepository<E extends CrudEntity<E>, M extends CrudMapper<E>, S extends CrudDaoImpl<M, E>> {
public abstract class CrudRepository<D extends CrudDao<E>, E extends CrudEntity<E>> {

    @Autowired
    private D dao;
    private Class<E> entityClass;
    ;

    @PostConstruct
    public void init() {
        //  获取第1个泛型参数
        Type type = TypeUtil.getTypeArgument(this.getClass(), 0);
        if (type instanceof Class<?>) {
            entityClass = (Class<E>) type;
        }
    }

    public boolean save(Object object) {
        E obj = BeanUtil.copy(object, entityClass);
        return dao.save(obj);
    }


    public boolean saveBatch(Collection<?> entities) {
        List<E> ts = BeanUtil.copyCollection(entities, entityClass);
        return dao.saveBatch(ts);
    }


    public boolean saveBatch(Collection<?> entities, int batchSize) {
        List<E> ts = BeanUtil.copyCollection(entities, entityClass);
        return dao.saveBatch(ts, batchSize);
    }


    public boolean saveOrUpdate(Object object) {
        E obj = BeanUtil.copy(object, entityClass);
        return dao.saveOrUpdate(obj);
    }


    public boolean saveOrUpdateBatch(Collection<E> entities) {
        List<E> ts = BeanUtil.copyCollection(entities, entityClass);
        return dao.saveOrUpdateBatch(ts);
    }


    public boolean saveOrUpdateBatch(Collection<E> entities, int batchSize) {
        List<E> ts = BeanUtil.copyCollection(entities, entityClass);
        return dao.saveOrUpdateBatch(ts, batchSize);
    }


    public boolean remove(QueryWrapper query) {
        return dao.remove(query);
    }


    public boolean remove(QueryCondition condition) {
        return dao.remove(condition);
    }


    public boolean removeById(Object object) {
        E entity = BeanUtil.copy(object, entityClass);
        return dao.removeById(entity);
    }


    public boolean removeById(Serializable id) {
        return dao.removeById(id);
    }


    public boolean removeByIds(Collection<? extends Serializable> ids) {
        return dao.removeByIds(ids);
    }


    public boolean removeByMap(Map<String, Object> query) {
        return dao.removeByMap(query);
    }


    public boolean updateById(E object) {
        E entity = BeanUtil.copy(object, entityClass);
        return dao.updateById(entity);
    }


    public boolean updateById(Object object, boolean ignoreNulls) {
        E entity = BeanUtil.copy(object, entityClass);
        return dao.updateById(entity, ignoreNulls);
    }


    public boolean update(Object object, Map<String, Object> query) {
        E entity = BeanUtil.copy(object, entityClass);
        return dao.update(entity, query);
    }


    public boolean update(Object object, QueryWrapper query) {
        E entity = BeanUtil.copy(object, entityClass);
        return dao.update(entity, query);
    }


    public boolean update(Object object, QueryCondition condition) {
        E entity = BeanUtil.copy(object, entityClass);
        return dao.update(entity, condition);
    }


    public boolean updateBatch(Collection<?> entities) {
        List<E> ts = BeanUtil.copyCollection(entities, entityClass);
        return dao.updateBatch(ts);
    }


    public boolean updateBatch(Collection<?> entities, boolean ignoreNulls) {
        List<E> ts = BeanUtil.copyCollection(entities, entityClass);
        return dao.updateBatch(ts, ignoreNulls);
    }


    public boolean updateBatch(Collection<?> entities, int batchSize) {
        List<E> ts = BeanUtil.copyCollection(entities, entityClass);
        return dao.updateBatch(ts, batchSize);
    }


    public boolean updateBatch(Collection<?> entities, int batchSize, boolean ignoreNulls) {
        List<E> ts = BeanUtil.copyCollection(entities, entityClass);
        return dao.updateBatch(ts, batchSize, ignoreNulls);
    }


    public E getById(Serializable id) {
        return dao.getById(id);
    }


    public E getOneByEntityId(Object object) {
        E entity = BeanUtil.copy(object, entityClass);
        return dao.getOneByEntityId(entity);
    }


    public Optional<E> getByEntityIdOpt(Object object) {
        E entity = BeanUtil.copy(object, entityClass);
        return dao.getByEntityIdOpt(entity);
    }


    public Optional<E> getByIdOpt(Serializable id) {
        return dao.getByIdOpt(id);
    }


    public E getOne(QueryWrapper query) {
        return dao.getOne(query);
    }


    public Optional<E> getOneOpt(QueryWrapper query) {
        return dao.getOneOpt(query);
    }


    public <R> R getOneAs(QueryWrapper query, Class<R> asEype) {
        return dao.getOneAs(query, asEype);
    }


    public <R> Optional<R> getOneAsOpt(QueryWrapper query, Class<R> asEype) {
        return dao.getOneAsOpt(query, asEype);
    }


    public E getOne(QueryCondition condition) {
        return dao.getOne(condition);
    }


    public Optional<E> getOneOpt(QueryCondition condition) {
        return dao.getOneOpt(condition);
    }


    public Object getObj(QueryWrapper query) {
        return dao.getObj(query);
    }


    public Optional<Object> getObjOpt(QueryWrapper query) {
        return dao.getObjOpt(query);
    }


    public <R> R getObjAs(QueryWrapper query, Class<R> asEype) {
        return dao.getObjAs(query, asEype);
    }


    public <R> Optional<R> getObjAsOpt(QueryWrapper query, Class<R> asEype) {
        return dao.getObjAsOpt(query, asEype);
    }


    public List<Object> objList(QueryWrapper query) {
        return dao.objList(query);
    }


    public <R> List<R> objListAs(QueryWrapper query, Class<R> asEype) {
        return dao.objListAs(query, asEype);
    }


    public List<E> list() {
        return dao.list();
    }


    public List<E> list(QueryWrapper query) {
        return dao.list(query);
    }


    public List<E> list(QueryCondition condition) {
        return dao.list(condition);
    }


    public <R> List<R> listAs(QueryWrapper query, Class<R> asEype) {
        return dao.listAs(query, asEype);
    }


    public List<E> listByIds(Collection<? extends Serializable> ids) {
        return dao.listByIds(ids);
    }


    public List<E> listByMap(Map<String, Object> query) {
        return dao.listByMap(query);
    }


    public boolean exists(QueryWrapper query) {
        return dao.exists(query);
    }


    public boolean exists(QueryCondition condition) {
        return dao.exists(condition);
    }


    public long count() {
        return dao.count();
    }


    public long count(QueryWrapper query) {
        return dao.count(query);
    }


    public long count(QueryCondition condition) {
        return dao.count(condition);
    }


    public Page<E> page(Page<?> page) {

        Page<E> p = new Page<>(page.getPageSize(), page.getPageNumber());
        return dao.page(p);
    }


    public Page<E> page(Page<?> page, QueryWrapper query) {
        Page<E> p = new Page<>(page.getPageSize(), page.getPageNumber());
        return dao.page(p, query);
    }


    public Page<E> page(Page<?> page, QueryCondition condition) {
        Page<E> p = new Page<>(page.getPageSize(), page.getPageNumber());
        return dao.page(p, condition);
    }


    public <R> Page<R> pageAs(Page<R> page, QueryWrapper query, Class<R> asEype) {
        return dao.pageAs(page, query, asEype);
    }


    public QueryWrapper query() {
        return dao.query();
    }


    public QueryChain<E> queryChain() {
        return dao.queryChain();
    }


    public UpdateChain<E> updateChain() {
        return dao.updateChain();
    }
}
