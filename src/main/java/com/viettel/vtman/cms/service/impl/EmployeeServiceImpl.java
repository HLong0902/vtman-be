package com.viettel.vtman.cms.service.impl;

import com.google.gson.Gson;
import com.viettel.vtman.cms.dao.EmployeeDAO;
import com.viettel.vtman.cms.dto.EmployeeDto;
import com.viettel.vtman.cms.entity.Employee;
import com.viettel.vtman.cms.service.EmployeeService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger LOGGER = LogManager.getLogger(EmployeeServiceImpl.class);

    @Autowired
    private Gson gson;

    @Autowired
    private EmployeeDAO dao;
    @Override
    public List<EmployeeDto> findAllByDepartmentId(Long departmentId) {
        return dao.findAllById(departmentId);
    }

    @Override
    public EmployeeDto findNameById(Long employeeId) {
        return dao.findNameById(employeeId);
    }

    @Override
    public List<Employee> findAll() {
        return dao.findAll();
    }

    @Override
    public EmployeeDto findByUserId(Long userId) {
        try {
            return new EmployeeDto(dao.findByUserId(userId));
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public EmployeeDto findByEmployeeCode(String employeeCode) {
        try {
            return new EmployeeDto(dao.findByEmployeeCode(employeeCode));
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public EmployeeDto getOrCreateFromJwt() {
//        Map<String, Object> jwt = (Map<String, Object>) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        EmployeeDto dto = this.findByUserId(Long.valueOf(jwt.get("userId").toString()));
//
//        if (dto == null) {
//            Employee employee = dao.createFromJwt(jwt);
//            if (employee != null) {
//                return new EmployeeDto(employee);
//            }
//        }
//
//        return dto;
        return (EmployeeDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Override
    public EmployeeDto getOrCreateFromJwtApp(String employeeCode) {
        Map<String, Object> jwt = (Map<String, Object>) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOGGER.info("jwt app info: " + gson.toJson(jwt));
//        EmployeeDto dto = this.findByUserId(Long.valueOf(jwt.get("userid").toString()));
//
//        if (dto == null) {
//            Employee employee = dao.createFromJwtApp(jwt, employeeCode);
//            if (employee != null) {
//                return new EmployeeDto(employee);
//            }
//        }
//        LOGGER.info("employeeDto app: " + gson.toJson(dto));
//        return dto;

        Employee empCheck = dao.findByUserId(Long.valueOf(jwt.get("userid").toString()));
        if (Objects.nonNull(empCheck)) {
            empCheck.setEmployeeCode(employeeCode);
            empCheck.setEmployeeName(String.valueOf(jwt.get("name")));
            empCheck.setUserName(String.valueOf(jwt.get("username")));
            empCheck.setPhone(String.valueOf(jwt.get("phone")));
            empCheck.setPostOfficeCode(String.valueOf(jwt.get("ma_buucuc")));
            empCheck.setUpdatedBy(empCheck.getEmployeeId());
            empCheck.setUpdatedDate(new Date());

            empCheck = dao.updateEmployee(empCheck);
            return new EmployeeDto(empCheck);
        }

        Employee empNew = dao.createFromJwtApp(jwt, employeeCode);
        if (Objects.nonNull(empNew)) {
            return new EmployeeDto(empNew);
        }

        return null;
    }

    @Override
    public List<Employee> findAllSortByName() {
        return dao.findAllSortByName();
    }

    @Override
    public Employee findByEmployeeId (Long employeeId){
        return dao.findByEmployeeId(employeeId);
    }

    @Override
    public Employee update(Employee employee){
        return dao.updateEmployee(employee);
    }

    @Override
    public EmployeeDto createEmployeeCms(Map<String, Object> jwt) {
        Employee employee = dao.createFromJwt(jwt);
        if (employee != null) {
            return new EmployeeDto(employee);
        }
        return null;
    }
    @Override
    public EmployeeDto findByRoleId(Long roleId) {
        return dao.findByRoleId(roleId);
    }

    @Override
    public List<Employee> findByIds(List<Long> employeeIds) {
        return dao.findByIds(employeeIds);
    }

    @Override
    public Employee updateEmployeeCms(Long employeeId, Map<String, Object> jwt) {
        Employee employee = dao.findByEmployeeId(employeeId);
        employee.setEmployeeCode(StringUtils.isEmpty(jwt.get("ma_nhanvien")) ? String.valueOf(jwt.get("username")) : String.valueOf(jwt.get("ma_nhanvien")));
        employee.setEmployeeName(jwt.get("lastname") + " " + jwt.get("firstname"));
        employee.setEmail(String.valueOf(jwt.get("email")));
        employee.setUserName(String.valueOf(jwt.get("username")));
        employee.setPhone(String.valueOf(jwt.get("phone")));
        employee.setPostOfficeCode(String.valueOf(jwt.get("mabuucuc")));
        employee.setUpdatedBy(employeeId);
        employee.setUpdatedDate(new Date());

        return dao.updateEmployee(employee);
    }
}
