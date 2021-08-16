package com.viettel.vtman.cms.dao.impl;

import com.viettel.vtman.cms.base.dao.BaseFWDAOImpl;
import com.viettel.vtman.cms.dao.TopicDAO;
import com.viettel.vtman.cms.dao.WorkCalendarDAO;
import com.viettel.vtman.cms.dto.TopicDto;
import com.viettel.vtman.cms.dto.WorkCalendarDTO;
import com.viettel.vtman.cms.entity.Employee;
import com.viettel.vtman.cms.entity.QuestionDefinition;
import com.viettel.vtman.cms.entity.Topic;
import com.viettel.vtman.cms.entity.WorkCalendar;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DateType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
@Transactional
public class WorkCalendarDAOImpl extends BaseFWDAOImpl<WorkCalendar> implements WorkCalendarDAO {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<WorkCalendarDTO> findAll() {
        StringBuilder sql = new StringBuilder();
        sql.append(" select a.WORK_CALENDAR_ID as workCalendarId,");
        sql.append("  a.DAYS as days,");
        sql.append("  a.STATUS as status,");
        sql.append("  a.END_WORK_TIME_AM as endWorkTimeAM,");
        sql.append("  a.BEGIN_WORK_TIME_AM as beginWorkTimeAM,");
        sql.append("  a.END_WORK_TIME_PM as endWorkTimePM,");
        sql.append("  a.BEGIN_WORK_TIME_PM as beginWorkTimePM,");
        sql.append("  a.CREATED_DATE as createdDate,");
        sql.append("  a.CREATED_BY as createdBy,");
        sql.append("  a.UPDATED_DATE as updatedDate,");
        sql.append("  a.IS_UPDATE as isUpdate,");
        sql.append("  a.UPDATED_BY as updatedBy ");
        sql.append("  from WORK_CALENDAR a");
        SQLQuery q = (SQLQuery) entityManager.createNativeQuery(sql.toString());
        q.addScalar("workCalendarId", LongType.INSTANCE);
        q.addScalar("days", StringType.INSTANCE);
        q.addScalar("status", LongType.INSTANCE);
        q.addScalar("endWorkTimeAM", StringType.INSTANCE);
        q.addScalar("beginWorkTimeAM", StringType.INSTANCE);
        q.addScalar("endWorkTimePM", StringType.INSTANCE);
        q.addScalar("beginWorkTimePM", StringType.INSTANCE);
        q.addScalar("createdDate", DateType.INSTANCE);
        q.addScalar("createdBy", LongType.INSTANCE);
        q.addScalar("updatedDate", DateType.INSTANCE);
        q.addScalar("updatedBy", LongType.INSTANCE);
        q.addScalar("isUpdate", StringType.INSTANCE);
        q.setResultTransformer(Transformers.aliasToBean(WorkCalendarDTO.class));
        List<WorkCalendarDTO> list = q.getResultList();

        return list;
    }

    @Override
    public WorkCalendarDTO findAllByDays(String days) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select a.WORK_CALENDAR_ID as workCalendarId,");
        sql.append("  a.DAYS as days,");
        sql.append("  a.STATUS as status,");
        sql.append("  a.END_WORK_TIME_AM as endWorkTimeAM,");
        sql.append("  a.BEGIN_WORK_TIME_AM as beginWorkTimeAM,");
        sql.append("  a.END_WORK_TIME_PM as endWorkTimePM,");
        sql.append("  a.BEGIN_WORK_TIME_PM as beginWorkTimePM,");
        sql.append("  a.CREATED_DATE as createdDate,");
        sql.append("  a.CREATED_BY as createdBy,");
        sql.append("  a.UPDATED_DATE as updatedDate,");
        sql.append("  a.IS_UPDATE as isUpdate,");
        sql.append("  a.UPDATED_BY as updatedBy ");
        sql.append("  from WORK_CALENDAR a");
        sql.append("  where a.DAYS = :days");
        SQLQuery q = (SQLQuery) entityManager.createNativeQuery(sql.toString());
        q.setParameter("days", days);
        q.addScalar("workCalendarId", LongType.INSTANCE);
        q.addScalar("days", StringType.INSTANCE);
        q.addScalar("status", LongType.INSTANCE);
        q.addScalar("isUpdate", StringType.INSTANCE);
        q.addScalar("endWorkTimeAM", StringType.INSTANCE);
        q.addScalar("beginWorkTimeAM", StringType.INSTANCE);
        q.addScalar("endWorkTimePM", StringType.INSTANCE);
        q.addScalar("beginWorkTimePM", StringType.INSTANCE);
        q.addScalar("createdDate", DateType.INSTANCE);
        q.addScalar("createdBy", LongType.INSTANCE);
        q.addScalar("updatedDate", DateType.INSTANCE);
        q.addScalar("updatedBy", LongType.INSTANCE);
        q.setResultTransformer(Transformers.aliasToBean(WorkCalendarDTO.class));
        List<WorkCalendarDTO> list = q.getResultList();
        if (list != null && list.size() > 0)
            return list.get(0);
        return null;

    }

    @Override
    public WorkCalendar updateWorkCalendar(WorkCalendar calendar) {
        this.update(calendar);
        return calendar;
    }

    @Override
    public WorkCalendar findById(Long workCalendarId) {
        return this.get(WorkCalendar.class, workCalendarId);
    }

    @Override
    public List<WorkCalendarDTO> findByType() {
        StringBuilder sql = new StringBuilder();
        sql.append(" select a.WORK_CALENDAR_ID as workCalendarId,");
        sql.append("  a.DAYS as days,");
        sql.append("  a.STATUS as status,");
        sql.append("  a.END_WORK_TIME_AM as endWorkTimeAM,");
        sql.append("  a.BEGIN_WORK_TIME_AM as beginWorkTimeAM,");
        sql.append("  a.END_WORK_TIME_PM as endWorkTimePM,");
        sql.append("  a.BEGIN_WORK_TIME_PM as beginWorkTimePM,");
        sql.append("  a.CREATED_DATE as createdDate,");
        sql.append("  a.CREATED_BY as createdBy,");
        sql.append("  a.UPDATED_DATE as updatedDate,");
        sql.append("  a.UPDATED_BY as updatedBy ");
        sql.append("  from WORK_CALENDAR a where a.IS_UPDATE = 'Y' ");
        SQLQuery q = (SQLQuery) entityManager.createNativeQuery(sql.toString());
        q.addScalar("workCalendarId", LongType.INSTANCE);
        q.addScalar("days", StringType.INSTANCE);
        q.addScalar("status", LongType.INSTANCE);
        q.addScalar("endWorkTimeAM", StringType.INSTANCE);
        q.addScalar("beginWorkTimeAM", StringType.INSTANCE);
        q.addScalar("endWorkTimePM", StringType.INSTANCE);
        q.addScalar("beginWorkTimePM", StringType.INSTANCE);
        q.addScalar("createdDate", DateType.INSTANCE);
        q.addScalar("createdBy", LongType.INSTANCE);
        q.addScalar("updatedDate", DateType.INSTANCE);
        q.addScalar("updatedBy", LongType.INSTANCE);
        q.setResultTransformer(Transformers.aliasToBean(WorkCalendarDTO.class));
        List<WorkCalendarDTO> list = q.getResultList();
        return list;
    }

    @Override
    public WorkCalendar insert(WorkCalendar calendar) {
        this.save(calendar);
        return calendar;
    }

    @Override
    public List<WorkCalendar> getAllCalendars() {
        return this.getAll(WorkCalendar.class);
    }

}
