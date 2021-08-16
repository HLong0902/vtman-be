package com.viettel.vtman.cms.dao.impl;

import com.viettel.vtman.cms.base.dao.BaseFWDAOImpl;
import com.viettel.vtman.cms.dao.NotificationFaqDAO;
import com.viettel.vtman.cms.dto.NotificationFaqDTO;
import com.viettel.vtman.cms.entity.NotificationFaq;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DateType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;

import static com.viettel.vtman.cms.message.Const.STATUS_QUESTION.QUESTION_CLOSED;
import static com.viettel.vtman.cms.message.Const.STATUS_QUESTION.QUESTION_NEW;

@Repository
@Transactional
public class NotificationDAOImpl extends BaseFWDAOImpl<NotificationFaq> implements NotificationFaqDAO {
    @Override
    public Long insertNotificationFaq(NotificationFaq notificationFaq) {
        return this.saveObject(notificationFaq);
    }

    @Override
    public String updateNotificationFaq(NotificationFaq notificationFaq) {
        return this.update(notificationFaq);
    }

    @Override
    public NotificationFaq findById(Long notificationId) {
        return this.get(NotificationFaq.class, notificationId);
    }

    @Override
    public List<NotificationFaqDTO> findByEmployeeId(Long employeeId) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT notification_id as notificationId, notification_title as notificationTitle, ");
        sql.append("notification_name as notificationName, DATE_FORMAT(notification_date, '%Y-%m-%d %H:%i:%s') as notificationDateStr, ");
        sql.append("employee_id as employeeId, is_view as isView, notification_date as notificationDate, ");
        sql.append("history_faq_id as historyFaqId, topic_id as topicId, notification_type as notificationType ");
        sql.append("FROM NOTIFICATION_FAQ ");
        sql.append("WHERE employee_id = :employeeId ");
        sql.append("ORDER BY notification_date DESC");

        SQLQuery query = this.getSession().createSQLQuery(sql.toString());
        query.addScalar("notificationId", LongType.INSTANCE);
        query.addScalar("notificationTitle", StringType.INSTANCE);
        query.addScalar("notificationName", StringType.INSTANCE);
        query.addScalar("notificationDateStr", StringType.INSTANCE);
        query.addScalar("employeeId", LongType.INSTANCE);
        query.addScalar("isView", LongType.INSTANCE);
        query.addScalar("notificationDate", DateType.INSTANCE);
        query.addScalar("historyFaqId", LongType.INSTANCE);
        query.addScalar("topicId", LongType.INSTANCE);
        query.addScalar("notificationType", LongType.INSTANCE);
        query.setResultTransformer(Transformers.aliasToBean(NotificationFaqDTO.class));

