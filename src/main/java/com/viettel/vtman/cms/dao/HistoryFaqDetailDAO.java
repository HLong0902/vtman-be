package com.viettel.vtman.cms.dao;

import com.viettel.vtman.cms.dto.HistoryFaqDetailCMSDTO;
import com.viettel.vtman.cms.entity.HistoryFaqDetail;

import java.util.List;

public interface HistoryFaqDetailDAO {
    List<HistoryFaqDetailCMSDTO> detail(HistoryFaqDetailCMSDTO historyFaqDetailCMSDTO);
    HistoryFaqDetail findById(Long id);
    String updateHisFaqDetail(HistoryFaqDetail historyFaqDetail);
    String insertHisFaqDetail(HistoryFaqDetail hisFaqDetail);
}
