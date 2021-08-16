package com.viettel.vtman.cms.controller;

import com.google.gson.Gson;
import com.viettel.vtman.cms.entity.Evaluate;
import com.viettel.vtman.cms.message.CommonController;
import com.viettel.vtman.cms.service.EvaluateService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController

@RequestMapping("/api/evaluate")
public class EvaluateController extends CommonController {

    private static final Logger LOGGER = LogManager.getLogger(EvaluateController.class);

    @Autowired
    private EvaluateService evaluateService;

    private Gson gson = new Gson();

    @PostMapping("/insert")
    public ResponseEntity<?> insertEvaluate(@RequestBody Evaluate evaluate) {
        LOGGER.info("insertEvaluate param: " + gson.toJson(evaluate));
        try {
            String result = evaluateService.insertEvaluate(evaluate);
            if (StringUtils.isEmpty(result)) {
                LOGGER.error("insertEvaluate: " + HttpStatus.BAD_REQUEST.toString());
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            LOGGER.info("result insertEvaluate: " + result);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error("insertEvaluate error: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
