package com.viettel.vtman.cms.dto;

import lombok.Data;

import java.util.List;

@Data
public class HisFaqResultMBDTO {
    private Long countReceived;
    private List<HistoryFaqDTO> lstHisFaqDTO;
}
