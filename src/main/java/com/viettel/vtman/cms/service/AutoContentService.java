package com.viettel.vtman.cms.service;

import com.viettel.vtman.cms.dto.AutoContentDTO;
import com.viettel.vtman.cms.entity.AutoContent;
import com.viettel.vtman.cms.entity.AutoContentType;

import java.util.List;

public interface AutoContentService {
    String save(AutoContentDTO autoContentDTO) throws Exception;
    String update(AutoContentDTO autoContentDTO) throws Exception;
    List<AutoContent> getAutoContentContain(Long autoContentType, String name, int page, int pageSize) throws Exception;
    AutoContentDTO getAutoContentById(Long autoContentId);
    List<AutoContentType> getType();
    void deleteById(Long autoContentId);
    AutoContentDTO getAutoContentByType(Long autoContentType);
    List<AutoContent> getAll();
}
