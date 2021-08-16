package com.viettel.vtman.cms.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;
@Data
public class QuestionDefinitionDTO {
    private Long questionDefinitionId;
    private String answerDefinition;
    private String questionDefinitionName;
    private Long topicId;
    private Long numberOrder;
    private String description;
    private Date createdDate;
    private Long createdBy;
    private Date updatedDate;
    private Long updatedBy;
    private Long status;
    private Integer page;
    private Integer pageSize;
    private Integer totalRecord;
    private int start;
    private List data;
    private String topicName;
    private String topicCode;
    private List<QuestionDefinitionDTO> listError;
    private QuestionDefinitionDTO listValid;
    private QuestionDefinitionDTO listInvalid;
    private String messageError;
    private Long id;
    private String statusStr;
    private String numberOrderStr;
    private String answerDefinitionFlat;

}
