package com.viettel.vtman.cms.service.impl;

import com.viettel.vtman.cms.dao.PushNotificationDAO;
import com.viettel.vtman.cms.dto.ObjectResultPage;
import com.viettel.vtman.cms.dto.PushNotificationDTO;
import com.viettel.vtman.cms.service.PushNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PushNotificationServiceImpl implements PushNotificationService {
    @Autowired
    private PushNotificationDAO pushNotificationDAO;

    @Override
    public List<PushNotificationDTO> getNotificationList() {
        return pushNotificationDAO.getNotificationList();
    }

    @Override
    public List<PushNotificationDTO> getAll(Long answerEmployeeId, ObjectResultPage objectResultPage) {
        return pushNotificationDAO.getAll(answerEmployeeId, objectResultPage);
    }
}
