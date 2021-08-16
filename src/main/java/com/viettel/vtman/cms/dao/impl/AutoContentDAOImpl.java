package com.viettel.vtman.cms.dao.impl;

import com.viettel.vtman.cms.base.dao.BaseFWDAOImpl;
import com.viettel.vtman.cms.controller.AutoContentController;
import com.viettel.vtman.cms.dao.AutoContentDAO;
import com.viettel.vtman.cms.entity.AutoContent;
import com.viettel.vtman.cms.entity.AutoContentType;
import com.viettel.vtman.cms.utils.Common;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class AutoContentDAOImpl extends BaseFWDAOImpl<AutoContent>  implements AutoContentDAO {
    @Override
    public List<AutoContentType> getType() {
        return this.getSession().createCriteria(AutoContentType.class).list();
    }
    @Override
    public List<AutoContent> getAutoContentByType(Long autoContentType) {
        return this.findByProperties(AutoContent.class, "autoContentType", autoContentType, "isActive", 1L);
    }

    @Override
    public List<AutoContent> getAutoContent() {
        return this.getSession().createCriteria(AutoContent.class).list();
    }

    @Override
    public List<AutoContent> getAutoContentById(Long autoContentById) {
        return this.findByProperties(AutoContent.class, "automaticContentId", autoContentById);
    }

    public List<AutoContent> getAutoContentContain(Long autoContentType, String name, int page, int pageSize) {
        StringBuilder sqlCount = new StringBuilder();
        sqlCount.append("SELECT COUNT(*) FROM " + AutoContent.class.getName() + " t INNER JOIN " + AutoContentType.class.getName());
        sqlCount.append(" x ON x.automaticContentType = t.autoContentType WHERE UPPER(t.automaticContentName) LIKE :name ");

        StringBuilder sqlSelect = new StringBuilder();
        sqlSelect.append("SELECT new map(t.automaticContentId as automaticContentId,");
        sqlSelect.append("t.automaticContentName as automaticContentName, t.autoContentType as autoContentType,");
        sqlSelect.append("t.numberOrder as numberOrder, t.description as description,");
        sqlSelect.append("t.isActive as isActive, t.createdDate as createdDate,");
        sqlSelect.append("t.createdBy as createdBy, t.updatedDate as updatedDate,");
        sqlSelect.append("t.updatedBy as updatedBy, x.name as type) ");
        sqlSelect.append("FROM " + AutoContent.class.getName() + " t INNER JOIN " + AutoContentType.class.getName());
        sqlSelect.append(" x ON x.automaticContentType = t.autoContentType WHERE UPPER(t.automaticContentName) LIKE :name ");

        if(autoContentType != null) {
            sqlCount.append(" AND t.autoContentType = :autoContentType");
            sqlSelect.append(" AND t.autoContentType = :autoContentType ");
        }
        sqlSelect.append(" ORDER BY t.autoContentType, t.automaticContentName");

        Query count = this.getSession().createQuery(sqlCount.toString());
        Query query = this.getSession().createQuery(sqlSelect.toString());
        name = Common.escapeStringForMySQL(name.trim().toUpperCase());
        query.setParameter("name", "%" + name + "%");
        count.setParameter("name", "%" + name + "%");

        if(autoContentType != null) {
            query.setParameter("autoContentType", autoContentType);
            count.setParameter("autoContentType", autoContentType);
        }
        AutoContentController.setCount(count.list().get(0));
        query.setFirstResult((page-1)*pageSize);
        query.setMaxResults(pageSize);
        return query.list();
    }
    @Override
    public void deleteById(Long autoContentId) {
        this.deleteById(autoContentId, AutoContent.class, "automaticContentId");
    }

    @Override
    public String create(AutoContent autoContent) {
        return this.save(autoContent);
    }
    @Override
    public String edit(AutoContent autoContent) {
        return this.update(autoContent);
    }

}
