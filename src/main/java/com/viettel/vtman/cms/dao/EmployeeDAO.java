package com.viettel.vtman.cms.dao;

import com.viettel.vtman.cms.dto.EmployeeDto;
import com.viettel.vtman.cms.entity.Employee;

import java.util.List;
import java.util.Map;

public interface EmployeeDAO {
    EmployeeDto findNameById(Long employeeId);
    List<EmployeeDto> findAllById(Long departmentId);
    List<Employee> findAll();

    Employee findByUserId(Long userId);
    Employee findByEmployeeCode(String employeeCode);

    Employee createFromJwt(Map<String, Object> jwt);
    Employee createFromJwtApp(Map<String, Object> jwt, String employeeCode);
    Employee findById(Long employeeId);
    List<Employee> findAllSortByName();
    Employee findByEmployeeId(Long employeeId);
    Employee updateEmployee(Employee employee);
    EmployeeDto findByRoleId(Long roleId);
    List<Employee> findByIds(List<Long> employeeIds);
}
