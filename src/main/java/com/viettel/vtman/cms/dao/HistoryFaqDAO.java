package com.viettel.vtman.cms.dao;

import com.viettel.vtman.cms.dto.HisFaqNotificationDTO;
import com.viettel.vtman.cms.dto.HistoryFaqCMSDTO;
import com.viettel.vtman.cms.dto.HistoryFaqDTO;
import com.viettel.vtman.cms.dto.HistoryFaqDetailDTO;
import com.viettel.vtman.cms.entity.HistoryFaq;

import java.text.ParseException;
import java.util.List;

public interface HistoryFaqDAO {
    List<HistoryFaqDTO> getHistoryFaqs(Long employeeId, Boolean role, String keySearch, Long type);
    List<HistoryFaqDTO> getHistoryFaqWithoutTopic(Long employeeId, Boolean role, String keySearch, Long type);
    Long insertHisFaq(HistoryFaq historyFaq);
    HistoryFaq findById(Long id);
    String updateHisFaq(HistoryFaq historyFaq);
    HistoryFaqDTO getHisFaqById(Long hisFaqId, Long topicId);
    HistoryFaqCMSDTO getHisFaqByIdFaq(Long historyFaqId);
    List<HistoryFaqDetailDTO> getHisFaqDetailByHisFaqId(Long hisFaqId);
    List<HistoryFaqCMSDTO> search(HistoryFaqCMSDTO historyFaqDTO) throws ParseException;
    List<HistoryFaqDTO> countTopicId(Long topicId);
    List<HisFaqNotificationDTO> getHistoryFaqPushNotification();
    List<HistoryFaq> findByAnswerIsNull(Long employeeId);
    List<HistoryFaqDTO> checkQuestionStatus(Long departmentId);
}
