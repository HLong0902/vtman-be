package com.viettel.vtman.cms.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity(name = "NOTIFICATION_FAQ")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationFaq {
    @Id
    @Column(name = "NOTIFICATION_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;
    @Column(name = "NOTIFICATION_TITLE")
    private String notificationTitle;
    @Column(name = "NOTIFICATION_NAME")
    private String notificationName;
    @Column(name = "NOTIFICATION_DATE")
    private Date notificationDate;
    @Column(name = "EMPLOYEE_ID")
    private Long employeeId;
    @Column(name = "IS_VIEW")
    private Long isView;
    @Column(name = "HISTORY_FAQ_ID")
    private Long historyFaqId;
    @Column(name = "TOPIC_ID")
    private Long topicId;
    @Column(name = "NOTIFICATION_TYPE")
    private Long notificationType;
    @Column(name = "TYPE_CMS")
    private Long typeCms;
}
