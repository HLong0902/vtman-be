package com.viettel.vtman.cms.dao;

import com.viettel.vtman.cms.dto.QuesDefiSuggDTO;

import java.util.List;

public interface QuesDefiSuggDAO {
    List<QuesDefiSuggDTO> getSuggestion(String questionDefinitionName);
}
