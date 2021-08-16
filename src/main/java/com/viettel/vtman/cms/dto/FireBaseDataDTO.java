package com.viettel.vtman.cms.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FireBaseDataDTO {
    private String hisFaqId;
    private String topicId;
    private String notificationType;
    private String typeCms;
}
