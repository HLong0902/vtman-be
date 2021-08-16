package com.viettel.vtman.cms.base.dao;

import org.hibernate.Session;

import java.util.List;

public interface BaseFWDAO<T> {
    Session getSession();

    void closeSession();

    void rollbackTransaction();

    void beginTransaction();

    String delete(T var1);

    String save(T var1);

    Long saveObject(T obj);

    String update(T var1);

    <T> List<T> getAll(Class<T> var1);

    <T> T get(Class<T> var1, Long var2);

    <T> T get(Class<T> var1, String var2);

    <T> List<T> findByProperty(Class<T> tableName, String propertyName, Object value, String orderClause);

    <T> List<T> findByProperties(Class<T> var1, Object... var2);

    void deleteById(Long id, Class className, String idColumn);

    void deleteByParam(String id, Class className, String idColumn);

    void deleteByIds(List<Long> arrId, Class className, String idColumn);

    String saveAll(List<T> lst);
}
