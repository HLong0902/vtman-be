package com.viettel.vtman.cms.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "PERMISSION_ACTION")
public class PermissionAction {
    @Id
    @Column(name = "PERMISSION_ACTION_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long permissionActionId;

    @Column(name = "PAGE_ID")
    private Long pageId;

    @Column(name = "ROLE_ID")
    private Long roleId;

    @Column(name = "ACTION_ID")
    private String actionId;

    @Column(name = "STATUS")
    private Long status;

    @Column(name = "CREATED_DATE")
    @CreationTimestamp
    private Date createdDate;

    @Column(name = "CREATED_BY")
    private Long createdBy;

    @Column(name = "UPDATED_DATE")
    @UpdateTimestamp
    private Date updatedDate;

    @Column(name = "UPDATED_BY")
    private Long updatedBy;

    @Transient
    private Menu propMenu;

    @Transient
    private Page propPage;

    public PermissionAction(PermissionAction permissionAction, Page page, Menu menu) {
        this.permissionActionId = permissionAction.permissionActionId;
        this.pageId = permissionAction.pageId;
        this.roleId = permissionAction.roleId;
        this.actionId = permissionAction.actionId;
        this.status = permissionAction.status;
        this.createdDate = permissionAction.createdDate;
        this.createdBy = permissionAction.createdBy;
        this.updatedDate = permissionAction.updatedDate;
        this.updatedBy = permissionAction.updatedBy;

        this.propPage = page;
        this.propMenu = menu;
    }
}
