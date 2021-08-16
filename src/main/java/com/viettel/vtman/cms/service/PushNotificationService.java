package com.viettel.vtman.cms.service;

import com.viettel.vtman.cms.dto.ObjectResultPage;
import com.viettel.vtman.cms.dto.PushNotificationDTO;

import java.util.List;

public interface PushNotificationService {
    List<PushNotificationDTO> getNotificationList();
    List<PushNotificationDTO> getAll(Long answerEmployeeId, ObjectResultPage objectResultPage);
}
