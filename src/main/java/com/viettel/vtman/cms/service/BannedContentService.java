package com.viettel.vtman.cms.service;

import com.viettel.vtman.cms.dto.BannedContentDTO;
import com.viettel.vtman.cms.entity.BannedContent;

import java.util.List;

public interface BannedContentService {
    List<BannedContent> find(String name, int page, int pageSize) throws Exception;
    String save(BannedContent bannedContent) throws Exception;
    String update(BannedContent bannedContent) throws Exception;
    BannedContent findById(Long bannedContentId) throws Exception;
    void deleteById(Long bannedContentId);
    Boolean checkContentBanned(String answer);
    BannedContentDTO findByName(String bannedContentName);
    BannedContent updateData(BannedContent bannedContent);
}
