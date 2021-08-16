package com.viettel.vtman.cms.dto;

import lombok.Data;

import java.util.List;

@Data
public class HistoryFaqDTO {

    private Long departmentId; // Id phong ban
    private String departmentName; // Ten phong ban
    private Long topicId; // Id chu de
    private String topicName; // Ten chu de
    private Long historyFaqId; // Id cau hoi
    private String historyFaqCode; // Code cau hoi
    private String historyFaqName; // Noi dung cau hoi
    private Long status; // Trang thai cau hoi
    private Long employeeId; // Id nguoi hoi
    private String employeeCode; // Code nguoi hoi
    private String employeeName; // Ten nguoi hoi
    private String employeeEmail; // Email nguoi hoi
    private String createdDate; // Thoi gian hoi
    private String answerDate; // Thoi gian tra loi
    private String answer;  // Cau tra loi
    private Long empAnswerId;   // Id nhan vien tra loi
    private String empAnswerCode;   // Code nhan vien tra loi
    private String empAnswerName;   // Ten nhan vien tra loi
    private Long type;
    private String expiredDate; // thoi gian han tra loi
    private Long rating;
    private String comment;     // danh gia cau hoi
    private Long countRating;
    private Boolean isRoleQuestion; // check nguoi dau moi role nguoi hoi or nguoi tra loi
    private List<HistoryFaqDetailDTO> lstHisFaqDetail;
}
