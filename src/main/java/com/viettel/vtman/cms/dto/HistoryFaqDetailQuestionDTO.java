package com.viettel.vtman.cms.dto;

import lombok.Data;

import java.sql.Timestamp;
import java.util.Date;

@Data
public class HistoryFaqDetailQuestionDTO {
    private String employeeCodeQuestion;
    private String employeeNameQuestion;
    private String hisFaqDetailName;
    private Timestamp createdDate;
    private String createdDateResult;
    private String departmentName;
    private String topicName;
    private String historyFaqCode;
    private String historyFaqName;
    private String employeePostOfficeCode;




}