        query.setParameter("employeeId", employeeId);
        List<NotificationFaqDTO> result = query.list();
        return result;
    }

    @Override
    public List<NotificationFaqDTO> cmsNotificationByEmployeeId(Long employeeId, int pageIndex, int pageSize) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT a.notification_id as notificationId, a.notification_title as notificationTitle, ");
        sql.append("b.HISTORY_FAQ_NAME as notificationName, DATE_FORMAT(a.notification_date, '%Y-%m-%d %H:%i:%s') as notificationDateStr, ");
        sql.append("a.employee_id as employeeId, a.is_view as isView, a.notification_date as notificationDate, ");
        sql.append("a.history_faq_id as historyFaqId, a.topic_id as topicId, a.notification_type as notificationType ");
        sql.append(",c.EMPLOYEE_CODE as createdByEmployeeCode ");
        sql.append(",c.EMPLOYEE_NAME as createdByEmployeeName ");
        sql.append(",d.DEPARTMENT_NAME as departmentName ");
        sql.append(",a.TYPE_CMS as typeCms ");
        sql.append("FROM NOTIFICATION_FAQ a ");
        sql.append("LEFT JOIN HISTORY_FAQ b ON a.HISTORY_FAQ_ID = b.HISTORY_FAQ_ID ");
        sql.append("LEFT JOIN EMPLOYEE c ON b.CREATED_BY = c.EMPLOYEE_ID ");
        sql.append("LEFT JOIN DEPARTMENT d ON b.DEPARTMENT_ID = d.DEPARTMENT_ID ");
        sql.append("WHERE a.employee_id = :employeeId ");
        sql.append("AND c.employee_id IS NOT NULL ");
        sql.append("AND (a.TYPE_CMS = :statusNew OR a.TYPE_CMS = :statusClose) ");
        sql.append("ORDER BY a.notification_date DESC");

        SQLQuery query = this.getSession().createSQLQuery(sql.toString());
        query.addScalar("notificationId", LongType.INSTANCE);
        query.addScalar("notificationTitle", StringType.INSTANCE);
        query.addScalar("notificationName", StringType.INSTANCE);
        query.addScalar("notificationDateStr", StringType.INSTANCE);
        query.addScalar("employeeId", LongType.INSTANCE);
        query.addScalar("isView", LongType.INSTANCE);
        query.addScalar("notificationDate", DateType.INSTANCE);
        query.addScalar("historyFaqId", LongType.INSTANCE);
        query.addScalar("topicId", LongType.INSTANCE);
        query.addScalar("notificationType", LongType.INSTANCE);
        query.addScalar("createdByEmployeeCode", StringType.INSTANCE);
        query.addScalar("createdByEmployeeName", StringType.INSTANCE);
        query.addScalar("departmentName", StringType.INSTANCE);
        query.addScalar("typeCms", LongType.INSTANCE);
        query.setResultTransformer(Transformers.aliasToBean(NotificationFaqDTO.class));

        query.setParameter("statusNew", QUESTION_NEW);
        query.setParameter("statusClose", QUESTION_CLOSED);

        query.setParameter("employeeId", employeeId);
//        query.setMaxResults(pageSize);
//        query.setFirstResult((pageIndex - 1) * pageSize);

        List<NotificationFaqDTO> result = query.list();
        return result;
    }

    @Override
    public String insertAll(List<NotificationFaq> lst) {
        return this.saveAll(lst);
    }

    @Override
    public List<NotificationFaq> findByHisFaqId(Long hisFaqId) {
        return this.findByProperties(NotificationFaq.class, "historyFaqId", hisFaqId);
    }

    @Override
    public void deleteIds(List<Long> notificationIds) {
        this.deleteByIds(notificationIds, NotificationFaq.class, "notificationId");
    }

    @Override
    public int getCmsUnreadNotificationCount(Long employeeId) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(a.notification_id) ");
        sql.append("FROM NOTIFICATION_FAQ a ");
        sql.append("LEFT JOIN HISTORY_FAQ b ON a.HISTORY_FAQ_ID = b.HISTORY_FAQ_ID ");
        sql.append("LEFT JOIN EMPLOYEE c ON b.CREATED_BY = c.EMPLOYEE_ID ");
        sql.append("LEFT JOIN DEPARTMENT d ON b.DEPARTMENT_ID = d.DEPARTMENT_ID ");
        sql.append("WHERE a.employee_id = :employeeId ");
        sql.append("AND c.employee_id IS NOT NULL ");
        sql.append("AND (a.TYPE_CMS = :statusNew OR a.TYPE_CMS = :statusClose) ");
        sql.append("AND a.is_view = 1 ");
        sql.append("ORDER BY a.notification_date DESC");

        SQLQuery query = this.getSession().createSQLQuery(sql.toString());
        query.setParameter("statusNew", QUESTION_NEW);
        query.setParameter("statusClose", QUESTION_CLOSED);

        query.setParameter("employeeId", employeeId);
        return ((BigInteger) query.getSingleResult()).intValue();
    }

    @Override
    public List<NotificationFaq> findByMultipleParams(Long employeeId, Long hisFaqId, Long topicId, Long typeCms) {
        return this.findByProperties(NotificationFaq.class, "employeeId", employeeId, "historyFaqId", hisFaqId, "topicId", topicId, "typeCms", typeCms);
    }
}
