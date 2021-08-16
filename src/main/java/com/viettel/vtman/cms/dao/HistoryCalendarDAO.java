package com.viettel.vtman.cms.dao;

import com.viettel.vtman.cms.dto.HistoryCalendarDTO;
import com.viettel.vtman.cms.entity.HistoryCalendar;

import java.util.List;

public interface HistoryCalendarDAO {
    HistoryCalendar insert(HistoryCalendar calendar);
    List<HistoryCalendarDTO> getHistoryCalendar();
}
