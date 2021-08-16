package com.viettel.vtman.cms.dao;

import com.viettel.vtman.cms.dto.DayOffDTO;
import com.viettel.vtman.cms.dto.WorkCalendarDTO;
import com.viettel.vtman.cms.entity.DayOff;

import java.util.List;

public interface DayOffDAO {
    List<DayOffDTO> findAll();
    void deleteByDate(String dateStr);
    DayOffDTO findByDate(String dateStr);
    List<String> getHolidays();
    DayOff insert(DayOff dayOff);
    List<DayOffDTO> findAllInYear();
}
