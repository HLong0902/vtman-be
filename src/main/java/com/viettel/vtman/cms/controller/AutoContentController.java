package com.viettel.vtman.cms.controller;

import com.google.gson.Gson;
import com.viettel.vtman.cms.dto.AutoContentDTO;
import com.viettel.vtman.cms.entity.AutoContent;
import com.viettel.vtman.cms.message.CommonController;
import com.viettel.vtman.cms.service.AutoContentService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
import java.util.List;


@RestController
@RequestMapping(value = "/api/autoContent")
@RequiredArgsConstructor
public class AutoContentController extends CommonController {

    private static final Logger LOGGER = LogManager.getLogger(AutoContentController.class);

    @Autowired
    private AutoContentService autoContentService;
    private static Object count = 0;
    public static void setCount(Object count) {
        AutoContentController.count = count;
    }

    @GetMapping("/find")
    public ResponseEntity<?> getAutoContentByType(@RequestParam(name = "autoContentType", required = false) Long autoContentType,
                                                  @RequestParam(name = "name") String name,
                                                  @RequestParam(name = "page") int page,
                                                  @RequestParam(name = "pageSize") int pageSize) {
        try {
            List<AutoContent> result = autoContentService.getAutoContentContain(autoContentType, name, page, pageSize);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Access-Control-Expose-Headers", "count");
            headers.add("count", AutoContentController.count.toString());
            return new ResponseEntity<>(result, headers, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/detail")
    public ResponseEntity<?> getAutoContentById(@RequestParam(name = "autoContentId") Long id) {
        try {
            AutoContentDTO result = autoContentService.getAutoContentById(id);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete")
    public void delete(@RequestParam(name = "autoContentId") Long autoContentId) {
        try {
            autoContentService.deleteById(autoContentId);
        } catch (Exception ex) { }
    }


    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody AutoContentDTO autoContentDTO) throws Exception {
        try {
            autoContentService.save(autoContentDTO);
            return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.OK);
        }
    }

    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody AutoContentDTO autoContentDTO) throws Exception {
        try {
            autoContentService.update(autoContentDTO);
        } catch (Exception ex) { }
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @GetMapping("/type")
    public ResponseEntity<?> getType() throws Exception {
        try {
            return new ResponseEntity<>(autoContentService.getType(), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/findByType")
    public ResponseEntity<?> getAutoContentByType(@RequestParam(name = "autoContentType") Long autoContentType) {
        LOGGER.info("findByType param: autoContentType: " + autoContentType);
        try {
            AutoContentDTO result = autoContentService.getAutoContentByType(autoContentType);
            LOGGER.info("findByType result: " + new Gson().toJson(result));
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception ex) {
            LOGGER.error("findByType error: " + ex.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/export")
    public ResponseEntity exportToExcel(@RequestParam(name = "name") String name,
                              @RequestParam(name = "autoContentType") Long autoContentType) throws Exception {
        ExcelExporter excelExporter = new ExcelExporter(autoContentService.getAutoContentContain(autoContentType, name, 1, Integer.MAX_VALUE));

        byte[] res = excelExporter.export();
        return toSuccessResult(Base64.getEncoder().encodeToString(res));

    }
}
