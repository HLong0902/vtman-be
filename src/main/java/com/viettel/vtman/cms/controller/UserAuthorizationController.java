package com.viettel.vtman.cms.controller;

import com.viettel.vtman.cms.dto.DepartmentDTO;
import com.viettel.vtman.cms.dto.EmployeeDto;
import com.viettel.vtman.cms.dto.ObjectResultPage;
import com.viettel.vtman.cms.dto.RoleDTO;
import com.viettel.vtman.cms.dto.UserAuthorizationDTO;
import com.viettel.vtman.cms.entity.Employee;
import com.viettel.vtman.cms.entity.FunctionConfig;
import com.viettel.vtman.cms.entity.Topic;
import com.viettel.vtman.cms.message.CommonController;
import com.viettel.vtman.cms.message.Const;
import com.viettel.vtman.cms.service.DepartmentService;
import com.viettel.vtman.cms.service.EmployeeService;
import com.viettel.vtman.cms.service.FunctionConfigService;
import com.viettel.vtman.cms.service.RoleService;
import com.viettel.vtman.cms.service.TopicService;
import com.viettel.vtman.cms.service.UserAuthorizationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@RestController
@RequestMapping(value = "/api/userAuthorization")
public class UserAuthorizationController extends CommonController {

    private static final Logger LOGGER = LogManager.getLogger(UserAuthorizationController.class);

    @Autowired
    private UserAuthorizationService userAuthorizationService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private TopicService topicService;

    @Autowired
    private FunctionConfigService functionConfigService;

    @GetMapping(value = "/get")
    public ResponseEntity<?> searchUserAuthorization(@RequestParam("employee") String employee,
                                                     @RequestParam("roleId") Long roleId,
                                                     @RequestParam("departmentId") Long departmentId,
                                                     @RequestParam("page") Long page,
                                                     @RequestParam("pageSize") Long pageSize) {
        try {
            ObjectResultPage objectResultPage = new ObjectResultPage();
            objectResultPage.setPage(page);
            objectResultPage.setPageSize(pageSize);

            List<UserAuthorizationDTO> userAuthorizationDTOList = userAuthorizationService.searchUserAuthorization(employee, roleId, departmentId, objectResultPage);

            UserAuthorizationDTO data = new UserAuthorizationDTO();
            data.setData(userAuthorizationDTOList);
            data.setTotalRecord(objectResultPage.getTotalRecord());
            return toSuccessResult(userAuthorizationDTOList);
        } catch (Exception e) {
            LOGGER.error("UserAuthorization Display Error: " + e.getMessage());
            return toExceptionResult("FAIL", Const.API_RESPONSE.RETURN_CODE_ERROR);
        }
    }

    @GetMapping(value = "/employeeId")
    public ResponseEntity<?> getByEmployeeId(@RequestParam("employeeId") Long employeeId) {
        List<UserAuthorizationDTO> userAuthorizationDTOList;
        try {
            userAuthorizationDTOList = userAuthorizationService.getByEmployeeId(employeeId);
        } catch (Exception e) {
            LOGGER.error("UserAuthorization Get Emp By EmpId Error: " + e.getMessage());
            return toExceptionResult(e.getMessage(), Const.API_RESPONSE.RETURN_CODE_ERROR);
        }
        return toSuccessResult(userAuthorizationDTOList);
    }

    @GetMapping(value = "/role")
    public ResponseEntity<?> getRole() {
        try {
            List<RoleDTO> roleDTOList = roleService.findAll();
            return toSuccessResult(roleDTOList);
        } catch (Exception exception) {
            LOGGER.error("UserAuthorization Get Role Error: " + exception.getMessage());
            return toExceptionResult("FAIL", Const.API_RESPONSE.RETURN_CODE_ERROR);
        }
    }

    @GetMapping(value = "/employee")
    public ResponseEntity<?> getEmployee() {
        try {
            List<Employee> employeeList = employeeService.findAllSortByName();
            return toSuccessResult(employeeList);
        } catch (Exception e) {
            LOGGER.error("UserAuthorization Get Employee Error: " + e.getMessage());
            return toExceptionResult("FAIL", Const.API_RESPONSE.RETURN_CODE_ERROR);
        }
    }

