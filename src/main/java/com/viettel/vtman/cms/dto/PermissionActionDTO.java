package com.viettel.vtman.cms.dto;

import com.viettel.vtman.cms.entity.PermissionAction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PermissionActionDTO {

    private Long permissionActionId;
    private Long pageId;
    private Long roleId;
    private Long actionKey;
    private String actionId;
    private Long status;
    private Date createdDate;
    private Long createdBy;
    private Date updatedDate;
    private Long updatedBy;

    private PageDTO propPage;
    private MenuDTO propMenu;

    public PermissionActionDTO(PermissionAction permissionAction) {
        if (permissionAction != null) {
            this.permissionActionId = permissionAction.getPermissionActionId();
            this.pageId = permissionAction.getPageId();
            this.roleId = permissionAction.getRoleId();
            this.actionId = permissionAction.getActionId();
            this.status = permissionAction.getStatus();
            this.createdDate = permissionAction.getCreatedDate();
            this.createdBy = permissionAction.getCreatedBy();
            this.updatedDate = permissionAction.getUpdatedDate();
            this.updatedBy = permissionAction.getUpdatedBy();

            if (permissionAction.getPropPage() != null) {
                this.propPage = new PageDTO(permissionAction.getPropPage());
            }
            if (permissionAction.getPropMenu() != null) {
                this.propMenu = new MenuDTO(permissionAction.getPropMenu());
            }
        }
    }

}
