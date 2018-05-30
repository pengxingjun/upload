package com.web.base.repository.impl;

import com.web.base.repository.BaseDao;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseDaoImpl<T> implements BaseDao<T> {

    Logger logger = Logger.getLogger(BaseDaoImpl.class);

    @PersistenceContext
    protected EntityManager em;

    private Class<T> clazz;

    public BaseDaoImpl() {
        ParameterizedType pType = (ParameterizedType) this.getClass().getGenericSuperclass();// 获取当前new的对象的泛型父类
        this.clazz = (Class<T>) pType.getActualTypeArguments()[0];// 获取类型参数的真是值，就是泛型参数的个数；
    }

    @Override
    public List<T> findList(String sql, List<Object> args) {
        List<T> list = new ArrayList<T>();
        try {
            Query query = em.createNativeQuery(sql, clazz);
            if (args != null && args.size() > 0) {
                for (int i = 0; i < args.size(); i++) {
                    query.setParameter(i + 1, args.get(i));
                }
            }
            list = (List<T>) query.getResultList();
        } catch (NoResultException ne){
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("EntityManager执行sql出错：" + e.getMessage());
        }
        return list;
    }

    @Override
    public T findOne(String sql, List<Object> args) {
        T t = null;
        try {
            Query query = em.createNativeQuery(sql, clazz);
            if (args != null && args.size() > 0) {
                for (int i = 0; i < args.size(); i++) {
                    query.setParameter(i + 1, args.get(i));
                }
            }
            t = (T) query.getSingleResult();
        } catch (NoResultException ne){
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("EntityManager执行sql出错：" + e.getMessage());
        }
        return t;
    }

    @Override
    public Object findCount(String sql, List<Object> args) {
        Object obj = null;
        try {
            Query query = em.createNativeQuery(sql);
            if (args != null && args.size() > 0) {
                for (int i = 0; i < args.size(); i++) {
                    query.setParameter(i + 1, args.get(i));
                }
            }
            obj = query.getSingleResult();
        } catch (NoResultException ne){
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("EntityManager执行sql出错：" + e.getMessage());
        }
        return obj;
    }

    @Override
    public List<Map<String, Object>> findMapList(String sql, List<Object> args) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        try {
            Query query = em.createNativeQuery(sql, Map.class);
            if (args != null && args.size() > 0) {
                for (int i = 0; i < args.size(); i++) {
                    query.setParameter(i + 1, args.get(i));
                }
            }
            list = (List<Map<String, Object>>) query.getResultList();
        } catch (NoResultException ne){
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("EntityManager执行sql出错：" + e.getMessage());
        }
        return list;
    }

    @Override
    public Map<String, Object> findMap(String sql, List<Object> args) {
        Map<String, Object> map = new HashMap<>();
        try {
            Query query = em.createNativeQuery(sql, Map.class);
            if (args != null && args.size() > 0) {
                for (int i = 0; i < args.size(); i++) {
                    query.setParameter(i + 1, args.get(i));
                }
            }
            map = (Map<String, Object>) query.getSingleResult();
        } catch (NoResultException ne){
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("EntityManager执行sql出错：" + e.getMessage());
        }
        return map;
    }

    @Override
    public int updateQuery(String sql, List<Object> args) {
        int num = 0;
        try {
            Query query = em.createNativeQuery(sql);
            if (args != null && args.size() > 0) {
                for (int i = 0; i < args.size(); i++) {
                    query.setParameter(i + 1, args.get(i));
                }
            }
            num = query.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("EntityManager执行sql出错：" + e.getMessage());
        }
        return num;
    }

    @Override
    public int findListSize(String sql, List<Object> args) {
        int num = 0;
        try {
            sql = sql.toLowerCase();
            sql = "select count(1) num from (" + sql + ") t";
            Query query = em.createNativeQuery(sql);
            if (args != null && args.size() > 0) {
                for (int i = 0; i < args.size(); i++) {
                    query.setParameter(i + 1, args.get(i));
                }
            }
            num = Integer.parseInt(query.getSingleResult().toString());
        } catch (NoResultException ne){
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("EntityManager执行sql出错：" + e.getMessage());
        }
        return num;
    }

}
