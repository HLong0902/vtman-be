package com.viettel.vtman.cms.service;

import com.viettel.vtman.cms.dto.AppInfoDTO;
import com.viettel.vtman.cms.dto.HisFaqDetailInputDTO;
import com.viettel.vtman.cms.dto.HisFaqDetailOutDTO;
import com.viettel.vtman.cms.dto.HisFaqPushFirebaseDTO;
import com.viettel.vtman.cms.dto.HistoryFaqCMSDTO;
import com.viettel.vtman.cms.dto.HistoryFaqDTO;
import com.viettel.vtman.cms.dto.HistoryFaqPostReqDTO;
import com.viettel.vtman.cms.dto.NotificationFaqDTO;
import com.viettel.vtman.cms.entity.NotificationFaq;
//import com.viettel.vtman.cms.entity.TokenFcm;

import java.text.ParseException;
import java.util.List;

public interface HistoryFaqService {
    List<HistoryFaqDTO> getHistoryFaqs(Long employeeId, String keySearch, Boolean isAnswer, Long type);
    String insertHisFaq(HistoryFaqPostReqDTO dto) throws Exception;
    String checkInsertHisFaq(HistoryFaqPostReqDTO dto);
    HistoryFaqDTO getHisFaqDetailById(Long employeeId, Long hisFaqId, Long topicId);
    List<HistoryFaqCMSDTO> search(HistoryFaqCMSDTO historyFaqDTO) throws ParseException;
    HisFaqDetailOutDTO insertDetail(HisFaqDetailInputDTO dto) throws Exception;

    Long countNotificationHisFaqs(Long employeeId);

    List<NotificationFaqDTO> getNotificationFaqs(Long employeeId) throws Exception;
    HistoryFaqCMSDTO getHisFaqByIdFaq(Long historyFaqId);
    Long countReceived(List<HistoryFaqDTO> lstHisFaqDTO);

    String activeNotification(Long employeeId) throws Exception;
    String updateEvaluate(HistoryFaqPostReqDTO dto) throws Exception;
    String forwardHisFaqByTopic(HistoryFaqPostReqDTO dto) throws Exception;
    List<HisFaqPushFirebaseDTO> getHisFaqPushFireBase() throws Exception;

//    String insertTokenFcm(TokenFcm tokenFcm);
    String pushFirebaseNotification();
    String jobUpdateStatusExpireDate();
    String closeHistoryFaq(HistoryFaqPostReqDTO dto) throws Exception;
    String cancelHistoryFaq(HistoryFaqPostReqDTO dto) throws Exception;
    AppInfoDTO getAppInfo(Long employeeId);
    List<HistoryFaqDTO> countTopicId(Long topicId);

    List<NotificationFaqDTO> getCmsNotificationFaqs(Long employeeId, int pageIndex, int pageSize) throws Exception;
    int getCmsUnreadNotificationCount(Long employeeId);
    String readCmsNotificationFaqs(Long notificationId);

    List<HistoryFaqDTO> checkQuestionStatus(Long departmentId);
    List<NotificationFaq> findNotificationFaqByParams(Long employeeId, Long hisFaqId, Long topicId, Long typeCms);
}
