package com.viettel.vtman.cms.controller;

import com.viettel.vtman.cms.dto.DepartmentDTO;
import com.viettel.vtman.cms.dto.EmployeeDto;
import com.viettel.vtman.cms.dto.HistoryFaqDTO;
import com.viettel.vtman.cms.dto.ObjectResult;
import com.viettel.vtman.cms.dto.QuestionDefinitionDTO;
import com.viettel.vtman.cms.dto.TopicDto;
import com.viettel.vtman.cms.entity.Topic;
import com.viettel.vtman.cms.message.CommonController;
import com.viettel.vtman.cms.message.Const;
import com.viettel.vtman.cms.service.DepartmentService;
import com.viettel.vtman.cms.service.EmployeeService;
import com.viettel.vtman.cms.service.HistoryFaqService;
import com.viettel.vtman.cms.service.QuestionDefinitionService;
import com.viettel.vtman.cms.service.TopicService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@RestController
@RequestMapping("/api/topic")
public class TopicController extends CommonController {

    private static final Logger LOGGER = LogManager.getLogger(TopicController.class);
    @Autowired
    private TopicService topicService;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private HistoryFaqService historyFaqService;
    @Autowired
    private QuestionDefinitionService questionDefinitionService;

    @GetMapping("/")
    public List<Topic> getTopics() {
        return topicService.findAll();
    }


    @GetMapping("/search")
    public ResponseEntity<?> searchByName(@RequestParam("topicName") String topicName, @RequestParam("page") int page, @RequestParam("pageSize") int pageSize, @RequestParam("departmentId") Long departmentId) {
        try {
            topicName = URLDecoder.decode(topicName, StandardCharsets.UTF_8.name());
            ObjectResult objectResult = new ObjectResult();
            objectResult.setPage(page);
            objectResult.setPageSize(pageSize);
            EmployeeDto dto = new EmployeeDto();

            List<EmployeeDto> listEmployee = null;
            List<TopicDto> listData = topicService.searchByName(topicName, departmentId, objectResult);

            for (TopicDto obj : listData) {
                String result = obj.getAnswerEmployeeId();
                if (result != null && !"".equals(result)) {
                    if (result.contains(",")) {
                        String split[] = result.split(",");
                        if (split.length > 0) {
                            listEmployee = new ArrayList<>();

                            for (int i = 0; i < split.length; i++) {
                                dto = employeeService.findNameById(Long.parseLong(split[i].trim()));
                                if (dto != null)
                                    listEmployee.add(dto);
                            }
                            if (listEmployee != null && listEmployee.size() > 0)
                                obj.setAnswerEmployeeName(setCode(listEmployee));
                        }
                    } else {
                        listEmployee = new ArrayList<>();
                        dto = employeeService.findNameById(Long.parseLong(result));
                        listEmployee.add(dto);
                        obj.setAnswerEmployeeName(setCode(listEmployee));
                    }
                }


            }

            TopicDto data = new TopicDto();

            data.setData(listData);
            data.setTotalRecord(objectResult.getTotalRecord());
            data.setPageSize(objectResult.getPageSize());
            data.setStart(1);
            return toSuccessResult(listData);
        } catch (Exception ex) {
            LOGGER.error("Search error" + ex.getMessage());
            return toExceptionResult(ex.getMessage(), Const.API_RESPONSE.RETURN_CODE_ERROR);
        }

    }

    public String setCode(List<EmployeeDto> list) {
        String code = "";
        try {
            for (EmployeeDto listResult : list) {

                if (!"".equals(code.toString())) {
                    if (null != listResult.getPostOfficeCode() && !"".equals(listResult.getPostOfficeCode())) {
                        code = code + "; " + listResult.getPostOfficeCode();
                    } else {
                        code = code + "; ";
                    }

                } else {
                    if (StringUtils.isEmpty(listResult.getPostOfficeCode())) {
                        code = code;
                    } else {
                        code = code + listResult.getPostOfficeCode();
                    }

                }


                if (null != listResult.getEmployeeCode() && !"".equals(listResult.getEmployeeCode()) ) {
                    if (!"".equals(code.toString())) {
                        if(listResult.getPostOfficeCode()!= null && !"".equals(listResult.getPostOfficeCode())){
                            code = code + " - " + listResult.getEmployeeCode();
                        }else{
                            code = code  + listResult.getEmployeeCode();
                        }

                    } else {
                        code = code + listResult.getEmployeeCode();
                    }
                }


                if (null != listResult.getEmployeeName() && !"".equals(listResult.getEmployeeName())) {
                    if (!"".equals(code.toString())) {
                        code = code + " - " + listResult.getEmployeeName();
                    } else {
                        code = code + listResult.getEmployeeName();
                    }
                }


            }
        } catch (Exception e) {
            LOGGER.error("setCode error" + e.getMessage());
        }
        return code;
    }

