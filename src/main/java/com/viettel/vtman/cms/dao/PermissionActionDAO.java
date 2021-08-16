package com.viettel.vtman.cms.dao;

import com.viettel.vtman.cms.dto.PermissionActionDTO;
import com.viettel.vtman.cms.entity.PermissionAction;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermissionActionDAO {

    List<PermissionAction> listPagePermissionByDTO(PermissionActionDTO dto);

    PermissionAction getByDTO(PermissionActionDTO dto);

    PermissionAction updatePermissionAction(PermissionAction permissionAction);

    PermissionAction savePermissionAction(PermissionAction permissionAction);

    PermissionAction findByPageId(Long pageId);

    String deleteByDTO(PermissionActionDTO permissionActionDTO);

}
