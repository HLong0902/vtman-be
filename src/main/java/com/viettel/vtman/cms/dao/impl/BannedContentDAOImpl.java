package com.viettel.vtman.cms.dao.impl;


import com.viettel.vtman.cms.base.dao.BaseFWDAOImpl;
import com.viettel.vtman.cms.controller.BannedContentController;
import com.viettel.vtman.cms.dao.BannedContentDAO;
import com.viettel.vtman.cms.dto.BannedContentDTO;
import com.viettel.vtman.cms.entity.BannedContent;
import com.viettel.vtman.cms.utils.Common;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
@Transactional
public class BannedContentDAOImpl extends BaseFWDAOImpl<BannedContent> implements BannedContentDAO {

    @PersistenceContext
    private EntityManager entityManager;

    public List<BannedContent> find(String name, int page, int pageSize) {
        StringBuilder hql = new StringBuilder();
        StringBuilder sqlCount = new StringBuilder();
        sqlCount.append("SELECT COUNT(*)");
        hql.append(" FROM ");
        hql.append(BannedContent.class.getName());
        hql.append(" t WHERE upper(t.bannedContentName) LIKE :name");
        sqlCount.append(hql.toString());
        hql.append(" ORDER BY t.bannedContentName ASC NULLS LAST ");
        Query query = this.getSession().createQuery(hql.toString());
        Query count = this.getSession().createQuery(sqlCount.toString());
        name = Common.escapeStringForMySQL(name.trim().toUpperCase());
        query.setParameter("name", "%" + name + "%");
        count.setParameter("name", "%" + name + "%");
        BannedContentController.setCount(count.list().get(0));
        query.setFirstResult((page - 1)*pageSize);
        query.setMaxResults(pageSize);
        return query.list();
    }
    @Override
    public void deleteById(Long bannedContentId) {
        this.deleteById(bannedContentId, BannedContent.class, "bannedContentId");
    }

    @Override
    public BannedContent findById(Long bannedContentId) {
        return this.findByProperties(BannedContent.class, "bannedContentId", bannedContentId ).get(0);
    }

    @Override
    public String create(BannedContent bannedContent) {
        String name = bannedContent.getBannedContentName();
        Long id = bannedContent.getBannedContentId();
        if (notDuplicate(name, id)) return this.save(bannedContent);
        else return "Dữ liệu đã tồn tại.";
    }
    @Override
    public String edit(BannedContent bannedContent) {
        String name = bannedContent.getBannedContentName();
        Long id = bannedContent.getBannedContentId();
        if (notDuplicate(name, id)) return this.update(bannedContent);
        else return "Dữ liệu đã tồn tại.";    }

    private boolean notDuplicate(String name, Long id) {
        StringBuilder hql = new StringBuilder();
        hql.append(" SELECT 1 FROM ");
        hql.append(BannedContent.class.getName());
        hql.append(" t WHERE t.bannedContentName = :name AND t.bannedContentId != :id");
        Query query = this.getSession().createQuery(hql.toString());
        query.setParameter("name", name);
        query.setParameter("id", id);
        query.setMaxResults(1);
        return query.list().isEmpty();
    }

    @Override
    public List<BannedContent> getBannedContent() {
        return this.getSession().createCriteria(BannedContent.class).list();
    }

    @Override
    public List<BannedContent> findBannedContent(String inputText) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT content_banned_id as bannedContentId, content_banned_name as bannedContentName ");
        sql.append("FROM CONTENT_BANNED ");
        sql.append("WHERE LOCATE(CONTENT_BANNED_NAME, :inputText) ");

        SQLQuery query = this.getSession().createSQLQuery(sql.toString());
        query.addScalar("bannedContentId", LongType.INSTANCE);
        query.addScalar("bannedContentName", StringType.INSTANCE);
        query.setResultTransformer(Transformers.aliasToBean(BannedContent.class));
        query.setParameter("inputText", inputText);

        List<BannedContent> result = query.list();
        return result;
    }

    @Override
    public BannedContentDTO findByName(String bannedContentName){
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT cb.CONTENT_BANNED_ID as bannedContentId, ");
        sql.append(" cb.CONTENT_BANNED_NAME as bannedContentName, ");
        sql.append(" cb.DESCRIPTION as description ");
        sql.append(" FROM CONTENT_BANNED cb ");
        sql.append(" WHERE BINARY UPPER(cb.CONTENT_BANNED_NAME) = UPPER(:bannedContentName) ");
        SQLQuery q = (SQLQuery) entityManager.createNativeQuery(sql.toString());
        q.setParameter("bannedContentName", bannedContentName);
        q.addScalar("bannedContentId", LongType.INSTANCE);
        q.addScalar("bannedContentName", StringType.INSTANCE);
        q.addScalar("description", StringType.INSTANCE);
        q.setResultTransformer(Transformers.aliasToBean(BannedContentDTO.class));
        List<BannedContentDTO> list = q.getResultList();
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public BannedContent updateData(BannedContent bannedContent) {
        this.update(bannedContent);
        return bannedContent;
    }
}
