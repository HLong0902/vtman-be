package com.viettel.vtman.cms.service;

import com.viettel.vtman.cms.dto.FunctionConfigDTO;
import com.viettel.vtman.cms.entity.FunctionConfig;

import java.util.List;

public interface FunctionConfigService {
    List<FunctionConfigDTO> displayAll();
    FunctionConfig save (FunctionConfig functionConfig);
    FunctionConfig findById(Long functionConfigId);
    FunctionConfig update(FunctionConfig functionConfig);
    List<FunctionConfig> findAll();
}
