package com.viettel.vtman.cms.dao;

import com.viettel.vtman.cms.dto.WorkCalendarDTO;
import com.viettel.vtman.cms.entity.Topic;
import com.viettel.vtman.cms.entity.WorkCalendar;

import java.util.List;

public interface WorkCalendarDAO {

    List<WorkCalendarDTO> findAll();
    WorkCalendarDTO findAllByDays(String days);
    WorkCalendar updateWorkCalendar(WorkCalendar calendar);
    WorkCalendar findById(Long workCalendarId);
    List<WorkCalendarDTO> findByType();
    WorkCalendar insert(WorkCalendar calendar);
    List<WorkCalendar> getAllCalendars();
}
