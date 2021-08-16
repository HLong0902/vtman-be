package com.viettel.vtman.cms.dao.impl;

import com.viettel.vtman.cms.base.dao.BaseFWDAOImpl;
import com.viettel.vtman.cms.dao.TokenFcmDAO;
import com.viettel.vtman.cms.entity.TokenFcm;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@Transactional
public class TokenFcmDAOImpl extends BaseFWDAOImpl<TokenFcm> implements TokenFcmDAO {
    @Override
    public Long insertToken(TokenFcm tokenFcm) {
        return this.saveObject(tokenFcm);
    }

    @Override
    public String updateToken(TokenFcm tokenFcm) {
        return this.update(tokenFcm);
    }

    @Override
    public TokenFcm findByToken(String token) {
        List<TokenFcm> lstToken = this.findByProperties(TokenFcm.class, "token", token);
        return CollectionUtils.isNotEmpty(lstToken) ? lstToken.get(0) : null;
    }

    @Override
    public TokenFcm findByEmployeeId(Long employeeId) {
        List<TokenFcm> lstToken = this.findByProperties(TokenFcm.class, "employeeId", employeeId);
        return CollectionUtils.isNotEmpty(lstToken) ? lstToken.get(0) : null;
    }

    @Override
    public TokenFcm findByTokenOrEmployeeId(String token, Long employeeId) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT t.ID as id, t.EMPLOYEE_ID as employeeId, t.TOKEN as token ");
        sql.append("FROM TOKEN_FCM t ");
        sql.append("WHERE t.TOKEN = :token OR t.EMPLOYEE_ID = :employeeId ");

        SQLQuery query = getSession().createSQLQuery(sql.toString());
        query.addScalar("id", LongType.INSTANCE);
        query.addScalar("employeeId", LongType.INSTANCE);
        query.addScalar("token", StringType.INSTANCE);
        query.setParameter("token", token);
        query.setParameter("employeeId", employeeId);
        query.setResultTransformer(Transformers.aliasToBean(TokenFcm.class));
        return (TokenFcm) query.uniqueResult();
    }

    @Override
    public List<String> getAllToken(List<Long> employeeIds) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT t.ID as id, t.EMPLOYEE_ID as employeeId, t.TOKEN as token ");
        sql.append("FROM TOKEN_FCM t ");
        sql.append("WHERE t.EMPLOYEE_ID in (:employeeIds)");

        SQLQuery query = getSession().createSQLQuery(sql.toString());
        query.addScalar("id", LongType.INSTANCE);
        query.addScalar("employeeId", LongType.INSTANCE);
        query.addScalar("token", StringType.INSTANCE);
        query.setParameterList("employeeIds", employeeIds);
        query.setResultTransformer(Transformers.aliasToBean(TokenFcm.class));

        List<TokenFcm> lstToken = query.list();
        return CollectionUtils.isNotEmpty(lstToken) ? lstToken.stream().map(TokenFcm::getToken).collect(Collectors.toList()) : Collections.EMPTY_LIST;
    }
}

