package com.web.base.repository;

import java.util.List;
import java.util.Map;

/**
 * Dao基类
 * Created by Dean on 2017-7-18.
 */
public interface BaseDao<T> {

    //EntityManager
    List<T> findList(String sql, List<Object> args);
    T findOne(String sql, List<Object> args);
    Object findCount(String sql, List<Object> args);
    List<Map<String, Object>> findMapList(String sql, List<Object> args);
    Map<String, Object> findMap(String sql, List<Object> args);
    int updateQuery(String sql, List<Object> arrList);
    int findListSize(String sql, List<Object> arrList);

}
