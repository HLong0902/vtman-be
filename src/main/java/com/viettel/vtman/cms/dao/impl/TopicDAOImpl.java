package com.viettel.vtman.cms.dao.impl;

import com.viettel.vtman.cms.base.dao.BaseFWDAOImpl;
import com.viettel.vtman.cms.dao.TopicDAO;
import com.viettel.vtman.cms.dto.ObjectResult;
import com.viettel.vtman.cms.dto.TopicDto;
import com.viettel.vtman.cms.entity.Topic;
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
import javax.persistence.Query;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Repository
@Transactional
public class TopicDAOImpl extends BaseFWDAOImpl<Topic> implements TopicDAO {
    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public List<TopicDto> searchByName(String topicName, Long departmentId, ObjectResult objectResult) {
        StringBuilder sqlCount = new StringBuilder();
        Map<String, Object> param = new HashMap<>();
        StringBuilder sqlSelect = new StringBuilder();
        sqlSelect.append(" select a.TOPIC_ID as topicId,");
        sqlSelect.append("  a.TOPIC_NAME as topicName,");
        sqlSelect.append("  a.DEPARTMENT_ID as departmentId,");
        sqlSelect.append("  a.ANSWER_EMPLOYEE_ID as answerEmployeeId,");
        sqlSelect.append("  a.NUMBER_ORDER as numberOrder,");
        sqlSelect.append("  a.DESCRIPTION as description,");
        sqlSelect.append("  a.CREATED_DATE as createdDate,");
        sqlSelect.append("  a.CREATED_BY as createdBy,");
        sqlSelect.append("  a.UPDATED_DATE as updatedDate,");
        sqlSelect.append("  a.UPDATED_BY as updatedBy,");
        sqlSelect.append("  a.TOPIC_CODE as topicCode, ");
        sqlSelect.append("  (select c.DEPARTMENT_NAME  from  DEPARTMENT c where a.DEPARTMENT_ID = c.DEPARTMENT_ID  ) as departmentName, ");
        sqlSelect.append("  a.status");

        sqlCount.append("Select count(*) ");


        String condition = conditionQuery(param, topicName, departmentId);
        sqlSelect.append(condition);
        sqlCount.append(condition);
        sqlSelect.append(" order by a.NUMBER_ORDER is null, a.NUMBER_ORDER, a.TOPIC_NAME ");
        SQLQuery q = (SQLQuery) entityManager.createNativeQuery(sqlSelect.toString());

        SQLQuery queryCount = (SQLQuery) entityManager.createNativeQuery(sqlCount.toString());

        for (Map.Entry<String, Object> params : param.entrySet()) {
            q.setParameter(params.getKey(), params.getValue());
            queryCount.setParameter(params.getKey(), params.getValue());
        }

        List<Object[]> listResult = q.getResultList();

        q.addScalar("topicId", LongType.INSTANCE);
        q.addScalar("topicName", StringType.INSTANCE);
        q.addScalar("departmentId", LongType.INSTANCE);
        q.addScalar("answerEmployeeId", StringType.INSTANCE);
        q.addScalar("numberOrder", LongType.INSTANCE);
        q.addScalar("description", StringType.INSTANCE);
        q.addScalar("createdDate", DateType.INSTANCE);
        q.addScalar("createdBy", LongType.INSTANCE);
        q.addScalar("updatedDate", DateType.INSTANCE);
        q.addScalar("updatedBy", LongType.INSTANCE);
        q.addScalar("status", LongType.INSTANCE);
        q.addScalar("topicCode", StringType.INSTANCE);
        q.addScalar("departmentName", StringType.INSTANCE);


        q.setResultTransformer(Transformers.aliasToBean(TopicDto.class));


        q.setFirstResult((objectResult.getPage() - 1) * objectResult.getPageSize());
        q.setMaxResults(objectResult.getPageSize());


        List<TopicDto> result = q.list();
        for (TopicDto dto : result) {
            dto.setTotalRecord( Integer.parseInt(queryCount.getResultList().get(0).toString()));

            dto.setPage(objectResult.getPage());
            dto.setPageSize(objectResult.getPageSize());
        }

        return result;
    }

    @Override
    public List<Topic> findAll() {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT topic_id as topicId, topic_name as topicName, status as status, ");
        sql.append("number_order as numberOrder, department_id as departmentId, answer_employee_id as answerEmployeeId ");
        sql.append("FROM TOPIC ");
        sql.append("WHERE status = 1 ");
        sql.append("ORDER BY number_order is null, number_order, topic_name ");

        SQLQuery query = this.getSession().createSQLQuery(sql.toString());
        query.addScalar("topicId", LongType.INSTANCE);
        query.addScalar("topicName", StringType.INSTANCE);
        query.addScalar("status", LongType.INSTANCE);
        query.addScalar("numberOrder", LongType.INSTANCE);
        query.addScalar("departmentId", LongType.INSTANCE);
        query.addScalar("answerEmployeeId", StringType.INSTANCE);
        query.setResultTransformer(Transformers.aliasToBean(Topic.class));

        List<Topic> result = query.list();
        return result;
    }

