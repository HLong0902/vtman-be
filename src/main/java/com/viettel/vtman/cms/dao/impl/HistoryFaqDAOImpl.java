package com.viettel.vtman.cms.dao.impl;

import com.viettel.vtman.cms.base.dao.BaseFWDAOImpl;
import com.viettel.vtman.cms.dao.EmployeeDAO;
import com.viettel.vtman.cms.dao.HistoryFaqDAO;
import com.viettel.vtman.cms.dto.*;
import com.viettel.vtman.cms.entity.FunctionConfig;
import com.viettel.vtman.cms.entity.HistoryFaq;
import com.viettel.vtman.cms.infrastructure.enumeration.RoleGroup;
import com.viettel.vtman.cms.message.Const;
import com.viettel.vtman.cms.service.EmployeeService;
import com.viettel.vtman.cms.service.FunctionConfigService;
import com.viettel.vtman.cms.utils.Common;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.hibernate.type.TimestampType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


@Repository
@Transactional
public class HistoryFaqDAOImpl extends BaseFWDAOImpl<HistoryFaq> implements HistoryFaqDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private FunctionConfigService functionConfigService;

    @Override
    public List<HistoryFaqDTO> getHistoryFaqs(Long employeeId, Boolean isAnswer, String keySearch, Long type) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT topic.topic_id as topicId, topic.topic_name as topicName, ");
        if (!isAnswer || Const.TYPE_HAS_SEND.equals(type)) {
            sql.append("dept.department_id as departmentId, dept.department_name as departmentName, ");
        }
        sql.append("his.history_faq_id as historyFaqId, his.history_faq_code as historyFaqCode, his.history_faq_name as historyFaqName, ");
        sql.append("his.status as status, his.created_by as employeeId, his.answer as answer, ");
        sql.append("emp.employee_code as employeeCode, emp.employee_name as employeeName, emp.email as employeeEmail, ");
        sql.append("DATE_FORMAT(his.created_date, '%Y-%m-%d %H:%i:%s') as createdDate, ");
        sql.append("DATE_FORMAT(his.answer_date, '%Y-%m-%d %H:%i:%s') as answerDate, ");
        sql.append("DATE_FORMAT(his.maximum_date, '%Y-%m-%d %H:%i:%s') as expiredDate ");
        sql.append("FROM HISTORY_FAQ his JOIN TOPIC topic ON his.topic_id = topic.topic_id ");
        if (!isAnswer || Const.TYPE_HAS_SEND.equals(type)) {
            sql.append("LEFT JOIN DEPARTMENT dept ON dept.department_id = topic.department_id ");
        }
        sql.append("JOIN EMPLOYEE emp ON emp.employee_id = his.created_by ");
        sql.append("WHERE ");
        if (isAnswer && Const.TYPE_HAS_RECEIVED.equals(type)) {
            sql.append("topic.answer_employee_id like :employeeId ");
        } else if (isAnswer && Const.TYPE_HAS_SEND.equals(type)) {
            sql.append("his.created_by = :employeeId ");
        } else {
            sql.append("his.created_by = :employeeId ");
        }
        if (!StringUtils.isEmpty(keySearch)) {
            sql.append("AND (UPPER(his.history_faq_name) like :keySearch ");
            sql.append("OR UPPER(his.history_faq_code) like :keySearch ");
            sql.append("OR UPPER(topic.topic_name) like :keySearch ");
            sql.append("OR UPPER(his.answer) like :keySearch)");
        }
        sql.append(" ORDER BY his.created_date desc");

        SQLQuery query = getSession().createSQLQuery(sql.toString());
        if (!isAnswer || Const.TYPE_HAS_SEND.equals(type)) {
            query.addScalar("departmentId", LongType.INSTANCE);
            query.addScalar("departmentName", StringType.INSTANCE);
        }
        query.addScalar("topicId", LongType.INSTANCE);
        query.addScalar("topicName", StringType.INSTANCE);
        query.addScalar("historyFaqId", LongType.INSTANCE);
        query.addScalar("historyFaqCode", StringType.INSTANCE);
        query.addScalar("historyFaqName", StringType.INSTANCE);
        query.addScalar("status", LongType.INSTANCE);
        query.addScalar("employeeId", LongType.INSTANCE);
        query.addScalar("employeeCode", StringType.INSTANCE);
        query.addScalar("employeeName", StringType.INSTANCE);
        query.addScalar("employeeEmail", StringType.INSTANCE);
        query.addScalar("createdDate", StringType.INSTANCE);
        query.addScalar("answerDate", StringType.INSTANCE);
        query.addScalar("answer", StringType.INSTANCE);
        query.addScalar("expiredDate", StringType.INSTANCE);
        query.setResultTransformer(Transformers.aliasToBean(HistoryFaqDTO.class));

        if (isAnswer && Const.TYPE_HAS_RECEIVED.equals(type)) {
            query.setParameter("employeeId", "%" + employeeId + "%");
        } else {
            query.setParameter("employeeId", employeeId);
        }
        if (!StringUtils.isEmpty(keySearch)) {
            query.setParameter("keySearch", "%" + keySearch.trim().toUpperCase() + "%");
        }

        List<HistoryFaqDTO> result = query.list();
        return result;
    }

    @Override
    public List<HistoryFaqDTO> getHistoryFaqWithoutTopic(Long employeeId, Boolean isAnswer, String keySearch, Long type) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT dept.department_id as departmentId, dept.department_name as departmentName, ");
        sql.append("his.history_faq_id as historyFaqId, his.history_faq_code as historyFaqCode, his.history_faq_name as historyFaqName, ");
        sql.append("his.status as status, his.created_by as employeeId, DATE_FORMAT(his.created_date, '%Y-%m-%d %H:%i:%s') as createdDate, ");
        sql.append("DATE_FORMAT(his.answer_date, '%Y-%m-%d %H:%i:%s') as answerDate, his.answer as answer, ");
        sql.append("DATE_FORMAT(his.maximum_date, '%Y-%m-%d %H:%i:%s') as expiredDate, ");
        sql.append("emp.employee_code as employeeCode, emp.employee_name as employeeName, emp.email as employeeEmail ");
        sql.append("FROM HISTORY_FAQ his ");
        sql.append("JOIN DEPARTMENT dept ON dept.department_id = his.department_id ");
        sql.append("JOIN EMPLOYEE emp ON emp.employee_id = his.created_by ");
        if (isAnswer && Const.TYPE_HAS_RECEIVED.equals(type)) {
            sql.append("JOIN FUNCTION_CONFIG fg ON fg.DEPARTMENT_ID = his.DEPARTMENT_ID ");
            sql.append("WHERE fg.EMPLOYEE_ID like :employeeId AND his.TOPIC_ID is null ");
        } else {
            sql.append("WHERE his.created_by = :employeeId AND his.TOPIC_ID is null ");
        }
        if (!StringUtils.isEmpty(keySearch)) {
            sql.append("AND (UPPER(his.history_faq_name) like :keySearch ");
            sql.append("OR UPPER(his.history_faq_code) like :keySearch ");
            sql.append("OR UPPER(his.answer) like :keySearch)");
        }

        SQLQuery query = getSession().createSQLQuery(sql.toString());
        query.addScalar("departmentId", LongType.INSTANCE);
        query.addScalar("departmentName", StringType.INSTANCE);
        query.addScalar("historyFaqId", LongType.INSTANCE);
        query.addScalar("historyFaqCode", StringType.INSTANCE);
        query.addScalar("historyFaqName", StringType.INSTANCE);
        query.addScalar("status", LongType.INSTANCE);
        query.addScalar("employeeId", LongType.INSTANCE);
        query.addScalar("employeeCode", StringType.INSTANCE);
        query.addScalar("employeeName", StringType.INSTANCE);
        query.addScalar("employeeEmail", StringType.INSTANCE);
        query.addScalar("createdDate", StringType.INSTANCE);
        query.addScalar("answerDate", StringType.INSTANCE);
        query.addScalar("answer", StringType.INSTANCE);
        query.addScalar("expiredDate", StringType.INSTANCE);
        query.setResultTransformer(Transformers.aliasToBean(HistoryFaqDTO.class));

        if (isAnswer && Const.TYPE_HAS_RECEIVED.equals(type)) {
            query.setParameter("employeeId", "%" + employeeId + "%");
        } else {
            query.setParameter("employeeId", employeeId);
        }
        if (!StringUtils.isEmpty(keySearch)) {
            query.setParameter("keySearch", "%" + keySearch.trim().toUpperCase() + "%");
        }

        List<HistoryFaqDTO> result = query.list();
        return result;
    }

    @Override
    public Long insertHisFaq(HistoryFaq historyFaq) {
        try {
            return this.saveObject(historyFaq);
        } catch (HibernateException ex) {
            return 0L;
        }
    }

    @Override
    public HistoryFaq findById(Long id) {
        return this.get(HistoryFaq.class, id);
    }

    @Override
    public String updateHisFaq(HistoryFaq historyFaq) {
        return this.update(historyFaq);
    }

    @Override
    public HistoryFaqDTO getHisFaqById(Long hisFaqId, Long topicId) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT dept.department_id as departmentId, dept.department_name as departmentName, ");
        if (Objects.nonNull(topicId)) {
            sql.append("topic.topic_id as topicId, topic.topic_name as topicName, ");
        }
        sql.append("his.history_faq_id as historyFaqId, his.history_faq_code as historyFaqCode, his.history_faq_name as historyFaqName, ");
        sql.append("his.status as status, his.created_by as employeeId, DATE_FORMAT(his.created_date, '%Y-%m-%d %H:%i:%s') as createdDate, ");
        sql.append("DATE_FORMAT(his.answer_date, '%Y-%m-%d %H:%i:%s') as answerDate, emp.employee_code as employeeCode, emp.employee_name as employeeName, emp.email as employeeEmail, ");
        sql.append("his.answer as answer, emp2.employee_id as empAnswerId, emp2.employee_code as empAnswerCode, emp2.employee_name as empAnswerName, ");
        sql.append("his.rating as rating, his.comment as comment, his.count_rating as countRating, ");
        sql.append("DATE_FORMAT(his.maximum_date, '%Y-%m-%d %H:%i:%s') as expiredDate ");
        sql.append("FROM HISTORY_FAQ his ");
        if (Objects.nonNull(topicId)) {
            sql.append("JOIN TOPIC topic ON his.topic_id = topic.topic_id ");
        }
        sql.append("JOIN EMPLOYEE emp ON emp.employee_id = his.created_by ");
        sql.append("LEFT JOIN EMPLOYEE emp2 ON his.answer_employee_id = emp2.employee_id ");
        sql.append("LEFT JOIN DEPARTMENT dept ON emp2.department_id = dept.department_id ");
        sql.append("WHERE his.history_faq_id = :hisFaqId");

        SQLQuery query = getSession().createSQLQuery(sql.toString());
        query.addScalar("departmentId", LongType.INSTANCE);
        query.addScalar("departmentName", StringType.INSTANCE);
        if (Objects.nonNull(topicId)) {
            query.addScalar("topicId", LongType.INSTANCE);
            query.addScalar("topicName", StringType.INSTANCE);
        }
        query.addScalar("historyFaqId", LongType.INSTANCE);
        query.addScalar("historyFaqCode", StringType.INSTANCE);
        query.addScalar("historyFaqName", StringType.INSTANCE);
        query.addScalar("status", LongType.INSTANCE);
        query.addScalar("employeeId", LongType.INSTANCE);
        query.addScalar("employeeCode", StringType.INSTANCE);
        query.addScalar("employeeName", StringType.INSTANCE);
        query.addScalar("employeeEmail", StringType.INSTANCE);
        query.addScalar("createdDate", StringType.INSTANCE);
        query.addScalar("answerDate", StringType.INSTANCE);
        query.addScalar("answer", StringType.INSTANCE);
        query.addScalar("empAnswerId", LongType.INSTANCE);
        query.addScalar("empAnswerCode", StringType.INSTANCE);
        query.addScalar("empAnswerName", StringType.INSTANCE);
        query.addScalar("rating", LongType.INSTANCE);
        query.addScalar("comment", StringType.INSTANCE);
        query.addScalar("countRating", LongType.INSTANCE);
        query.addScalar("expiredDate", StringType.INSTANCE);
        query.setResultTransformer(Transformers.aliasToBean(HistoryFaqDTO.class));

        query.setParameter("hisFaqId", hisFaqId);

        return (HistoryFaqDTO) query.uniqueResult();
    }

    @Override
    public HistoryFaqCMSDTO getHisFaqByIdFaq(Long historyFaqId) {


        StringBuilder sqlSelect = new StringBuilder();
        sqlSelect.append(" select a.HISTORY_FAQ_ID as historyFaqId,");
        sqlSelect.append("  a.HISTORY_FAQ_CODE as historyFaqCode,");
        sqlSelect.append("  a.HISTORY_FAQ_NAME as historyFaqName,");
        sqlSelect.append("  a.ANSWER as answer,");
        sqlSelect.append("  a.ANSWER_EMPLOYEE_ID as answerEmployeeId,");
        sqlSelect.append("  a.STATUS as status,");
        sqlSelect.append("  a.TOPIC_ID as topicId,");
        sqlSelect.append("  a.CREATED_DATE as createdDate,");
        sqlSelect.append("  a.CREATED_BY as createdBy,");
        sqlSelect.append("  a.ANSWER_DATE as answerDate,");
        sqlSelect.append("  (select b.TOPIC_NAME from TOPIC b where a.TOPIC_ID = b.TOPIC_ID) as topicName,");
        sqlSelect.append("  (select e.EMPLOYEE_NAME  from  EMPLOYEE e where a.CREATED_BY = e.EMPLOYEE_ID  ) as employeeName, ");
        sqlSelect.append("  (select e.EMPLOYEE_CODE  from  EMPLOYEE e where a.CREATED_BY = e.EMPLOYEE_ID  ) as employeeCode, ");
        sqlSelect.append("  (select e.POST_OFFICE_CODE  from  EMPLOYEE e where a.CREATED_BY = e.EMPLOYEE_ID  ) as postOfficeCode ");
        sqlSelect.append("  from  HISTORY_FAQ a where a.HISTORY_FAQ_ID =:historyFaqId");
        SQLQuery q = (SQLQuery) entityManager.createNativeQuery(sqlSelect.toString());
        q.setParameter("historyFaqId", historyFaqId);
        q.addScalar("historyFaqId", LongType.INSTANCE);
        q.addScalar("historyFaqCode", StringType.INSTANCE);
        q.addScalar("historyFaqName", StringType.INSTANCE);
        q.addScalar("answer", StringType.INSTANCE);
        q.addScalar("answerEmployeeId", LongType.INSTANCE);
        q.addScalar("status", LongType.INSTANCE);
        q.addScalar("topicId", LongType.INSTANCE);
        q.addScalar("createdDate", TimestampType.INSTANCE);
        q.addScalar("createdBy", LongType.INSTANCE);
        q.addScalar("answerDate", TimestampType.INSTANCE);
        q.addScalar("topicName", StringType.INSTANCE);
        q.addScalar("employeeName", StringType.INSTANCE);
        q.addScalar("employeeCode", StringType.INSTANCE);
        q.addScalar("postOfficeCode", StringType.INSTANCE);
        q.setResultTransformer(Transformers.aliasToBean(HistoryFaqCMSDTO.class));
        List<HistoryFaqCMSDTO> list = q.getResultList();
        if (list != null && list.size() > 0)
            return list.get(0);
        return null;
    }

    @Override
    public List<HistoryFaqDetailDTO> getHisFaqDetailByHisFaqId(Long hisFaqId) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT hisDetail.history_faq_detail_id as hisFaqDetailId, hisDetail.history_faq_detail_name as hisFaqDetailName, ");
        sql.append("DATE_FORMAT(hisDetail.created_date, '%Y-%m-%d %H:%i:%s') as createdDate, DATE_FORMAT(hisDetail.answer_date, '%Y-%m-%d %H:%i:%s') as answerDate, hisDetail.answer as answer, ");
        sql.append("emp1.employee_id as empCreatedId, emp1.employee_code as empCreatedCode, emp1.employee_name as empCreatedName, ");
        sql.append("emp2.employee_id as empAnswerId, emp2.employee_code as empAnswerCode, emp2.employee_name as empAnswerName, ");
        sql.append("dept.department_id as departmentId, dept.department_name as departmentName ");
        sql.append("FROM HISTORY_FAQ_DETAIL hisDetail ");
        sql.append("LEFT JOIN EMPLOYEE emp1 ON hisDetail.created_by = emp1.employee_id ");
        sql.append("LEFT JOIN EMPLOYEE emp2 ON hisDetail.answer_employee_id = emp2.employee_id ");
        sql.append("LEFT JOIN DEPARTMENT dept ON emp2.department_id = dept.department_id ");
        sql.append("WHERE hisDetail.history_faq_id = :hisFaqId ");
        sql.append("ORDER BY hisDetail.history_faq_detail_id");

        SQLQuery query = getSession().createSQLQuery(sql.toString());
        query.addScalar("hisFaqDetailId", LongType.INSTANCE);
        query.addScalar("hisFaqDetailName", StringType.INSTANCE);
        query.addScalar("createdDate", StringType.INSTANCE);
        query.addScalar("answerDate", StringType.INSTANCE);
        query.addScalar("answer", StringType.INSTANCE);
        query.addScalar("empCreatedId", LongType.INSTANCE);
        query.addScalar("empCreatedCode", StringType.INSTANCE);
        query.addScalar("empCreatedName", StringType.INSTANCE);
        query.addScalar("empAnswerId", LongType.INSTANCE);
        query.addScalar("empAnswerCode", StringType.INSTANCE);
        query.addScalar("empAnswerName", StringType.INSTANCE);
        query.addScalar("departmentId", LongType.INSTANCE);
        query.addScalar("departmentName", StringType.INSTANCE);
        query.setResultTransformer(Transformers.aliasToBean(HistoryFaqDetailDTO.class));

        query.setParameter("hisFaqId", hisFaqId);

        List<HistoryFaqDetailDTO> result = query.list();
        return result;
    }

    @Override
    public List<HistoryFaqCMSDTO> search(HistoryFaqCMSDTO historyFaqDTO) throws ParseException {
//        StringBuilder sqlCount = new StringBuilder();
//        Map<String, Object> param = new HashMap<>();
//        StringBuilder sqlSelect = new StringBuilder();
//
//
//        sqlSelect.append(" select a.HISTORY_FAQ_ID as historyFaqId,");
//        sqlSelect.append("  a.HISTORY_FAQ_CODE as historyFaqCode,");
//        sqlSelect.append("  a.HISTORY_FAQ_NAME as historyFaqName,");
//        sqlSelect.append("  a.ANSWER as answer,");
//        sqlSelect.append("  a.ANSWER_EMPLOYEE_ID as answerEmployeeId,");
//        sqlSelect.append("  a.STATUS as status,");
//        sqlSelect.append("  a.TOPIC_ID as topicId,");
//        sqlSelect.append("  a.CREATED_DATE as createdDate,");
//        sqlSelect.append("  a.CREATED_BY as createdBy,");
//        sqlSelect.append("  a.ANSWER_DATE as answerDate,");
//        sqlSelect.append("  dp.DEPARTMENT_NAME as departmentName,");
//        sqlSelect.append("  (select b.TOPIC_NAME from TOPIC b where a.TOPIC_ID = b.TOPIC_ID) as topicName,");
//        sqlSelect.append("  (select e.EMPLOYEE_NAME  from  EMPLOYEE e where a.CREATED_BY = e.EMPLOYEE_ID  ) as employeeName, ");
//        sqlSelect.append("  (select e.EMPLOYEE_CODE  from  EMPLOYEE e where a.CREATED_BY = e.EMPLOYEE_ID  ) as employeeCode, ");
//        sqlSelect.append("  (select e.POST_OFFICE_CODE  from  EMPLOYEE e where a.CREATED_BY = e.EMPLOYEE_ID  ) as postOfficeCode ");
//        sqlCount.append("Select count(*) ");
//        String condition = conditionQuery(param, historyFaqDTO);
//        sqlSelect.append(condition);
//        sqlSelect.append(" order by topicName is null, topicName ,departmentName,a.CREATED_DATE desc ");
//        sqlCount.append(condition);
//        SQLQuery q = (SQLQuery) entityManager.createNativeQuery(sqlSelect.toString());
//        SQLQuery queryCount = (SQLQuery) entityManager.createNativeQuery(sqlCount.toString());
//        for (Map.Entry<String, Object> params : param.entrySet()) {
//            q.setParameter(params.getKey(), params.getValue());
//            queryCount.setParameter(params.getKey(), params.getValue());
//        }
//        List<Object[]> listResult = q.getResultList();
//        q.addScalar("historyFaqId", LongType.INSTANCE);
//        q.addScalar("postOfficeCode", StringType.INSTANCE);
//        q.addScalar("historyFaqCode", StringType.INSTANCE);
//        q.addScalar("historyFaqName", StringType.INSTANCE);
//        q.addScalar("answer", StringType.INSTANCE);
//        q.addScalar("answerEmployeeId", LongType.INSTANCE);
//        q.addScalar("status", LongType.INSTANCE);
//        q.addScalar("topicId", LongType.INSTANCE);
//        q.addScalar("createdDate", TimestampType.INSTANCE);
//        q.addScalar("createdBy", LongType.INSTANCE);
//        q.addScalar("answerDate", TimestampType.INSTANCE);
//        q.addScalar("topicName", StringType.INSTANCE);
//        q.addScalar("employeeName", StringType.INSTANCE);
//        q.addScalar("employeeCode", StringType.INSTANCE);
//        q.addScalar("departmentName", StringType.INSTANCE);
//        q.setResultTransformer(Transformers.aliasToBean(HistoryFaqCMSDTO.class));
//        q.setFirstResult((historyFaqDTO.getPage() - 1) * historyFaqDTO.getPageSize());
//        q.setMaxResults(historyFaqDTO.getPageSize());
//        List<HistoryFaqCMSDTO> result = q.list();
//        if (String.valueOf(historyFaqDTO.getPage()) !=null && historyFaqDTO.getPage() > 0
//                && String.valueOf(historyFaqDTO.getPageSize())!=null && historyFaqDTO.getPageSize() > 0)
//            for (HistoryFaqCMSDTO dto : result) {
//                dto.setTotalRecord(Integer.parseInt(queryCount.getResultList().get(0).toString()));
//                dto.setPage(historyFaqDTO.getPage());
//                dto.setPageSize(historyFaqDTO.getPageSize());
//            }
//        return result;
        Map<String, Object> mapParam = new HashMap<>();
        StringBuilder sqlSelect = new StringBuilder(" SELECT NEW " + HistoryFaqCMSDTO.class.getName() + "(a, b, c, d, e) ");
        StringBuilder sqlCount = new StringBuilder(" SELECT COUNT(a.historyFaqId) ");
        String condition = conditionQuery(mapParam, historyFaqDTO);
        sqlSelect.append(condition);
        sqlCount.append(condition);

        sqlSelect.append(" ORDER BY b.topicName ASC NULLS LAST, c.departmentName ASC NULLS LAST, a.createdDate DESC NULLS LAST ");

        Query querySelect = entityManager.createQuery(sqlSelect.toString());
        Query queryCount = entityManager.createQuery(sqlCount.toString());
        for (Map.Entry<String, Object> entry : mapParam.entrySet()) {
            querySelect.setParameter(entry.getKey(), entry.getValue());
            queryCount.setParameter(entry.getKey(), entry.getValue());
        }

        int totalRecord = Integer.parseInt(queryCount.getSingleResult().toString());

        if (historyFaqDTO.getPage() > 0 && historyFaqDTO.getPageSize() > 0) {
            querySelect.setFirstResult((historyFaqDTO.getPage() - 1) * historyFaqDTO.getPageSize());
            querySelect.setMaxResults(historyFaqDTO.getPageSize());
        }

        return ((List<HistoryFaqCMSDTO>) querySelect.getResultList()).stream().map(e -> {
            e.setTotalRecord(totalRecord);
            e.setPage(historyFaqDTO.getPage());
            e.setPage(historyFaqDTO.getPageSize());
            return e;
        }).collect(Collectors.toList());
    }

    @Override
    public List<HistoryFaqDTO> countTopicId(Long topicId) {
        StringBuilder sqlSelect = new StringBuilder();
        sqlSelect.append(" select a.HISTORY_FAQ_ID as historyFaqId ");
        sqlSelect.append(" from  HISTORY_FAQ a where a.TOPIC_ID = :topicId");
        SQLQuery q = (SQLQuery) entityManager.createNativeQuery(sqlSelect.toString());
        q.setParameter("topicId", topicId);
        q.addScalar("historyFaqId", LongType.INSTANCE);
        q.setResultTransformer(Transformers.aliasToBean(HistoryFaqDTO.class));
        List<HistoryFaqDTO> list = q.getResultList();
        return list;

    }

    private String conditionQuery(Map<String, Object> param, HistoryFaqCMSDTO historyFaqDTO) throws ParseException {
//         StringBuilder sql = new StringBuilder();
//        sql.append(" from HISTORY_FAQ a");
//        sql.append(" left join TOPIC tp on a.TOPIC_ID = tp.TOPIC_ID ");
//        sql.append(" left join DEPARTMENT dp on dp.DEPARTMENT_ID =  a.DEPARTMENT_ID ");
//        sql.append(" left join EMPLOYEE e ON a.CREATED_BY = e.EMPLOYEE_ID ");
//        sql.append(" where 1=1");
//        if (historyFaqDTO.getHistoryFaqName() != null && !"".equals(historyFaqDTO.getHistoryFaqName())) {
//            sql.append(" AND upper(a.HISTORY_FAQ_NAME) like  :historyFaqName ");
//            param.put("historyFaqName", "%" + Common.escapeStringForMySQL(historyFaqDTO.getHistoryFaqName().trim().toUpperCase()) + "%");
//        }
//        if (historyFaqDTO.getAnswer() != null && !"".equals(historyFaqDTO.getAnswer())) {
//            sql.append(" AND upper(a.ANSWER) like   :answer");
//            param.put("answer", "%" + Common.escapeStringForMySQL(historyFaqDTO.getAnswer().trim().toUpperCase()) + "%");
//        }
//        if (historyFaqDTO.getTopicId() != null) {
//            sql.append(" AND a.TOPIC_ID = :topicId ");
//            param.put("topicId", historyFaqDTO.getTopicId());
//        }
//        if (historyFaqDTO.getDepartmentId() != null) {
//            sql.append(" AND a.DEPARTMENT_ID  = :departmentId ");
//            param.put("departmentId", historyFaqDTO.getDepartmentId());
//        }
//
//        if (historyFaqDTO.getStatus() != null) {
//            sql.append(" AND a.STATUS = :status ");
//            param.put("status", historyFaqDTO.getStatus());
//        }
//        if (historyFaqDTO.getEmployeeName() != null && !"".equals(historyFaqDTO.getEmployeeName())) {
//            sql.append(" AND a.CREATED_BY  in (select k.EMPLOYEE_ID from EMPLOYEE k where  upper(k.EMPLOYEE_NAME) like :employeeName ) ");
//            param.put("employeeName", "%" + Common.escapeStringForMySQL(historyFaqDTO.getEmployeeName().trim().toUpperCase()) + "%");
//        }
//        if (historyFaqDTO.getStartDateAnswerSearch() != null && !"".equals(historyFaqDTO.getStartDateAnswerSearch())) {
//            sql.append(" AND a.ANSWER_DATE >=  :startDateAnswerSearch ");
//            param.put("startDateAnswerSearch", historyFaqDTO.getStartDateAnswerSearch());
//
//        }
//        if (historyFaqDTO.getEndDateAnswerSearch() != null && !"".equals(historyFaqDTO.getEndDateAnswerSearch())) {
//            sql.append(" AND a.ANSWER_DATE <= :endDateAnswerSearch ");
//            param.put("endDateAnswerSearch", historyFaqDTO.getEndDateAnswerSearch());
//
//        }
//
//        if (historyFaqDTO.getStartDateQuestionSearch() != null && !"".equals(historyFaqDTO.getStartDateQuestionSearch())) {
//            sql.append(" AND a.CREATED_DATE >=  :startDateQuestionSearch ");
//            param.put("startDateQuestionSearch", historyFaqDTO.getStartDateQuestionSearch());
//
//        }
//        if (historyFaqDTO.getEndDateQuestionSearch() != null && !"".equals(historyFaqDTO.getEndDateQuestionSearch())) {
//            sql.append(" AND a.CREATED_DATE <= :endDateQuestionSearch ");
//            param.put("endDateQuestionSearch", historyFaqDTO.getEndDateQuestionSearch());
//        }
//        if (!StringUtils.isEmpty(historyFaqDTO.getEmployeeSearch())) {
//            sql.append(" AND ( ");
//            sql.append("    UPPER(e.POST_OFFICE_CODE) like :employeeSearch ");
//            sql.append("    OR UPPER(e.EMPLOYEE_NAME) like :employeeSearch ");
//            sql.append("    OR UPPER(e.EMPLOYEE_CODE) like :employeeSearch ");
//            sql.append(" ) ");
//            param.put("employeeSearch", "%" + Common.escapeStringForMySQL(historyFaqDTO.getEmployeeSearch().trim().toUpperCase()) + "%");
//        }
//        return sql.toString();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        StringBuilder sql = new StringBuilder();
        sql.append(" FROM HistoryFaq a ");
        sql.append(" LEFT JOIN Topic b ON b.topicId = a.topicId ");
        sql.append(" LEFT JOIN Department c ON c.departmentId = a.departmentId ");
        sql.append(" LEFT JOIN Employee d ON d.employeeId = a.createdBy ");
        sql.append(" LEFT JOIN Employee e ON e.employeeId = a.answerEmployeeId ");
        sql.append(" WHERE 1=1");
        if (historyFaqDTO.getHistoryFaqName() != null && !"".equals(historyFaqDTO.getHistoryFaqName())) {
            sql.append(" AND upper(a.historyFaqName) like  :historyFaqName ");
            param.put("historyFaqName", "%" + Common.escapeStringForMySQL(historyFaqDTO.getHistoryFaqName().trim().toUpperCase()) + "%");
        }
        if (historyFaqDTO.getAnswer() != null && !"".equals(historyFaqDTO.getAnswer())) {
            sql.append(" AND upper(a.answer) like   :answer");
            param.put("answer", "%" + Common.escapeStringForMySQL(historyFaqDTO.getAnswer().trim().toUpperCase()) + "%");
        }
        if (historyFaqDTO.getTopicId() != null) {
            sql.append(" AND a.topicId = :topicId ");
            param.put("topicId", historyFaqDTO.getTopicId());
        }
        if (historyFaqDTO.getDepartmentId() != null) {
            sql.append(" AND a.departmentId  = :departmentId ");
            param.put("departmentId", historyFaqDTO.getDepartmentId());
        }

        if (historyFaqDTO.getStatus() != null) {
            sql.append(" AND a.status = :status ");
            param.put("status", historyFaqDTO.getStatus());
        }
        if (historyFaqDTO.getEmployeeName() != null && !"".equals(historyFaqDTO.getEmployeeName())) {
            sql.append(" AND a.createdBy  IN (select a1.employeeId from Employee a1 where  upper(a1.employeeName) like :employeeName ) ");
            param.put("employeeName", "%" + Common.escapeStringForMySQL(historyFaqDTO.getEmployeeName().trim().toUpperCase()) + "%");
        }
        if (historyFaqDTO.getStartDateAnswerSearch() != null && !"".equals(historyFaqDTO.getStartDateAnswerSearch())) {
            sql.append(" AND a.answerDate >=  :startDateAnswerSearch ");
            param.put("startDateAnswerSearch", simpleDateFormat.parse(historyFaqDTO.getStartDateAnswerSearch()));

        }
        if (historyFaqDTO.getEndDateAnswerSearch() != null && !"".equals(historyFaqDTO.getEndDateAnswerSearch())) {
            sql.append(" AND a.answerDate <= :endDateAnswerSearch ");
            param.put("endDateAnswerSearch", simpleDateFormat.parse(historyFaqDTO.getEndDateAnswerSearch()));

        }

        if (historyFaqDTO.getStartDateQuestionSearch() != null && !"".equals(historyFaqDTO.getStartDateQuestionSearch())) {
            sql.append(" AND a.createdDate >=  :startDateQuestionSearch ");
            param.put("startDateQuestionSearch", simpleDateFormat.parse(historyFaqDTO.getStartDateQuestionSearch()));

        }
        if (historyFaqDTO.getEndDateQuestionSearch() != null && !"".equals(historyFaqDTO.getEndDateQuestionSearch())) {
            sql.append(" AND a.createdDate <= :endDateQuestionSearch ");
            param.put("endDateQuestionSearch", simpleDateFormat.parse(historyFaqDTO.getEndDateQuestionSearch()));
        }
        if (!StringUtils.isEmpty(historyFaqDTO.getEmployeeSearch())) {
            sql.append(" AND ( ");
            sql.append("    UPPER(d.postOfficeCode) like :employeeSearch ");
            sql.append("    OR UPPER(d.employeeName) like :employeeSearch ");
            sql.append("    OR UPPER(d.employeeCode) like :employeeSearch ");
            sql.append(" ) ");
            param.put("employeeSearch", "%" + Common.escapeStringForMySQL(historyFaqDTO.getEmployeeSearch().trim().toUpperCase()) + "%");
        }
        if (!StringUtils.isEmpty(historyFaqDTO.getAnswerEmployee())) {
//            sql.append(" AND upper(e.employeeName) like  :answerEmployeeName ");
            sql.append(" AND ( ");
            sql.append("    UPPER(e.postOfficeCode) like :answerEmployeeName ");
            sql.append("    OR UPPER(e.employeeName) like :answerEmployeeName ");
            sql.append("    OR UPPER(e.employeeCode) like :answerEmployeeName ");
            sql.append(" ) ");
            param.put("answerEmployeeName", "%" + Common.escapeStringForMySQL(historyFaqDTO.getAnswerEmployee().trim().toUpperCase()) + "%");
        }

        EmployeeDto user = employeeService.getOrCreateFromJwt();
        boolean specialRole = false;
        if (Objects.nonNull(user) && Objects.nonNull(user.getRoleDTO()) && !StringUtils.isEmpty(user.getRoleDTO().getRoleGroup())) {
            if (user.getRoleDTO().getRoleGroup().equals(RoleGroup.GROUP_GD.name())) {
                specialRole = true;
            }
            if (user.getRoleDTO().getRoleGroup().equals(RoleGroup.GROUP_TPB.name())) {
                specialRole = true;
                FunctionConfig functionConfig = functionConfigService.findAll().stream().findFirst().orElse(null);
                if (Objects.nonNull(functionConfig)
                        && Objects.nonNull(functionConfig.getDepartmentId())
                        && Objects.nonNull(user.getDepartmentId())
                        && user.getDepartmentId().equals(functionConfig.getDepartmentId())) {
                    sql.append(" AND (b.departmentId = :userDepartmentId OR a.topicId IS NULL) ");
                    param.put("userDepartmentId", user.getDepartmentId());
                } else {
                    sql.append(" AND b.departmentId = :userDepartmentId ");
                    param.put("userDepartmentId", user.getDepartmentId());
                }
            }
        }
        if (!specialRole) {
            sql.append(" AND (  b.answerEmployeeId = :userEmployeeIdAlone ");
            sql.append("        OR b.answerEmployeeId LIKE :userEmployeeIdFirst ");
            sql.append("        OR b.answerEmployeeId LIKE :userEmployeeIdMid ");
            sql.append("        OR b.answerEmployeeId LIKE :userEmployeeIdLast ");
            sql.append(" ) ");
            param.put("userEmployeeIdAlone", user.getEmployeeId().toString());
            param.put("userEmployeeIdFirst", user.getEmployeeId().toString() + ",%");
            param.put("userEmployeeIdMid", "%," + user.getEmployeeId().toString() + ",%");
            param.put("userEmployeeIdLast", "%," + user.getEmployeeId().toString());
        }

        return sql.toString();
    }

    @Override
    public List<HisFaqNotificationDTO> getHistoryFaqPushNotification(){
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT hf.HISTORY_FAQ_ID as historyFaqId, hf.HISTORY_FAQ_CODE as historyFaqCode, hf.HISTORY_FAQ_NAME as historyFaqName, hf.ANSWER as answer, ");
        sql.append(" hf.TOPIC_ID as topicId, hf.CREATED_DATE as createdDate, hf.CREATED_BY as employeeId, hf.ANSWER_DATE as answerDate, ");
        sql.append(" DATE_FORMAT(hf.CREATED_DATE, '%Y-%m-%d %H:%i:%s') as strCreatedDate, ");
        sql.append(" DATE_FORMAT(hf.ANSWER_DATE, '%Y-%m-%d %H:%i:%s') as strAnswerDate, ");
        sql.append(" DATE_FORMAT(hf.maximum_date, '%Y-%m-%d %H:%i:%s') as maximumDate, ");
        sql.append(" DATE_FORMAT(hf.response_date, '%Y-%m-%d %H:%i:%s') as responseDate, ");
        sql.append(" e.EMPLOYEE_NAME as employeeName, e.EMPLOYEE_CODE as employeeCode ");
        sql.append(" FROM HISTORY_FAQ hf ");
        sql.append(" JOIN EMPLOYEE e on hf.CREATED_BY = e.EMPLOYEE_ID ");
        sql.append(" WHERE hf.answer is null and hf.status not in (4, 5) ORDER BY hf.HISTORY_FAQ_ID DESC ");

        SQLQuery query = getSession().createSQLQuery(sql.toString());
        query.addScalar("historyFaqId", LongType.INSTANCE);
        query.addScalar("historyFaqCode", StringType.INSTANCE);
        query.addScalar("historyFaqName", StringType.INSTANCE);
        query.addScalar("answer", StringType.INSTANCE);
        query.addScalar("topicId", LongType.INSTANCE);
        query.addScalar("createdDate", TimestampType.INSTANCE);
        query.addScalar("employeeId", LongType.INSTANCE);
        query.addScalar("answerDate", TimestampType.INSTANCE);
        query.addScalar("strCreatedDate", StringType.INSTANCE);
        query.addScalar("strAnswerDate", StringType.INSTANCE);
        query.addScalar("employeeName", StringType.INSTANCE);
        query.addScalar("employeeCode", StringType.INSTANCE);
        query.addScalar("maximumDate", StringType.INSTANCE);
        query.addScalar("responseDate", StringType.INSTANCE);
        query.setResultTransformer(Transformers.aliasToBean(HisFaqNotificationDTO.class));

        List<HisFaqNotificationDTO> result = query.list();
        return result;
    }

    @Override
    public List<HistoryFaq> findByAnswerIsNull(Long employeeId) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT history_faq_id as historyFaqId, history_faq_code as historyFaqCode, ");
        sql.append("history_faq_name as historyFaqName, created_by as createdBy, status as status ");
        sql.append("FROM HISTORY_FAQ ");
        sql.append("WHERE status = 1 AND created_by = :employeeId ");
        sql.append("AND answer IS NULL");

        SQLQuery query = getSession().createSQLQuery(sql.toString());
        query.addScalar("historyFaqId", LongType.INSTANCE);
        query.addScalar("historyFaqCode", StringType.INSTANCE);
        query.addScalar("historyFaqName", StringType.INSTANCE);
        query.addScalar("createdBy", LongType.INSTANCE);
        query.addScalar("status", LongType.INSTANCE);
        query.setResultTransformer(Transformers.aliasToBean(HistoryFaq.class));

        query.setParameter("employeeId", employeeId);

        List<HistoryFaq> result = query.list();
        return result;
    }

    @Override
    public List<HistoryFaqDTO> checkQuestionStatus(Long departmentId){
        StringBuilder sql = new StringBuilder();
        sql.append("Select hf.HISTORY_FAQ_ID as historyFaqId, ");
        sql.append("hf.HISTORY_FAQ_CODE as historyFaqCode, ");
        sql.append("hf.HISTORY_FAQ_NAME as historyFaqName, ");
        sql.append("hf.ANSWER as answer, ");
        sql.append("hf.ANSWER_EMPLOYEE_ID as empAnswerId, ");
        sql.append("hf.STATUS as status, ");
        sql.append("hf.TOPIC_ID as topicId, ");
        sql.append("hf.DEPARTMENT_ID as departmentId, ");
        sql.append("hf.ANSWER_DATE as answerDate ");
        sql.append("from HISTORY_FAQ hf ");
        sql.append("join FUNCTION_CONFIG fc ");
        sql.append("on hf.DEPARTMENT_ID = fc.DEPARTMENT_ID ");
        sql.append("where hf.STATUS = 1 and fc.DEPARTMENT_ID = :departmentId");

        SQLQuery query = getSession().createSQLQuery(sql.toString());

        query.addScalar("historyFaqId", LongType.INSTANCE);
        query.addScalar("historyFaqCode", StringType.INSTANCE);
        query.addScalar("historyFaqName", StringType.INSTANCE);
        query.addScalar("answer", StringType.INSTANCE);
        query.addScalar("empAnswerId", LongType.INSTANCE);
        query.addScalar("answerDate", StringType.INSTANCE);
        query.setResultTransformer(Transformers.aliasToBean(HistoryFaqDTO.class));
        query.setParameter("departmentId", departmentId);
        List<HistoryFaqDTO> listResult = query.getResultList();
        return listResult;
    }
}
