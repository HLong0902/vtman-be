package com.viettel.vtman.cms.service.impl;

import com.viettel.vtman.cms.dao.QuesDefiSuggDAO;
import com.viettel.vtman.cms.dto.QuesDefiSuggDTO;
import com.viettel.vtman.cms.service.QuesDefiSuggService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuesDefiSuggServiceImpl implements QuesDefiSuggService {
    @Autowired
    private QuesDefiSuggDAO quesDefiSuggDAO;

    @Override
    public List<QuesDefiSuggDTO> getSuggestion (String questionDefinitionName){
        return quesDefiSuggDAO.getSuggestion(questionDefinitionName);
    }
}