    public List<TopicDto> setData(List<EmployeeDto> list) {
        List<String> result = new ArrayList<>();

        List<TopicDto> resultDto = new ArrayList<>();

        try {
            for (EmployeeDto listResult : list) {
                TopicDto topicDto = new TopicDto();
                String code = "";
                String name = "";

                if (null != listResult.getEmployeeCode() && !"".equals(code.toString())) {
                    code = code + listResult.getEmployeeCode();
                }
                topicDto.setEmployeeCode(code);
                if (null != listResult.getEmployeeName() && !"".equals(name.toString())) {
                    name = name + listResult.getEmployeeName();
                }
                topicDto.setEmployName(name);

                topicDto.setAnswerEmployeeId(String.valueOf(listResult.getEmployeeId()));

                resultDto.add(topicDto);


            }

        } catch (Exception e) {
            LOGGER.error("setData error" + e.getMessage());
        }
        return resultDto;


    }

    @GetMapping("/delete")
    public ResponseEntity<?> deleteById(@RequestParam("topicId") Long topicId) {
        try {
            List<QuestionDefinitionDTO> listCountQuestion = questionDefinitionService.countTopicId(topicId);
            List<HistoryFaqDTO> listCountHis = historyFaqService.countTopicId(topicId);

            Topic result = topicService.findById(topicId);
            if (result != null) {
                if (listCountHis.size() == 0 && listCountQuestion.size() == 0) {
                    topicService.deleteById(topicId);
                } else {
                    return toSuccessResultTopicId(result);
                }

            } else {
                return toExceptionResult(null, Const.API_RESPONSE.TOPIC_ID_NOT_FOUND);
            }

            return toSuccessResult(null);
        } catch (Exception ex) {
            LOGGER.error("delete Topic error" + ex.getMessage());
            return toExceptionResult(ex.getMessage(), Const.API_RESPONSE.RETURN_CODE_ERROR);
        }

    }

    @GetMapping("/department")
    public ResponseEntity<?> getDepartment() {
        try {
            List<DepartmentDTO> listData = departmentService.findAll();
            return toSuccessResult(listData);

        } catch (Exception ex) {
            LOGGER.error("getDepartMent topic error" + ex.getMessage());
            return toExceptionResult(ex.getMessage(), Const.API_RESPONSE.RETURN_CODE_ERROR);
        }
    }

    @GetMapping("/employee")
    public ResponseEntity<?> getEmployee(@RequestParam("departmentId") Long departmentId) {
        List<EmployeeDto> listData;
        try {
            listData = employeeService.findAllByDepartmentId(departmentId);
        } catch (Exception e) {
            LOGGER.error("gêtmployee topic error" + e.getMessage());
            return toExceptionResult(e.getMessage(), Const.API_RESPONSE.RETURN_CODE_ERROR);
        }
        return toSuccessResult(listData);
    }


    @PostMapping("/edit")
    public ResponseEntity<?> updateById(@RequestParam("topicId") Long topicId, @RequestBody TopicDto topic) throws Exception {
        Topic data = new Topic();
        try {

            Date date = new Date();
            TopicDto checkDupplicateName = new TopicDto();
            checkDupplicateName = topicService.findByName(topic.getTopicName());
            TopicDto checkDupplicateCode = new TopicDto();
            checkDupplicateCode = topicService.findByCode(topic.getTopicCode());
            if (checkDupplicateName != null) {
                if (!topicId.equals(checkDupplicateName.getTopicId())) {
                    checkDupplicateName.setMessageError(Const.API_RESPONSE.TOPIC_NAME_DUPLICATE);
                    return toSuccessResultErrorTopicId(checkDupplicateName);
                } else {
                    Topic result = topicService.findById(topicId);
                    result.setTopicName(topic.getTopicName());
                    result.setAnswerEmployeeId(topic.getAnswerEmployeeId());
                    result.setDescription(topic.getDescription());
                    result.setDepartmentId(topic.getDepartmentId());
                    result.setStatus(topic.getStatus());
                    result.setNumberOrder(topic.getNumberOrder());
                    result.setTopicCode(topic.getTopicCode().toUpperCase());
                    result.setUpdatedDate(date);
                    result.setUpdatedBy(topic.getUpdatedBy());
                    data = topicService.update(result);

                }
            } else if (checkDupplicateCode != null) {
                if (!topicId.equals(checkDupplicateCode.getTopicId())) {
                    checkDupplicateCode.setMessageError(Const.API_RESPONSE.TOPIC_CODE_DUPLICATE);
                    return toSuccessResultErrorTopicId(checkDupplicateCode);
                } else {
                    Topic result = topicService.findById(topicId);


                    result.setTopicName(topic.getTopicName());
                    result.setAnswerEmployeeId(topic.getAnswerEmployeeId());
                    result.setDescription(topic.getDescription());
                    result.setDepartmentId(topic.getDepartmentId());
                    result.setStatus(topic.getStatus());
                    result.setNumberOrder(topic.getNumberOrder());
                    result.setTopicCode(topic.getTopicCode().toUpperCase());
                    result.setUpdatedDate(date);
                    result.setUpdatedBy(topic.getUpdatedBy());
                    data = topicService.update(result);
                }


            } else {
                Topic result = topicService.findById(topicId);


                result.setTopicName(topic.getTopicName());
                result.setAnswerEmployeeId(topic.getAnswerEmployeeId());
                result.setDescription(topic.getDescription());
                result.setDepartmentId(topic.getDepartmentId());
                result.setStatus(topic.getStatus());
                result.setNumberOrder(topic.getNumberOrder());
                result.setTopicCode(topic.getTopicCode().toUpperCase());
                result.setUpdatedDate(date);
                result.setUpdatedBy(topic.getUpdatedBy());
                data = topicService.update(result);
            }
        } catch (Exception ex) {
            LOGGER.error("edit topic error" + ex.getMessage());
            return toExceptionResult(ex.getMessage(), Const.API_RESPONSE.RETURN_CODE_ERROR);
        }
        return toSuccessResult(data);

    }


