package com.viettel.vtman.cms.dto;

import lombok.Data;

import java.util.Date;

@Data
public class HisFaqNotificationDTO {
    private Long notification;       //thong bao trang thai
    private Long historyFaqId;       //id cau hoi
    private String historyFaqCode;   //ma cau hoi
    private String historyFaqName;  //ND cau hoi
    private Date createdDate;        //thoi gian hoi
    private Long employeeId;        // id nguoi hoi
    private String employeeCode;         //ma nhan vien nguoi hoi
    private String employeeName;    //ten nguoi hoi
    private String answer;           //noi dung tra loi
    private Date answerDate;         //thoi gian tra loi
    private String strCreatedDate;
    private String strAnswerDate;
    private Long timeRemain; // Thời gian còn lại
    private Long topicId;
    private String pushDate;
    private String maximumDate; // Thoi gian het han
    private String responseDate; // Thoi gian cho phan hoi
}
