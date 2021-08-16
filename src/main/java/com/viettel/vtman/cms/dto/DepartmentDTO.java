package com.viettel.vtman.cms.dto;

import com.viettel.vtman.cms.entity.Department;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentDTO {

    private Long departmentId;

    private String departmentName;

    private String departmentCode;

    private String description;

    private Long status;

    private Date createdDate;

    private Long createdBy;

    private Date updatedDate;

    private Long updatedBy;

    private Integer pageIndex;

    private Integer pageSize;

    private Long departmentIdUniqueCheck;

    private String departmentNameUniqueCheck;

    private String departmentCodeUniqueCheck;

    public DepartmentDTO(Department entity) {
        this.departmentId = entity.getDepartmentId();
        this.departmentName = entity.getDepartmentName();
        this.departmentCode = entity.getDepartmentCode();
        this.description = entity.getDescription();
        this.status = entity.getStatus();
        this.createdDate = entity.getCreatedDate();
        this.createdBy = entity.getCreatedBy();
        this.updatedDate = entity.getUpdatedDate();
        this.updatedBy = entity.getUpdatedBy();
    }
}
