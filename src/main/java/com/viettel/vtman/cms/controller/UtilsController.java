package com.viettel.vtman.cms.controller;

import com.viettel.vtman.cms.dto.EmployeeDto;
import com.viettel.vtman.cms.message.CommonController;
import com.viettel.vtman.cms.message.Const;
import com.viettel.vtman.cms.service.EmployeeService;
import com.viettel.vtman.cms.utils.ApplicationConfigurationProp;
import com.viettel.vtman.cms.utils.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;


@RestController
@RequestMapping("/api/utils")
public class UtilsController extends CommonController {

    @Value("${server.port}")
    private String serverPort;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private ApplicationConfigurationProp configurationProp;

    private static final List<String> contentTypes = Arrays.asList("image/png", "image/jpeg", "image/gif");

    @PostMapping("/uploadImage")
    public ResponseEntity<?> uploadFile(@RequestParam(value = "image") MultipartFile image) {
        if(!contentTypes.contains(image.getContentType())) {
            return toExceptionResult("Chỉ cho phép upload ảnh", Const.API_RESPONSE.RETURN_CODE_ERROR);
        }

        if (image.getSize() * 0.00000095367432 >= 1.6) {
            return toExceptionResult("Chỉ cho phép dung lượng tối đa 1.6 MB", Const.API_RESPONSE.RETURN_CODE_ERROR);
        }

        EmployeeDto employeeDto = employeeService.getOrCreateFromJwt();
        String path = Common.uploadFile(image, "image/", employeeDto.getUserId().toString(), image.getName(), configurationProp);
        if (!StringUtils.isEmpty(path)) {
            if (configurationProp.getEnvironmentUploadFile().equals("LINUX")) {
                return toSuccessResult(configurationProp.getServerAddress() + ":" + serverPort + configurationProp.getPathDownload() + path.replace(configurationProp.getPathUploadFileExtention(), ""));
            } else {
                return toSuccessResult(configurationProp.getServerAddress() + ":" + serverPort + configurationProp.getPathDownload() + "/" + path.replace(System.getProperty("user.dir") + configurationProp.getPathUpload(), ""));
            }
        }

        return toExceptionResult("Có lỗi khi upload file", Const.API_RESPONSE.RETURN_CODE_ERROR);
    }

}
