package com.viettel.vtman.cms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
public class HisFaqDetailOutDTO {
    private String text;
    private String createDate;
    private List<String> listContentBanned;
}
