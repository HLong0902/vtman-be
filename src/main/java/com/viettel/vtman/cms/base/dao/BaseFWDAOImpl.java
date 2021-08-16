package com.viettel.vtman.cms.base.dao;

import com.viettel.vtman.cms.base.util.CommonUtils;
import com.viettel.vtman.cms.message.Const;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Repository
public class BaseFWDAOImpl<T> implements BaseFWDAO<T> {

    @Autowired
    private SessionFactory sessionFactory;
    public static final ThreadLocal<Session> threadLocal = new ThreadLocal();
    private static Session session;

    @Override
    public Session getSession() {
        return this.sessionFactory.getCurrentSession();
    }

    @Override
    public void closeSession() {
        this.session = (Session)threadLocal.get();
        threadLocal.remove();
        if (this.session != null && this.session.isOpen()) {
            this.session.close();
        }
    }

    @Override
    public void rollbackTransaction() {
        this.session = this.getSession();
        if (this.session != null && this.session.isOpen() && this.session.getTransaction().isActive()) {
            this.session.getTransaction().rollback();
            this.session.clear();
        }

        this.closeSession();
    }

    @Override
    public void beginTransaction() {
        this.session.beginTransaction();
    }

    public SessionFactory getSessionFactory() {
        return this.sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void flushSession() {
        this.getSession().flush();
        this.getSession().clear();
    }

    @Override
    @Transactional
    public String delete(T var1) {
//        this.getSession().beginTransaction();
        this.getSession().delete(var1);
//        this.getSession().getTransaction().commit();
        return Const.SUCCESS;
    }

    @Override
    @Transactional
    public String save(T var1) {
        try {
//            this.getSession().beginTransaction();
            this.getSession().save(var1);
//            this.getSession().getTransaction().commit();
            this.getSession().flush();
            return Const.SUCCESS;
        } catch (HibernateException ex) {
            return ex.getMessage();
        }
    }

    @Override
    @Transactional
    public Long saveObject(T obj) {
        try {
//            this.getSession().beginTransaction();
            long id = (Long)this.getSession().save(obj);
//            this.getSession().getTransaction().commit();
            return id;
        } catch (HibernateException ex) {
            return 0L;
        }
    }

    @Override
    public String saveAll(List<T> lst) {
        try {
            for (int i = 0; i < lst.size(); i++) {
                this.getSession().persist(lst.get(i));
                this.flushSession();
            }
            return Const.SUCCESS;
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    @Override
    @Transactional
    public String update(T var1) {
        try {
//            this.getSession().beginTransaction();
            this.getSession().update(var1);
//            this.getSession().getTransaction().commit();
            return Const.SUCCESS;
        } catch (HibernateException ex) {
            return ex.getMessage();
        }
    }

    @Override
    public <T> List<T> getAll(Class<T> var1) {
        Criteria crit = this.getSession().createCriteria(var1);
        return crit.list();
    }

    @Override
    public <T> T get(Class<T> type, Long id) {
        return this.getSession().get(type, id);
    }

    @Override
    public <T> T get(Class<T> type, String id) {
        return this.getSession().get(type, id);
    }

    @Override
    public <T> List<T> findByProperty(Class<T> tableName, String propertyName, Object value, String orderClause) {
        StringBuilder hql = new StringBuilder();
        hql.append(" FROM ");
        hql.append(tableName.getName());
        hql.append(" t WHERE t.");
        hql.append(propertyName);
        hql.append(" = :");
        hql.append(propertyName);
        if (!CommonUtils.isNullOrEmpty(orderClause)) {
            hql.append(" ORDER BY ");
            hql.append(orderClause);
        }
        Query query = this.getSession().createQuery(hql.toString());
        query.setParameter(propertyName, value);
        return query.list();
    }

    @Override
    public <T> List<T> findByProperties(Class<T> tableName, Object... pairs) {
        StringBuilder hql = new StringBuilder();
        hql.append(" FROM ");
        hql.append(tableName.getName());
        hql.append(" t WHERE 1 = 1 ");
        List<Object> lstParam = new ArrayList();
        List<Object> lstParam2 = new ArrayList();
        if (pairs != null && pairs.length % 2 == 0) {
            int index = 0;
            Object[] var6 = pairs.clone();
            int var7 = pairs.length;

            for(int var8 = 0; var8 < var7; ++var8) {
                Object obj = var6[var8];
                if (index % 2 == 0) {
                    hql.append(" AND t.");
                    hql.append(obj.toString());
                    hql.append(" = :");
                    hql.append(obj.toString());
                    lstParam2.add(obj);
                } else {
                    lstParam.add(obj);
                }

                ++index;
            }
        }

        Query query = this.getSession().createQuery(hql.toString());

        for(Integer pos = 0; pos < lstParam.size(); pos = pos + 1) {
            query.setParameter((String)lstParam2.get(pos), lstParam.get(pos));
        }

        return query.list();
    }

    @Override
    @Transactional
    public void deleteById(Long id, Class className, String idColumn) {
        String hql = " DELETE FROM " + className.getName() + " t WHERE t." + idColumn + " = :" + idColumn;
        Query query = this.createQuery(hql);
//        this.getSession().beginTransaction();
        query.setParameter(idColumn, id);
        query.executeUpdate();
//        this.getSession().getTransaction().commit();
    }

    @Override
    @Transactional
    public void deleteByParam(String id, Class className, String idColumn) {
        String hql = " DELETE FROM " + className.getName() + " t WHERE t." + idColumn + " = :" + idColumn;
        Query query = this.createQuery(hql);
//        this.getSession().beginTransaction();
        query.setParameter(idColumn, id);
        query.executeUpdate();
//        this.getSession().getTransaction().commit();
    }

    @Override
    @Transactional
    public void deleteByIds(List<Long> arrId, Class className, String idColumn) {
        if (arrId != null && !arrId.isEmpty()) {
            StringBuilder hql = new StringBuilder(" DELETE FROM " + className.getName() + " t WHERE 1 = 1 ");
            List<List<Long>> parList = CommonUtils.partition(arrId, 999);
            int parSize = parList.size();
            if (parSize > 0) {
                for(int i = 0; i < parSize; ++i) {
                    hql.append(" AND t.").append(idColumn).append(" IN (:ids_").append(i).append(") ");
                }

                Query query = this.createQuery(hql.toString());

                for(int i = 0; i < parSize; ++i) {
                    query.setParameterList("ids_" + i, (Collection)parList.get(i));
                }
//                this.getSession().beginTransaction();
                query.executeUpdate();
//                this.getSession().getTransaction().commit();
            }
        }
    }

    public Query createQuery(String hql) {
        return this.getSession().createQuery(hql);
    }
}

