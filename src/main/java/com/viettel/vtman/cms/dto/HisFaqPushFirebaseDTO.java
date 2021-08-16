package com.viettel.vtman.cms.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HisFaqPushFirebaseDTO {
    private String title;
    private String body;
    private Long userId;
    private FireBaseDataDTO data;
}
