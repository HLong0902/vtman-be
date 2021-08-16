package com.viettel.vtman.cms.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;
@Data
public class TopicDto {
    private String answerEmployeeId;
    private Long topicId;
    private String topicName;
    private Long departmentId;
   /* private Long answerEmployeeId;*/
    private Long numberOrder;
    private String description;
    private Long isActive;
    private Date createdDate;
    private Long createdBy;
    private Date updatedDate;
    private Long updatedBy;
    private String employName;
    private Integer page;
    private Integer pageSize;
    private Integer totalRecord;
    private int start;
    private List data;
    private String departmentName;
    private String topicCode;
    private Long status;
    private String answerEmployeeName;
    private List<TopicDto> resultName;
    private String employeeCode;
    private String messageError;


}
