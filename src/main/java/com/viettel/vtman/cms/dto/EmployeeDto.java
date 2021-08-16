package com.viettel.vtman.cms.dto;

import com.viettel.vtman.cms.entity.Employee;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDto {
    private Long employeeId;
    private Long departmentId;
    private Long status;
    private String description;
    private String email;
    private String employeeCode;
    private String employeeName;
    private Long positionId;
    private Long isActive;
    private Date createdDate;
    private Long createdBy;
    private Date updatedDate;
    private Long updatedBy;
    private Long userId;
    private Long roleId;
    private String userName;
    private Map<String, Object> jwtPayload;
    private String phone;
    private String postOfficeCode;

    private RoleDTO roleDTO;

    /*
    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public Long getPositionId() {
        return positionId;
    }

    public void setPositionId(Long positionId) {
        this.positionId = positionId;
    }

    public Long getIsActive() {
        return isActive;
    }

    public void setIsActive(Long isActive) {
        this.isActive = isActive;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    public Long getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }
    */

    public EmployeeDto(Employee employee) {
        this.employeeId = employee.getEmployeeId();
        this.employeeName = employee.getEmployeeName();
        this.departmentId = employee.getDepartmentId();
        this.status = employee.getStatus();
        this.description = employee.getDescription();
        this.createdDate = employee.getCreatedDate();
        this.createdBy = employee.getCreatedBy();
        this.updatedDate = employee.getUpdatedDate();
        this.updatedBy = employee.getUpdatedBy();
        this.email= employee.getEmail();
        this.employeeCode = employee.getEmployeeCode();
        this.positionId = employee.getPositionId();
        this.userId = employee.getUserId();
        this.roleId = employee.getRoleId();
        this.userName = employee.getUserName();
        this.phone = employee.getPhone();
        this.postOfficeCode = employee.getPostOfficeCode();
    }
}
