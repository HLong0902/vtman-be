package com.viettel.vtman.cms.dao;

import com.viettel.vtman.cms.dto.BannedContentDTO;
import com.viettel.vtman.cms.entity.BannedContent;

import java.util.List;

public interface BannedContentDAO {
    List<BannedContent> find(String name, int page, int pageSize);
    String create(BannedContent autoContent);
    String edit(BannedContent autoContent);
    void deleteById(Long autoContentId);
    BannedContent findById(Long bannedContentId);
    List<BannedContent> getBannedContent();
    List<BannedContent> findBannedContent(String inputText);
    BannedContentDTO findByName(String bannedContentName);
    BannedContent updateData(BannedContent bannedContent);
}
