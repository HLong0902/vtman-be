package com.viettel.vtman.cms.service.impl;

import com.viettel.vtman.cms.dao.HistoryFaqDAO;
import com.viettel.vtman.cms.dao.HistoryFaqDetailDAO;
import com.viettel.vtman.cms.dto.HistoryFaqDetailCMSDTO;
import com.viettel.vtman.cms.service.HistoryFaqDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class HistoryFaqDetailServiceImpl implements HistoryFaqDetailService {
    @Autowired
    private HistoryFaqDetailDAO historyFaqDetailDAO;
    @Override
    public List<HistoryFaqDetailCMSDTO> detail(HistoryFaqDetailCMSDTO historyFaqDetailCMSDTO) {
        return historyFaqDetailDAO.detail(historyFaqDetailCMSDTO);
    }
}
