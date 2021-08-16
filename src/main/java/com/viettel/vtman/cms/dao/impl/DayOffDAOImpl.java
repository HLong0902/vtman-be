package com.viettel.vtman.cms.dao.impl;


import com.viettel.vtman.cms.base.dao.BaseFWDAOImpl;
import com.viettel.vtman.cms.dao.DayOffDAO;
import com.viettel.vtman.cms.dto.DayOffDTO;
import com.viettel.vtman.cms.dto.TopicDto;
import com.viettel.vtman.cms.dto.WorkCalendarDTO;
import com.viettel.vtman.cms.entity.DayOff;
import com.viettel.vtman.cms.entity.Department;
import org.apache.commons.collections.CollectionUtils;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@Transactional
public class DayOffDAOImpl extends BaseFWDAOImpl<DayOff> implements DayOffDAO {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<DayOffDTO> findAll() {
        StringBuilder sql = new StringBuilder();
        sql.append(" select day (DAY_OFF.DATE) as days,month (DAY_OFF.DATE)  as months,year (DAY_OFF.DATE) as years, DATE as fullYears, DATE_STR as dateStr, dayofweek(DATE) as dayOfWeek   from DAY_OFF order by DATE asc");
        SQLQuery q = (SQLQuery) entityManager.createNativeQuery(sql.toString());
        q.addScalar("days", StringType.INSTANCE);
        q.addScalar("months", StringType.INSTANCE);
        q.addScalar("years", StringType.INSTANCE);
        q.addScalar("fullYears", DateType.INSTANCE);
        q.addScalar("dateStr", StringType.INSTANCE);
        q.addScalar("dayOfWeek", LongType.INSTANCE);
        q.setResultTransformer(Transformers.aliasToBean(DayOffDTO.class));
        List<DayOffDTO> list = q.getResultList();

        return list;
    }

    @Override
    public void deleteByDate(String  dateStr) {
//        StringBuilder sql = new StringBuilder();
//        sql.append(" delete from DAY_OFF where DATE =:date");
//        SQLQuery q = (SQLQuery) entityManager.createNativeQuery(sql.toString());
//        q.setParameter("date", date);
//        q.executeUpdate();
        this.deleteByParam(dateStr, DayOff.class, "dateStr" );
    }

    @Override
    public DayOffDTO findByDate(String dateStr) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select a.DAY_OFF_ID as dayOffId");

        sql.append("  from DAY_OFF a");
        sql.append("  where a.DATE_STR = :dateStr");
        SQLQuery q = (SQLQuery) entityManager.createNativeQuery(sql.toString());
        q.setParameter("dateStr", dateStr);
        q.addScalar("dayOffId", LongType.INSTANCE);
        q.setResultTransformer(Transformers.aliasToBean(DayOffDTO.class));
        List<DayOffDTO> list = q.getResultList();
        if (list != null && list.size() > 0)
            return list.get(0);
        return null;
    }

    @Override
    public List<String> getHolidays() {
        List<String> result = new ArrayList<>();
        List<DayOff> lstDayOff = this.getAll(DayOff.class);
        if (CollectionUtils.isNotEmpty(lstDayOff)) {
            result = lstDayOff.stream().map(DayOff::getDateStr).collect(Collectors.toList());
        }
        return result;
    }

    @Override
    public DayOff insert(DayOff dayOff) {
        this.save(dayOff);
        return dayOff;
    }

    @Override
    public List<DayOffDTO> findAllInYear() {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT a ");
        sql.append(" FROM DayOff a ");
        sql.append(" WHERE YEAR(a.date) = :yearParam ");
        sql.append(" ORDER BY a.date ASC NULLS LAST ");

        Query query = entityManager.createQuery(sql.toString());
        query.setParameter("yearParam", Calendar.getInstance().get(Calendar.YEAR));

        return (List<DayOffDTO>) query.getResultList().stream().map(e -> new DayOffDTO((DayOff) e)).collect(Collectors.toList());
    }
}
