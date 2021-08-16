package com.viettel.vtman.cms.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserAuthorizationDTO {
    private Long employeeId;
    private String employeeCode;
    private String employeeName;
    private Long departmentId;
    private String departmentName;
    private String email;
    private Long roleId;
    private String roleName;
    private Long page;
    private Long pageSize;
    private Long totalRecord;
    private List<UserAuthorizationDTO> data;
    private String employee;
    private String postOfficeCode;
    private String phone;
}
