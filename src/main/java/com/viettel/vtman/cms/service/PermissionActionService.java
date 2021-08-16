package com.viettel.vtman.cms.service;

import com.viettel.vtman.cms.dto.PermissionActionDTO;
import com.viettel.vtman.cms.entity.PermissionAction;

import java.util.List;

public interface PermissionActionService {

    List<PermissionActionDTO> getPermissionActionByDTO(PermissionActionDTO dto);

    PermissionActionDTO updateOrSave(PermissionActionDTO permissionActionDTO);

    PermissionAction findByPageId(Long pageId);

    String deleteByDTO(PermissionActionDTO permissionActionDTO);

}
