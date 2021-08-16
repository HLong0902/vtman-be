package com.viettel.vtman.cms.service;

import com.viettel.vtman.cms.dto.WorkCalendarDTO;
import com.viettel.vtman.cms.entity.WorkCalendar;

import java.util.List;

public interface WorkCalendarService {
    public List<WorkCalendarDTO> findAll();
    public WorkCalendarDTO getByDay(String days);
    WorkCalendar updateWorkCalendar(WorkCalendar calendar);
    WorkCalendar findById(Long workCalendarId);
    List<WorkCalendarDTO> findByType();
    WorkCalendar insert(WorkCalendar calendar);
}
