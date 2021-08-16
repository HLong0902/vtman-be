package com.viettel.vtman.cms.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "TOPIC")
@Data
public class Topic {

    @Id
    @Column(name = "TOPIC_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long topicId;
    @Column(name = "TOPIC_NAME")
    private String topicName;
    @Column(name = "DEPARTMENT_ID")
    private Long departmentId;
    @Column(name = "ANSWER_EMPLOYEE_ID")
    private String answerEmployeeId;
    @Column(name = "NUMBER_ORDER")
    private Long numberOrder;
    @Column(name = "DESCRIPTION")
    private String description;
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
    @Column(name = "TOPIC_CODE")
    private String topicCode;

}
