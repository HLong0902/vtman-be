package com.viettel.vtman.cms.service;

import com.viettel.vtman.cms.dto.QuesDefiSuggDTO;

import java.util.List;

public interface QuesDefiSuggService {
    List<QuesDefiSuggDTO> getSuggestion (String questionDefinitionName);
}
