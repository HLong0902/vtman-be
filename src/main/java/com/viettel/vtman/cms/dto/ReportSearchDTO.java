package com.viettel.vtman.cms.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ReportSearchDTO {
    private Date fromDate;
    private Date toDate;
    private Long topicId;
    private Long departmentId;
    private Integer page;
    private Integer pageSize;
}
