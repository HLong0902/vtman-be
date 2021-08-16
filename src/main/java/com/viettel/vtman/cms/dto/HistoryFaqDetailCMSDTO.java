package com.viettel.vtman.cms.dto;

import lombok.Data;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Data
public class HistoryFaqDetailCMSDTO {
    private Long hisFaqDetailId;
    private String historyFaqCode;
    private String historyFaqName;
    private Long topicId;
    private String hisFaqDetailName;
    private Timestamp createdDate;
    private String answer;
    private Long answerEmployeeId;
    private Long status;
    private Long historyFaqId;
    private Long hisFaqId;
    private Long createdBy;
    private Timestamp answerDate;
    private String employeeName;
    private String employeeCode;
    private String employeeNameAnswer;
    private String employeeCodeAnswer;
    private String employeePostOfficeCodeAnswer;
    private String employeeNameQuestion;
    private String employeeCodeQuestion;
    private String employeePostOfficeCode;
//    private String description;
    private int page;
    private int pageSize;
    private int totalRecord;
    private int start;
    private List data;
    private HistoryFaqDetailAnswerDTO listEmployeeAnswer;
    private HistoryFaqDetailQuestionDTO listEmployeeQuestion;
    private String departmentName;
    private String departmentNameAnswer;
    private String departmentNameQuestion;
    private String topicName;


}
