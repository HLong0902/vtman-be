package com.viettel.vtman.cms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.ByteStreams;
import com.viettel.vtman.cms.dto.EmployeeDto;
import com.viettel.vtman.cms.dto.ObjectResult;
import com.viettel.vtman.cms.dto.QuestionDefinitionDTO;
import com.viettel.vtman.cms.dto.TopicDto;
import com.viettel.vtman.cms.entity.QuestionDefinition;
import com.viettel.vtman.cms.message.CommonController;
import com.viettel.vtman.cms.message.Const;
import com.viettel.vtman.cms.service.EmployeeService;
import com.viettel.vtman.cms.service.QuestionDefinitionService;
import com.viettel.vtman.cms.service.TopicService;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.viettel.vtman.cms.message.Const.EXPORT_EXCEL.FILE_NAME_QUESTION_DEFINITION_ERROR;


@RestController
@RequestMapping("/api/question")
public class QuestionDefinitionController extends CommonController {

    private static final Logger LOGGER = LogManager.getLogger(QuestionDefinitionController.class);
    @Autowired
    private QuestionDefinitionService questionDefinitionService;
    @Autowired
    private TopicService topicService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam("questionDefinitionName") String questionDefinitionName, @RequestParam("answerDefinition") String answerDefinition,
                                    @RequestParam("topicId") Long topicId, @RequestParam("page") Integer page, @RequestParam("pageSize") Integer pageSize) {
        try {
            questionDefinitionName = URLDecoder.decode(questionDefinitionName, StandardCharsets.UTF_8.name());
            answerDefinition = URLDecoder.decode(answerDefinition, StandardCharsets.UTF_8.name());
            ObjectResult objectResult = new ObjectResult();
            objectResult.setPage(page);
            objectResult.setPageSize(pageSize);
            EmployeeDto dto = new EmployeeDto();
            QuestionDefinitionDTO data = new QuestionDefinitionDTO();
            List<QuestionDefinitionDTO> listData = questionDefinitionService.search(questionDefinitionName, answerDefinition, topicId, objectResult);
            data.setData(listData);
            data.setTotalRecord(objectResult.getTotalRecord());
            data.setPageSize(objectResult.getPageSize());
            data.setStart(1);
            return toSuccessResult(listData);
        } catch (Exception ex) {
            LOGGER.error("search question  error: " + ex.getMessage());
            return toExceptionResult(ex.getMessage(), Const.API_RESPONSE.RETURN_CODE_ERROR);
        }

    }

    @GetMapping("/edit")
    public ResponseEntity<?> updateById(@RequestParam("questionDefinitionId") Long questionDefinitionId) throws Exception {
        try {
            QuestionDefinition bean = questionDefinitionService.findById(questionDefinitionId);
            return toSuccessResult(bean);
        } catch (Exception e) {
            LOGGER.error("edit question  error: " + e.getMessage());
            return toExceptionResult(e.getMessage(), Const.API_RESPONSE.RETURN_CODE_ERROR);
        }

    }

    @GetMapping("/topic")
    public ResponseEntity<?> getTopic() throws Exception {
        try {
            List<TopicDto> listData = topicService.findAllByStatus();
            return toSuccessResult(listData);
        } catch (Exception e) {
            LOGGER.error("getall topic question error: " + e.getMessage());
            return toExceptionResult(e.getMessage(), Const.API_RESPONSE.RETURN_CODE_ERROR);
        }

    }


    @PostMapping("/edit")
    public ResponseEntity<?> updateById(@RequestParam("questionDefinitionId") Long questionDefinitionId,
                                        @RequestBody QuestionDefinitionDTO questionDefinition) throws Exception {
        QuestionDefinition data = new QuestionDefinition();
        Date date = new Date();
        try {

            QuestionDefinition result = questionDefinitionService.findById(questionDefinitionId);
            QuestionDefinitionDTO beanCheck = null;
            QuestionDefinitionDTO beanCheckQuestionName = null;
            beanCheckQuestionName = new QuestionDefinitionDTO();
            QuestionDefinitionDTO params = new QuestionDefinitionDTO();
            params.setQuestionDefinitionName(questionDefinition.getQuestionDefinitionName());
            params.setTopicId(questionDefinition.getTopicId());

            beanCheckQuestionName = questionDefinitionService.checkQuestionDefinitionName(params);

            if (beanCheckQuestionName != null) {
                if (beanCheckQuestionName.getQuestionDefinitionId().equals(questionDefinitionId)) {


                    result.setAnswerDefinition(questionDefinition.getAnswerDefinition());
                    result.setTopicId(questionDefinition.getTopicId());
                    result.setQuestionDefinitionName(questionDefinition.getQuestionDefinitionName());
                    result.setNumberOrder(questionDefinition.getNumberOrder());
                    result.setDescription(questionDefinition.getDescription());
                    result.setStatus(questionDefinition.getStatus());
                    result.setUpdatedDate(date);
                    result.setUpdatedBy(questionDefinition.getUpdatedBy());
                    result.setAnswerDefinitionFlat(StringUtils.isEmpty(result.getAnswerDefinition()) ? "" : Jsoup.parse(result.getAnswerDefinition()).text());
                    data = questionDefinitionService.update(result);

                } else {
                    return toExceptionResult(null, Const.API_RESPONSE.QUESTION_DEFINITION_NAME);
                }

            } else {
                result.setAnswerDefinition(questionDefinition.getAnswerDefinition());
                result.setTopicId(questionDefinition.getTopicId());
                result.setQuestionDefinitionName(questionDefinition.getQuestionDefinitionName());
                result.setNumberOrder(questionDefinition.getNumberOrder());
                result.setDescription(questionDefinition.getDescription());
                result.setStatus(questionDefinition.getStatus());
                result.setUpdatedDate(date);
                result.setUpdatedBy(questionDefinition.getUpdatedBy());
                result.setAnswerDefinitionFlat(StringUtils.isEmpty(result.getAnswerDefinition()) ? "" : Jsoup.parse(result.getAnswerDefinition()).text());
                data = questionDefinitionService.update(result);
            }
        } catch (Exception ex) {
            LOGGER.error("edit question  error: " + ex.getMessage());
            return toExceptionResult(ex.getMessage(), Const.API_RESPONSE.RETURN_CODE_ERROR);
        }
        return
                toSuccessResult(data);
    }


    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody QuestionDefinitionDTO questionDefinitionDTO) throws Exception {
        QuestionDefinition result = new QuestionDefinition();
        QuestionDefinition bean = new QuestionDefinition();

        Date date = new Date();

        try {
            QuestionDefinitionDTO beanCheckDefinitonName = null;
            QuestionDefinitionDTO beanCheck = null;

            beanCheckDefinitonName = new QuestionDefinitionDTO();
            QuestionDefinitionDTO params = new QuestionDefinitionDTO();
            params.setQuestionDefinitionName(questionDefinitionDTO.getQuestionDefinitionName());
            params.setTopicId(questionDefinitionDTO.getTopicId());

            beanCheckDefinitonName = questionDefinitionService.checkQuestionDefinitionName(params);

            if (beanCheckDefinitonName == null) {


                QuestionDefinition data = new QuestionDefinition();
                data.setNumberOrder(questionDefinitionDTO.getNumberOrder());
                data.setAnswerDefinition(questionDefinitionDTO.getAnswerDefinition());
                data.setTopicId(questionDefinitionDTO.getTopicId());
                data.setQuestionDefinitionName(questionDefinitionDTO.getQuestionDefinitionName());
                data.setDescription(questionDefinitionDTO.getDescription());
                data.setStatus(questionDefinitionDTO.getStatus());
                data.setCreatedBy(questionDefinitionDTO.getCreatedBy());
                data.setCreatedDate(date);
                data.setAnswerDefinitionFlat(StringUtils.isEmpty(data.getAnswerDefinition()) ? "" : Jsoup.parse(data.getAnswerDefinition()).text());
                result = questionDefinitionService.save(data);


            } else {
                return toExceptionResult(null, Const.API_RESPONSE.QUESTION_DEFINITION_NAME);
            }


        } catch (Exception e) {
            LOGGER.error("create question  error: " + e.getMessage());
            return toExceptionResult(e.getMessage(), Const.API_RESPONSE.RETURN_CODE_ERROR);
        }
        return toSuccessResult(result);
    }

    @GetMapping("/delete")
    public ResponseEntity<?> deleteById(@RequestParam("questionDefinitionId") Long questionDefinitionId) {
        try {
            QuestionDefinition result = questionDefinitionService.findById(questionDefinitionId);
            if (result != null) {
                questionDefinitionService.deleteById(questionDefinitionId);

            } else {
                return toExceptionResult(null, Const.API_RESPONSE.QUESTION_ID_NOT_FOUND);
            }
            return toSuccessResult(null);

        } catch (Exception ex) {
            LOGGER.error("delete question  error: " + ex.getMessage());
            return toExceptionResult(ex.getMessage(), Const.API_RESPONSE.RETURN_CODE_ERROR);
        }

    }

    /*
    @PostMapping("/exportExcel")
    public ResponseEntity<?> exportExcel(@RequestBody List<QuestionDefinitionDTO> questionDefinitionDTO) {
        File currentDirFile = new File(".");
        String helper = currentDirFile.getAbsolutePath();
        String filepath = null;

        try {
//            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
//            String filePath1 = classloader.getResource("../" + "doc-template/ims").getPath();
            String currentDir = helper.substring(0, helper.length() - currentDirFile.getCanonicalPath().length());
            filepath = helper.substring(0, helper.length() - 1);
            InetAddress ia = InetAddress.getLocalHost();
            String str = ia.getHostAddress();

            SimpleDateFormat timeFormat = new SimpleDateFormat("HH_mm_ss_SSS");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd");
            Calendar calendar = Calendar.getInstance();

            filepath = filepath.concat("template\\" + dateFormat.format(calendar.getTime()) + "\\" + timeFormat.format(calendar.getTime()) + Const.EXPORT_EXCEL.FILE_NAME + String.valueOf((int) (Math.random() * 10000)) + "." + Const.XLS);
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFFont font = workbook.createFont();
            int rownum = 0;
            Cell cell;
            HSSFSheet sheet = workbook.createSheet(Const.EXPORT_EXCEL.FILE_NAME);
            Row row = sheet.createRow(0);
            HSSFCellStyle style = workbook.createCellStyle();
            font.setBold(true);
            style.setFont(font);
            cell = row.createCell(0, CellType.STRING);
            cell.setCellValue(Const.EXPORT_EXCEL.STT);
            cell.setCellStyle(style);
            cell = row.createCell(1, CellType.STRING);
            cell.setCellValue(Const.EXPORT_EXCEL.QUESTION_ANSWER);
            cell.setCellStyle(style);
            cell = row.createCell(2, CellType.STRING);
            cell.setCellValue(Const.EXPORT_EXCEL.ANSWER_NAME);
            cell.setCellStyle(style);
            cell = row.createCell(3, CellType.STRING);
            cell.setCellValue(Const.EXPORT_EXCEL.TOPIC_ID);
            cell.setCellStyle(style);
            cell = row.createCell(4, CellType.STRING);
            cell.setCellValue(Const.EXPORT_EXCEL.NUMBER_ORDER);
            cell.setCellStyle(style);
            cell = row.createCell(5, CellType.STRING);
            cell.setCellValue(Const.EXPORT_EXCEL.DESCRIPTION);
            cell.setCellStyle(style);
            cell = row.createCell(6, CellType.STRING);
            cell.setCellValue(Const.EXPORT_EXCEL.STATUS);
            cell.setCellStyle(style);
            int count = 0;
            for (QuestionDefinitionDTO emp : questionDefinitionDTO) {
                rownum++;
                row = sheet.createRow(rownum);
                cell = row.createCell(0, CellType.STRING);
                cell.setCellValue(count++);
                cell = row.createCell(1, CellType.STRING);
                cell.setCellValue(emp.getQuestionDefinitionName());
                cell = row.createCell(2, CellType.STRING);
                cell.setCellValue(emp.getAnswerDefinition());
                cell = row.createCell(3, CellType.STRING);
                cell.setCellValue(emp.getTopicCode());
                cell = row.createCell(4, CellType.STRING);
                cell.setCellValue(emp.getNumberOrder());
                cell = row.createCell(5, CellType.STRING);
                cell.setCellValue(emp.getDescription());
                cell = row.createCell(6, CellType.STRING);
                cell.setCellValue(emp.getStatus());
            }

            File file = new File(filepath.toString());
            file.getParentFile().mkdirs();
            FileOutputStream outFile = null;
            try {
                outFile = new FileOutputStream(file);
            } catch (FileNotFoundException e) {
            }
            try {
                workbook.write(outFile);
            } catch (IOException e) {
            }
            outFile.close();
        } catch (Exception e) {
            return toExceptionResult(e.getMessage(), Const.API_RESPONSE.RETURN_CODE_ERROR);
        }
        return toSuccessResult(filepath.replace("\\", "/"));
    }
    */

    @PostMapping("/importExcel")
    public ResponseEntity<?> create(@RequestBody Map<String, Object> requestData) throws Exception {
//        Long createdBy = Long.valueOf(requestData.get("createdBy").toString());
        Long createdBy = employeeService.getOrCreateFromJwt().getEmployeeId();
        List<QuestionDefinitionDTO> questionDefinitionDTO = ((List<Map<String, Object>>) requestData.get("data")).stream()
                .map(e -> objectMapper.convertValue(e, QuestionDefinitionDTO.class)).collect(Collectors.toList());

        QuestionDefinition bean = new QuestionDefinition();
        QuestionDefinitionDTO beanCheckDefinitonName = new QuestionDefinitionDTO();
        QuestionDefinitionDTO params = new QuestionDefinitionDTO();
        QuestionDefinitionDTO beanError = new QuestionDefinitionDTO();
        List<QuestionDefinitionDTO> result = new ArrayList<>();
        List<QuestionDefinitionDTO> listCase = new ArrayList<>();
        TopicDto topicDto = new TopicDto();
        try {
            if (questionDefinitionDTO.size() > 0 && questionDefinitionDTO != null) {
                for (QuestionDefinitionDTO data : questionDefinitionDTO) {
                    String messageError = "";
                    QuestionDefinitionDTO beanCheck = questionDefinitionService.checkNumberOrder(data.getNumberOrder());

                    topicDto = topicService.findByCode(data.getTopicCode());
                    if (topicDto == null) {

                        messageError = messageError + Const.API_RESPONSE.NOT_FOUND_TOPIC_CODE;
                        data.setMessageError(messageError);
                    } else {
                        data.setTopicId(topicDto.getTopicId());
                    }
                }
            }
            listCase = listCheckError(questionDefinitionDTO);
            for (QuestionDefinitionDTO listSave : listCase) {
                if (listSave.getMessageError() == null || "".equals(listSave.getMessageError())) {
                    params.setQuestionDefinitionName(listSave.getListValid().getQuestionDefinitionName());
                    params.setTopicId(listSave.getListValid().getTopicId());
                    beanCheckDefinitonName = questionDefinitionService.checkQuestionDefinitionName(params);

                    if (beanCheckDefinitonName == null) {
                        QuestionDefinition questionDefinition = new QuestionDefinition();
                        Date date = new Date();
                        questionDefinition.setAnswerDefinition((listSave.getListValid().getAnswerDefinition()) != null ? listSave.getListValid().getAnswerDefinition() : "");
                        questionDefinition.setTopicId(listSave.getListValid().getTopicId() != null ? listSave.getListValid().getTopicId() : null);
                        questionDefinition.setQuestionDefinitionName(listSave.getListValid().getQuestionDefinitionName() != null ? listSave.getListValid().getQuestionDefinitionName() : "");
                        questionDefinition.setNumberOrder(listSave.getListValid().getNumberOrder());
                        questionDefinition.setDescription(listSave.getListValid().getDescription() != null ? listSave.getListValid().getDescription() : "");
                        questionDefinition.setStatus(listSave.getListValid().getStatus() != null ? listSave.getListValid().getStatus() : null);
                        questionDefinition.setCreatedDate(date);
                        questionDefinition.setCreatedBy(createdBy);
                        questionDefinition.setAnswerDefinitionFlat(StringUtils.isEmpty(questionDefinition.getAnswerDefinition()) ? "" : Jsoup.parse(questionDefinition.getAnswerDefinition()).text());
                        questionDefinitionService.save(questionDefinition);
                    } else {
                        result.add(listSave);
                    }
                } else {
                    /*  QuestionDefinitionDTO dto = new QuestionDefinitionDTO();*/
                 /*   dto.setId(listSave.getListInvalid().getId());
                    dto.setTopicName(listSave.getListInvalid().getTopicName());
                    dto.setAnswerDefinition(listSave.getListInvalid().getAnswerDefinition());
                    dto.setTopicId(listSave.getListInvalid().getTopicId());
                    dto.setQuestionDefinitionName(listSave.getListInvalid().getQuestionDefinitionName());
                    dto.setNumberOrder(listSave.getListInvalid().getNumberOrder());
                    dto.setDescription(listSave.getListInvalid().getDescription());
                    dto.setStatus(listSave.getListInvalid().getStatus());
                    dto.setMessageError(listSave.getListInvalid().getMessageError());*/
                    result.add(listSave);
                }
            }
        } catch (Exception e) {
            LOGGER.error("importexcel question  error: " + e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return toSuccessResultErrorTopicId(result);
    }

    public List<QuestionDefinitionDTO> listCheckError(List<QuestionDefinitionDTO> listData) {
        List<QuestionDefinitionDTO> listCheckError = new ArrayList<>();
//        for (QuestionDefinitionDTO listCheck : listData) {
//            if (listCheck.getMessageError() != null && !"".equals(listCheck.getMessageError())) {
//                listCheck.setListInvalid(listCheck);
//            } else {
//                listCheck.setListValid(listCheck);
//            }
//            listCheckError.add(listCheck);
//        }
        if (listData != null && listData.size() > 0) {
            for (int i = 0; i < listData.size(); i++) {
                QuestionDefinitionDTO obj = new QuestionDefinitionDTO();
                if (!StringUtils.isEmpty(listData.get(i).getMessageError())) {
                    obj.setListInvalid(listData.get(i));
                } else {
                    obj.setListValid(listData.get(i));
                }
                listCheckError.add(obj);
            }
        }

        return listCheckError;
    }

    @GetMapping("/findById")
    public ResponseEntity<?> getQuestionsByTopicId(@RequestParam(name = "topicId") Long topicId) {
        LOGGER.info("getQuestionsByTopicId param: topicId: " + topicId);
        try {
            List<QuestionDefinition> result = questionDefinitionService.getQuestionsByTopicId(topicId);
            LOGGER.info("getQuestionsByTopicId result: " + result);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception ex) {
            LOGGER.error("getQuestionsByTopicId error: " + ex.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/suggestion")
    public ResponseEntity<?> getSuggestion(@RequestParam("questionDefinitionName") String questionDefinitionName) throws Exception {
        LOGGER.info("getSuggestion param: questionDefinitionName: " + questionDefinitionName);
        try {
            List<QuestionDefinition> listQuestionDefinitionSuggestion = questionDefinitionService.getSuggestion(questionDefinitionName);
            LOGGER.info("getSuggestion result: " + listQuestionDefinitionSuggestion);
            return toSuccessResult(listQuestionDefinitionSuggestion);
        } catch (Exception e) {
            LOGGER.error("getSuggestion error: " + e.getMessage());
            return toExceptionResult(e.getMessage(), Const.API_RESPONSE.RETURN_CODE_ERROR);
        }

    }

    @PostMapping(value = "/editRecordError")
    public ResponseEntity<?> editRecordError(@RequestBody QuestionDefinitionDTO questionDefinitionDTO) throws Exception {
        try {
            QuestionDefinitionDTO params = new QuestionDefinitionDTO();
            params.setTopicId(questionDefinitionDTO.getTopicId());
            params.setQuestionDefinitionName(questionDefinitionDTO.getQuestionDefinitionName());
            QuestionDefinitionDTO beanCheckDefinitonName = new QuestionDefinitionDTO();
            beanCheckDefinitonName = questionDefinitionService.checkQuestionDefinitionName(params);
            if (beanCheckDefinitonName != null) {
                return toExceptionResult(null, Const.API_RESPONSE.RETURN_CODE_ERROR_NOTFOUND);
            } else {
                return toSuccessResult(questionDefinitionDTO);
            }


        } catch (Exception e) {
            LOGGER.error("edit record error question : " + e.getMessage());
            return toExceptionResult(e.getMessage(), Const.API_RESPONSE.RETURN_CODE_ERROR);
        }

    }

    /*
    @GetMapping("/exportExcel/downloader")
    public ResponseEntity<?> exportExcelDownloader(@RequestParam("filePath") String filePath) {
        try {
            File file = new File(filePath);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName());

            Path path = Paths.get(file.getAbsolutePath());
            ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

            ResponseEntity responseEntity = ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(file.length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);

            file.delete();

            return responseEntity;
        } catch (Exception ex) {
            return toExceptionResult(ex.getMessage(), Const.API_RESPONSE.RETURN_CODE_ERROR);
        }
    }
    */

    @PostMapping("/export/Excel/failQuestionTemplate")
    public ResponseEntity<?> exportExcelDownload(@RequestBody List<QuestionDefinitionDTO> questionDefinitionDTO) throws IOException {
        byte[] fileContent;
        XSSFWorkbook workbook = null;

        try {
            workbook = new XSSFWorkbook();
            XSSFFont fontHeader = workbook.createFont();
            fontHeader.setBold(true);
            fontHeader.setFontHeight(14);
            XSSFCellStyle styleHeader = workbook.createCellStyle();
            styleHeader.setFont(fontHeader);
            styleHeader.setFillForegroundColor(IndexedColors.PALE_BLUE.index);
            styleHeader.setAlignment(HorizontalAlignment.CENTER);
            styleHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            styleHeader.setBorderTop(BorderStyle.THIN);
            styleHeader.setBorderBottom(BorderStyle.THIN);
            styleHeader.setBorderLeft(BorderStyle.THIN);
            styleHeader.setBorderRight(BorderStyle.THIN);

            Cell cell;
            XSSFSheet sheet = workbook.createSheet(FILE_NAME_QUESTION_DEFINITION_ERROR);

            CellStyle style = workbook.createCellStyle();
            XSSFFont font = workbook.createFont();
            font.setBold(true);
            font.setFontHeight(17);
            style.setFont(font);
            style.setAlignment(HorizontalAlignment.CENTER);
            Row firstRow = sheet.createRow(0);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));
            cell = firstRow.createCell(0, CellType.STRING);
            cell.setCellValue(FILE_NAME_QUESTION_DEFINITION_ERROR);
            cell.setCellStyle(style);

            int rownum = 2;

            Row row = sheet.createRow(rownum);

            cell = row.createCell(0, CellType.STRING);
            cell.setCellValue(Const.EXPORT_EXCEL.QUESTION_DEFINITION_NAME);
            cell.setCellStyle(styleHeader);
            cell = row.createCell(1, CellType.STRING);
            cell.setCellValue(Const.EXPORT_EXCEL.ANSWER_DEFINITION);
            cell.setCellStyle(styleHeader);
            cell = row.createCell(2, CellType.STRING);
            cell.setCellValue(Const.EXPORT_EXCEL.TOPIC_CODE);
            cell.setCellStyle(styleHeader);
            cell = row.createCell(3, CellType.STRING);
            cell.setCellValue(Const.EXPORT_EXCEL.NUMBER_ORDER);
            cell.setCellStyle(styleHeader);
            cell = row.createCell(4, CellType.STRING);
            cell.setCellValue(Const.EXPORT_EXCEL.DESCRIPTION);
            cell.setCellStyle(styleHeader);
            cell = row.createCell(5, CellType.STRING);
            cell.setCellValue(Const.EXPORT_EXCEL.STATUS);
            cell.setCellStyle(styleHeader);
            cell = row.createCell(6, CellType.STRING);
            cell.setCellValue(Const.EXPORT_EXCEL.ERROR_DETAIL);
            cell.setCellStyle(styleHeader);

            XSSFFont fontSTT = workbook.createFont();
            fontSTT.setFontHeight(14);
            XSSFCellStyle styleSTT = workbook.createCellStyle();
            styleSTT.setFont(fontSTT);
            styleSTT.setBorderTop(BorderStyle.THIN);
            styleSTT.setBorderBottom(BorderStyle.THIN);
            styleSTT.setBorderLeft(BorderStyle.THIN);
            styleSTT.setBorderRight(BorderStyle.THIN);
            styleSTT.setAlignment(HorizontalAlignment.CENTER);
            styleSTT.setVerticalAlignment(VerticalAlignment.CENTER);

            XSSFFont fontCell = workbook.createFont();
            fontCell.setFontHeight(14);
            XSSFCellStyle styleCell = workbook.createCellStyle();
            styleCell.setFont(fontCell);
            styleCell.setBorderTop(BorderStyle.THIN);
            styleCell.setBorderBottom(BorderStyle.THIN);
            styleCell.setBorderLeft(BorderStyle.THIN);
            styleCell.setBorderRight(BorderStyle.THIN);
            styleCell.setWrapText(true);
            styleCell.setVerticalAlignment(VerticalAlignment.TOP);

            for (QuestionDefinitionDTO emp : questionDefinitionDTO) {
                rownum++;
                row = sheet.createRow(rownum);

                cell = row.createCell(0, CellType.STRING);
                cell.setCellValue(StringUtils.isEmpty(emp.getQuestionDefinitionName()) ? "" : emp.getQuestionDefinitionName());
                cell.setCellStyle(styleCell);

                cell = row.createCell(1, CellType.STRING);
                cell.setCellValue(StringUtils.isEmpty(emp.getAnswerDefinition()) ? "" : Jsoup.parse(emp.getAnswerDefinition()).text());
                cell.setCellStyle(styleCell);

                cell = row.createCell(2, CellType.STRING);
                cell.setCellValue(StringUtils.isEmpty(emp.getTopicCode()) ? "" : emp.getTopicCode());
                cell.setCellStyle(styleCell);

                cell = row.createCell(3, CellType.STRING);
                cell.setCellValue(StringUtils.isEmpty(emp.getNumberOrderStr()) ? "" : emp.getNumberOrderStr());
                cell.setCellStyle(styleSTT);

                cell = row.createCell(4, CellType.STRING);
                cell.setCellValue(StringUtils.isEmpty(emp.getDescription()) ? "" : emp.getDescription());
                cell.setCellStyle(styleCell);

                cell = row.createCell(5, CellType.STRING);
                if (StringUtils.isEmpty(emp.getStatusStr())) {
                    cell.setCellValue("");
                } else {
                    if ("0".equals(emp.getStatusStr().trim())) {
                        cell.setCellValue(Const.EXPORT_EXCEL.KHONG_HOAT_DONG);
                    } else if ("1".equals(emp.getStatusStr().trim())) {
                        cell.setCellValue(Const.EXPORT_EXCEL.DANG_HOAT_DONG);
                    } else {
                        cell.setCellValue(emp.getStatusStr());
                    }
                }
                cell.setCellStyle(styleCell);

                cell = row.createCell(6, CellType.STRING);
                cell.setCellValue(StringUtils.isEmpty(emp.getMessageError()) ? "" : emp.getMessageError());
                cell.setCellStyle(styleCell);
            }

            sheet.setColumnWidth(0, 13000);
            sheet.setColumnWidth(1, 13000);
            sheet.setColumnWidth(2, 7000);
            sheet.setColumnWidth(3, 3000);
            sheet.setColumnWidth(4, 13000);
            sheet.setColumnWidth(5, 7000);
            sheet.setColumnWidth(6, 13000);

            File outputFile = File.createTempFile("test", ".xls");
            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                workbook.write(fos);
            }

            fileContent = FileUtils.readFileToByteArray(outputFile);
        } catch (Exception e) {
            return toExceptionResult(e.getMessage(), Const.API_RESPONSE.RETURN_CODE_ERROR);
        } finally {
            workbook.close();
        }

        return toSuccessResult(Base64.getEncoder().encodeToString(fileContent));
    }

    @GetMapping(value = "/import/download/failQuestionTemplate")
    public ResponseEntity<?> importDownloadTemplate() {
        try {
            byte[] bytes = ByteStreams.toByteArray(new ClassPathResource("import_excel_template/question_definition/fail_question.xlsx").getInputStream());
            ByteArrayResource resource = new ByteArrayResource(bytes);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMdd_HHmmss");

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + URLEncoder.encode("Danh sách câu hỏi tự định nghĩa_", StandardCharsets.UTF_8.toString()).replaceAll("\\+", "%20") + "_" + simpleDateFormat.format(new Date()) + ".xlsx");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(bytes.length)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (Exception e) {
            return toExceptionResult(e.getMessage(), Const.API_RESPONSE.RETURN_CODE_ERROR);
        }
    }


    @GetMapping("/exportQuestion")
    public ResponseEntity<?> search(@RequestParam("questionDefinitionName") String questionDefinitionName, @RequestParam("answerDefinition") String answerDefinition,
                                    @RequestParam("topicId") Long topicId) throws UnsupportedEncodingException {
        questionDefinitionName = URLDecoder.decode(questionDefinitionName, StandardCharsets.UTF_8.name());
        answerDefinition = URLDecoder.decode(answerDefinition, StandardCharsets.UTF_8.name());

        byte[] fileContent;
        File currentDirFile = new File(".");
        String helper = currentDirFile.getAbsolutePath();
        String filepath = null;
        XSSFWorkbook workbook = null;
        try {
            workbook = new XSSFWorkbook();
            XSSFCellStyle style = workbook.createCellStyle();
            List<QuestionDefinitionDTO> listData = questionDefinitionService.exportQuestion(questionDefinitionName, answerDefinition, topicId);
            XSSFFont font = workbook.createFont();
            font.setBold(true);
            font.setFontHeight(14);
            style.setFont(font);

            Cell cell;
            XSSFSheet sheet = workbook.createSheet(Const.EXPORT_EXCEL.FILE_NAME_QUESTION_DEFINITION);

            CellStyle style2 = workbook.createCellStyle();
            XSSFFont font2 = workbook.createFont();
            font2.setBold(true);
            font2.setFontHeight(17);
            style2.setFont(font2);
            style2.setAlignment(HorizontalAlignment.CENTER);
            Row firstRow = sheet.createRow(0);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));
            cell = firstRow.createCell(0, CellType.STRING);
            cell.setCellValue("DANH SÁCH CÂU HỎI TỰ ĐỊNH NGHĨA");
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
            cell.setCellValue(Const.EXPORT_EXCEL.TOPIC_NAME);
            cell.setCellStyle(style);
            sheet.autoSizeColumn(2);
            cell = row.createCell(3, CellType.STRING);
            cell.setCellValue(Const.EXPORT_EXCEL.TT);
            cell.setCellStyle(style);
            sheet.autoSizeColumn(3);
            cell = row.createCell(4, CellType.STRING);
            cell.setCellValue(Const.EXPORT_EXCEL.DESCRIPTION);
            cell.setCellStyle(style);
            sheet.autoSizeColumn(4);
            cell = row.createCell(5, CellType.STRING);
            cell.setCellStyle(style);
            cell.setCellValue(Const.EXPORT_EXCEL.STATUS);
            sheet.autoSizeColumn(5);

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

            XSSFCellStyle style3 = workbook.createCellStyle();
            style1.setWrapText(true);
            XSSFFont font3 = workbook.createFont();
            font3.setFontHeight(14);
            style3.setFont(font3);
            style3.setBorderTop(BorderStyle.THIN);
            style3.setBorderBottom(BorderStyle.THIN);
            style3.setBorderLeft(BorderStyle.THIN);
            style3.setBorderRight(BorderStyle.THIN);
            style3.setVerticalAlignment(VerticalAlignment.TOP);
            style3.setAlignment(HorizontalAlignment.CENTER);

            int count = 0;
            for (QuestionDefinitionDTO emp : listData) {
                rownum++;
                row = sheet.createRow(rownum);
                cell = row.createCell(0, CellType.STRING);
                cell.setCellValue(emp.getQuestionDefinitionName());
                cell.setCellStyle(style1);
                sheet.setColumnWidth(0, 17000);
                cell = row.createCell(1, CellType.STRING);
                cell.setCellValue(Jsoup.parse(emp.getAnswerDefinition()).text());
                cell.setCellStyle(style1);
                sheet.setColumnWidth(1, 17000);
                cell = row.createCell(2, CellType.STRING);
                cell.setCellValue(emp.getTopicName());
                cell.setCellStyle(style1);
                sheet.setColumnWidth(2, 6000);

                cell = row.createCell(3, CellType.STRING);
                cell.setCellValue(emp.getNumberOrder() == null ? "" : emp.getNumberOrder().toString());
                cell.setCellStyle(style3);
                sheet.setColumnWidth(3, 2000);

                cell = row.createCell(4, CellType.STRING);
                cell.setCellValue(emp.getDescription());
                cell.setCellStyle(style1);
                sheet.setColumnWidth(4, 17000);

                cell = row.createCell(5, CellType.STRING);
                cell.setCellValue(emp.getStatus() == 1L ? "Hoạt động" : "Không hoạt động");
                cell.setCellStyle(style1);
                sheet.setColumnWidth(5, 6000);
            }

            File outputFile = File.createTempFile("test", ".xls");
            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                workbook.write(fos);
            }

            fileContent = FileUtils.readFileToByteArray(outputFile);

        } catch (Exception ex) {
            LOGGER.error("export question error: " + ex.getMessage());
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


}
