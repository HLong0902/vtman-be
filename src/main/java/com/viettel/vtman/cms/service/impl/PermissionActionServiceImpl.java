package com.viettel.vtman.cms.service.impl;

import com.viettel.vtman.cms.dao.PermissionActionDAO;
import com.viettel.vtman.cms.dto.EmployeeDto;
import com.viettel.vtman.cms.dto.PermissionActionDTO;
import com.viettel.vtman.cms.entity.PermissionAction;
import com.viettel.vtman.cms.service.EmployeeService;
import com.viettel.vtman.cms.service.PermissionActionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PermissionActionServiceImpl implements PermissionActionService {

    @Autowired
    private PermissionActionDAO permissionActionDAO;

    @Autowired
    private EmployeeService employeeService;

    @Override
    public List<PermissionActionDTO> getPermissionActionByDTO(PermissionActionDTO dto) {
        return permissionActionDAO.listPagePermissionByDTO(dto).stream().map(PermissionActionDTO::new).collect(Collectors.toList());
    }

    @Override
    public PermissionActionDTO updateOrSave(PermissionActionDTO permissionActionDTO) {
        EmployeeDto user = employeeService.getOrCreateFromJwt();

        PermissionAction permissionAction = permissionActionDAO.getByDTO(permissionActionDTO);
        if (permissionAction != null) {
            permissionAction.setActionId(permissionActionDTO.getActionId());
            permissionAction.setUpdatedBy(user.getEmployeeId());
            permissionAction = permissionActionDAO.updatePermissionAction(permissionAction);
            return permissionAction == null ? null : new PermissionActionDTO(permissionAction);
        } else {
            permissionAction = new PermissionAction();
            permissionAction.setPageId(permissionActionDTO.getPageId());
            permissionAction.setRoleId(permissionActionDTO.getRoleId());
            permissionAction.setActionId(permissionActionDTO.getActionId());
            permissionAction.setStatus(1L);
            permissionAction.setCreatedBy(user.getEmployeeId());
            permissionAction.setUpdatedBy(user.getEmployeeId());
            permissionAction = permissionActionDAO.savePermissionAction(permissionAction);
            return permissionAction == null ? null : new PermissionActionDTO(permissionAction);
        }
    }

    @Override
    public PermissionAction findByPageId(Long pageId) {
        return permissionActionDAO.findByPageId(pageId);
    }

    @Override
    public String deleteByDTO(PermissionActionDTO permissionActionDTO) {
        return permissionActionDAO.deleteByDTO(permissionActionDTO);
    }
}
