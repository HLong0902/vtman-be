package com.viettel.vtman.cms.dto;

import com.viettel.vtman.cms.entity.Role;
import com.viettel.vtman.cms.infrastructure.enumeration.RoleGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.StringUtils;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoleDTO {
    private Long roleId;
    private String roleName;
    private String description;
    private Long status;
    private Date createdDate;
    private Long createdBy;
    private Date updatedDate;
    private Long updatedBy;
    private String roleGroup;

    public RoleDTO(Role entity) {
        this.roleId = entity.getRoleId();
        this.roleName = entity.getRoleName();
        this.description = entity.getDescription();
        this.status = entity.getStatus();
        this.createdBy = entity.getCreatedBy();
        this.createdDate = entity.getCreatedDate();
        this.updatedBy = entity.getUpdatedBy();
        this.updatedDate = entity.getUpdatedDate();
        this.roleGroup = entity.getRoleGroup();
    }
}
