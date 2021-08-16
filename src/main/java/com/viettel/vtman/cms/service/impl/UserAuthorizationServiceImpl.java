package com.viettel.vtman.cms.service.impl;

import com.viettel.vtman.cms.dao.UserAuthorizationDAO;
import com.viettel.vtman.cms.dto.ObjectResultPage;
import com.viettel.vtman.cms.dto.UserAuthorizationDTO;
import com.viettel.vtman.cms.service.UserAuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserAuthorizationServiceImpl implements UserAuthorizationService {
    @Autowired
    private UserAuthorizationDAO userAuthorizationDAO;

    @Override
    public List<UserAuthorizationDTO> searchUserAuthorization(String employee, Long roleId, Long departmentId, ObjectResultPage objectResultPage) {
        return userAuthorizationDAO.getUserAuthorization(employee, roleId, departmentId, objectResultPage);
    }

    @Override
    public List<UserAuthorizationDTO> getByEmployeeId(Long employeeId) {
        return userAuthorizationDAO.getByEmployeeId(employeeId);
    }
}
