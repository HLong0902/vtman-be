package com.viettel.vtman.cms.service;

import com.viettel.vtman.cms.dto.EmployeeDto;
import com.viettel.vtman.cms.entity.Employee;

import java.util.List;
import java.util.Map;

public interface EmployeeService {
    List<EmployeeDto> findAllByDepartmentId(Long departmentId);
    EmployeeDto findNameById(Long employeeId);
    List<Employee> findAll();

    EmployeeDto findByUserId(Long userId);
    EmployeeDto findByEmployeeCode(String employeeCode);

    EmployeeDto getOrCreateFromJwt();

    EmployeeDto getOrCreateFromJwtApp(String employeeCode);

    List<Employee> findAllSortByName();

    Employee findByEmployeeId(Long employeeId);

    Employee update(Employee employee);

    EmployeeDto createEmployeeCms(Map<String, Object> jwt);
    EmployeeDto findByRoleId(Long roleId);

    List<Employee> findByIds(List<Long> employeeIds);

    Employee updateEmployeeCms(Long employeeId, Map<String, Object> jwt);
}
