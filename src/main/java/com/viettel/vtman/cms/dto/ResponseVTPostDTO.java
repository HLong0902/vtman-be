package com.viettel.vtman.cms.dto;

import lombok.Data;

@Data
public class ResponseVTPostDTO {
    private DataVTPostDTO data;
    private ErrorVTPostDTO error;
}
