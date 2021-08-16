package com.viettel.vtman.cms.dao.impl;

import com.viettel.vtman.cms.base.dao.BaseFWDAOImpl;
import com.viettel.vtman.cms.dao.QuestionDefinitionDAO;
import com.viettel.vtman.cms.dto.HistoryFaqDTO;
import com.viettel.vtman.cms.dto.ObjectResult;
import com.viettel.vtman.cms.dto.QuestionDefinitionDTO;
import com.viettel.vtman.cms.dto.TopicDto;
import com.viettel.vtman.cms.entity.QuestionDefinition;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Transactional
public class QuestionDefinitionDAOImpl extends BaseFWDAOImpl<QuestionDefinition> implements QuestionDefinitionDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<QuestionDefinitionDTO> search(String questionDefinitionName, String answerDefinition, Long topicId, ObjectResult objectResult) {
        StringBuilder sqlCount = new StringBuilder();
        Map<String, Object> param = new HashMap<>();
        StringBuilder sqlSelect = new StringBuilder();
        sqlSelect.append(" select a.QUESTION_DEFINITION_ID as questionDefinitionId,");
        sqlSelect.append("  a.ANSWER_DEFINITION as answerDefinition,");
        sqlSelect.append("  a.CREATED_BY as createdBy,");
        sqlSelect.append("  a.CREATED_DATE as createdDate,");
        sqlSelect.append("  a.DESCRIPTION as description,");
        sqlSelect.append("  a.NUMBER_ORDER as numberOrder,");
        sqlSelect.append("  a.QUESTION_DEFINITION_NAME as questionDefinitionName,");
        sqlSelect.append("  a.STATUS as status,");
        sqlSelect.append("  a.TOPIC_ID as topicId,");
        sqlSelect.append("  a.UPDATED_BY as updatedBy,");
        sqlSelect.append("  (select b.TOPIC_NAME from TOPIC b where a.TOPIC_ID = b.TOPIC_ID) as topicName,");
        sqlSelect.append("  a.UPDATED_DATE as updatedDate");


        sqlCount.append("Select count(*) ");


        String condition = conditionQuery(param, questionDefinitionName, answerDefinition, topicId);
        sqlSelect.append(condition);
        sqlCount.append(condition);
        sqlSelect.append("  ORDER BY a.NUMBER_ORDER is null, a.NUMBER_ORDER, a.ANSWER_DEFINITION ");
        SQLQuery q = (SQLQuery) entityManager.createNativeQuery(sqlSelect.toString());

        SQLQuery queryCount = (SQLQuery) entityManager.createNativeQuery(sqlCount.toString());

        for (Map.Entry<String, Object> params : param.entrySet()) {
            q.setParameter(params.getKey(), params.getValue());
            queryCount.setParameter(params.getKey(), params.getValue());
        }

        List<Object[]> listResult = q.getResultList();

        q.addScalar("questionDefinitionId", LongType.INSTANCE);
        q.addScalar("answerDefinition", StringType.INSTANCE);
        q.addScalar("questionDefinitionName", StringType.INSTANCE);
        q.addScalar("topicId", LongType.INSTANCE);
        q.addScalar("numberOrder", LongType.INSTANCE);
        q.addScalar("description", StringType.INSTANCE);
        q.addScalar("topicName", StringType.INSTANCE);
        q.addScalar("createdDate", DateType.INSTANCE);
        q.addScalar("createdBy", LongType.INSTANCE);
        q.addScalar("updatedDate", DateType.INSTANCE);
        q.addScalar("updatedBy", LongType.INSTANCE);
        q.addScalar("status", LongType.INSTANCE);
        q.setResultTransformer(Transformers.aliasToBean(QuestionDefinitionDTO.class));


        q.setFirstResult((objectResult.getPage() - 1) * objectResult.getPageSize());
        q.setMaxResults(objectResult.getPageSize());


        List<QuestionDefinitionDTO> result = q.list();
        if (objectResult.getPage() != 0 && objectResult.getPageSize()!=null) {
            for (QuestionDefinitionDTO dto : result) {
                dto.setTotalRecord(Integer.parseInt(queryCount.getResultList().get(0).toString()));
                dto.setPage(objectResult.getPage());
                dto.setPageSize(objectResult.getPageSize());
            }
        }

