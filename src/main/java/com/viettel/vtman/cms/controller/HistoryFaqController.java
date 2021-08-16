package com.viettel.vtman.cms.controller;

import com.google.gson.Gson;
import com.viettel.vtman.cms.dto.*;
import com.viettel.vtman.cms.entity.Employee;
import com.viettel.vtman.cms.entity.NotificationFaq;
import com.viettel.vtman.cms.message.CommonController;
import com.viettel.vtman.cms.message.Const;
import com.viettel.vtman.cms.service.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.viettel.vtman.cms.message.Const.PAGE_COMPONENT.HISTORY_FAQ;
import static com.viettel.vtman.cms.message.Const.STATUS_QUESTION.QUESTION_NEW;


@RestController
@RequestMapping("/api/historyFaqs")
public class HistoryFaqController extends CommonController {

    private static final Logger LOGGER = LogManager.getLogger(HistoryFaqController.class);

    @Autowired
    private HistoryFaqService historyFaqService;
    @Autowired
    private TopicService topicService;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private HistoryFaqDetailService historyFaqDetailService;
    @Autowired
    private PageService pageService;

    private Gson gson = new Gson();

    @GetMapping("/find")
    public ResponseEntity<?> getHistoryFaqs(@RequestParam(name = "employeeId") Long employeeId, @RequestParam(name = "keySearch") String keySearch,
                                            @RequestParam(name = "isAnswer") Boolean isAnswer, @RequestParam(name = "type") Long type) {
        LOGGER.info("getHistoryFaqs param: employeeId: " + employeeId + ",keySearch: " + keySearch + ",isAnswer: " + isAnswer + ",type: " + type);
        try {
            List<HistoryFaqDTO> lstHisFaqDTO = historyFaqService.getHistoryFaqs(employeeId, keySearch, isAnswer, type);
            Long countReceived = historyFaqService.countReceived(lstHisFaqDTO);
            HisFaqResultMBDTO result = new HisFaqResultMBDTO();
            result.setLstHisFaqDTO(lstHisFaqDTO);
            result.setCountReceived(countReceived);
            LOGGER.info("getHistoryFaqs result: " + gson.toJson(result));
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error("getHistoryFaqs error: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/insert")
    public ResponseEntity<?> insertHisFaq(@RequestBody HistoryFaqPostReqDTO dto) {
        LOGGER.info("insertHisFaq param: " + gson.toJson(dto));
        try {
            String result = historyFaqService.insertHisFaq(dto);
            if (StringUtils.EMPTY.equals(result)) {
                LOGGER.error("insertHisFaq: " + HttpStatus.BAD_REQUEST.toString());
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            LOGGER.info("result insertHisFaq: " + result);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error("insertHisFaq error: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/checkInsert")
    public ResponseEntity<?> checkInsertHisFaq(@RequestBody HistoryFaqPostReqDTO dto) {
        LOGGER.info("checkInsertHisFaq param: " + gson.toJson(dto));
        try {
            String result = historyFaqService.checkInsertHisFaq(dto);
            if (StringUtils.EMPTY.equals(result)) {
                LOGGER.error("insertHisFaq: " + HttpStatus.BAD_REQUEST.toString());
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            LOGGER.info("result insertHisFaq: " + result);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error("checkInsertHisFaq error: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/detail")
    public ResponseEntity<?> getHisFaqDetailById(@RequestParam(name = "employeeId") Long employeeId, @RequestParam(name = "hisFaqId") Long hisFaqId, @RequestParam(name = "topicId") Long topicId) {
        LOGGER.info("getHisFaqDetailById param: employeeId: " + employeeId + ",hisFaqId: " + hisFaqId + ",topicId: " + topicId);
        try {
            HistoryFaqDTO result = historyFaqService.getHisFaqDetailById(employeeId, hisFaqId, topicId);
            LOGGER.info("getHisFaqDetailById result: " + gson.toJson(result));
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error("getHisFaqDetailById error: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/checkDetailNotify")
    public ResponseEntity<?> checkDetailNotify(@RequestParam(name = "employeeId") String employeeId, @RequestParam(name = "hisFaqId") String hisFaqId,
                                               @RequestParam(name = "topicId") String topicId, @RequestParam(name = "typeCms", required = false) String typeCms) {
        LOGGER.info("checkDetailNotify param: employeeId: " + employeeId + ",hisFaqId: " + hisFaqId + ",topicId: " + topicId + ",typeCms: " + typeCms);
        try {
            List<NotificationFaq> lstNotifies = historyFaqService.findNotificationFaqByParams(Long.valueOf(employeeId), Long.valueOf(hisFaqId), Long.valueOf(topicId), Long.valueOf(typeCms));
            if (CollectionUtils.isEmpty(lstNotifies)) {
                LOGGER.info("checkDetailNotify: " + HttpStatus.NOT_FOUND.toString());
                return new ResponseEntity<>(Const.ERROR, HttpStatus.NOT_FOUND);
            }
            String result = historyFaqService.activeNotification(lstNotifies.get(0).getNotificationId());
            LOGGER.info("checkDetailNotify result: " + gson.toJson(result));
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error("getHisFaqDetailById error: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/detailHisFaq")
    public ResponseEntity<?> getHisFaqDetailByIdHisFaq(@RequestParam(name = "hisFaqId") Long hisFaqId) {
        try {
            HistoryFaqDTO result = historyFaqService.getHisFaqDetailById(null, hisFaqId, null);
            return toSuccessResult(result);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/topic")
    public ResponseEntity<?> getAllTopic() {
        try {
            List<TopicDto> listData = topicService.findAllByStatus();
            return toSuccessResult(listData);

        } catch (Exception e) {
            return toExceptionResult(e.getMessage(), Const.API_RESPONSE.RETURN_CODE_ERROR);
        }

    }

    @GetMapping("/employee")
    public ResponseEntity<?> getEmployee() {
        List<Employee> listData;
        try {
            listData = employeeService.findAll();
        } catch (Exception e) {
            return toExceptionResult(e.getMessage(), Const.API_RESPONSE.RETURN_CODE_ERROR);
        }
        return toSuccessResult(listData);
    }

    @GetMapping("/department")
    public ResponseEntity<?> getDepartment() {
        try {
            List<DepartmentDTO> listData = departmentService.findAll();
            return toSuccessResult(listData);

        } catch (Exception ex) {
            return toExceptionResult(ex.getMessage(), Const.API_RESPONSE.RETURN_CODE_ERROR);
        }
    }

    @GetMapping("/departmentStatus")
    public ResponseEntity<?> getDepartmentStatus() {
        try {
            List<DepartmentDTO> listData = departmentService.findAllStatus();
            return toSuccessResult(listData);

        } catch (Exception ex) {
            return toExceptionResult(ex.getMessage(), Const.API_RESPONSE.RETURN_CODE_ERROR);
        }
    }


    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody HistoryFaqCMSDTO historyFaqDTO) {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat formatterDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        SimpleDateFormat dateFormatStartDate = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        SimpleDateFormat dateFormatEndDate = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            HistoryFaqCMSDTO dto = new HistoryFaqCMSDTO();
            if (historyFaqDTO.getStartDate() != null && historyFaqDTO.getStartDate().length > 0) {
                String[] startDate = historyFaqDTO.getStartDate();
                if(startDate!=null && startDate.length>0){
                    if (startDate[0] != null && !"".equals(startDate[0])) {
                        String strDate1 = dateFormatStartDate.format(dateFormat.parse(startDate[0]));
                        dto.setStartDateQuestionSearch(strDate1);
                    }

                    if (startDate[1] != null && !"".equals(startDate[1])) {
                        String strDate = dateFormatEndDate.format(dateFormat.parse(startDate[1]));
                        dto.setEndDateQuestionSearch(strDate);
                    }
                    else{
                        dto.setEndDateQuestionSearch(dateFormatEndDate.format(new Date()));
                    }
                }
            }
            if (historyFaqDTO.getEndDate() != null && historyFaqDTO.getEndDate().length > 0) {
                String[] endDate = historyFaqDTO.getEndDate();
                if(endDate!=null && endDate.length>0){
                    if (endDate[0] != null && !"".equals(endDate[0])) {
                        String strDate1 = dateFormatStartDate.format(dateFormat.parse(endDate[0]));
                        dto.setStartDateAnswerSearch(strDate1);
                    }

                    if (endDate[1] != null && !"".equals(endDate[1])) {
                        String strDate = dateFormatEndDate.format(dateFormat.parse(endDate[1]));
                        dto.setEndDateAnswerSearch(strDate);
                    }else{
                        dto.setEndDateAnswerSearch(dateFormatEndDate.format(new Date()));
                    }
                }
            }

            dto.setPage(historyFaqDTO.getPage());
            dto.setPageSize(historyFaqDTO.getPageSize());

            dto.setAnswer(URLDecoder.decode(historyFaqDTO.getAnswer(),StandardCharsets.UTF_8.name()));
            dto.setHistoryFaqName(URLDecoder.decode(historyFaqDTO.getHistoryFaqName(),StandardCharsets.UTF_8.name())  );
            dto.setTopicId(historyFaqDTO.getTopicId());
            dto.setDepartmentId(historyFaqDTO.getDepartmentId());
            dto.setStatus(historyFaqDTO.getStatus());
            dto.setAnswerEmployee(URLDecoder.decode(historyFaqDTO.getAnswerEmployee(), StandardCharsets.UTF_8.name()));
//            dto.setEmployeeName(URLDecoder.decode(historyFaqDTO.getEmployeeName(),StandardCharsets.UTF_8.name()));
            dto.setEmployeeSearch(org.springframework.util.StringUtils.isEmpty(historyFaqDTO.getEmployeeName()) ? null : URLDecoder.decode(historyFaqDTO.getEmployeeName(), StandardCharsets.UTF_8.name()));
            HistoryFaqCMSDTO data = new HistoryFaqCMSDTO();
            List<HistoryFaqCMSDTO> listData = historyFaqService.search(dto);
            String fullName = "";
            if (listData != null && listData.size() > 0) {
                for (HistoryFaqCMSDTO bean : listData) {
                    if (bean.getCreatedDate() != null /*&& !"".equals(bean.getCreatedDate())*/) {
                        String createDate = bean.getCreatedDate().toString();
                        String date = createDate.substring(0, createDate.length() - 2);
                        Date dateConvert = (Date) formatter.parse(date);
                        String strDate = formatterDate.format(dateConvert);
                        bean.setCreateDateResult(strDate);
                    }
                    if (bean.getAnswerDate() != null /*&& !"".equals(bean.getAnswerDate())*/) {
                        String answerDate = bean.getAnswerDate().toString();
                        String date = answerDate.substring(0, answerDate.length() - 2);
                        Date dateConvert = (Date) formatter.parse(date);
                        String strDate = formatterDate.format(dateConvert);
                        bean.setAnswerDateResult(strDate);
                    }
                    fullName = bean.getPostOfficeCode() == null || bean.getPostOfficeCode() == "" ?"" :bean.getPostOfficeCode();
                    if(bean.getPostOfficeCode() != null && !"".equals(bean.getPostOfficeCode())){
                        if(bean.getPostOfficeCode() != null && !"".equals(bean.getPostOfficeCode())){
                            fullName = fullName + " - " + bean.getEmployeeCode() + " - " +   bean.getEmployeeName();
                            bean.setFullName(fullName);

                        }else{
                            fullName = fullName + " - " + bean.getEmployeeCode() + " - " + bean.getEmployeeName();
                            bean.setFullName(fullName);
                        }

                    }else{
                        fullName = fullName + bean.getEmployeeCode() + " - " + bean.getEmployeeName();
                        bean.setFullName(fullName);
                    }

                }

            }
            data.setData(listData);
            data.setTotalRecord(historyFaqDTO.getTotalRecord());
            data.setPageSize(historyFaqDTO.getPageSize());
            data.setStart(1);
            return toSuccessResult(listData);
        } catch (Exception ex) {
            LOGGER.error("searchHidtoryCms error: " + ex.getMessage());
            return toExceptionResult(ex.getMessage(), Const.API_RESPONSE.RETURN_CODE_ERROR);
        }
    }

    @PostMapping("/detail/insert")
    public ResponseEntity<?> insertDetail(@RequestBody HisFaqDetailInputDTO dto) {
        LOGGER.info("insertDetail param: " + gson.toJson(dto));
        try {
            HisFaqDetailOutDTO result = historyFaqService.insertDetail(dto);
            if (Objects.isNull(result)) {
                LOGGER.error("insertDetail: " + HttpStatus.BAD_REQUEST.toString());
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            LOGGER.info("insertDetail result: " + gson.toJson(result));
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error("insertDetail error: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/count")
    public ResponseEntity<?> countNotificationHisFaqs(@RequestParam(name = "employeeId") Long employeeId) {
        LOGGER.info("countNotificationHisFaqs param: employeeId: " + employeeId);
        try {
            Long result = historyFaqService.countNotificationHisFaqs(employeeId);
            LOGGER.info("countNotificationHisFaqs result: " + result);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error("countNotificationHisFaqs error: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/notification/questionAnswer")
    public ResponseEntity<?> getNotificationQuestion(@RequestParam(name = "employeeId") Long employeeId) {
        LOGGER.info("getNotificationQuestion param: employeeId: " + employeeId);
        try {
            List<NotificationFaqDTO> result = historyFaqService.getNotificationFaqs(employeeId);
            LOGGER.info("getNotificationQuestion result: " + gson.toJson(result));
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error("getNotificationQuestion error: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/detailByHisFaq")
    public ResponseEntity<?> detailHisFaq(@RequestParam("historyFaqId") Long historyFaqId) {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat formatterDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        try {
            HistoryFaqCMSDTO result = new HistoryFaqCMSDTO();
            result = historyFaqService.getHisFaqByIdFaq(historyFaqId);
            if (result.getAnswerDate() != null /*&& !"".equals(result.getAnswerDate())*/) {
                String answerDate = result.getAnswerDate().toString();


                String date = answerDate.substring(0, answerDate.length() - 2);
                Date dateConvert = (Date) formatter.parse(date);
                String strDate = formatterDate.format(dateConvert);
                result.setAnswerDateResult(strDate);
            } else {
                result.setAnswerDateResult("");
            }
            if (result.getCreatedDate() != null /*&& !"".equals(result.getCreatedDate())*/) {
                String createDate = result.getCreatedDate().toString();
                String date = createDate.substring(0, createDate.length() - 2);
                Date dateConvert = (Date) formatter.parse(date);
                String strDate = formatterDate.format(dateConvert);
                result.setCreateDateResult(strDate);
            } else {
                result.setCreateDateResult("");
            }
            String fullName = "";
            if (result.getPostOfficeCode() != null && !"".equals(result.getPostOfficeCode())) {
                fullName = fullName.concat(result.getPostOfficeCode().concat("-"));
            } else {
                fullName = "";
            }
            if (result.getEmployeeCode() != null && !"".equals(result.getEmployeeCode())) {
                fullName = fullName.concat(result.getEmployeeCode().concat("-"));
            }
            if (result.getEmployeeName() != null && !"".equals(result.getEmployeeName())) {
                fullName = fullName.concat(result.getEmployeeName());
            }
            result.setFullName(fullName);
            return toSuccessResult(result);
        } catch (Exception e) {
            LOGGER.error("getDetailHistoryCMS error: " + e.getMessage());
            return toExceptionResult(e.getMessage(), Const.API_RESPONSE.RETURN_CODE_ERROR);
        }
    }

    @PostMapping("/searchHisFaqId")
    public ResponseEntity<?> searchHisFaq(@RequestBody HistoryFaqDetailCMSDTO historyFaqDTO) {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat formatterDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        List<HistoryFaqDetailCMSDTO> listData = new ArrayList<>();
        try {
            HistoryFaqDetailCMSDTO dto = new HistoryFaqDetailCMSDTO();
            dto.setPage(historyFaqDTO.getPage());
            dto.setPageSize(historyFaqDTO.getPageSize());
            dto.setHistoryFaqId(historyFaqDTO.getHistoryFaqId());

            HistoryFaqDetailCMSDTO data = new HistoryFaqDetailCMSDTO();
            listData = historyFaqDetailService.detail(dto);
            if (listData != null && listData.size() > 0) {
                for (HistoryFaqDetailCMSDTO dataConvert : listData) {

                    HistoryFaqDetailAnswerDTO answerDTO = new HistoryFaqDetailAnswerDTO();
                    HistoryFaqDetailQuestionDTO questionDTO = new HistoryFaqDetailQuestionDTO();
                    answerDTO.setAnswer(dataConvert.getAnswer());
                    if (dataConvert.getAnswerDate() != null /*&& !"".equals(dataConvert.getAnswerDate())*/) {
                        String date = dataConvert.getAnswerDate().toString().substring(0, dataConvert.getAnswerDate().toString().length() - 2);
                        Date dateConvert = (Date) formatter.parse(date);
                        String strDate = formatterDate.format(dateConvert);
                        answerDTO.setAnswerDateResult(strDate);
                    }
                    answerDTO.setAnswerDate(dataConvert.getAnswerDate());
                    answerDTO.setDepartmentName(dataConvert.getDepartmentNameAnswer());
                    answerDTO.setEmployeeCodeAnswer(dataConvert.getEmployeeCodeAnswer());
                    answerDTO.setEmployeeNameAnswer(dataConvert.getEmployeeNameAnswer());
                    answerDTO.setEmployeePostOfficeCodeAnswer(dataConvert.getEmployeePostOfficeCodeAnswer());
                    answerDTO.setHistoryFaqName(dataConvert.getHistoryFaqName());
                    questionDTO.setEmployeeCodeQuestion(dataConvert.getEmployeeCodeQuestion());
                    questionDTO.setEmployeeNameQuestion(dataConvert.getEmployeeNameQuestion());
                    questionDTO.setHisFaqDetailName(dataConvert.getHisFaqDetailName());
                    questionDTO.setDepartmentName(dataConvert.getDepartmentNameQuestion());
                    questionDTO.setHistoryFaqName(dataConvert.getHistoryFaqName());
                    questionDTO.setEmployeePostOfficeCode(dataConvert.getEmployeePostOfficeCode());

                    if (dataConvert.getCreatedDate() != null /*&& !"".equals(dataConvert.getCreatedDate())*/) {
                        String date = dataConvert.getCreatedDate().toString().substring(0, dataConvert.getCreatedDate().toString().length() - 2);
                        Date dateConvert = (Date) formatter.parse(date);
                        String strDate = formatterDate.format(dateConvert);
                        questionDTO.setCreatedDateResult(strDate);
                    }
                    questionDTO.setCreatedDate(dataConvert.getCreatedDate());
                    dataConvert.setListEmployeeAnswer(answerDTO);
                    dataConvert.setListEmployeeQuestion(questionDTO);
                }
            }
//            HistoryFaqDetailCMSDTO objects = listData.get(0);
//            listData.set(0, objects);
            for (HistoryFaqDetailCMSDTO removeNull : listData) {
                if(removeNull.getHisFaqDetailId()== null || "".equals(removeNull.getHisFaqDetailName())){
                    removeNull.getListEmployeeQuestion().setHisFaqDetailName("");
                    removeNull.getListEmployeeQuestion().setHistoryFaqName("");
                }

            }

            data.setData(listData);
            data.setTotalRecord(historyFaqDTO.getTotalRecord());
            data.setPageSize(historyFaqDTO.getPageSize());
            data.setStart(1);

        } catch (Exception ex) {
            LOGGER.error("getHistoryFaqsDetail error: " + ex.getMessage());
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return toSuccessResult(listData);
    }

    @PostMapping("/notification/active")
    public ResponseEntity<?> activeNotification(@RequestBody NotificationFaq notificationFaq) {
        LOGGER.info("activeNotification param: " + gson.toJson(notificationFaq));
        try {
            String result = historyFaqService.activeNotification(notificationFaq.getNotificationId());
            LOGGER.info("activeNotification result: " + result);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error("activeNotification error: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/exportHistory")
    public ResponseEntity<?> exportHistory(@RequestBody HistoryFaqCMSDTO historyFaqCMSDTO) {
        byte[] fileContent;
        File currentDirFile = new File(".");
        String helper = currentDirFile.getAbsolutePath();
        String filepath = null;
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat formatterDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        SimpleDateFormat dateFormatStartDate = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        SimpleDateFormat dateFormatEndDate = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        XSSFWorkbook workbook = null;
        try {

            HistoryFaqCMSDTO dto = new HistoryFaqCMSDTO();

            if (historyFaqCMSDTO.getStartDate() != null && historyFaqCMSDTO.getStartDate().length > 0) {
                String[] startDate = historyFaqCMSDTO.getStartDate();
                if(startDate!=null && startDate.length>0){
                    if (startDate[0] != null && !"".equals(startDate[0])) {
                        String strDate1 = dateFormatStartDate.format(dateFormat.parse(startDate[0]));
                        dto.setStartDateQuestionSearch(strDate1);
                    }

                    if (startDate[1] != null && !"".equals(startDate[1])) {
                        String strDate = dateFormatEndDate.format(dateFormat.parse(startDate[1]));
                        dto.setEndDateQuestionSearch(strDate);
                    }
                    else{
                        dto.setEndDateQuestionSearch(dateFormatEndDate.format(new Date()));
                    }
                }
            }
            if (historyFaqCMSDTO.getEndDate() != null && historyFaqCMSDTO.getEndDate().length > 0) {
                String[] endDate = historyFaqCMSDTO.getEndDate();
                if(endDate!=null && endDate.length>0){
                    if (endDate[0] != null && !"".equals(endDate[0])) {
                        String strDate1 = dateFormatStartDate.format(dateFormat.parse(endDate[0]));
                        dto.setStartDateAnswerSearch(strDate1);
                    }

                    if (endDate[1] != null && !"".equals(endDate[1])) {
                        String strDate = dateFormatEndDate.format(dateFormat.parse(endDate[1]));
                        dto.setEndDateAnswerSearch(strDate);
                    }else{
                        dto.setEndDateAnswerSearch(dateFormatEndDate.format(new Date()));
                    }
                }
            }


            dto.setAnswer(URLDecoder.decode(historyFaqCMSDTO.getAnswer(), StandardCharsets.UTF_8.name()));
            dto.setHistoryFaqName(URLDecoder.decode(historyFaqCMSDTO.getHistoryFaqName(), StandardCharsets.UTF_8.name()));
            dto.setTopicId(historyFaqCMSDTO.getTopicId());
            dto.setDepartmentId(historyFaqCMSDTO.getDepartmentId());
            dto.setStatus(historyFaqCMSDTO.getStatus());
            dto.setEmployeeName(URLDecoder.decode(historyFaqCMSDTO.getEmployeeName(), StandardCharsets.UTF_8.name()));
            dto.setAnswerEmployee(URLDecoder.decode(historyFaqCMSDTO.getAnswerEmployee(), StandardCharsets.UTF_8.name()));
            dto.setEmployeeSearch(org.springframework.util.StringUtils.isEmpty(historyFaqCMSDTO.getEmployeeName()) ? null : URLDecoder.decode(historyFaqCMSDTO.getEmployeeName(), StandardCharsets.UTF_8.name()));
            HistoryFaqCMSDTO data = new HistoryFaqCMSDTO();
            List<HistoryFaqCMSDTO> listData = historyFaqService.search(dto);
            String fullName = "";
            if (listData != null && listData.size() > 0) {
                for (HistoryFaqCMSDTO bean : listData) {
                    if (bean.getCreatedDate() != null /*&& !"".equals(bean.getCreatedDate())*/) {
                        String createDate = bean.getCreatedDate().toString();
                        String date = createDate.substring(0, createDate.length() - 2);
                        Date dateConvert = (Date) formatter.parse(date);
                        String strDate = formatterDate.format(dateConvert);
                        bean.setCreateDateResult(strDate);
                    }
                    if (bean.getAnswerDate() != null /*&& !"".equals(bean.getAnswerDate())*/) {
                        String answerDate = bean.getAnswerDate().toString();
                        String date = answerDate.substring(0, answerDate.length() - 2);
                        Date dateConvert = (Date) formatter.parse(date);
                        String strDate = formatterDate.format(dateConvert);
                        bean.setAnswerDateResult(strDate);
                    }

                    fullName = bean.getPostOfficeCode() == null || bean.getPostOfficeCode() == "" ?"" :bean.getPostOfficeCode();
                    if(bean.getPostOfficeCode() != null && !"".equals(bean.getPostOfficeCode())){
                        if(bean.getPostOfficeCode() != null && !"".equals(bean.getPostOfficeCode())){
                            fullName = fullName + " - " + bean.getEmployeeCode() + " - " +   bean.getEmployeeName();
                            bean.setFullName(fullName);

                        }else{
                            fullName = fullName + " - " + bean.getEmployeeCode() + " - " + bean.getEmployeeName();
                            bean.setFullName(fullName);
                        }

                    }else{
                        fullName = fullName + bean.getEmployeeCode() + " - " + bean.getEmployeeName();
                        bean.setFullName(fullName);
                    }
                }

            }

            workbook = new XSSFWorkbook();
            XSSFFont font = workbook.createFont();
            XSSFFont font2 = workbook.createFont();
            XSSFCellStyle style = workbook.createCellStyle();
            CellStyle style2 = workbook.createCellStyle();
            style2.setFont(font2);
            style2.setAlignment(HorizontalAlignment.CENTER);
            font.setBold(true);
            font.setFontHeight(14);
            font2.setBold(true);
            font2.setFontHeight(17);
            style.setFont(font);
            font.setBold(true);
            style.setFont(font);
            Cell cell;
            XSSFSheet sheet = workbook.createSheet(Const.EXPORT_EXCEL.FILE_NAME_HISTORY);
            Row firstRow = sheet.createRow(0);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 7));
            cell = firstRow.createCell(0, CellType.STRING);
            cell.setCellValue("DANH SÁCH LỊCH SỬ HỎI ĐÁP");
            cell.setCellStyle(style2);

            int rownum = 2;
            Row row = sheet.createRow(rownum);
            cell = row.createCell(0, CellType.STRING);
            cell.setCellValue(Const.EXPORT_EXCEL.QUESTION_DEFINITION_NAME);
            cell.setCellStyle(style);
            sheet.autoSizeColumn(0);
            cell = row.createCell(1, CellType.STRING);
            cell.setCellValue(Const.EXPORT_EXCEL.ANSWER_DEFINITION);
            cell.setCellStyle(style);
            sheet.autoSizeColumn(1);
            cell = row.createCell(2, CellType.STRING);
            cell.setCellValue(Const.EXPORT_EXCEL.TOPIC);
            cell.setCellStyle(style);
            sheet.autoSizeColumn(2);
            cell = row.createCell(3, CellType.STRING);
            cell.setCellValue(Const.EXPORT_EXCEL.DEPARTMENT_NAME);
            cell.setCellStyle(style);
            sheet.autoSizeColumn(3);
            cell = row.createCell(4, CellType.STRING);
            cell.setCellValue(Const.EXPORT_EXCEL.QUESTION_NAME);
            cell.setCellStyle(style);
            sheet.autoSizeColumn(4);
            cell = row.createCell(5, CellType.STRING);
            cell.setCellValue(Const.EXPORT_EXCEL.ANSWER_NAME);
            cell.setCellStyle(style);
            sheet.autoSizeColumn(5);
            cell = row.createCell(6, CellType.STRING);
            cell.setCellValue(Const.EXPORT_EXCEL.QUESTION_DATE);
            cell.setCellStyle(style);
            sheet.autoSizeColumn(6);
            cell = row.createCell(7, CellType.STRING);
            cell.setCellValue(Const.EXPORT_EXCEL.ANSWER_DATE);
            cell.setCellStyle(style);
            sheet.autoSizeColumn(7);
            cell = row.createCell(8, CellType.STRING);
            cell.setCellValue(Const.EXPORT_EXCEL.STATUS);
            cell.setCellStyle(style);
            sheet.autoSizeColumn(8);
            style.setFillForegroundColor(IndexedColors.PALE_BLUE.index);
            style.setAlignment(HorizontalAlignment.CENTER);
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            style.setBorderTop(BorderStyle.THIN);
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            // style cua data
            XSSFCellStyle style1 = workbook.createCellStyle();
            style1.setWrapText(true);
            XSSFFont font1 = workbook.createFont();
            font1.setFontHeight(14);
            style1.setFont(font1);
            style1.setBorderTop(BorderStyle.THIN);
            style1.setBorderBottom(BorderStyle.THIN);
            style1.setBorderLeft(BorderStyle.THIN);
            style1.setBorderRight(BorderStyle.THIN);
            style1.setVerticalAlignment(VerticalAlignment.TOP);
            int count = 0;
            for (HistoryFaqCMSDTO emp : listData) {
                rownum++;
                row = sheet.createRow(rownum);

                cell = row.createCell(0, CellType.STRING);
                cell.setCellValue(emp.getHistoryFaqName());
                cell.setCellStyle(style1);
                sheet.setColumnWidth(0, 13000);

                cell = row.createCell(1, CellType.STRING);
                cell.setCellValue(emp.getAnswer());
                cell.setCellStyle(style1);
                sheet.setColumnWidth(1, 13000);

                cell = row.createCell(2, CellType.STRING);
                cell.setCellValue(emp.getTopicName());
                cell.setCellStyle(style1);
                sheet.setColumnWidth(2, 7000);
                cell = row.createCell(3, CellType.STRING);
                cell.setCellValue(emp.getDepartmentName());
                cell.setCellStyle(style1);
                sheet.setColumnWidth(3, 7000);

                cell = row.createCell(4, CellType.STRING);
                cell.setCellValue(emp.getFullName());
                cell.setCellStyle(style1);
                sheet.setColumnWidth(4, 7000);

                cell = row.createCell(5, CellType.STRING);
                cell.setCellValue(emp.getAnswerEmployee());
                cell.setCellStyle(style1);
                sheet.setColumnWidth(5, 7000);

                cell = row.createCell(6, CellType.STRING);
                cell.setCellValue(emp.getCreateDateResult());
                cell.setCellStyle(style1);
                sheet.setColumnWidth(6, 7000);

                cell = row.createCell(7, CellType.STRING);
                cell.setCellValue(emp.getAnswerDateResult());
                cell.setCellStyle(style1);
                sheet.setColumnWidth(7, 7000);
                cell = row.createCell(8, CellType.STRING);
                if(emp.getStatus() ==1l){
                    cell.setCellValue("Chưa trả lời");
                }
                if(emp.getStatus() == 2L){
                    cell.setCellValue("Đã trả lời");
                }
                if(emp.getStatus() == 3L){
                    cell.setCellValue("Hết hạn trả lời");
                }
                if(emp.getStatus() == 4L){
                    cell.setCellValue("Đã đóng");
                }
                if(emp.getStatus() == 5L){
                    cell.setCellValue("Đã hủy");
                }
                cell.setCellStyle(style1);
                sheet.setColumnWidth(8, 7000);
            }
            File outputFile = File.createTempFile("test", ".xls");
            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                workbook.write(fos);
            }


            fileContent = FileUtils.readFileToByteArray(outputFile);
        } catch (Exception e) {
            LOGGER.error("ExportSearch History Error" + e.getMessage());
            return toExceptionResult(e.getMessage(), Const.API_RESPONSE.RETURN_CODE_ERROR);
        } finally {
            try {
                if (Objects.nonNull(workbook)) {
                    workbook.close();
                }
            } catch (IOException e) {
            }
        }
        return toSuccessResult(Base64.getEncoder().encodeToString(fileContent));

    }
    @PostMapping("/exportHistoryDetail")
    public ResponseEntity<?> exportHistoryDetail(@RequestBody HistoryFaqDetailCMSDTO historyFaqCMSDTO) {
        byte[] fileContent;
        File currentDirFile = new File(".");
        String helper = currentDirFile.getAbsolutePath();
        String filepath = null;
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat formatterDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        XSSFWorkbook workbook = null;
        try {
            HistoryFaqDetailCMSDTO dto = new HistoryFaqDetailCMSDTO();
            dto.setHistoryFaqId(historyFaqCMSDTO.getHistoryFaqId());

            HistoryFaqDetailCMSDTO data = new HistoryFaqDetailCMSDTO();
            List<HistoryFaqDetailCMSDTO> listData = historyFaqDetailService.detail(dto);
            if (listData != null && listData.size() > 0) {
                for (HistoryFaqDetailCMSDTO dataConvert : listData) {

                    HistoryFaqDetailAnswerDTO answerDTO = new HistoryFaqDetailAnswerDTO();
                    HistoryFaqDetailQuestionDTO questionDTO = new HistoryFaqDetailQuestionDTO();
                    answerDTO.setAnswer(dataConvert.getAnswer());
                    if (dataConvert.getAnswerDate() != null /*&& !"".equals(dataConvert.getAnswerDate())*/) {
                        String date = dataConvert.getAnswerDate().toString().substring(0, dataConvert.getAnswerDate().toString().length() - 2);
                        Date dateConvert = (Date) formatter.parse(date);
                        String strDate = formatterDate.format(dateConvert);
                        answerDTO.setAnswerDateResult(strDate);
                    }
                    answerDTO.setTopicName(dataConvert.getTopicName());
                    answerDTO.setAnswerDate(dataConvert.getAnswerDate());
                    answerDTO.setDepartmentName(dataConvert.getDepartmentNameAnswer());
                    answerDTO.setEmployeeCodeAnswer(dataConvert.getEmployeeCodeAnswer());
                    answerDTO.setEmployeeNameAnswer(dataConvert.getEmployeeNameAnswer());
                    answerDTO.setHistoryFaqCode(dataConvert.getHistoryFaqCode());
                    answerDTO.setHistoryFaqDetailName(dataConvert.getHisFaqDetailName());
                    answerDTO.setHistoryFaqName(dataConvert.getHisFaqDetailName());
                    questionDTO.setEmployeeCodeQuestion(dataConvert.getEmployeeCodeQuestion());
                    questionDTO.setEmployeeNameQuestion(dataConvert.getEmployeeNameQuestion());
                    questionDTO.setHisFaqDetailName(dataConvert.getHistoryFaqName());
                    questionDTO.setDepartmentName(dataConvert.getDepartmentNameQuestion());
                    questionDTO.setTopicName(dataConvert.getTopicName());
                    questionDTO.setHistoryFaqCode(data.getHistoryFaqCode());
                    questionDTO.setHistoryFaqName(dataConvert.getHistoryFaqName());
                    questionDTO.setHisFaqDetailName(dataConvert.getHisFaqDetailName());

                    if (dataConvert.getCreatedDate() != null /*&& !"".equals(dataConvert.getCreatedDate())*/) {
                        String date = dataConvert.getCreatedDate().toString().substring(0, dataConvert.getCreatedDate().toString().length() - 2);
                        Date dateConvert = (Date) formatter.parse(date);
                        String strDate = formatterDate.format(dateConvert);
                        questionDTO.setCreatedDateResult(strDate);
                    }
                    questionDTO.setCreatedDate(dataConvert.getCreatedDate());
                    dataConvert.setListEmployeeAnswer(answerDTO);
                    dataConvert.setListEmployeeQuestion(questionDTO);
                }
            }
            workbook = new XSSFWorkbook();
            XSSFFont font2 = workbook.createFont();
            XSSFFont font = workbook.createFont();
            CellStyle style2 = workbook.createCellStyle();
            font2.setBold(true);
            font2.setFontHeight(17);
            style2.setFont(font2);
            style2.setAlignment(HorizontalAlignment.CENTER);
            Cell cell;
            XSSFSheet sheet = workbook.createSheet(Const.EXPORT_EXCEL.FILE_NAME_HISTORY);
            Row firstRow = sheet.createRow(0);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 8));
            cell = firstRow.createCell(0, CellType.STRING);
            cell.setCellValue("DANH SÁCH CHI TIẾT LỊCH SỬ HỎI ĐÁP");
            cell.setCellStyle(style2);
            int rownum = 2;
            XSSFCellStyle style = workbook.createCellStyle();
            font.setBold(true);
            font.setFontHeight(14);
            style.setFont(font);
            Row row = sheet.createRow(rownum);
            cell = row.createCell(0, CellType.STRING);
            cell.setCellValue(Const.EXPORT_EXCEL.TOPIC_QUESTION);
            cell.setCellStyle(style);
            sheet.autoSizeColumn(0);
            cell = row.createCell(1, CellType.STRING);
            cell.setCellValue(Const.EXPORT_EXCEL.TOPIC);
            cell.setCellStyle(style);
            sheet.autoSizeColumn(1);
            cell = row.createCell(2, CellType.STRING);
            cell.setCellValue(Const.EXPORT_EXCEL.NAME_QUESTION);
            cell.setCellStyle(style);
            sheet.autoSizeColumn(2);
            cell = row.createCell(3, CellType.STRING);
            cell.setCellValue(Const.EXPORT_EXCEL.QUESTION_DEFINITION_NAME);
            cell.setCellStyle(style);
            sheet.autoSizeColumn(3);
            cell = row.createCell(4, CellType.STRING);
            cell.setCellValue(Const.EXPORT_EXCEL.ANSWER_DEFINITION);
            cell.setCellStyle(style);
            sheet.autoSizeColumn(4);
            cell = row.createCell(5, CellType.STRING);
            cell.setCellValue(Const.EXPORT_EXCEL.NAME_ANSWER);
            cell.setCellStyle(style);
            sheet.autoSizeColumn(5);
            cell = row.createCell(6, CellType.STRING);
            cell.setCellValue(Const.EXPORT_EXCEL.QUESTION_DATE);
            cell.setCellStyle(style);
            sheet.autoSizeColumn(6);
            cell = row.createCell(7, CellType.STRING);
            cell.setCellValue(Const.EXPORT_EXCEL.ANSWER_DATE);
            cell.setCellStyle(style);
            sheet.autoSizeColumn(7);
            cell = row.createCell(8, CellType.STRING);
            cell.setCellValue(Const.EXPORT_EXCEL.DEPARTMENT_ANSWER);
            cell.setCellStyle(style);
            sheet.autoSizeColumn(8);
            style.setFillForegroundColor(IndexedColors.PALE_BLUE.index);
            style.setAlignment(HorizontalAlignment.CENTER);
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            style.setBorderTop(BorderStyle.THIN);
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            // style cua data
            XSSFCellStyle style1 = workbook.createCellStyle();
            style1.setWrapText(true);
            XSSFFont font1 = workbook.createFont();
            font1.setFontHeight(14);
            style1.setFont(font1);
            style1.setBorderTop(BorderStyle.THIN);
            style1.setBorderBottom(BorderStyle.THIN);
            style1.setBorderLeft(BorderStyle.THIN);
            style1.setBorderRight(BorderStyle.THIN);
            style1.setVerticalAlignment(VerticalAlignment.TOP);
            int count = 0;
            for (HistoryFaqDetailCMSDTO emp : listData) {
                rownum++;
                row = sheet.createRow(rownum);

                cell = row.createCell(0, CellType.STRING);
                cell.setCellValue(emp.getHistoryFaqCode());
                cell.setCellStyle(style1);
                sheet.setColumnWidth(0, 4000);

                cell = row.createCell(1, CellType.STRING);
                cell.setCellValue(emp.getListEmployeeQuestion().getTopicName());
                cell.setCellStyle(style1);
                sheet.setColumnWidth(1, 6000);
                String rsFullName  ="";
                cell = row.createCell(2, CellType.STRING);
                StringBuilder questionEmployee = new StringBuilder();
                if (!org.springframework.util.StringUtils.isEmpty(emp.getEmployeePostOfficeCode())) {
                    if (questionEmployee.length() > 0) {
                        questionEmployee.append(" - ");
                    }
                    questionEmployee.append(emp.getEmployeePostOfficeCode());
                }
                if (!org.springframework.util.StringUtils.isEmpty(emp.getEmployeeCodeQuestion())) {
                    if (questionEmployee.length() > 0) {
                        questionEmployee.append(" - ");
                    }
                    questionEmployee.append(emp.getEmployeeCodeQuestion());
                }
                if (!org.springframework.util.StringUtils.isEmpty(emp.getEmployeeNameQuestion())) {
                    if (questionEmployee.length() > 0) {
                        questionEmployee.append(" - ");
                    }
                    questionEmployee.append(emp.getEmployeeNameQuestion());
                }
                cell.setCellValue(questionEmployee.toString());
                cell.setCellStyle(style1);
                sheet.setColumnWidth(2, 9000);

                cell = row.createCell(3, CellType.STRING);
                cell.setCellValue(emp.getListEmployeeQuestion().getHisFaqDetailName());
                cell.setCellStyle(style1);
                sheet.setColumnWidth(3, 10000);
                cell = row.createCell(4, CellType.STRING);
                cell.setCellValue(emp.getListEmployeeAnswer().getAnswer());
                cell.setCellStyle(style1);
                sheet.setColumnWidth(4, 10000);
                cell = row.createCell(5, CellType.STRING);
                StringBuilder answerEmployee = new StringBuilder();
                if (!org.springframework.util.StringUtils.isEmpty(emp.getEmployeePostOfficeCodeAnswer())) {
                    if (answerEmployee.length() > 0) {
                        answerEmployee.append(" - ");
                    }
                    answerEmployee.append(emp.getEmployeePostOfficeCodeAnswer());
                }
                if (!org.springframework.util.StringUtils.isEmpty(emp.getEmployeeCodeAnswer())) {
                    if (answerEmployee.length() > 0) {
                        answerEmployee.append(" - ");
                    }
                    answerEmployee.append(emp.getEmployeeCodeAnswer());
                }
                if (!org.springframework.util.StringUtils.isEmpty(emp.getEmployeeNameAnswer())) {
                    if (answerEmployee.length() > 0) {
                        answerEmployee.append(" - ");
                    }
                    answerEmployee.append(emp.getEmployeeNameAnswer());
                }
                cell.setCellValue(answerEmployee.toString());
                cell.setCellStyle(style1);
                sheet.setColumnWidth(5, 9000);


                cell = row.createCell(6, CellType.STRING);
                cell.setCellValue(emp.getListEmployeeQuestion().getCreatedDateResult());
                cell.setCellStyle(style1);
                sheet.setColumnWidth(6, 7000);


                cell = row.createCell(7, CellType.STRING);
                cell.setCellValue(emp.getListEmployeeAnswer().getAnswerDateResult());
                cell.setCellStyle(style1);
                sheet.setColumnWidth(7, 7000);

                cell = row.createCell(8, CellType.STRING);
                cell.setCellValue(emp.getListEmployeeAnswer().getDepartmentName());
                cell.setCellStyle(style1);
                sheet.setColumnWidth(8, 6000);

            }

            File outputFile = File.createTempFile("test", ".xls");
            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                workbook.write(fos);
            }

            fileContent = FileUtils.readFileToByteArray(outputFile);


        } catch (Exception ex) {
            LOGGER.error("Search Detail History error" + ex.getMessage());
            return toExceptionResult(ex.getMessage(), Const.API_RESPONSE.RETURN_CODE_ERROR);
        } finally {
            try {
                if (Objects.nonNull(workbook)) {
                    workbook.close();
                }
            } catch (IOException e) {
            }
        }

        return toSuccessResult(Base64.getEncoder().encodeToString(fileContent));
    }

    @PostMapping("/evaluate")
    public ResponseEntity<?> evaluate(@RequestBody HistoryFaqPostReqDTO dto) {
        LOGGER.info("evaluate param: " + gson.toJson(dto));
        try {
            String result = historyFaqService.updateEvaluate(dto);
            if (StringUtils.EMPTY.equals(result)) {
                LOGGER.error("evaluate: " + HttpStatus.BAD_REQUEST.toString());
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            LOGGER.info("evaluate result: " + result);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error("evaluate error: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/forwardByTopic")
    public ResponseEntity<?> forwardHisFaqByTopic(@RequestBody HistoryFaqPostReqDTO dto) {
        LOGGER.info("forwardByTopic param: " + gson.toJson(dto));
        try {
            String result = historyFaqService.forwardHisFaqByTopic(dto);
            LOGGER.info("result forwardByTopic: " + result);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error("forwardHisFaqByTopic error: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

//    @PostMapping("/insert/tokenFcm")
//    public ResponseEntity<?> insertTokenFcm(@RequestBody TokenFcm tokenFcm) {
//        LOGGER.info("insertTokenFcm param: " + gson.toJson(tokenFcm));
//        try {
//            String result = historyFaqService.insertTokenFcm(tokenFcm);
//            if (StringUtils.EMPTY.equals(result)) {
//                LOGGER.info("insertTokenFcm: " + HttpStatus.BAD_REQUEST.toString());
//                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//            }
//            LOGGER.info("result insertTokenFcm: " + result);
//            return new ResponseEntity<>(result, HttpStatus.OK);
//        } catch (Exception e) {
//            LOGGER.error("insertTokenFcm error: " + e.getMessage());
//            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

    @PostMapping("/closeHisFaq")
    public ResponseEntity<?> closeHistoryFaq(@RequestBody HistoryFaqPostReqDTO dto) {
        LOGGER.info("closeHisFaq param: " + gson.toJson(dto));
        try {
            String result = historyFaqService.closeHistoryFaq(dto);
            if (StringUtils.EMPTY.equals(result)) {
                LOGGER.error("closeHisFaq: " + HttpStatus.BAD_REQUEST.toString());
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            LOGGER.info("closeHisFaq result: " + result);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e){
            LOGGER.error("closeHisFaq error: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/cancelHisFaq")
    public ResponseEntity<?> cancelHistoryFaq(@RequestBody HistoryFaqPostReqDTO dto) {
        LOGGER.info("cancelHistoryFaq param: " + gson.toJson(dto));
        try {
            String result = historyFaqService.cancelHistoryFaq(dto);
            if (StringUtils.EMPTY.equals(result)) {
                LOGGER.error("cancelHisFaq: " + HttpStatus.BAD_REQUEST.toString());
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            LOGGER.info("cancelHisFaq result: " + result);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e){
            LOGGER.error("cancelHisFaq error: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/appInfo")
    public ResponseEntity<?> getApplicationInfo(@RequestParam(name = "employeeCode", required = false) String employeeCode) {
        LOGGER.info("getApplicationInfo param: employeeCode: " + employeeCode);
        try {
            EmployeeDto employeeDto = employeeService.getOrCreateFromJwtApp(employeeCode);
            if (Objects.isNull(employeeDto)) {
                LOGGER.error("getApplicationInfo error: Dữ liệu tài khoản người dùng có lỗi");
                return new ResponseEntity<>("Dữ liệu tài khoản người dùng có lỗi", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            AppInfoDTO result = historyFaqService.getAppInfo(employeeDto.getEmployeeId());
            LOGGER.info("getApplicationInfo result: " + gson.toJson(result));
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error("getApplicationInfo error: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    @GetMapping("/notification/questionAnswerCms")
    public ResponseEntity<?> getNotificationQuestionCms(@RequestParam(name = "employeeId", required = false) Long employeeId,
                                                        @RequestParam(name = "pageSize", required = false) Integer pageSize,
                                                        @RequestParam(name = "pageIndex", required = false) Integer pageIndex) {
        try {
            EmployeeDto employee = employeeService.getOrCreateFromJwt();

            Map<String, Object> response = new HashMap<>();
            response.put("unreadCount", historyFaqService.getCmsUnreadNotificationCount(employeeId != null ? employeeId : employee.getEmployeeId()));

            pageSize = pageSize == null ? 10 : pageSize;
            pageIndex = pageIndex == null ? 0 : pageIndex;

            PageDTO pageDTO = pageService.getPageByComponent(HISTORY_FAQ);

            List<NotificationFaqDTO> result = historyFaqService.getCmsNotificationFaqs(employeeId != null ? employeeId : employee.getEmployeeId(), pageIndex, pageSize);
            response.put("data", result.stream().map(e -> {
                Map<String, Object> map = new HashMap<>();
                if (e.getNotificationName().length() > 30) {
                    e.setNotificationName(e.getNotificationName().substring(0,30) + "...");
                }
                if (e.getTypeCms().equals(QUESTION_NEW)) {
                    map.put("title", "Câu hỏi đã nhận");
                    map.put("body", e.getCreatedByEmployeeCode() + " - " + e.getCreatedByEmployeeName() + " đã gửi câu hỏi đến " + e.getDepartmentName() + ": \"" + e.getNotificationName() + "\"");
                } else {
                    map.put("title", "Câu hỏi đã hết hạn");
                    map.put("body", "Câu hỏi của " + e.getCreatedByEmployeeCode() + " - " + e.getCreatedByEmployeeName() + ": \"" + e.getNotificationName() + "\" được gửi tới " + e.getDepartmentName() + " đã hết hạn");
                }
                map.put("title", e.getTypeCms().equals(QUESTION_NEW) ? "Câu hỏi đã nhận" : "Câu hỏi đã hết hạn");
                map.put("historyFaqId", e.getHistoryFaqId());
                map.put("notificationId", e.getNotificationId());
                map.put("isView", e.getIsView());
                map.put("typeCms", e.getTypeCms());
                map.put("path", pageDTO.getPropMenu().getMenuPath() + pageDTO.getPath() + "/" + e.getHistoryFaqId());
                map.put("notificationDateStr", e.getNotificationDateStr());
                map.put("notificationDate", e.getNotificationDate());

                return map;
            }).collect(Collectors.toList()));

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/notification/cmsReaded")
    public ResponseEntity<?> updateNotificationCms(@RequestParam(name = "notificationId") long notificationId,
                                                   @RequestParam(name = "employeeId", required = false) Long employeeId) {
        try {
            EmployeeDto employee = employeeService.getOrCreateFromJwt();
            Map<String, Object> respone = new HashMap<>();
            respone.put("data", historyFaqService.readCmsNotificationFaqs(notificationId));
            respone.put("count", historyFaqService.getCmsUnreadNotificationCount(employeeId != null ? employeeId : employee.getEmployeeId()));

            return toSuccessResult(respone);
        } catch (Exception ex) {
            return toExceptionResult("FAIL", Const.API_RESPONSE.RETURN_CODE_ERROR);
        }
    }
}
