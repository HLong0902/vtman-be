package com.viettel.vtman.cms.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity(name = "QUESTION_DEFINITION")
@Data
public class QuestionDefinition {
    @Id
    @Column(name = "QUESTION_DEFINITION_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long questionDefinitionId;
    @Column(name = "QUESTION_DEFINITION_NAME")
    private String questionDefinitionName;
    @Column(name = "TOPIC_ID")
    private Long topicId;
    @Column(name = "ANSWER_DEFINITION")
    private String answerDefinition;
    @Column(name = "NUMBER_ORDER")
    private Long numberOrder;
    @Column(name = "DESCRIPTION")
    private String description;
    @Column(name = "CREATED_DATE")
    private Date createdDate;
    @Column(name = "CREATED_BY")
    private Long createdBy;
    @Column(name = "UPDATED_DATE")
    private Date updatedDate;
    @Column(name = "UPDATED_BY")
    private Long updatedBy;
    @Column(name = "STATUS")
    private Long status;
    @Column(name = "ANSWER_DEFINITION_FLAT")
    private String answerDefinitionFlat;
}
