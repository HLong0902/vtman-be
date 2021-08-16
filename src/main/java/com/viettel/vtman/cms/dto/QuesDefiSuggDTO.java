package com.viettel.vtman.cms.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class QuesDefiSuggDTO {
    private Long questionDefinitionId;
    private String answerDefinition;
    private String questionDefinitionName;
    private Long topicId;
    private String topicName;
    private String topicCode;
    private String description;
    private Date createdDate;
    private Long createdBy;
    private Date updatedDate;
    private Long updatedBy;
    private Long status;
}
