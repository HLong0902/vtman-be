package com.viettel.vtman.cms.dto;

import lombok.Data;

import java.util.Date;

@Data
public class NotificationFaqDTO {
    private Long notificationId;
    private String notificationTitle;
    private String notificationName;
    private Date notificationDate;
    private String notificationDateStr;
    private Long employeeId;
    private Long isView;
    private Long historyFaqId;
    private Long topicId;
    private Long notificationType;
    private Long typeCms;
    private String createdByEmployeeCode;
    private String createdByEmployeeName;
    private String departmentName;
}
