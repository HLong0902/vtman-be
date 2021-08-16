package com.viettel.vtman.cms.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "PAGE")
public class Page {
    @Id
    @Column(name = "PAGE_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pageId;

    @Column(name = "PAGE_CODE")
    private String pageCode;

    @Column(name = "PAGE_NAME")
    private String pageName;

    @Column(name = "MENU_ID")
    private Long menuId;

    @Column(name = "NUMBER_ORDER")
    private Long numberOrder;

    @Column(name = "COMPONENT")
    private String component;

    @Column(name = "PATH")
    private String path;

    @Column(name = "AVAILABLE_ACTION_ID")
    private String availableActionId;

    @Column(name = "STATUS")
    private Long status;

    @Column(name = "CREATED_DATE")
    private Date createdDate;

    @Column(name = "CREATED_BY")
    private Long createdBy;

    @Column(name = "UPDATED_DATE")
    private Date updatedDate;

    @Column(name = "UPDATED_BY")
    private Long updatedBy;

    @Transient
    private Menu propMenu;

    @Transient
    private PermissionAction propPermissionAction;

    @Transient
    private Long isSystemPage;

    public Page(Page page, Menu propMenu, PermissionAction propPermissionAction) {
        this.pageId = page.pageId;
        this.pageCode = page.pageCode;
        this.pageName = page.pageName;
        this.menuId = page.menuId;
        this.numberOrder = page.numberOrder;
        this.component = page.component;
        this.path = page.path;
        this.availableActionId = page.availableActionId;
        this.status = page.status;
        this.createdDate = page.createdDate;
        this.createdBy = page.createdBy;
        this.updatedDate = page.updatedDate;
        this.updatedBy = page.updatedBy;

        this.propMenu = propMenu;
        this.propPermissionAction = propPermissionAction;
    }

    public Page(Page page, Menu propMenu) {
        this(page, propMenu, null);
    }
}
