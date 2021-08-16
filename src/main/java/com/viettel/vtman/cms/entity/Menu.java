package com.viettel.vtman.cms.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@NoArgsConstructor
@Entity
@Table(name = "MENU")
@Data
public class Menu {

    @Id
    @Column(name = "MENU_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long menuId;

    @Column(name = "MENU_NAME")
    private String menuName;

    @Column(name = "NUMBER_ORDER")
    private Long numberOrder;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "ICON")
    private String icon;

    @Column(name = "CREATED_DATE")
    private Date createdDate;

    @Column(name = "CREATED_BY")
    private Long createdBy;

    @Column(name = "UPDATED_DATE")
    private Date updatedDate;

    @Column(name = "UPDATED_BY")
    private Long updatedBy;

    @Column(name = "STATUS")
    private Long status;

    @Column(name = "MENU_PATH")
    private String menuPath;

    @Transient
    private Page propPage;

    public Menu(Menu menu, Page propPage) {
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
        }

        this.propPage = propPage;
    }


}
