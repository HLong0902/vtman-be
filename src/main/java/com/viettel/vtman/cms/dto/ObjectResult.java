package com.viettel.vtman.cms.dto;

import lombok.Data;

import java.util.List;

@Data
public class ObjectResult {
    private Integer page;

    private Integer pageSize;

    private Integer totalRecord;

    private String topicName;

    private List listRecord;

    private String errorMgs;

}
