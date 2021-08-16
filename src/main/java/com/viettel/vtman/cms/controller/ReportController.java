package com.viettel.vtman.cms.controller;

import com.viettel.vtman.cms.dao.ReportDAO;
import com.viettel.vtman.cms.dto.EmployeeDto;
import com.viettel.vtman.cms.dto.ObjectResult;
import com.viettel.vtman.cms.dto.ReportSearchDTO;
import com.viettel.vtman.cms.message.CommonController;
import com.viettel.vtman.cms.message.Const;
import com.viettel.vtman.cms.service.EmployeeService;
import com.viettel.vtman.cms.utils.Common;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


@RestController
@RequestMapping(value = "/api/report")
@RequiredArgsConstructor
public class ReportController extends CommonController {
    private static final Logger LOGGER = LogManager.getLogger(ReportController.class);
    @Autowired
    private ReportDAO reportDAO;

    @Autowired
    private EmployeeService employeeService;

    public static Object count = 0;

    @PostMapping("/answerPercent")
    public ResponseEntity<?> getPercent(@RequestBody ReportSearchDTO reportSearchDTO) {
//        return toSuccessResult(reportDAO.getAnswerPercentReport(reportSearchDTO));
        ObjectResult result = reportDAO.getAnswerPercentReport(reportSearchDTO);

        if (StringUtils.isEmpty(result.getErrorMgs())) {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Access-Control-Expose-Headers", "count");
            headers.add("count", result.getTotalRecord().toString());

            return new ResponseEntity<>(result.getListRecord(), headers, HttpStatus.OK);
        } else {
            return toExceptionResult(result.getErrorMgs(), Const.API_RESPONSE.RETURN_CODE_ERROR);
        }
    }

    @PostMapping("/topic")
    public ResponseEntity<?> getTopicReport(@RequestBody ReportSearchDTO reportSearchDTO) {
        try {
            reportSearchDTO.setFromDate(Objects.isNull(reportSearchDTO.getFromDate()) ? null : Common.toBeginOfDate(reportSearchDTO.getFromDate()));
            reportSearchDTO.setToDate(Objects.isNull(reportSearchDTO.getToDate()) ? null : Common.toEndOfDate(reportSearchDTO.getToDate()));
            setDepartment(reportSearchDTO);

        } catch (Exception e) {
            LOGGER.error("report topic error:" + e.getMessage());
            return toExceptionResult(e.getMessage(), Const.API_RESPONSE.RETURN_CODE_ERROR);
        }
        return new ResponseEntity<>(reportDAO.getTopicReport(reportSearchDTO), getHeaders(), HttpStatus.OK);
    }

    @PostMapping("/rating")
    public ResponseEntity<?> getRating(@RequestBody ReportSearchDTO reportSearchDTO) {
//        setDepartment(reportSearchDTO);
//        return new ResponseEntity<>(reportDAO.getRatingReport(reportSearchDTO), getHeaders(), HttpStatus.OK);
        ObjectResult result = reportDAO.getRatingReport(reportSearchDTO);

        if (StringUtils.isEmpty(result.getErrorMgs())) {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Access-Control-Expose-Headers", "count");
            headers.add("count", result.getTotalRecord().toString());

            return new ResponseEntity<>(result.getListRecord(), headers, HttpStatus.OK);
        } else {
            return toExceptionResult(result.getErrorMgs(), Const.API_RESPONSE.RETURN_CODE_ERROR);
        }
    }

    @PostMapping("/question")
    public ResponseEntity<?> getQuestion(@RequestBody ReportSearchDTO reportSearchDTO) {
        try {
            reportSearchDTO.setFromDate(Objects.isNull(reportSearchDTO.getFromDate()) ? null : Common.toBeginOfDate(reportSearchDTO.getFromDate()));
            reportSearchDTO.setToDate(Objects.isNull(reportSearchDTO.getToDate()) ? null : Common.toEndOfDate(reportSearchDTO.getToDate()));
            setDepartment(reportSearchDTO);
        } catch (Exception e) {
            LOGGER.error("report question error:" + e.getMessage());
            return toExceptionResult(e.getMessage(), Const.API_RESPONSE.RETURN_CODE_ERROR);
        }
        return new ResponseEntity<>(reportDAO.getQuestionReport(reportSearchDTO), getHeaders(), HttpStatus.OK);
    }

    @GetMapping("/getTopic")
    public ResponseEntity<?> getTopics() {
        return new ResponseEntity<>(reportDAO.getTopics(), getHeaders(), HttpStatus.OK);
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Access-Control-Expose-Headers", "count");
        headers.add("count", count.toString());
        return headers;
    }

    @PostMapping("/answerPercent/export")
    public ResponseEntity<?> exportAnswerPercent(@RequestBody ReportSearchDTO reportSearchDTO) throws Exception {
        setDepartment(reportSearchDTO);
        String sheetname = "BÁO CÁO TỶ LỆ TRẢ LỜI CỦA PHÒNG BAN";
        String title = "BÁO CÁO TỶ LỆ TRẢ LỜI CỦA PHÒNG BAN";
        String[] headers = {"Phòng ban", "Tổng số câu hỏi", "Số lượng đã trả lời", "Tỷ lệ trả lời (%)", "KPI"};
        String[] fields = {"department", "total", "answered", "percent", "kpi"};
        try {
            byte[] res = exportToExcel(reportDAO.getAnswerPercentReport(reportSearchDTO).getListRecord(), sheetname, title, headers, fields, reportSearchDTO.getFromDate(), reportSearchDTO.getToDate());
            return toSuccessResult(Base64.getEncoder().encodeToString(res));
        } catch (Exception e) {
            return toExceptionResult(e.getMessage(), Const.API_RESPONSE.RETURN_CODE_ERROR);
        }
    }

