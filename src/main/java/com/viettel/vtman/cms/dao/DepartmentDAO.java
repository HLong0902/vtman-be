package com.viettel.vtman.cms.dao;

import com.viettel.vtman.cms.dto.DepartmentDTO;
import com.viettel.vtman.cms.entity.Department;
import com.viettel.vtman.cms.entity.Menu;

import java.util.List;

public interface DepartmentDAO {
    List<DepartmentDTO> findAll();

    List<Department> searchByDto(DepartmentDTO dto);

    long countByDto(DepartmentDTO dto);

    void deleteByDepartmentId(Long departmentId);

    String updateByEntity(Department department);

    String createByEntity(Department department);

    boolean checkSafeDelete(Long departmentId);

    List<DepartmentDTO> findAllStatus();

    Department getDepartmentById(Long departmentId);
}
