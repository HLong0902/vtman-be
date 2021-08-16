package com.viettel.vtman.cms.dao;

import com.viettel.vtman.cms.dto.ActionDTO;

public interface ActionDAO {
    ActionDTO findNameById(Long actionId);
}
