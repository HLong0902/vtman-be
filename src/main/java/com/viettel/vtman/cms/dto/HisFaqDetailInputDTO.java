package com.viettel.vtman.cms.dto;

import lombok.Data;

@Data
public class HisFaqDetailInputDTO {
    private Long employeeId;
    private Long hisFaqId;
    private String textInput;
    private Boolean isAnswer;
    private Long type;
}