    @PostMapping("/topic/export")
    public ResponseEntity<?> exportTopic(@RequestBody ReportSearchDTO reportSearchDTO) throws Exception {
        reportSearchDTO.setFromDate(Objects.isNull(reportSearchDTO.getFromDate()) ? null : Common.toBeginOfDate(reportSearchDTO.getFromDate()));
        reportSearchDTO.setToDate(Objects.isNull(reportSearchDTO.getToDate()) ? null : Common.toEndOfDate(reportSearchDTO.getToDate()));
        setDepartment(reportSearchDTO);
        String sheetname = "BÁO CÁO CÂU HỎI ĐÃ NHẬN TỪ PHÒNG BAN THEO CHỦ ĐỀ";
        String title = "BÁO CÁO CÂU HỎI ĐÃ NHẬN TỪ PHÒNG BAN THEO CHỦ ĐỀ";
        String[] headers = {"Phòng ban", "Chủ đề", "Tổng số câu hỏi đã nhận", "Số lượng đã trả lời", "Số lượng đã hết hạn", "Số lượng được đánh giá"};
        String[] fields = {"department", "topic", "total", "answered", "expired", "rated"};
        try {
            byte[] res = exportToExcel(reportDAO.getTopicReport(reportSearchDTO), sheetname, title, headers, fields, reportSearchDTO.getFromDate(), reportSearchDTO.getToDate());
            return toSuccessResult(Base64.getEncoder().encodeToString(res));
        } catch (Exception e) {
            LOGGER.error("export topic error:" + e.getMessage());
            return toExceptionResult(e.getMessage(), Const.API_RESPONSE.RETURN_CODE_ERROR);
        }
    }

    @PostMapping("/rating/export")
    public ResponseEntity<?> exportRating(@RequestBody ReportSearchDTO reportSearchDTO) throws Exception {
        setDepartment(reportSearchDTO);
        String sheetname = "BÁO CÁO CHẤT LƯỢNG TRẢ LỜI CỦA PHÒNG BAN";
        String title = "BÁO CÁO CHẤT LƯỢNG TRẢ LỜI CỦA PHÒNG BAN";
        String[] headers = {"Phòng ban", "Tổng số câu hỏi", "Số lượng đã trả lời", "Số câu được đánh giá", "Điểm số đánh giá"};
        String[] fields = {"department", "total", "answered", "rated", "rating"};
        try {
            byte[] res = exportToExcel(reportDAO.getRatingReport(reportSearchDTO).getListRecord(), sheetname, title, headers, fields, reportSearchDTO.getFromDate(), reportSearchDTO.getToDate());
            return toSuccessResult(Base64.getEncoder().encodeToString(res));
        } catch (Exception e) {
            return toExceptionResult(e.getMessage(), Const.API_RESPONSE.RETURN_CODE_ERROR);
        }
    }

    @PostMapping("/question/export")
    public ResponseEntity<?> exportQuestion(@RequestBody ReportSearchDTO reportSearchDTO) throws Exception {
        reportSearchDTO.setFromDate(Objects.isNull(reportSearchDTO.getFromDate()) ? null : Common.toBeginOfDate(reportSearchDTO.getFromDate()));
        reportSearchDTO.setToDate(Objects.isNull(reportSearchDTO.getToDate()) ? null : Common.toEndOfDate(reportSearchDTO.getToDate()));
        setDepartment(reportSearchDTO);
        String sheetname = "CÂU HỎI TỰ ĐẶT THEO PHÒNG BAN";
        String title = "BÁO CÁO CÂU HỎI TỰ ĐẶT THEO PHÒNG BAN";
        String[] headers = {"Phòng ban", "Chủ đề", "Câu hỏi", "Câu trả lời", "Thông tin nhân viên", "Đầu mối trả lời"};
        String[] fields = {"department", "topic", "question", "answer", "employee", "answerEmp"};
        try {
            byte[] res = exportToExcel(reportDAO.getQuestionReport(reportSearchDTO), sheetname, title, headers, fields, reportSearchDTO.getFromDate(), reportSearchDTO.getToDate());
            return toSuccessResult(Base64.getEncoder().encodeToString(res));
        } catch (Exception e) {
            LOGGER.error("export question error:" + e.getMessage());
            return toExceptionResult(e.getMessage(), Const.API_RESPONSE.RETURN_CODE_ERROR);
        }
    }

    public byte[] exportToExcel(List<HashMap<String, Object>> list, String sheetname,
                                String title, String[] headers, String[] fields, Date startDate, Date endDate) throws Exception {
        ReportExcelExporter excelExporter = new ReportExcelExporter(list);
        return excelExporter.export(sheetname, title, headers, fields, startDate, endDate);
    }

    private void setDepartment(ReportSearchDTO reportSearchDTO) {
        EmployeeDto emp = employeeService.getOrCreateFromJwt();
        if (emp.getRoleId() != 1 && emp.getRoleId() != 2)
            reportSearchDTO.setDepartmentId(emp.getDepartmentId());
    }
}
