package com.viettel.vtman.cms.service;

import com.viettel.vtman.cms.dto.DayOffDTO;
import com.viettel.vtman.cms.entity.DayOff;

import java.util.List;

public interface DayOffService {
    List<DayOffDTO> findAll();
    void deleteByDate(String dateStr);
    DayOffDTO findByDate(String dateStr);
    DayOff insert(DayOff dayOff);
    List<DayOffDTO> findAllInYear();
}
