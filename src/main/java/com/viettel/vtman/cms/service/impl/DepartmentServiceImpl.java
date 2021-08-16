package com.viettel.vtman.cms.service.impl;

import com.viettel.vtman.cms.dao.DepartmentDAO;
import com.viettel.vtman.cms.dao.FunctionConfigDAO;
import com.viettel.vtman.cms.dao.TopicDAO;
import com.viettel.vtman.cms.dto.DepartmentDTO;
import com.viettel.vtman.cms.dto.TopicDto;
import com.viettel.vtman.cms.entity.Department;
import com.viettel.vtman.cms.entity.FunctionConfig;
import com.viettel.vtman.cms.entity.Topic;
import com.viettel.vtman.cms.infrastructure.CMSConst;
import com.viettel.vtman.cms.message.Const;
import com.viettel.vtman.cms.service.DepartmentService;
import com.viettel.vtman.cms.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentDAO departmentDAO;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private TopicDAO topicDAO;

    @Autowired
    private FunctionConfigDAO functionConfigDAO;

    @Override
    public List<DepartmentDTO> findAll() {
        return departmentDAO.findAll();
    }

    @Override
    public List<DepartmentDTO> searchByDto(DepartmentDTO dto) {
        return departmentDAO.searchByDto(dto).stream().map(e -> new DepartmentDTO(e)).collect(Collectors.toList());
    }

    @Override
    public long countByDto(DepartmentDTO dto) {
        return departmentDAO.countByDto(dto);
    }

    @Override
    public void deleteByDepartmentId(Long departmentId) {
        departmentDAO.deleteByDepartmentId(departmentId);
    }

    @Override
    public String updateByDto(DepartmentDTO dto) {
        DepartmentDTO searchDto = new DepartmentDTO();
        searchDto.setDepartmentId(dto.getDepartmentId());
        List<Department> departmentList = departmentDAO.searchByDto(searchDto);
        if (departmentList.isEmpty()) {
            //return "Department not found";
            return  Const.API_RESPONSE.DEPARTMENT_NOT_EXIT;
        }

        String duplicateCheck = duplicateCheck(dto, dto.getDepartmentId());
        if (!StringUtils.isEmpty(duplicateCheck)) {
            return duplicateCheck;
        }

        Department entity = departmentList.get(0);

        if (dto.getStatus().equals(CMSConst.TOPIC_STATUS.KHONG_HOAT_DONG) && entity.getStatus().equals(CMSConst.TOPIC_STATUS.HOAT_DONG)) {
            TopicDto topicSearchDto = new TopicDto();
            topicSearchDto.setDepartmentId(entity.getDepartmentId());
            List<Topic> topicList = topicDAO.searchByDto(topicSearchDto);
            if (!topicList.isEmpty()) {
               // return "Không thể dừng hoạt động Phòng ban đã có chủ đề";
                return  Const.API_RESPONSE.DEPARTMENT_EXIT_TOPIC;
            }

            FunctionConfig functionConfig = functionConfigDAO.findAll().stream().findFirst().orElse(null);
            if (Objects.nonNull(functionConfig)
                    && Objects.nonNull(functionConfig.getDepartmentId())
                    && functionConfig.getDepartmentId().compareTo(dto.getDepartmentId()) == 0) {
                return Const.API_RESPONSE.DEPARTMENT_EXIT_FUNC;
            }
        }

        entity.setDepartmentName(dto.getDepartmentName());
        entity.setDepartmentCode(dto.getDepartmentCode());
        entity.setDescription(dto.getDescription());
        entity.setStatus(dto.getStatus());
        entity.setUpdatedBy(employeeService.getOrCreateFromJwt().getEmployeeId());
        entity.setUpdatedDate(new Date());
        return departmentDAO.updateByEntity(departmentList.get(0));
    }

    @Override
    public String createByDto(DepartmentDTO dto) {
        String duplicateCheck = duplicateCheck(dto, null);
        if (!StringUtils.isEmpty(duplicateCheck)) {
            return duplicateCheck;
        }

        Department department = new Department();
        department.setDepartmentName(dto.getDepartmentName());
        department.setDepartmentCode(dto.getDepartmentCode());
        department.setDescription(dto.getDescription());
        department.setStatus(dto.getStatus());
        department.setCreatedBy(employeeService.getOrCreateFromJwt().getEmployeeId());
        department.setCreatedDate(new Date());
        return departmentDAO.createByEntity(department);
    }

    @Override
    public boolean checkSafeDelete(Long departmentId) {
        return departmentDAO.checkSafeDelete(departmentId);
    }

    @Override
    public List<DepartmentDTO> findAllStatus() {
        return departmentDAO.findAllStatus();
    }

    public String duplicateCheck(DepartmentDTO dto, Long srcDepartmentId) {
        DepartmentDTO searchDto;
        List<Department> departmentList;

        searchDto = new DepartmentDTO();
        searchDto.setDepartmentIdUniqueCheck(Objects.isNull(srcDepartmentId) ? null : srcDepartmentId);
        searchDto.setDepartmentCodeUniqueCheck(dto.getDepartmentCode());
        departmentList = departmentDAO.searchByDto(searchDto);
        if (!departmentList.isEmpty()) {
//            return "Mã phòng ban bị trùng";
            return  Const.API_RESPONSE.DEPARTMENT_EXIT_CODE;
        }

        searchDto = new DepartmentDTO();
        searchDto.setDepartmentIdUniqueCheck(Objects.isNull(srcDepartmentId) ? null : srcDepartmentId);
        searchDto.setDepartmentNameUniqueCheck(dto.getDepartmentName());
        departmentList = departmentDAO.searchByDto(searchDto);
        if (!departmentList.isEmpty()) {
            //return "Tên phòng ban bị trùng";
            return  Const.API_RESPONSE.DEPARTMENT_EXIT_NAME;
        }

        return null;
    }

    @Override
    public  Department getDepartmentById(Long departmentId){
        return departmentDAO.getDepartmentById(departmentId);
    }
}
