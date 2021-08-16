package com.viettel.vtman.cms.service.impl;

import com.viettel.vtman.cms.dao.HistoryCalendarDAO;
import com.viettel.vtman.cms.dto.HistoryCalendarDTO;
import com.viettel.vtman.cms.entity.HistoryCalendar;
import com.viettel.vtman.cms.service.HistoryCalendarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HistoryCalendarServiceImpl  implements HistoryCalendarService {
    @Autowired
    private HistoryCalendarDAO historyCalendarDAO;
    @Override
    public HistoryCalendar insert(HistoryCalendar calendar) {
        return historyCalendarDAO.insert(calendar);
    }

    @Override
    public List<HistoryCalendarDTO> getHistoryCalendar() {
        return historyCalendarDAO.getHistoryCalendar();
    }
}
