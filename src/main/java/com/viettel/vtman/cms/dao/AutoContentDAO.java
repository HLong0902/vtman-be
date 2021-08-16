package com.viettel.vtman.cms.dao;

import com.viettel.vtman.cms.dto.AutoContentDTO;
import com.viettel.vtman.cms.entity.AutoContent;
import com.viettel.vtman.cms.entity.AutoContentType;

import java.util.List;
import java.util.Map;

public interface AutoContentDAO {

    List<AutoContent> getAutoContentByType(Long autoContentType);
    List<AutoContent> getAutoContent();
    List<AutoContentType> getType();
    List<AutoContent> getAutoContentContain(Long autoContentType, String name, int from, int to);
    List<AutoContent> getAutoContentById(Long autoContentId);
    String create(AutoContent autoContent);
    String edit(AutoContent autoContent);
    void deleteById(Long autoContentId);

}
