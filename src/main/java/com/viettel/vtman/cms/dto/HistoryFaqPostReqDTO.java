package com.viettel.vtman.cms.dto;

import lombok.Data;

@Data
public class HistoryFaqPostReqDTO {
    private Long historyFaqId;
    private String historyFaqName;
    private Long createdBy;
    private Long topicId;
    private Long rating;
    private String comment;
}
