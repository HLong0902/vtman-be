package com.viettel.vtman.cms.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity(name = "HISTORY_FAQ_DETAIL")
@Data
public class HistoryFaqDetail {
    @Id
    @Column(name = "HISTORY_FAQ_DETAIL_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long historyFaqDetailId;
    @Column(name = "HISTORY_FAQ_DETAIL_NAME")
    private String historyFaqDetailName;
    @Column(name = "HISTORY_FAQ_ID")
    private Long historyFaqId;
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
}
