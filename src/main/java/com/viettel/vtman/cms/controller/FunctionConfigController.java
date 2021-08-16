package com.viettel.vtman.cms.controller;

import com.viettel.vtman.cms.dto.DepartmentDTO;
import com.viettel.vtman.cms.dto.EmployeeDto;
import com.viettel.vtman.cms.dto.FunctionConfigDTO;
import com.viettel.vtman.cms.dto.HistoryFaqDTO;
import com.viettel.vtman.cms.entity.FunctionConfig;
import com.viettel.vtman.cms.message.CommonController;
import com.viettel.vtman.cms.message.Const;
import com.viettel.vtman.cms.service.DepartmentService;
import com.viettel.vtman.cms.service.EmployeeService;
import com.viettel.vtman.cms.service.FunctionConfigService;
import com.viettel.vtman.cms.service.HistoryFaqService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;


@RestController
@RequestMapping(value = "/api/functionConfig")
public class FunctionConfigController extends CommonController {
    private static final Logger LOGGER = LogManager.getLogger(FunctionConfigController.class);
    @Autowired
    private FunctionConfigService functionConfigService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private HistoryFaqService historyFaqService;

    @PostMapping(value = "/add")
    public ResponseEntity<?> save(@RequestBody FunctionConfigDTO functionConfig) throws Exception{
        FunctionConfig result = new FunctionConfig();
        FunctionConfig data = new FunctionConfig();
        try{
            EmployeeDto employeeDto = employeeService.getOrCreateFromJwt();
            if (functionConfigService.findAll().size()>0){
                return toExceptionResult("DO_NOT_ALLOW", Const.API_RESPONSE.RETURN_CODE_ERROR);
            }
            Date date = new Date();
            data.setDepartmentId(functionConfig.getDepartmentId());
            data.setEmployeeId(functionConfig.getEmployeeId());
            data.setMaximumResponseTime(functionConfig.getMaximumResponseTime());
            data.setResponseRemindingTime(functionConfig.getResponseRemindingTime());
            data.setMaximumWaitingTime(functionConfig.getMaximumWaitingTime());
            data.setRemindingWaitingTime(functionConfig.getRemindingWaitingTime());
            data.setMaximumQASession(functionConfig.getMaximumQASession());
            data.setAnswerKPIPercent(functionConfig.getAnswerKPIPercent());
            data.setCreatedBy(employeeDto.getEmployeeId());
            data.setCreatedDate(date);
            result = functionConfigService.save(data);

        }catch (Exception exception){
            LOGGER.error("FunctionConfigController Create Error: " + exception.getMessage());
            return toExceptionResult(exception.getMessage(), Const.API_RESPONSE.RETURN_CODE_ERROR);
        }
        return toSuccessResult(result);
    }

    @PutMapping(value = "/update")
    public ResponseEntity<?> update(@RequestParam ("functionConfigId") Long functionConfigId,
                                    @RequestBody FunctionConfigDTO functionConfig) throws Exception{
        FunctionConfig data = new FunctionConfig();
        try {
            EmployeeDto employeeDto = employeeService.getOrCreateFromJwt();
            Date date = new Date();
            FunctionConfig checkDuplicateId = functionConfigService.findById(functionConfig.getFunctionConfigId());
            List<HistoryFaqDTO> checkQuestionStatus = new ArrayList<>();

            if (Objects.nonNull(checkDuplicateId)){
                if (checkDuplicateId.getDepartmentId().compareTo(functionConfig.getDepartmentId()) != 0) {
                    checkQuestionStatus = historyFaqService.checkQuestionStatus(functionConfigService.findById(functionConfigId).getDepartmentId());
                }
//                if (!checkQuestionStatus.isEmpty()){
//                    return toAnswerQuestions(checkQuestionStatus);
//                }
                if (!functionConfigId.equals(checkDuplicateId.getFunctionConfigId())){
                    return toSuccessResultErrorTopicId(checkDuplicateId);
                }
                else {
                    FunctionConfig result = functionConfigService.findById(functionConfigId);
                    result.setDepartmentId(functionConfig.getDepartmentId());
                    result.setEmployeeId(functionConfig.getEmployeeId());
                    result.setMaximumResponseTime(functionConfig.getMaximumResponseTime());
                    result.setResponseRemindingTime(functionConfig.getResponseRemindingTime());
                    result.setMaximumWaitingTime(functionConfig.getMaximumWaitingTime());
                    result.setRemindingWaitingTime(functionConfig.getRemindingWaitingTime());
                    result.setMaximumQASession(functionConfig.getMaximumQASession());
                    result.setAnswerKPIPercent(functionConfig.getAnswerKPIPercent());
                    result.setUpdatedBy(employeeDto.getEmployeeId());
                    result.setUpdatedDate(date);

                    data = functionConfigService.update(result);
                }
            }
            else {
                FunctionConfig result = functionConfigService.findById(functionConfigId);
                result.setDepartmentId(functionConfig.getDepartmentId());
                result.setEmployeeId(functionConfig.getEmployeeId());
                result.setMaximumResponseTime(functionConfig.getMaximumResponseTime());
                result.setResponseRemindingTime(functionConfig.getResponseRemindingTime());
                result.setMaximumWaitingTime(functionConfig.getMaximumWaitingTime());
                result.setRemindingWaitingTime(functionConfig.getRemindingWaitingTime());
                result.setMaximumQASession(functionConfig.getMaximumQASession());
                result.setAnswerKPIPercent(functionConfig.getAnswerKPIPercent());
                result.setUpdatedBy(employeeDto.getEmployeeId());
                result.setUpdatedDate(date);

                data = functionConfigService.update(result);
            }
        }catch (Exception exception){
            LOGGER.error("FunctionConfigController Update Error: " + exception.getMessage());
            return toExceptionResult(exception.getMessage(), Const.API_RESPONSE.RETURN_CODE_ERROR);
        }
        return toSuccessResult(data);
    }

