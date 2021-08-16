package com.viettel.vtman.cms.dao.impl;

import com.viettel.vtman.cms.base.dao.BaseFWDAOImpl;
import com.viettel.vtman.cms.dao.HistoryCalendarDAO;
import com.viettel.vtman.cms.dto.HistoryCalendarDTO;
import com.viettel.vtman.cms.dto.TopicDto;
import com.viettel.vtman.cms.entity.HistoryCalendar;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DateType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.hibernate.type.TimestampType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
@Transactional
public class HistoryCalendarDAOImpl extends BaseFWDAOImpl<HistoryCalendar> implements HistoryCalendarDAO {
    @PersistenceContext
    private EntityManager entityManager;
    @Override
    public HistoryCalendar insert(HistoryCalendar calendar) {
        this.save(calendar);
        return calendar;

    }

    @Override
    public List<HistoryCalendarDTO> getHistoryCalendar() {
        StringBuilder sql = new StringBuilder();
        sql.append(" select a.HISTORY_CALENDAR_ID as historyCalendarId,");
        sql.append("  a.WORK_CALENDAR_ID as workCalendarId,");
        sql.append("  a.CREATE_DATE as createDate,");
        sql.append("  a.STATUS as status,");
        sql.append("  a.DATE as date,");
        sql.append("  a.DATE_STR as dateStr");
        sql.append("  from HISTORY_CALENDAR a WHERE a.CREATE_DATE = (select MAX(CREATE_DATE)  from HISTORY_CALENDAR WHERE WORK_CALENDAR_ID = a.WORK_CALENDAR_ID ) ");
        SQLQuery q = (SQLQuery) entityManager.createNativeQuery(sql.toString());
        q.addScalar("historyCalendarId", LongType.INSTANCE);
        q.addScalar("workCalendarId", LongType.INSTANCE);
        q.addScalar("createDate", DateType.INSTANCE);
        q.addScalar("status", LongType.INSTANCE);
        q.addScalar("date", DateType.INSTANCE);
        q.addScalar("dateStr", StringType.INSTANCE);
        q.setResultTransformer(Transformers.aliasToBean(HistoryCalendarDTO.class));
        List<HistoryCalendarDTO> list = q.getResultList();
        return list;
    }
}
