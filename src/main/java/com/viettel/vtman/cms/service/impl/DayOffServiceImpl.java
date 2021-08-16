package com.viettel.vtman.cms.service.impl;

import com.viettel.vtman.cms.dao.DayOffDAO;
import com.viettel.vtman.cms.dto.DayOffDTO;
import com.viettel.vtman.cms.entity.DayOff;
import com.viettel.vtman.cms.service.DayOffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DayOffServiceImpl  implements DayOffService {
    @Autowired
    private DayOffDAO dayOffDAO;
    @Override
    public List<DayOffDTO> findAll() {
        return dayOffDAO.findAll();
    }

    @Override
    public void deleteByDate(String dateStr) {
        dayOffDAO.deleteByDate(dateStr);
    }

    @Override
    public DayOffDTO findByDate(String dateStr) {
        return dayOffDAO.findByDate(dateStr);
    }

    @Override
    public DayOff insert(DayOff dayOff) {
        return dayOffDAO.insert(dayOff);
    }

    @Override
    public List<DayOffDTO> findAllInYear() {
        return dayOffDAO.findAllInYear();
    }
}
