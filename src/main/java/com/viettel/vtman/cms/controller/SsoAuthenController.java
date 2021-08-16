package com.viettel.vtman.cms.controller;

import com.viettel.vtman.cms.dto.EmployeeDto;
import com.viettel.vtman.cms.message.CommonController;
import com.viettel.vtman.cms.message.Const;
import com.viettel.vtman.cms.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/sso")
public class SsoAuthenController extends CommonController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/test")
    public ResponseEntity<?> ssoTest() {
        return toSuccessResult(HttpStatus.OK);
    }

    @GetMapping("/user/info")
    public ResponseEntity<?> userInfo() {
        EmployeeDto dto = employeeService.getOrCreateFromJwt();

        if (dto != null) {
            if (dto.getStatus().equals(1L)) {
                return toSuccessResult(dto);
            } else {
                return toExceptionResult("Tài khoản người dùng đã bị khóa", Const.API_RESPONSE.RETURN_CODE_ERROR);
            }
        }

        return toExceptionResult("Dữ liệu tài khoản người dùng có lỗi", Const.API_RESPONSE.RETURN_CODE_ERROR);
    }

}
