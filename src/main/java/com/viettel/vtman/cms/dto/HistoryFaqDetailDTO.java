package com.viettel.vtman.cms.dto;

import lombok.Data;

@Data
public class HistoryFaqDetailDTO {

    private Long hisFaqDetailId;
    private String hisFaqDetailName;
    private String createdDate;
    private Long empCreatedId;
    private String empCreatedCode;
    private String empCreatedName;
    private String answer;
    private String answerDate;
    private Long empAnswerId;
    private String empAnswerCode;
    private String empAnswerName;
    private Long departmentId;
    private String departmentName;

}
