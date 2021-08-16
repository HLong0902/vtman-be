package com.viettel.vtman.cms.service.impl;

import com.viettel.vtman.cms.dao.ActionDAO;
import com.viettel.vtman.cms.dto.ActionDTO;
import com.viettel.vtman.cms.service.ActionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ActionServiceImpl implements ActionService {
    @Autowired
    private ActionDAO actionDAO;

    @Override
    public ActionDTO findNameById(Long actionId) {
        return actionDAO.findNameById(actionId);
    }
}