        return result;

    }

    @Override
    public QuestionDefinition findById(Long questionDefinitionId) {
        return this.get(QuestionDefinition.class, questionDefinitionId);
    }


    @Override
    public void deleteById(Long questionDefinitionId) {
        this.deleteById(questionDefinitionId, QuestionDefinition.class, "questionDefinitionId");
    }

    @Override
    public QuestionDefinition insert(QuestionDefinition questionDefinition) {
        this.save(questionDefinition);
        return questionDefinition;
    }

    @Override
    public QuestionDefinition updateQuestion(QuestionDefinition questionDefinition) {
        this.update(questionDefinition);
        return questionDefinition;
    }


    private String conditionQuery(Map<String, Object> param, String questionDefinitionName, String answerDefinition, Long topicId) {
        StringBuilder sql = new StringBuilder();
        sql.append(" from QUESTION_DEFINITION a");
        sql.append(" where 1=1");
        if (questionDefinitionName != null && !"".equals(questionDefinitionName)) {

            sql.append(" AND upper(a.QUESTION_DEFINITION_NAME) like  :questionDefinitionName ");
            param.put("questionDefinitionName", "%" + Common.escapeStringForMySQL(questionDefinitionName.trim().toUpperCase()) + "%");
        }
        if (answerDefinition != null && !"".equals(answerDefinition)) {
            sql.append(" AND upper(a.ANSWER_DEFINITION_FLAT) like   :answerDefinition");
            param.put("answerDefinition", "%" + Common.escapeStringForMySQL(answerDefinition.trim().toUpperCase()) + "%");
        }
        if (topicId != null ) {
            sql.append(" AND a.TOPIC_ID = :topicId ");
            param.put("topicId", topicId);
        }
        return sql.toString();
    }

    @Override
    public List<QuestionDefinition> importExcel(List<QuestionDefinitionDTO> questionDefinitions) {
        List<QuestionDefinition> resutl = null;
        QuestionDefinition bean = new QuestionDefinition();
        for (QuestionDefinitionDTO dto : questionDefinitions) {
            bean.setQuestionDefinitionId(dto.getQuestionDefinitionId());
            bean.setQuestionDefinitionName(dto.getQuestionDefinitionName());
            bean.setStatus(dto.getStatus());
            bean.setAnswerDefinition(dto.getAnswerDefinition());
            bean.setTopicId(dto.getTopicId());
            bean.setDescription(dto.getDescription());
            bean.setNumberOrder(dto.getNumberOrder());
            bean.setUpdatedDate(dto.getUpdatedDate());
            bean.setCreatedBy(dto.getCreatedBy());
            bean.setCreatedDate(dto.getCreatedDate());
            bean.setUpdatedBy(dto.getUpdatedBy());

            resutl = new ArrayList<>();
            this.save(bean);
            resutl.add(bean);
        }
        return resutl;

    }

    @Override
    public String exportExcel(List<QuestionDefinitionDTO> questionDefinitionDTO) {
        return null;
    }

    @Override
    public List<QuestionDefinition> getQuestionsByTopicId(Long topicId) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT qd.question_definition_id as questionDefinitionId, qd.question_definition_name as questionDefinitionName, ");
        sql.append("qd.topic_id as topicId, qd.answer_definition as answerDefinition, qd.status as status, qd.number_order as numberOrder ");
        sql.append("FROM QUESTION_DEFINITION qd JOIN TOPIC t ON qd.topic_id = t.topic_id ");
        sql.append("WHERE t.status = 1 AND qd.status = 1 AND qd.topic_id = :topicId ");
        sql.append("ORDER BY qd.number_order is null, qd.number_order, qd.question_definition_name ");

        SQLQuery query = this.getSession().createSQLQuery(sql.toString());
        query.addScalar("questionDefinitionId", LongType.INSTANCE);
        query.addScalar("questionDefinitionName", StringType.INSTANCE);
        query.addScalar("topicId", LongType.INSTANCE);
        query.addScalar("answerDefinition", StringType.INSTANCE);
        query.addScalar("status", LongType.INSTANCE);
        query.addScalar("numberOrder", LongType.INSTANCE);
        query.setResultTransformer(Transformers.aliasToBean(QuestionDefinition.class));
        query.setParameter("topicId", topicId);

        List<QuestionDefinition> result = query.list();
        return result;
    }

    @Override
    public List<QuestionDefinition> getSuggestion(String questionDefinitionName) {
        List<QuestionDefinition> resultBean = new ArrayList<>();
        try {
            StringBuilder sqlSelect = new StringBuilder();
            sqlSelect.append(" SELECT a.QUESTION_DEFINITION_ID as questionDefinitionId,");
            sqlSelect.append(" a.QUESTION_DEFINITION_NAME AS questionDefinitionName, ");
            sqlSelect.append(" a.ANSWER_DEFINITION AS answerDefinition, ");
            sqlSelect.append(" a.TOPIC_ID as topicId, ");
            sqlSelect.append(" a.NUMBER_ORDER as numberOrder, ");
            sqlSelect.append(" a.DESCRIPTION as description, ");
            sqlSelect.append(" a.CREATED_DATE as createdDate, ");
            sqlSelect.append(" a.CREATED_BY as createdBy, ");
            sqlSelect.append(" a.UPDATED_DATE as updatedDate, ");
            sqlSelect.append(" a.UPDATED_BY as updatedBy, ");
            sqlSelect.append(" a.STATUS as status ");
            sqlSelect.append(" FROM QUESTION_DEFINITION a ");
//            sqlSelect.append(" WHERE MATCH (a.QUESTION_DEFINITION_NAME) AGAINST (:questionDefinitionName) ");
            sqlSelect.append("WHERE a.QUESTION_DEFINITION_NAME LIKE :questionDefinitionName ");
            sqlSelect.append(" AND a.STATUS = 1 ");
//            sqlSelect.append(" ORDER BY a.number_order is null, a.number_order, a.QUESTION_DEFINITION_NAME ");

            SQLQuery query = (SQLQuery) entityManager.createNativeQuery(sqlSelect.toString());

            query.setParameter("questionDefinitionName", "%" + questionDefinitionName + "%");

            query.addScalar("questionDefinitionId", LongType.INSTANCE);
            query.addScalar("questionDefinitionName", StringType.INSTANCE);
            query.addScalar("answerDefinition", StringType.INSTANCE);
            query.addScalar("topicId", LongType.INSTANCE);
            query.addScalar("numberOrder", LongType.INSTANCE);
            query.addScalar("description", StringType.INSTANCE);
            query.addScalar("createdDate", DateType.INSTANCE);
            query.addScalar("createdBy", LongType.INSTANCE);
            query.addScalar("updatedDate", DateType.INSTANCE);
            query.addScalar("updatedBy", LongType.INSTANCE);
            query.addScalar("status", LongType.INSTANCE);

            query.setResultTransformer(Transformers.aliasToBean(QuestionDefinition.class));
            resultBean = query.list();


        } catch (Exception e) {
        }

        return resultBean;
    }

    @Override
    public QuestionDefinitionDTO checkNumberOrder(Long numberOder) {
        StringBuilder sqlCount = new StringBuilder();
        StringBuilder sqlSelect = new StringBuilder();
        sqlSelect.append(" select a.QUESTION_DEFINITION_ID as questionDefinitionId,");
        sqlSelect.append("  a.NUMBER_ORDER as numberOrder");
        sqlSelect.append("  from QUESTION_DEFINITION a");
        sqlSelect.append("  where a.NUMBER_ORDER =:numberOrder");
        SQLQuery q = (SQLQuery) entityManager.createNativeQuery(sqlSelect.toString());
        q.setParameter("numberOrder", numberOder);
        q.addScalar("numberOrder", LongType.INSTANCE);
        q.addScalar("questionDefinitionId", LongType.INSTANCE);
        q.setResultTransformer(Transformers.aliasToBean(QuestionDefinitionDTO.class));
        List<QuestionDefinitionDTO> list = q.getResultList();
        if (list != null && list.size() > 0)
            return list.get(0);
        return null;
    }

    @Override
    public QuestionDefinitionDTO checkQuestionDefinitionName(QuestionDefinitionDTO questionDefinitionDTO) {
        StringBuilder sqlCount = new StringBuilder();
        StringBuilder sqlSelect = new StringBuilder();
        sqlSelect.append(" select a.QUESTION_DEFINITION_ID as questionDefinitionId ");
        sqlSelect.append("  from QUESTION_DEFINITION a ");
        sqlSelect.append("  where a.TOPIC_ID = :topicId and BINARY UPPER(a.QUESTION_DEFINITION_NAME) = :questionDefinitionName ");
        SQLQuery q = (SQLQuery) entityManager.createNativeQuery(sqlSelect.toString());
        q.setParameter("topicId", questionDefinitionDTO.getTopicId());
        q.setParameter("questionDefinitionName", Common.escapeStringForMySQL(questionDefinitionDTO.getQuestionDefinitionName().toUpperCase()));
        q.addScalar("questionDefinitionId", LongType.INSTANCE);
        q.setResultTransformer(Transformers.aliasToBean(QuestionDefinitionDTO.class));
        List<QuestionDefinitionDTO> list = q.getResultList();
        if (list != null && list.size() > 0)
            return list.get(0);
        return null;
    }

    @Override
    public Long countMax() {

        StringBuilder sqlSelect = new StringBuilder();
        sqlSelect.append("SELECT MAX(NUMBER_ORDER) FROM QUESTION_DEFINITION");
        SQLQuery q = (SQLQuery) entityManager.createNativeQuery(sqlSelect.toString());
        String count = q.getResultList().get(0).toString();
        return Long.parseLong(count);
    }

    @Override
    public List<QuestionDefinitionDTO> exportQuestion(String questionDefinitionName, String answerDefinition, Long topicId) {
        StringBuilder sqlCount = new StringBuilder();
        Map<String, Object> param = new HashMap<>();
        StringBuilder sqlSelect = new StringBuilder();
        sqlSelect.append(" select a.QUESTION_DEFINITION_ID as questionDefinitionId,");
        sqlSelect.append("  a.ANSWER_DEFINITION as answerDefinition,");
        sqlSelect.append("  a.CREATED_BY as createdBy,");
        sqlSelect.append("  a.CREATED_DATE as createdDate,");
        sqlSelect.append("  a.DESCRIPTION as description,");
        sqlSelect.append("  a.NUMBER_ORDER as numberOrder,");
        sqlSelect.append("  a.QUESTION_DEFINITION_NAME as questionDefinitionName,");
        sqlSelect.append("  a.STATUS as status,");
        sqlSelect.append("  a.TOPIC_ID as topicId,");
        sqlSelect.append("  a.UPDATED_BY as updatedBy,");
        sqlSelect.append("  (select b.TOPIC_NAME from TOPIC b where a.TOPIC_ID = b.TOPIC_ID) as topicName,");
        sqlSelect.append("  a.UPDATED_DATE as updatedDate");
        String condition = conditionQuery(param, questionDefinitionName, answerDefinition, topicId);
        sqlSelect.append(condition);
        sqlSelect.append("  ORDER BY a.NUMBER_ORDER is null, a.NUMBER_ORDER, a.ANSWER_DEFINITION ");
        SQLQuery q = (SQLQuery) entityManager.createNativeQuery(sqlSelect.toString());
        for (Map.Entry<String, Object> params : param.entrySet()) {
            q.setParameter(params.getKey(), params.getValue());

        }
        List<Object[]> listResult = q.getResultList();
        q.addScalar("questionDefinitionId", LongType.INSTANCE);
        q.addScalar("answerDefinition", StringType.INSTANCE);
        q.addScalar("questionDefinitionName", StringType.INSTANCE);
        q.addScalar("topicId", LongType.INSTANCE);
        q.addScalar("numberOrder", LongType.INSTANCE);
        q.addScalar("description", StringType.INSTANCE);
        q.addScalar("topicName", StringType.INSTANCE);
        q.addScalar("createdDate", DateType.INSTANCE);
        q.addScalar("createdBy", LongType.INSTANCE);
        q.addScalar("updatedDate", DateType.INSTANCE);
        q.addScalar("updatedBy", LongType.INSTANCE);
        q.addScalar("status", LongType.INSTANCE);
        q.setResultTransformer(Transformers.aliasToBean(QuestionDefinitionDTO.class));
        List<QuestionDefinitionDTO> result = q.list();
        return result;
    }

    @Override
    public List<QuestionDefinitionDTO> countTopicId(Long topicId) {
        StringBuilder sqlSelect = new StringBuilder();
        sqlSelect.append(" select a.QUESTION_DEFINITION_ID as questionDefinitionId ");
        sqlSelect.append(" from  QUESTION_DEFINITION a where a.TOPIC_ID = :topicId");
        SQLQuery q = (SQLQuery) entityManager.createNativeQuery(sqlSelect.toString());
        q.setParameter("topicId", topicId);
        q.addScalar("questionDefinitionId", LongType.INSTANCE);
        q.setResultTransformer(Transformers.aliasToBean(QuestionDefinitionDTO.class));
        List<QuestionDefinitionDTO> list = q.getResultList();
        return list;
    }

}
