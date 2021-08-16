package com.viettel.vtman.cms.dao;

import com.viettel.vtman.cms.dto.ObjectResultPage;
import com.viettel.vtman.cms.dto.UserAuthorizationDTO;

import java.util.List;

public interface UserAuthorizationDAO {
    List<UserAuthorizationDTO> getUserAuthorization(String employee, Long roleId, Long departmentId, ObjectResultPage objectResultPage);
    List<UserAuthorizationDTO> getByEmployeeId(Long employeeId);
}
