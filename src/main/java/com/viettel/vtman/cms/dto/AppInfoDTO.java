package com.viettel.vtman.cms.dto;

import lombok.Data;

@Data
public class AppInfoDTO {
    private Long employeeId;
    private Boolean isAnswer;
    private Double maximumTime;
    private Double remindingTime;
    private Long maximumQASession;
}