    @GetMapping("/edit")
    public ResponseEntity<?> updateById(@RequestParam("topicId") Long topicId) throws Exception {
        TopicDto topicDto = new TopicDto();
        try {
            EmployeeDto dto = new EmployeeDto();
            topicDto = topicService.findId(topicId);
            List<EmployeeDto> listEmployee = new ArrayList<>();
            String result = topicDto.getAnswerEmployeeId();
            if (!"".equals(result) && result != null) {
                if (result.contains(",")) {
                    String split[] = result.split(",");
                    if (split.length > 0) {
                        listEmployee = new ArrayList<>();

                        for (int i = 0; i < split.length; i++) {
                            dto = employeeService.findNameById(Long.parseLong(split[i]));
                            if (dto != null)
                                listEmployee.add(dto);
                        }
                        if (listEmployee != null && listEmployee.size() > 0)
                            topicDto.setResultName(setData(listEmployee));
                    }
                } else {
                    dto = employeeService.findNameById(Long.parseLong(result));
                    if (dto != null)
                        listEmployee.add(dto);
                    if (listEmployee != null && listEmployee.size() > 0)
                        topicDto.setResultName(setData(listEmployee));
                }

            } else {
                topicDto.setEmployName("");
                topicDto.setEmployeeCode("");
            }

        } catch (Exception e) {
            LOGGER.error("editGet topic error" + e.getMessage());
            return toExceptionResult(e.getMessage(), Const.API_RESPONSE.RETURN_CODE_ERROR);
        }

        return toSuccessResult(topicDto);

    }


    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody TopicDto topicDto) throws Exception {
        Topic result = new Topic();
        Topic data = new Topic();
        try {
            TopicDto checkDupplicateName = new TopicDto();
            checkDupplicateName = topicService.findByName(topicDto.getTopicName());
            TopicDto checkDupplicateCode = new TopicDto();
            checkDupplicateCode = topicService.findByCode(topicDto.getTopicCode());
            if (checkDupplicateName != null) {
                checkDupplicateName.setMessageError(Const.API_RESPONSE.TOPIC_NAME);
                return toSuccessResultErrorTopicName(checkDupplicateName);
            } else if (checkDupplicateCode != null) {
                checkDupplicateCode.setMessageError(Const.API_RESPONSE.TOPIC_NAME_DUPLICATE);
                return toSuccessResultErrorTopicId(checkDupplicateCode);
            } else {
                Date date = new Date();
                data.setTopicCode(topicDto.getTopicCode().toUpperCase());
                data.setTopicName(topicDto.getTopicName());
                data.setDepartmentId(topicDto.getDepartmentId());
                data.setAnswerEmployeeId(topicDto.getAnswerEmployeeId());
                data.setDescription(topicDto.getDescription());
                data.setNumberOrder(topicDto.getNumberOrder());
                data.setStatus(topicDto.getStatus());
                data.setCreatedDate(date);
                data.setCreatedBy(topicDto.getCreatedBy());
                result = topicService.save(data);
            }
        } catch (Exception e) {
            LOGGER.error("create topic error" + e.getMessage());
            return toExceptionResult(e.getMessage(), Const.API_RESPONSE.RETURN_CODE_ERROR);
        }
        return toSuccessResult(result);

    }

    @GetMapping("/forward")
    public ResponseEntity<?> getForwardTopic(@RequestParam(name = "topicId") Long topicId) {
        LOGGER.info("getForwardTopic param: topicId: " + topicId);
        try {
            List<TopicDto> result = topicService.getForwardTopic(topicId);
            LOGGER.info("getForwardTopic result: " + result);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error("getForwardTopic error: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
