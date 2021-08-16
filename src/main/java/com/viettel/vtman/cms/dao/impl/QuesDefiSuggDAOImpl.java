package com.viettel.vtman.cms.dao.impl;

import com.viettel.vtman.cms.dao.QuesDefiSuggDAO;
import com.viettel.vtman.cms.dto.QuesDefiSuggDTO;
import com.viettel.vtman.cms.utils.Common;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DateType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional
public class QuesDefiSuggDAOImpl implements QuesDefiSuggDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<QuesDefiSuggDTO> getSuggestion(String questionDefinitionName){
        List<QuesDefiSuggDTO> quesDefiSuggDTOList = new ArrayList<>();
        try {
            StringBuilder sqlSelect = new StringBuilder();
            sqlSelect.append(" SELECT a.QUESTION_DEFINITION_ID as questionDefinitionId,");
            sqlSelect.append(" a.QUESTION_DEFINITION_NAME AS questionDefinitionName, ");
            sqlSelect.append(" a.ANSWER_DEFINITION AS answerDefinition, ");
            sqlSelect.append(" a.TOPIC_ID as topicId, ");
            sqlSelect.append(" a.DESCRIPTION as description, ");
            sqlSelect.append(" a.CREATED_DATE as createdDate, ");
            sqlSelect.append(" a.CREATED_BY as createdBy, ");
            sqlSelect.append(" a.UPDATED_DATE as updatedDate, ");
            sqlSelect.append(" a.UPDATED_BY as updatedBy, ");
            sqlSelect.append(" a.STATUS as status, ");
            sqlSelect.append(" t.TOPIC_NAME as topicName, ");
            sqlSelect.append(" t.TOPIC_CODE as topicCode, ");
            sqlSelect.append(" a.STATUS as status ");

            sqlSelect.append(" FROM QUESTION_DEFINITION a JOIN TOPIC t ON a.TOPIC_ID = t.TOPIC_ID ");
            sqlSelect.append(" WHERE a.QUESTION_DEFINITION_NAME LIKE :questionDefinitionName ");
            sqlSelect.append(" AND a.STATUS = 1 ");
            sqlSelect.append(" order by a.QUESTION_DEFINITION_NAME ");

            SQLQuery query = (SQLQuery) entityManager.createNativeQuery(sqlSelect.toString());

            query.setParameter("questionDefinitionName", "%" + Common.escapeStringForMySQL(questionDefinitionName) + "%");

            query.addScalar("questionDefinitionId", LongType.INSTANCE);
            query.addScalar("questionDefinitionName", StringType.INSTANCE);
            query.addScalar("answerDefinition", StringType.INSTANCE);
            query.addScalar("topicId", LongType.INSTANCE);
            query.addScalar("description", StringType.INSTANCE);
            query.addScalar("createdDate", DateType.INSTANCE);
            query.addScalar("createdBy", LongType.INSTANCE);
            query.addScalar("updatedDate", DateType.INSTANCE);
            query.addScalar("updatedBy", LongType.INSTANCE);
            query.addScalar("topicName", StringType.INSTANCE);
            query.addScalar("topicCode", StringType.INSTANCE);
            query.addScalar("status", LongType.INSTANCE);

            query.setResultTransformer(Transformers.aliasToBean(QuesDefiSuggDTO.class));
            quesDefiSuggDTOList= query.list();


        } catch (Exception e) {
        }
        return quesDefiSuggDTOList;
    }
}
