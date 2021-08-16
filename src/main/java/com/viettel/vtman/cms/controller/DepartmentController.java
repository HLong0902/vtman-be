package com.viettel.vtman.cms.controller;

import com.viettel.vtman.cms.dto.DepartmentDTO;
import com.viettel.vtman.cms.entity.Department;
import com.viettel.vtman.cms.message.CommonController;
import com.viettel.vtman.cms.message.Const;
import com.viettel.vtman.cms.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/department")
public class DepartmentController extends CommonController {

    @Autowired
    private DepartmentService departmentService;

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody DepartmentDTO dto) {
        try {
            Map<String, Object> result = new HashMap<>();
            result.put("count", departmentService.countByDto(dto));
            result.put("list", departmentService.searchByDto(dto));
            return toSuccessResult(result);
        } catch (Exception ex) {
            return toExceptionResult(ex.getMessage(), Const.API_RESPONSE.RETURN_CODE_ERROR);
        }
    }

    @GetMapping("/view")
    public ResponseEntity<?> view(@RequestParam(name = "departmentId") Long departmentId) {
        try {
            DepartmentDTO searchDto = new DepartmentDTO();
            searchDto.setDepartmentId(departmentId);
            List<DepartmentDTO> dtoList = departmentService.searchByDto(searchDto);
            return toSuccessResult(dtoList.size() > 0 ? dtoList.get(0) : new DepartmentDTO());
        } catch (Exception ex) {
            return toExceptionResult(ex.getMessage(), Const.API_RESPONSE.RETURN_CODE_ERROR);
        }
    }

    @GetMapping("/delete")
    public ResponseEntity<?> delete(@RequestParam(name = "departmentId") Long departmentId) {
        try {
            if (departmentService.checkSafeDelete(departmentId)) {
                Department department = departmentService.getDepartmentById(departmentId);
                if(department==null){
                    return toExceptionResult("Bản ghi không tồn tại trong hệ thống", Const.API_RESPONSE.DEPARTMENT_NOT_EXIT);
                }
                departmentService.deleteByDepartmentId(departmentId);
                return toSuccessResult("SUCCESS");
            } else {
                return toExceptionResult("Không thể xóa Phòng ban đang được sử dụng", Const.API_RESPONSE.DEPARTMENT_IS_USED);
            }
        } catch (Exception ex) {
            return toExceptionResult(ex.getMessage(), Const.API_RESPONSE.RETURN_CODE_ERROR);
        }
    }

    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody DepartmentDTO dto) {
        try {
            String result = departmentService.updateByDto(dto);
            if ("SUCCESS".equals(result)) {
                return toSuccessResult(result);
            } else {
                return toExceptionResult("ERR",result );
            }
        } catch (Exception ex) {
            return toExceptionResult(ex.getMessage(), Const.API_RESPONSE.RETURN_CODE_ERROR);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody DepartmentDTO dto) {
        try {
            String result = departmentService.createByDto(dto);
            if ("SUCCESS".equals(result)) {
                return toSuccessResult(result);
            } else {
                return toExceptionResult("ERR",result );
            }
        } catch (Exception ex) {
            return toExceptionResult(ex.getMessage(), Const.API_RESPONSE.RETURN_CODE_ERROR);
        }
    }

}
