package com.viettel.vtman.cms.dao.impl;

import com.viettel.vtman.cms.dao.PushNotificationDAO;
import com.viettel.vtman.cms.dto.ObjectResultPage;
import com.viettel.vtman.cms.dto.PushNotificationDTO;
import com.viettel.vtman.cms.message.Const;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Transactional
public class PushNotificationDAOImpl implements PushNotificationDAO {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<PushNotificationDTO> getNotificationList() {
        StringBuilder sqlSelect = new StringBuilder();
        sqlSelect.append("SELECT ");
        sqlSelect.append("hf.HISTORY_FAQ_ID as historyFaqId, ");
        sqlSelect.append("hf.HISTORY_FAQ_NAME as historyFAQName, ");
        sqlSelect.append("hf.STATUS as status, ");
        sqlSelect.append("hf.ANSWER_EMPLOYEE_ID as employeeId, ");
        sqlSelect.append("e.EMPLOYEE_CODE as employeeCode, ");
        sqlSelect.append("e.EMPLOYEE_NAME as employeeName, ");
        sqlSelect.append("d.DEPARTMENT_ID as departmentId, ");
        sqlSelect.append("d.DEPARTMENT_NAME as departmentName ");
        sqlSelect.append("FROM ");
        sqlSelect.append("HISTORY_FAQ hf JOIN ");
        sqlSelect.append("EMPLOYEE e ON hf.ANSWER_EMPLOYEE_ID = e.EMPLOYEE_ID JOIN ");
        sqlSelect.append("TOPIC t ON t.TOPIC_ID = hf.TOPIC_ID JOIN ");
        sqlSelect.append("DEPARTMENT d ON d.DEPARTMENT_ID = e.DEPARTMENT_ID ");
        sqlSelect.append("WHERE hf.STATUS = 1 or hf.STATUS = 3 ");
        sqlSelect.append("order by hf.CREATED_DATE DESC LIMIT 4 ");
        SQLQuery query = (SQLQuery) entityManager.createNativeQuery(sqlSelect.toString());
        query.addScalar("historyFaqId", LongType.INSTANCE);
        query.addScalar("historyFAQName", StringType.INSTANCE);
        query.addScalar("status", LongType.INSTANCE);
        query.addScalar("employeeId", LongType.INSTANCE);
        query.addScalar("employeeCode", StringType.INSTANCE);
        query.addScalar("employeeName", StringType.INSTANCE);
        query.addScalar("departmentId", LongType.INSTANCE);
        query.addScalar("departmentName", StringType.INSTANCE);
        query.setResultTransformer(Transformers.aliasToBean(PushNotificationDTO.class));
        List<PushNotificationDTO> pushNotificationDTOList = query.getResultList();
        if (pushNotificationDTOList!=null && pushNotificationDTOList.size()>0){
            return pushNotificationDTOList;
        }
        return null;
    }

    @Override
    public List<PushNotificationDTO> getAll(Long answerEmployeeId, ObjectResultPage objectResultPage) {
        StringBuilder sqlCount = new StringBuilder();
        StringBuilder sqlSelect = new StringBuilder();
        Map<String, Object> param = new HashMap<>();
        sqlSelect.append("SELECT ");
        sqlSelect.append("hf.HISTORY_FAQ_ID as historyFaqId, ");
        sqlSelect.append("hf.HISTORY_FAQ_NAME as historyFAQName, ");
        sqlSelect.append("hf.STATUS as status, ");
        sqlSelect.append("hf.ANSWER_EMPLOYEE_ID as employeeId, ");
        sqlSelect.append("e.EMPLOYEE_CODE as employeeCode, ");
        sqlSelect.append("e.EMPLOYEE_NAME as employeeName, ");
        sqlSelect.append("d.DEPARTMENT_ID as departmentId, ");
        sqlSelect.append("d.DEPARTMENT_NAME as departmentName, ");
        sqlSelect.append("concat(concat(concat(m.MENU_PATH, p.PATH),'/'),hf.HISTORY_FAQ_ID) as path ");

        sqlCount.append("select count(*) ");

        String condition = conditionQuery(param, answerEmployeeId);
        sqlCount.append(condition);
        sqlSelect.append(condition);
        sqlSelect.append("order by hf.CREATED_DATE DESC ");
        SQLQuery queryCount = (SQLQuery) entityManager.createNativeQuery(sqlCount.toString());
        SQLQuery querySelect = (SQLQuery) entityManager.createNativeQuery(sqlSelect.toString());

        for (Map.Entry<String, Object> params : param.entrySet()){
            querySelect.setParameter(params.getKey(), params.getValue());
            queryCount.setParameter(params.getKey(), params.getValue());
        }

        List<Object[]> listResult = querySelect.getResultList();
        querySelect.addScalar("historyFaqId", LongType.INSTANCE);
        querySelect.addScalar("historyFAQName", StringType.INSTANCE);
        querySelect.addScalar("status", LongType.INSTANCE);
        querySelect.addScalar("employeeId", LongType.INSTANCE);
        querySelect.addScalar("employeeCode", StringType.INSTANCE);
        querySelect.addScalar("employeeName", StringType.INSTANCE);
        querySelect.addScalar("departmentId", LongType.INSTANCE);
        querySelect.addScalar("departmentName", StringType.INSTANCE);
        querySelect.addScalar("path", StringType.INSTANCE);

        querySelect.setResultTransformer(Transformers.aliasToBean(PushNotificationDTO.class));
        querySelect.setFirstResult((objectResultPage.getPage().intValue()-1) * objectResultPage.getPageSize().intValue());
        querySelect.setMaxResults(objectResultPage.getPageSize().intValue());

        List<PushNotificationDTO> result = querySelect.list();

        for (PushNotificationDTO pushNotificationDTO : result){
            pushNotificationDTO.setTotalRecord(Long.parseLong(queryCount.getResultList().get(0).toString()));
            pushNotificationDTO.setPage(objectResultPage.getPage());
            pushNotificationDTO.setPageSize(objectResultPage.getPageSize());
            pushNotificationDTO.setMessageReceived(pushNotificationDTO.getStatus());
            pushNotificationDTO.setTitleReceived(pushNotificationDTO.getStatus());
        }
        return result;
    }

    private String conditionQuery(Map<String, Object> param, Long answerEmployeeId){
        StringBuilder sql = new StringBuilder();
        sql.append("FROM ");
        sql.append("HISTORY_FAQ hf JOIN ");
        sql.append("EMPLOYEE e ON hf.ANSWER_EMPLOYEE_ID = e.EMPLOYEE_ID JOIN ");
        sql.append("TOPIC t ON t.TOPIC_ID = hf.TOPIC_ID JOIN ");
        sql.append("DEPARTMENT d ON d.DEPARTMENT_ID = e.DEPARTMENT_ID, ");
        sql.append("MENU m join PAGE p on m.MENU_ID = p.MENU_ID ");
        sql.append("WHERE (hf.STATUS = 1 or hf.STATUS = 3) and p.COMPONENT = :HistoryFaq and hf.ANSWER_EMPLOYEE_ID = :answerEmployeeId ");
        param.put("HistoryFaq", Const.PAGE_COMPONENT.HISTORY_FAQ);
        param.put("answerEmployeeId", answerEmployeeId);
        return sql.toString();
    }
}