    @GetMapping(value = "/display")
    public ResponseEntity<?> displayList(){
        try {
            EmployeeDto dto = new EmployeeDto();
            List<EmployeeDto> employeeDtoList = null;
            List<FunctionConfigDTO> functionConfigDTOList = functionConfigService.displayAll();

            for (FunctionConfigDTO obj : functionConfigDTOList) {
                String result = obj.getEmployeeId();
                if (result != null && Objects.nonNull(result)){
                    if (result.contains(",")){
                        String splitData [] = result.split(",");
                        if (splitData.length > 0){
                            employeeDtoList = new ArrayList<>();

                            for (int i = 0; i<splitData.length; i++){
                                dto = employeeService.findNameById(Long.parseLong(splitData[i]));
                                if (dto!=null){
                                    employeeDtoList.add(dto);
                                }
                            }
                            if (employeeDtoList !=null && employeeDtoList.size()>0){
                                obj.setEmployeeName(setCode(employeeDtoList));
                            }
                        }
                    }
                    else {
                        employeeDtoList = new ArrayList<>();
                        dto = employeeService.findNameById(Long.parseLong(result));
                        employeeDtoList.add(dto);
                        obj.setEmployeeName(setCode(employeeDtoList));
                    }
                }
            }
            return toSuccessResult(functionConfigDTOList);
        }catch (Exception exception){
            LOGGER.error("FunctionConfigController Display Error: " + exception.getMessage());
            return toExceptionResult(exception.getMessage(), Const.API_RESPONSE.RETURN_CODE_ERROR);
        }
    }

    public String setCode(List<EmployeeDto> list) {
        String code = "";
        try {
            for (EmployeeDto listResult : list) {
                if (null != listResult.getEmployeeCode()) {
                    if (!"".equals(code.toString())) {
                        code = code + "," + listResult.getEmployeeCode();
                    } else {
                        code = code + listResult.getEmployeeCode();
                    }
                }

                if (null != listResult.getEmployeeName()) {
                    if (!"".equals(code.toString())) {
                        code = code + "-" + listResult.getEmployeeName();
                    } else {
                        code = code + listResult.getEmployeeName();
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("FunctionConfigController Set Code Error: " + e.getMessage());
        }
        return code;
    }

    @GetMapping(value = "/employee")
    public ResponseEntity<?> getEmployeeBy(@RequestParam("departmentId") Long departmentId){
        List<EmployeeDto> listDat;
        try{
            listDat = employeeService.findAllByDepartmentId(departmentId);
        }catch (Exception e){
            LOGGER.error("FunctionConfigController Get Employee By Department Id Error: " + e.getMessage());
            return toExceptionResult(e.getMessage(), Const.API_RESPONSE.RETURN_CODE_ERROR);
        }
        return toSuccessResult(listDat);
    }

    @GetMapping("/department")
    public ResponseEntity<?> getDepartment() {
        try {
            List<DepartmentDTO> listData = departmentService.findAll();
            return toSuccessResult(listData);

        } catch (Exception ex) {
            LOGGER.error("FunctionConfigController Get Department Error: " + ex.getMessage());
            return toExceptionResult(ex.getMessage(), Const.API_RESPONSE.RETURN_CODE_ERROR);
        }
    }
}
