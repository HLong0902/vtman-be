package com.viettel.vtman.cms.entity;

import lombok.Data;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity(name = "EVALUATE")
@Data
public class Evaluate {
    @Id
    @Column(name = "EVALUATE_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long evaluateId;
    @Column(name = "EVALUATE_NAME")
    private String evaluateName;
    @Column(name = "EMPLOYEE_ID")
    private Long employeeId;
    @Column(name = "CREATED_DATE")
    private Date createdDate;
}
