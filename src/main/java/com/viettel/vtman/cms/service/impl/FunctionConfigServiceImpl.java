package com.viettel.vtman.cms.service.impl;

import com.viettel.vtman.cms.dao.FunctionConfigDAO;
import com.viettel.vtman.cms.dto.FunctionConfigDTO;
import com.viettel.vtman.cms.entity.FunctionConfig;
import com.viettel.vtman.cms.service.FunctionConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FunctionConfigServiceImpl implements FunctionConfigService {
    @Autowired
    private FunctionConfigDAO functionConfigDAO;

    @Override
    public List<FunctionConfigDTO> displayAll(){
        return functionConfigDAO.displayAll();
    }

    @Override
    public FunctionConfig save (FunctionConfig functionConfig){
        return functionConfigDAO.insert(functionConfig);
    }

    @Override
    public FunctionConfig findById(Long functionConfigId){
        return functionConfigDAO.findById(functionConfigId);
    }

    @Override
    public FunctionConfig update(FunctionConfig functionConfig){
        return functionConfigDAO.updateFunctionConfig(functionConfig);
    }

    @Override
    public List<FunctionConfig> findAll() {
        return functionConfigDAO.findAll();
    }
}
