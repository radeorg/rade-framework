package org.dows.rade.crud;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.update.UpdateWrapper;
import org.apache.ibatis.annotations.*;
import org.dows.rade.mybatis.QueryProvider;
import org.dows.rade.mybatis.TreeProvider;

import java.util.List;
import java.util.Map;

/**
 * @description: </br>
 * @author: lait.zhang@gmail.com
 * @date: 12/23/2024 5:29 PM
 * @history: </br>
 * <author>      <time>      <version>    <desc>
 * 修改人姓名      修改时间        版本号       描述
 */
public interface CrudMapper<T> extends BaseMapper<T> {
    /**
     * 根据pid 查询，该树下所有子节点信息
     *
     * @param clazz
     * @param pid
     * @return
     */
    @SelectProvider(type = TreeProvider.class, method = "listTreeByPid")
    List<Map<String, Object>> listTreeByPid(Class<T> clazz, Long pid);

    /**
     * 根据pid 查询，该树下所有子节点信息 并根据树的层级过滤
     *
     * @param clazz
     * @param pid
     * @return
     */
    @SelectProvider(type = TreeProvider.class, method = "listTreeByPidAndLevel")
    List<Map<String, Object>> listTreeByPidAndLevel(Class<T> clazz, Long pid, Integer level);


    /**
     * 根据ID查询，当前树节点及所有子节点信息
     *
     * @param clazz
     * @param id
     * @return
     */
    @SelectProvider(type = TreeProvider.class, method = "listTreeById")
    List<Map<String, Object>> listTreeById(Class<T> clazz, Long id);

    /**
     * 根据指定条件查询树
     *
     * @param clazz
     * @param
     * @return
     */
    @SelectProvider(type = TreeProvider.class, method = "listTreeByAppointColumn")
    List<Map<String, Object>> listTreeByAppointColumn(Class<T> clazz, Map<String, String> kvMap);

    /**
     * 根据指定条件查询
     *
     * @param clazz
     * @param
     * @return
     */
    @SelectProvider(type = QueryProvider.class, method = "queryByAppointColumn")
    List<Map<String, Object>> queryByAppointColumn(Class<T> clazz, Map<String, String> kvMap);

    /**
     * 查询
     *
     * @param id 主键
     * @return 测试
     */
    @Select("select * from ${table} where id = #{id}")
    Map<String, String> dynamicSelectById(@Param("table") String table, @Param("id") Integer id);

    /**
     * 查询列表
     *
     * @return 测试集合
     */
    @Select("select * from ${table} ${ew.customSqlSegment}")
    List<Map<String, String>> dynamicSelectList(@Param("table") String table, @Param("ew") QueryWrapper data);


    /**
     * 修改
     *
     * @return 结果
     */
    @Update("update ${table} set ${ew.getSqlSet} where ${ew.getSqlSegment}")
    int dynamicUpdate(@Param("table") String table, @Param("ew") UpdateWrapper data);

    /**
     * 删除
     *
     * @param id 测试主键
     * @return 结果
     */
    @Delete("delete from ${table} where id = #{id}")
    int dynamicDeleteById(@Param("table") String table, @Param("id") Integer id);

    /**
     * 批量删除
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    default int dynamicDeleteByIds(String table, Integer[] ids) {
        int flag = 0;
        for (Integer id : ids) {
            flag += dynamicDeleteById(table, id);
        }
        return flag;
    }

}
