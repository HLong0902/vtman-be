package com.viettel.vtman.cms.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "HISTORY_FAQ")
@Data
public class HistoryFaq {
    @Id
    @Column(name = "HISTORY_FAQ_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long historyFaqId;
    @Column(name = "HISTORY_FAQ_NAME")
    private String historyFaqName;
    @Column(name = "STATUS")
    private Long status;
    @Column(name = "CREATED_DATE")
    private Date createdDate;
    @Column(name = "CREATED_BY")
    private Long createdBy;
    @Column(name = "ANSWER_DATE")
    private Date answerDate;
    @Column(name = "ANSWER")
    private String answer;
    @Column(name = "ANSWER_EMPLOYEE_ID")
    private Long answerEmployeeId;
    @Column(name = "HISTORY_FAQ_CODE")
    private String historyFaqCode;
    @Column(name = "TOPIC_ID")
    private Long topicId;
    @Column(name = "DEPARTMENT_ID")
    private Long departmentId;
    @Column(name = "RATING")
    private Long rating;
    @Column(name = "COMMENT")
    private String comment;
    @Column(name = "COUNT_RATING")
    private Long countRating;
    @Column(name = "MAXIMUM_DATE")
    private Date maximumDate;
    @Column(name = "RESPONSE_DATE")
    private Date responseDate;
    @Column(name = "FORWARD_DATE")
    private Date forwardDate;
}
