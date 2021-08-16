package com.viettel.vtman.cms.dto;

import com.viettel.vtman.cms.entity.Page;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PageDTO {
    private String pageCode;
    private String pageName;
    private Long menuId;
    private String menuName;
    private String actionName;
    private String availableActionId;
    private Long numberOrder;
    private String path;
    private Long status;
    private List<PageDTO> data;
    private Long totalRecord;
    private Long page;
    private Long pageSize;
    private Long start;
    private String messageError;
    private Long createdBy;
    private Date createdDate;
    private Long updatedBy;
    private Date updatedDate;
    private Long pageId;
    private String component;

    private MenuDTO propMenu;
    private PermissionActionDTO propPermissionAction;
    private Long isSystemPage;

    public PageDTO(Page page) {
        if (page != null) {
            this.pageId = page.getPageId();
            this.pageCode = page.getPageCode();
            this.pageName = page.getPageName();
            this.menuId = page.getMenuId();
            this.numberOrder = page.getNumberOrder();
            this.path = page.getPath();
            this.status = page.getStatus();
            this.createdDate = page.getCreatedDate();
            this.createdBy = page.getCreatedBy();
            this.updatedDate = page.getUpdatedDate();
            this.updatedBy = page.getUpdatedBy();
            this.availableActionId = page.getAvailableActionId();
            this.component = page.getComponent();

            if (page.getPropMenu() != null) {
                this.propMenu = new MenuDTO(page.getPropMenu());
            }
            if (page.getPropPermissionAction() != null) {
                this.propPermissionAction = new PermissionActionDTO(page.getPropPermissionAction());
            }
        }
    }

}
