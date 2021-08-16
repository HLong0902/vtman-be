package com.viettel.vtman.cms.dao;

import com.viettel.vtman.cms.dto.NotificationFaqDTO;
import com.viettel.vtman.cms.entity.NotificationFaq;

import java.util.List;

public interface NotificationFaqDAO {
    Long insertNotificationFaq(NotificationFaq notificationFaq);
    String updateNotificationFaq(NotificationFaq notificationFaq);
    NotificationFaq findById(Long notificationId);
    List<NotificationFaqDTO> findByEmployeeId(Long employeeId);
    List<NotificationFaqDTO> cmsNotificationByEmployeeId(Long employeeId, int pageIndex, int pageSize);
    String insertAll(List<NotificationFaq> lst);
    List<NotificationFaq> findByHisFaqId(Long hisFaqId);
    void deleteIds(List<Long> notificationIds);
    int getCmsUnreadNotificationCount(Long employeeId);
    List<NotificationFaq> findByMultipleParams(Long employeeId, Long hisFaqId, Long topicId, Long typeCms);
}
