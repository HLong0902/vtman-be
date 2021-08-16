package com.viettel.vtman.cms.dao.impl;

import com.viettel.vtman.cms.dao.ActionDAO;
import com.viettel.vtman.cms.dto.ActionDTO;
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
public class ActionDAOImpl implements ActionDAO {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public ActionDTO findNameById(Long actionId) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT a.ACTION_ID as actionId, a.ACTION_NAME as actionName ");
        sql.append("FROM ACTION a ");
        sql.append("where a.ACTION_ID = :actionId ");
        SQLQuery query = (SQLQuery) entityManager.createNativeQuery(sql.toString());
        query.setParameter("actionId", actionId);
        query.addScalar("actionId", LongType.INSTANCE);
        query.addScalar("actionName", StringType.INSTANCE);
        query.setResultTransformer(Transformers.aliasToBean(ActionDTO.class));
        List<ActionDTO> list = query.getResultList();
        if (list != null && list.size() > 0)
            return list.get(0);
        return null;
    }
}
