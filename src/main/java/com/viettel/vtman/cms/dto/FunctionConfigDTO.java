package com.viettel.vtman.cms.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FunctionConfigDTO {
    private Long departmentId;
    private String departmentName;
    private String employeeId;
    private String employeeName;
    private Long maximumResponseTime;
    private Long responseRemindingTime;
    private Double maximumWaitingTime;
    private Double remindingWaitingTime;
    private Long maximumQASession;
    private Long answerKPIPercent;
    private Long functionConfigId;
    private Long createdBy;
    private Date createdDate;
    private Date updatedDate;
    private Long updatedBy;
}
