package com.viettel.vtman.cms.service;

import com.viettel.vtman.cms.dto.DepartmentDTO;
import com.viettel.vtman.cms.entity.Department;
import com.viettel.vtman.cms.entity.Menu;

import java.util.List;

public interface DepartmentService {
    List<DepartmentDTO> findAll();

    List<DepartmentDTO> searchByDto(DepartmentDTO dto);

    long countByDto(DepartmentDTO dto);

    void deleteByDepartmentId(Long departmentId);

    String updateByDto(DepartmentDTO dto);

    String createByDto(DepartmentDTO dto);

    boolean checkSafeDelete(Long departmentId);

    List<DepartmentDTO> findAllStatus();

    Department getDepartmentById(Long departmentId);
}
