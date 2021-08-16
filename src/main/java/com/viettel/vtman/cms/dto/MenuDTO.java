package com.viettel.vtman.cms.dto;

import com.viettel.vtman.cms.entity.Menu;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MenuDTO {

    private Long menuId;
    private String menuName;
    private Long numberOrder;
    private String description;
    private String icon;
    private Date createdDate;
    private Long createdBy;
    private Date updatedDate;
    private Long updatedBy;
    private Long status;
    private String menuPath;

    private PageDTO propPage;

    public MenuDTO(Menu menu) {
        if (menu != null) {
            this.menuId = menu.getMenuId();
            this.menuName = menu.getMenuName();
            this.numberOrder = menu.getNumberOrder();
            this.description = menu.getDescription();
            this.icon = menu.getIcon();
            this.createdDate = menu.getCreatedDate();
            this.createdBy = menu.getCreatedBy();
            this.updatedDate = menu.getUpdatedDate();
            this.updatedBy = menu.getUpdatedBy();
            this.status = menu.getStatus();
            this.menuPath = menu.getMenuPath();

            if (menu.getPropPage() != null) {
                this.propPage = new PageDTO(menu.getPropPage());
            }
        }
    }

}
