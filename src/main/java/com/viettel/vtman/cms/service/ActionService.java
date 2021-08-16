package com.viettel.vtman.cms.service;

import com.viettel.vtman.cms.dto.ActionDTO;

public interface ActionService {
    ActionDTO findNameById(Long actionId);
}
