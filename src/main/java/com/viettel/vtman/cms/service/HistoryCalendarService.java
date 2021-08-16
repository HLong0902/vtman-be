package com.viettel.vtman.cms.service;

import com.viettel.vtman.cms.dto.HistoryCalendarDTO;
import com.viettel.vtman.cms.entity.HistoryCalendar;

import java.util.List;

public interface HistoryCalendarService {
    HistoryCalendar insert(HistoryCalendar calendar);
    List<HistoryCalendarDTO> getHistoryCalendar();
}
