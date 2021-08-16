package com.viettel.vtman.cms.dao;

import com.viettel.vtman.cms.dto.FunctionConfigDTO;
import com.viettel.vtman.cms.entity.FunctionConfig;

import java.util.List;
public interface FunctionConfigDAO {
    List<FunctionConfigDTO> displayAll();
    FunctionConfig insert(FunctionConfig functionConfig);
    FunctionConfig findById(Long functionConfigId);
    FunctionConfig updateFunctionConfig(FunctionConfig functionConfig);
    List<FunctionConfig> findAll();
}
