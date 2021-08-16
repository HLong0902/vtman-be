package com.viettel.vtman.cms.dto;

import com.viettel.vtman.cms.entity.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoryFaqCMSDTO {

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
    private Timestamp createdDate; // Thoi gian hoi
    private Timestamp answerDate; // Thoi gian tra loi
    private String answer;  // Cau tra loi
    private Long empAnswerId;   // Id nhan vien tra loi
    private String empAnswerCode;   // Code nhan vien tra loi
    private String empAnswerName;   // Ten nhan vien tra loi
    private Long type;
    private Long countReceived;
    private List<HistoryFaqDetailDTO> lstHisFaqDetail;
    private Long answerEmployeeId;
    private Date startDateQuestion;
    private Date endDateQuestion;
    private Date startDateAnswer;
    private Date endDateAnswer;
    private String startDateQuestionSearch;
    private String endDateQuestionSearch;
    private String startDateAnswerSearch;
    private String endDateAnswerSearch;
    private String answerDateResult;
    private String createDateResult;
    private String[] startDate;
    private String[] endDate;
    private Long createdBy;
    private int page;
    private int pageSize;
    private int totalRecord;
    private int start;
    private List data;
    private String fullName;
    private String postOfficeCode;
    private String employeeSearch;
    private String answerEmployee;

    public HistoryFaqCMSDTO(HistoryFaq historyFaq, Topic topic, Department department, Employee employee, Employee answerEmployee) {
        if (Objects.nonNull(historyFaq)) {
            this.historyFaqId = historyFaq.getHistoryFaqId();
            this.historyFaqCode = historyFaq.getHistoryFaqCode();
            this.historyFaqName = historyFaq.getHistoryFaqName();
            this.answer = historyFaq.getAnswer();
            this.answerEmployeeId = historyFaq.getAnswerEmployeeId();
            this.status = historyFaq.getStatus();
            this.topicId = historyFaq.getTopicId();
            this.createdDate = Objects.isNull(historyFaq.getCreatedDate()) ? null : new Timestamp(historyFaq.getCreatedDate().getTime());
            this.createdBy = historyFaq.getCreatedBy();
            this.answerDate = Objects.isNull(historyFaq.getAnswerDate()) ? null : new Timestamp(historyFaq.getAnswerDate().getTime());
        }

        if (Objects.nonNull(topic)) {
            this.topicName = topic.getTopicName();
        }

        if (Objects.nonNull(department)) {
            this.departmentName = department.getDepartmentName();
        }

        if (Objects.nonNull(employee)) {
            this.employeeName = employee.getEmployeeName();
            this.employeeCode = employee.getEmployeeCode();
            this.postOfficeCode = employee.getPostOfficeCode();
        }

        if (Objects.nonNull(answerEmployee)) {
            if (!StringUtils.isEmpty(answerEmployee.getPostOfficeCode())) {
                this.answerEmployee = answerEmployee.getPostOfficeCode();
            }

            if (!StringUtils.isEmpty(answerEmployee.getEmployeeCode())) {
                this.answerEmployee = StringUtils.isEmpty(this.answerEmployee) ? answerEmployee.getEmployeeCode() : this.answerEmployee + " - " + answerEmployee.getEmployeeCode();
            }

            if (!StringUtils.isEmpty(answerEmployee.getEmployeeName())) {
                this.answerEmployee = StringUtils.isEmpty(this.answerEmployee) ? answerEmployee.getEmployeeName() : this.answerEmployee + " - " + answerEmployee.getEmployeeName();
            }
        }
    }
}
