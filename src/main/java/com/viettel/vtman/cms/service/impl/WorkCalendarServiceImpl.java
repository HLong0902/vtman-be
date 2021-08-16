package com.viettel.vtman.cms.service.impl;

import com.viettel.vtman.cms.dao.WorkCalendarDAO;
import com.viettel.vtman.cms.dto.WorkCalendarDTO;
import com.viettel.vtman.cms.entity.WorkCalendar;
import com.viettel.vtman.cms.service.WorkCalendarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkCalendarServiceImpl implements WorkCalendarService {
    @Autowired
    private WorkCalendarDAO workCalendarDAO;
    @Override
    public List<WorkCalendarDTO> findAll() {
        return  workCalendarDAO.findAll();
    }

    @Override
    public WorkCalendarDTO getByDay(String days) {
        return workCalendarDAO.findAllByDays(days);
    }

    @Override
    public WorkCalendar updateWorkCalendar(WorkCalendar calendar) {
        return workCalendarDAO.updateWorkCalendar(calendar);
    }

    @Override
    public WorkCalendar findById(Long workCalendarId) {
        return workCalendarDAO.findById(workCalendarId);
    }

    @Override
    public List<WorkCalendarDTO> findByType() {
        return workCalendarDAO.findByType();
    }

    @Override
    public WorkCalendar insert(WorkCalendar calendar) {
        return workCalendarDAO.insert(calendar);
    }
}