    @Override
    public List<Topic> findAllNotCheckStatus() {
        return this.getAll(Topic.class);
    }

    @Override
    public List<Topic> findAllWithNotNullAnsEmpId(){
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT TOPIC_ID as topicId, topic_name as topicName, status as status, ");
        sql.append("number_order as numberOrder, department_id as departmentId, answer_employee_id as answerEmployeeId ");
        sql.append("FROM TOPIC ");
        sql.append("WHERE answer_employee_id is not null and STATUS = 1 ");
        sql.append("ORDER BY number_order is null, number_order, topic_name ");

        SQLQuery query = this.getSession().createSQLQuery(sql.toString());
        query.addScalar("topicId", LongType.INSTANCE);
        query.addScalar("topicName", StringType.INSTANCE);
        query.addScalar("status", LongType.INSTANCE);
        query.addScalar("numberOrder", LongType.INSTANCE);
        query.addScalar("departmentId", LongType.INSTANCE);
        query.addScalar("answerEmployeeId", StringType.INSTANCE);
        query.setResultTransformer(Transformers.aliasToBean(Topic.class));

        List<Topic> result = query.list();
        return result;
    }

    @Override
    public void deleteById(Long topicId) {

        this.deleteById(topicId, Topic.class, "topicId");

    }

    @Override
    public Topic findById(Long topicId) {
        return this.get(Topic.class, topicId);
    }

