package com.viettel.vtman.cms.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BannedContentDTO {
    private Long bannedContentId;
    private String bannedContentName;
    private String description;
    private Date createdDate;
    private Long createdBy;
    private Date updatedDate;
    private Long updatedBy;
}
