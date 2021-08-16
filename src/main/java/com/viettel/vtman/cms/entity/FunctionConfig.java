package com.viettel.vtman.cms.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity(name = "FUNCTION_CONFIG")
@Data
public class FunctionConfig {

    @Id
    @Column(name = "FUNCTION_CONFIG_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long functionConfigId;

    @Column(name = "DEPARTMENT_ID")
    private Long departmentId;

    @Column(name = "EMPLOYEE_ID")
    private String employeeId;

    @Column(name = "MAXIMUM_RESPONSE_TIME")
    private Long maximumResponseTime;

    @Column(name = "RESPONSE_REMINDING_TIME")
    private Long responseRemindingTime;

    @Column(name = "MAXIMUM_WAITING_TIME")
    private Double maximumWaitingTime;

    @Column(name = "REMINDING_WAITING_TIME")
    private Double remindingWaitingTime;

    @Column(name = "MAXIMUM_QA_SESSION")
    private Long maximumQASession;

    @Column(name = "ANSWER_KPI_PERCENT")
    private Long answerKPIPercent;

    @Column(name = "CREATED_DATE")
    private Date createdDate;

    @Column(name = "CREATED_BY")
    private Long createdBy;

    @Column(name = "UPDATED_DATE")
    private Date updatedDate;

    @Column(name = "UPDATED_BY")
    private Long updatedBy;
}
