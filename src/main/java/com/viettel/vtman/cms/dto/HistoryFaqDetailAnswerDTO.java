package com.viettel.vtman.cms.dto;

import lombok.Data;

import java.sql.Timestamp;
import java.util.Date;
@Data
public class HistoryFaqDetailAnswerDTO {
    private String departmentName;
    private String employeeCodeAnswer;
    private String employeeNameAnswer;
    private String employeePostOfficeCodeAnswer;
    private String answer;
    private Timestamp answerDate;
    private String answerDateResult;
    private String topicName;
    private String historyFaqCode;
    private String historyFaqDetailName;
    private String historyFaqName;
}
