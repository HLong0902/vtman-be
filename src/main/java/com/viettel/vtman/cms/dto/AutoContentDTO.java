package com.viettel.vtman.cms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AutoContentDTO {
    private Long automaticContentId;
    private String[] autoContentList;
    private Long autoContentType;
    private Long numberOrder;
    private String description;
    private Long isActive;
    private Date createdDate;
    private Long createdBy;
    private Date updatedDate;
    private Long updatedBy;

}
