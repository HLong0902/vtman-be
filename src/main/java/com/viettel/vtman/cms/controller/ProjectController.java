package com.viettel.vtman.cms.controller;


import com.viettel.vtman.cms.infrastructure.base.response.Response;
import com.viettel.vtman.cms.entity.Project;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;



@RestController
@RequestMapping("api/projects")
public class ProjectController {
    private static Logger LOGGER = LogManager.getLogger(ProjectController.class);

    @GetMapping("/")
    public Response<List<Project>> Filter() throws Exception {
        Response<List<Project>> result = new Response<List<Project>>();
        LOGGER.warn("test WARNING");
        return  result;
    }
}