    @GetMapping(value = "/department")
    public ResponseEntity<?> getDepartment() {
        try {
            List<DepartmentDTO> departmentDTOList = departmentService.findAll();
            return toSuccessResult(departmentDTOList);
        } catch (Exception e) {
            LOGGER.error("UserAuthorization Get Department Error: " + e.getMessage());
            return toExceptionResult("FAIL", Const.API_RESPONSE.RETURN_CODE_ERROR);
        }
    }

    @PutMapping(value = "/update")
    public ResponseEntity<?> updateUserAuthorization(@RequestBody EmployeeDto employee) throws Exception {
        Employee data = new Employee();
        EmployeeDto employeeDto = employeeService.getOrCreateFromJwt();
        try {
            Employee result = employeeService.findByEmployeeId(employee.getEmployeeId());
            if (result == null) {
                return toExceptionResult("EMPLOYEE_ID_NOT_FOUND", Const.API_RESPONSE.EMPLOYEE_ID_NOT_FOUND);
            } else if (Objects.nonNull(employee.getDepartmentId())
                    && Objects.nonNull(result.getDepartmentId())
                    && employee.getDepartmentId().equals(result.getDepartmentId())) {
                result.setRoleId(employee.getRoleId());
                result.setUpdatedBy(employeeDto.getEmployeeId());
                result.setUpdatedDate(new Date());
                data = employeeService.update(result);
            } else {
                boolean functionConfigFlag = true;
                FunctionConfig functionConfig = functionConfigService.findAll().stream().findFirst().orElse(null);
                if (Objects.nonNull(functionConfig) && !StringUtils.isEmpty(functionConfig.getEmployeeId())) {
                    if (Arrays.stream(functionConfig.getEmployeeId().split(","))
                            .filter(e -> e.trim().equals(employee.getEmployeeId().toString())).count() > 0) {
                        functionConfigFlag = false;
                    }
                }

                List<Topic> topicList = topicService.findAll().stream().filter(e -> {
                    if (!StringUtils.isEmpty(e.getAnswerEmployeeId())) {
                        if (Arrays.stream(e.getAnswerEmployeeId().split(","))
                                .filter(f -> f.trim().equals(employee.getEmployeeId().toString())).count() > 0) {
                            return true;
                        }
                    }
                    return false;
                }).collect(Collectors.toList());

                if (topicList.isEmpty() && functionConfigFlag) {
                    result.setRoleId(employee.getRoleId());
                    result.setDepartmentId(employee.getDepartmentId());
                    result.setUpdatedBy(employeeDto.getEmployeeId());
                    result.setUpdatedDate(new Date());
                    data = employeeService.update(result);
                } else {
                    List<String> errData = new ArrayList<>();
                    if (!functionConfigFlag) {
                        StringBuilder errMgs = new StringBuilder("Phòng ban mặc định - ");
                        if (Objects.nonNull(functionConfig.getDepartmentId())) {
                            DepartmentDTO departmentDTO = new DepartmentDTO();
                            departmentDTO.setDepartmentId(functionConfig.getDepartmentId());
                            DepartmentDTO department = departmentService.searchByDto(departmentDTO).stream().findFirst().orElse(null);
                            assert department != null;
                            errMgs.append(department.getDepartmentName());
                        }
                        errData.add(errMgs.toString());
                    }


                    for (Topic topic : topicList) {
                        StringBuilder errMgs = new StringBuilder("Chủ đề ");
                        errMgs.append(topic.getTopicName());
                        errMgs.append(" - ");
                        if (Objects.nonNull(topic.getDepartmentId())) {
                            DepartmentDTO departmentDTO = new DepartmentDTO();
                            departmentDTO.setDepartmentId(topic.getDepartmentId());
                            DepartmentDTO department = departmentService.searchByDto(departmentDTO).stream().findFirst().orElse(null);
                            assert department != null;
                            errMgs.append(department.getDepartmentName());
                        }
                        errData.add(errMgs.toString());
                    }

                    return toExceptionResult("Nhân viên " + result.getEmployeeName() + " đang là đầu mối của: ", errData, Const.API_RESPONSE.RETURN_CODE_ERROR);
                }
            }
        } catch (Exception e) {
            LOGGER.error("UserAuthorization Update Error: " + e.getMessage());
            return toExceptionResult("FAIL", Const.API_RESPONSE.RETURN_CODE_ERROR);
        }
        return toSuccessResult(data);
    }
}
