package com.viettel.vtman.cms.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PushNotificationDTO {
    private Long historyFaqId;
    private String historyFAQName;
    private Long status;
    private Long employeeId;
    private String employeeCode;
    private String employeeName;
    private Long departmentId;
    private String departmentName;
    private String message;
    private String tittle;
    private Long totalRecord;
    private Long page;
    private Long pageSize;
    private List<PushNotificationDTO> data;
    private String path;


    public void setMessageReceived(Long status){
        if (status == 1){
            if (historyFAQName.length()<=35) {
                message = employeeCode + " - " + employeeName + " đã gửi câu hỏi đến " + departmentName + ": \"" + historyFAQName + "\"";
            }
            else {
                message = employeeCode + " - " + employeeName + " đã gửi câu hỏi đến " + departmentName + ": \"" + historyFAQName.substring(0,35) + "..." + "\"";
            }
        }
        if (status == 3){
            if (historyFAQName.length()<=35) {
                message = "Câu hỏi của " + employeeCode + " - " + employeeName + ": \"" + historyFAQName + "\"" + " được gửi tới " + departmentName + " đã hết hạn";
            }
            else {
                message = "Câu hỏi của " + employeeCode + " - " + employeeName + ": \"" + historyFAQName.substring(0,35) +"..." + "\"" + " được gửi tới " + departmentName + " đã hết hạn";
            }
        }
    }

    public void setTitleReceived(Long status){
        if (status == 1){
            tittle = "Câu hỏi đã nhận";
        }
        if (status == 3){
            tittle = "Câu hỏi đã hết hạn";
        }
    }

}
