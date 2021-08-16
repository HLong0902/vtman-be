package com.viettel.vtman.cms.controller;

import com.viettel.vtman.cms.dto.BannedContentDTO;
import com.viettel.vtman.cms.dto.EmployeeDto;
import com.viettel.vtman.cms.entity.BannedContent;
import com.viettel.vtman.cms.message.CommonController;
import com.viettel.vtman.cms.message.Const;
import com.viettel.vtman.cms.service.BannedContentService;
import com.viettel.vtman.cms.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Objects;


@RestController
@RequestMapping(value = "/api/bannedContent")
@RequiredArgsConstructor
public class BannedContentController extends CommonController {

    private static final Logger LOGGER = LogManager.getLogger(BannedContentController.class);

    @Autowired
    private BannedContentService bannedContentService;

    @Autowired
    private EmployeeService employeeService;
    private static Object count = 0;
    public static void setCount(Object count) {
        BannedContentController.count = count;
    }
    @GetMapping({"/", ""})
    public ResponseEntity<?> find(@RequestParam(name = "name", required = false) String name,
                                  @RequestParam(name = "page") int page,
                                  @RequestParam(name = "pageSize") int pageSize) {
        try {

            List<BannedContent> result = bannedContentService.find(
                    name == null ? "" :name, page, pageSize);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Access-Control-Expose-Headers", "count");
            headers.add("count", BannedContentController.count.toString());
            return new ResponseEntity<>(result, headers, HttpStatus.OK);
        } catch (Exception ex) {
            LOGGER.error("Banned Content Display Error: " + ex.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping({"/detail"})
    public ResponseEntity<?> find(@RequestParam(name = "id") Long bannedContentId) {
        try {
            BannedContent result = bannedContentService.findById(bannedContentId);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception ex) {
            LOGGER.error("Banned Content Display Detail Error: " + ex.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @DeleteMapping("/delete")
    public void delete(@RequestParam(name = "id") Long bannedContentId) {
        try {
            bannedContentService.deleteById(bannedContentId);
        } catch (Exception ex) {
            LOGGER.error("Banned Content Delete Error: " + ex.getMessage());
        }
    }


    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody BannedContentDTO bannedContentDTO) throws Exception {
        BannedContent bannedContent = new BannedContent();
        if (Objects.isNull(bannedContentDTO)){
            return toExceptionResult("DATA_NULL", Const.API_RESPONSE.RETURN_CODE_ERROR);
        }
        try{
            EmployeeDto employeeDto = employeeService.getOrCreateFromJwt();
            BannedContentDTO checkDuplicateName = bannedContentService.findByName(bannedContentDTO.getBannedContentName());
            if (checkDuplicateName!=null){
                return toExceptionResult("DUPLICATE_BANNED_CONTENT_NAME", Const.API_RESPONSE.DUPLICATE_BANNED_CONTENT_NAME);
            }
            bannedContent.setBannedContentName(bannedContentDTO.getBannedContentName());
            bannedContent.setDescription(bannedContentDTO.getDescription());
            bannedContent.setCreatedDate(new Date());
            bannedContent.setCreatedBy(employeeDto.getEmployeeId());
            String result =  bannedContentService.save(bannedContent);
            if (result.equals(Const.SUCCESS)){
                return toSuccessResult(bannedContent);
            }
            else {
                return toExceptionResult(result, Const.API_RESPONSE.RETURN_CODE_ERROR);
            }
        }catch (Exception e){
            LOGGER.error("Banned Content Create Error: " + e.getMessage());
            return toExceptionResult("SOMETHING_WAS_WRONG", Const.API_RESPONSE.RETURN_CODE_ERROR);
        }
    }
    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody BannedContentDTO bannedContentDTO) throws Exception {
        if (Objects.isNull(bannedContentDTO)){
            return toExceptionResult("DATA_NULL", Const.API_RESPONSE.RETURN_CODE_ERROR);
        }
        try{
            BannedContent data = new BannedContent();
            BannedContent bannedContent = bannedContentService.findById(bannedContentDTO.getBannedContentId());
            EmployeeDto employeeDto = employeeService.getOrCreateFromJwt();
            if (!Objects.nonNull(bannedContent)){
                return toExceptionResult("BANNED_CONTENT_ID_NOT_FOUND", Const.API_RESPONSE.BANNED_CONTENT_ID_NOT_FOUND);
            }
            else {
                BannedContentDTO checkDuplicateName = bannedContentService.findByName(bannedContentDTO.getBannedContentName());
                if (checkDuplicateName==null){
                    bannedContent.setBannedContentName(bannedContentDTO.getBannedContentName());
                    bannedContent.setUpdatedDate(new Date());
                    bannedContent.setDescription(bannedContentDTO.getDescription());
                    bannedContent.setUpdatedBy(employeeDto.getEmployeeId());
                    data = bannedContentService.updateData(bannedContent);
                    return toSuccessResult(data);
                }
                if (!checkDuplicateName.getBannedContentId().equals(bannedContentDTO.getBannedContentId())){
                    return toExceptionResult("DUPLICATE_BANNED_CONTENT_NAME", Const.API_RESPONSE.DUPLICATE_BANNED_CONTENT_NAME);
                }
                else {
                    bannedContent.setBannedContentName(bannedContentDTO.getBannedContentName());
                    bannedContent.setUpdatedDate(new Date());
                    bannedContent.setDescription(bannedContentDTO.getDescription());
                    bannedContent.setUpdatedBy(employeeDto.getEmployeeId());
                    data = bannedContentService.updateData(bannedContent);
                    return toSuccessResult(data);
                }
            }
        }catch (Exception exception){
            LOGGER.error("Banned Content Update Error: " + exception.getMessage());
            return toExceptionResult("SOMETHING_WAS_WRONG", Const.API_RESPONSE.RETURN_CODE_ERROR);
        }
    }

    @PostMapping("/checkContentBanned")
    public ResponseEntity<?> checkContentBanned(@RequestParam(name = "answer") String answer){
        LOGGER.info("checkContentBanned param: answer: " + answer);
        try {
            Boolean result = bannedContentService.checkContentBanned(answer);
            LOGGER.info("checkContentBanned result: " + result);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error("checkContentBanned error:" + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
