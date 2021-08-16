package com.viettel.vtman.cms.controller;

import com.viettel.vtman.cms.dto.EmployeeDto;
import com.viettel.vtman.cms.dto.RoleDTO;
import com.viettel.vtman.cms.entity.Role;
import com.viettel.vtman.cms.infrastructure.enumeration.RoleGroup;
import com.viettel.vtman.cms.message.CommonController;
import com.viettel.vtman.cms.message.Const;
import com.viettel.vtman.cms.service.EmployeeService;
import com.viettel.vtman.cms.service.PermissionActionService;
import com.viettel.vtman.cms.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/role")
public class RoleController extends CommonController {

    public static Long count = 0L;
    @Autowired
    private RoleService roleService;

    @Autowired
    private PermissionActionService permissionActionService;

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/listAll")
    public ResponseEntity<?> listAllRole() {
        try {
            return toSuccessResult(roleService.findAll());
        } catch (Exception ex) {
            return toExceptionResult(ex.getMessage(), Const.API_RESPONSE.RETURN_CODE_ERROR);
        }
    }

    @GetMapping({"/", ""})
    public ResponseEntity<?> search(@RequestParam(name = "keyword") String key,
                                  @RequestParam(name = "page") int page,
                                  @RequestParam(name = "pageSize") int pageSize) {
        try {
            key = URLDecoder.decode(key, StandardCharsets.UTF_8.name());
            List<Role> res = roleService.find(key, page, pageSize);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Access-Control-Expose-Headers", "count");
            headers.add("count", String.valueOf(roleService.count(key, page, pageSize)));
            return new ResponseEntity<>(res, headers, HttpStatus.OK);
        } catch (Exception ex) {
            return toExceptionResult(ex.getMessage(), Const.API_RESPONSE.RETURN_CODE_ERROR);
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@RequestParam(name = "roleId") Long roleId) {
        if (roleService.getById(roleId).isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        else if (employeeService.findByRoleId(roleId)!=null){
            return toExceptionResult("ROLE_ID_IS_USED", Const.API_RESPONSE.ROLE_ID_IS_USED);
        }
        else{
            try {
                roleService.deleteById(roleId);
                return new ResponseEntity<>(HttpStatus.OK);
            } catch (Exception ex) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    @GetMapping("/detail")
    public ResponseEntity<?> getById(@RequestParam(name = "roleId") Long roleId) {
        try {
            return new ResponseEntity<>(roleService.getById(roleId), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody RoleDTO roleDTO) throws Exception {
        Role role = new Role();
        try {
            EmployeeDto employeeDto = employeeService.getOrCreateFromJwt();
            role.setRoleName(roleDTO.getRoleName());
            role.setDescription(roleDTO.getDescription());
            role.setStatus(roleDTO.getStatus());
            role.setRoleGroup(roleDTO.getRoleGroup());
            role.setCreatedDate(new Date());
            role.setCreatedBy(employeeDto.getEmployeeId());
            String result = roleService.save(role);
            if (result.equals(Const.SUCCESS)){
                return toSuccessResult(role);
            }
            else {
                return toExceptionResult(result, Const.API_RESPONSE.RETURN_CODE_ERROR);
            }
        } catch (Exception ex) {
            return toExceptionResult("FAIL", Const.API_RESPONSE.RETURN_CODE_ERROR);
        }
    }

    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody RoleDTO roleDTO) throws Exception {
        Role role = roleService.findById(roleDTO.getRoleId());
        try {
            EmployeeDto employeeDto = employeeService.getOrCreateFromJwt();
            if (!Objects.nonNull(role)){
                return toExceptionResult("ROLE_ID_NOT_FOUND", Const.API_RESPONSE.ROLE_ID_NOT_FOUND);
            }
            else {
                role.setRoleName(roleDTO.getRoleName());
                role.setDescription(roleDTO.getDescription());
                role.setStatus(roleDTO.getStatus());
                role.setRoleGroup(roleDTO.getRoleGroup());
                role.setUpdatedBy(employeeDto.getEmployeeId());
                role.setUpdatedDate(new Date());
                String result = roleService.update(role);
                if (result.equals(Const.SUCCESS)) {
                    return toSuccessResult(role);
                }
                else {
                    return toExceptionResult(result, Const.API_RESPONSE.RETURN_CODE_ERROR);
                }
            }
        } catch (Exception ex) {
            return toExceptionResult("FAIL", Const.API_RESPONSE.RETURN_CODE_ERROR);
        }
    }

    @GetMapping("/listRoleGroup")
    public ResponseEntity<?> listRoleGroup() {
        List<Map<String, String>> result = new ArrayList<>();
        Map<String, String> defaultRoleGroup = new HashMap<>();
        defaultRoleGroup.put("label", "Mặc định");
        defaultRoleGroup.put("value", null);
        result.add(defaultRoleGroup);

        result.addAll(Arrays.stream(RoleGroup.values()).map(e -> {
            Map<String, String> roleGroup = new HashMap<>();
            roleGroup.put("label", e.label);
            roleGroup.put("value", e.name());
            return roleGroup;
        }).collect(Collectors.toList()));

        return toSuccessResult(result);
    }

}