    @Override
    public TopicDto findByName(String topicName) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select a.TOPIC_ID as topicId,");
        sql.append("  a.TOPIC_CODE as topicCode");
        sql.append("  from TOPIC a");
        sql.append("  where BINARY UPPER(a.TOPIC_NAME) = UPPER(:topicName) ");
        SQLQuery q = (SQLQuery) entityManager.createNativeQuery(sql.toString());
        q.setParameter("topicName", Common.escapeStringForMySQL(topicName));
        q.addScalar("topicId", LongType.INSTANCE);
        q.addScalar("topicCode", StringType.INSTANCE);
        q.setResultTransformer(Transformers.aliasToBean(TopicDto.class));
        List<TopicDto> list = q.getResultList();
        if (list != null && list.size() > 0)
            return list.get(0);
        return null;
    }

    @Override
    public Topic insert(Topic topic) {
        this.save(topic);
        return topic;
    }

    @Override
    public Topic updateTopic(Topic topic) {
        this.update(topic);
        return topic;
    }


    @Override
    public TopicDto findByCode(String topicCode) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select a.TOPIC_ID as topicId, ");
        sql.append("  a.TOPIC_CODE as topicCode ");
        sql.append("  from TOPIC a");
        sql.append("  where BINARY UPPER(a.TOPIC_CODE) = UPPER(:topicCode) ");
        SQLQuery q = (SQLQuery) entityManager.createNativeQuery(sql.toString());
        q.setParameter("topicCode", topicCode);
        q.addScalar("topicId", LongType.INSTANCE);
        q.addScalar("topicCode", StringType.INSTANCE);
        q.setResultTransformer(Transformers.aliasToBean(TopicDto.class));
        List<TopicDto> list = q.getResultList();
        if (list != null && list.size() > 0)
            return list.get(0);
        return null;
    }

    @Override
    public TopicDto findId(Long topicId) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select a.TOPIC_ID as topicId,");
        sql.append("  a.TOPIC_NAME as topicName,");
        sql.append("  a.DEPARTMENT_ID as departmentId,");
        sql.append("  a.ANSWER_EMPLOYEE_ID as answerEmployeeId,");
        sql.append("  a.NUMBER_ORDER as numberOrder,");
        sql.append("  a.DESCRIPTION as description,");
        sql.append("  a.CREATED_DATE as createdDate,");
        sql.append("  a.CREATED_BY as createdBy,");
        sql.append("  a.UPDATED_DATE as updatedDate,");
        sql.append("  a.STATUS as status,");
        sql.append("  a.TOPIC_CODE as topicCode,");
        sql.append("  a.UPDATED_BY as updatedBy from TOPIC a");
        sql.append("  where a.TOPIC_ID = :topicId");
        SQLQuery q = (SQLQuery) entityManager.createNativeQuery(sql.toString());
        q.setParameter("topicId", topicId);
        q.addScalar("topicId", LongType.INSTANCE);
        q.addScalar("topicName", StringType.INSTANCE);
        q.addScalar("departmentId", LongType.INSTANCE);
        q.addScalar("answerEmployeeId", StringType.INSTANCE);
        q.addScalar("numberOrder", LongType.INSTANCE);
        q.addScalar("description", StringType.INSTANCE);
        q.addScalar("createdDate", DateType.INSTANCE);
        q.addScalar("createdBy", LongType.INSTANCE);
        q.addScalar("updatedDate", DateType.INSTANCE);
        q.addScalar("status", LongType.INSTANCE);
        q.addScalar("topicCode", StringType.INSTANCE);
        q.addScalar("updatedBy", LongType.INSTANCE);
        q.setResultTransformer(Transformers.aliasToBean(TopicDto.class));
        return (TopicDto) q.getSingleResult();
    }

    @Override
    public List<TopicDto> findAllByStatus() {
        StringBuilder sql = new StringBuilder();
        sql.append(" select a.TOPIC_ID as topicId,");
        sql.append("  a.TOPIC_NAME as topicName,");
        sql.append("  a.TOPIC_CODE as topicCode");
        sql.append("  from TOPIC a ");
        sql.append("   ORDER BY a.TOPIC_NAME ");
        SQLQuery q = (SQLQuery) entityManager.createNativeQuery(sql.toString());

        q.addScalar("topicId", LongType.INSTANCE);
        q.addScalar("topicName", StringType.INSTANCE);
        q.addScalar("topicCode", StringType.INSTANCE);
        q.setResultTransformer(Transformers.aliasToBean(TopicDto.class));
        List<TopicDto> list = q.getResultList();
        return list;
    }

    private String conditionQuery(Map<String, Object> param, String topicName, Long departmentId) {
        StringBuilder sql = new StringBuilder();
        sql.append(" from TOPIC a");
        sql.append(" where 1=1");
        if (topicName != null && !"".equals(topicName)) {
            sql.append(" AND upper(a.TOPIC_NAME) like :topicName ");
            param.put("topicName", "%" + Common.escapeStringForMySQL(topicName.trim().toUpperCase()) + "%");
        }
        if (departmentId != null ) {
            sql.append(" AND a.DEPARTMENT_ID = :departmentId ");
            param.put("departmentId", departmentId);
        }
        return sql.toString();
    }

    @Override
    public List<TopicDto> getForwardTopic(Long topicId) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT t.TOPIC_ID as topicId, ");
        sql.append(" t.TOPIC_NAME as topicName ");
        sql.append("  FROM TOPIC t ");
        sql.append("  where t.STATUS = 1 ");
        if (Objects.nonNull(topicId)) {
            sql.append("and t.TOPIC_ID != :topicId ");
        }
        sql.append("   ORDER BY t.TOPIC_NAME ");
        SQLQuery q = (SQLQuery) entityManager.createNativeQuery(sql.toString());

        q.addScalar("topicId", LongType.INSTANCE);
        q.addScalar("topicName", StringType.INSTANCE);
        if (Objects.nonNull(topicId)) {
            q.setParameter("topicId", topicId);
        }
        q.setResultTransformer(Transformers.aliasToBean(TopicDto.class));
        List<TopicDto> list = q.getResultList();

        return list;
    }

    @Override
    public List<Topic> findByEmployeeId(Long employeeId) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT topic_id as topicId, topic_name as topicName, answer_employee_id as answerEmployeeId, ");
        sql.append("department_id as departmentId, status as status, topic_code as topicCode ");
        sql.append("FROM TOPIC ");
        sql.append("WHERE answer_employee_id = " + employeeId + " OR answer_employee_id LIKE '" + employeeId + ",%' ");
        sql.append("OR answer_employee_id LIKE '%," + employeeId + ",%' ");
        sql.append("OR answer_employee_id LIKE '%," + employeeId + "%'");

        SQLQuery query = this.getSession().createSQLQuery(sql.toString());
        query.addScalar("topicId", LongType.INSTANCE);
        query.addScalar("topicName", StringType.INSTANCE);
        query.addScalar("answerEmployeeId", StringType.INSTANCE);
        query.addScalar("departmentId", LongType.INSTANCE);
        query.addScalar("status", LongType.INSTANCE);
        query.addScalar("topicCode", StringType.INSTANCE);
        query.setResultTransformer(Transformers.aliasToBean(Topic.class));

        List<Topic> result = query.list();
        return result;
    }

    @Override
    public List<Topic> searchByDto(TopicDto dto) {
        Map<String, Object> mapParam = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append(" SELECT a ");
        sql.append(" FROM Topic a ");
        sql.append(" WHERE a.topicId IS NOT NULL ");

        if (Objects.nonNull(dto.getDepartmentId())) {
            sql.append(" AND a.departmentId = :departmentId ");
            mapParam.put("departmentId", dto.getDepartmentId());
        }

        sql.append(" ORDER BY a.numberOrder ASC NULLS LAST ");

        Query query = entityManager.createQuery(sql.toString());
        for (Map.Entry<String, Object> entry : mapParam.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }

        return query.getResultList();
    }
}
