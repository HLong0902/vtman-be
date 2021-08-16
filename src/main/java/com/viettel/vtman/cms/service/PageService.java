package com.viettel.vtman.cms.service;

import com.viettel.vtman.cms.dto.GetRoutesDTO;
import com.viettel.vtman.cms.dto.ObjectResultPage;
import com.viettel.vtman.cms.dto.PageDTO;
import com.viettel.vtman.cms.entity.Page;

import java.util.List;

public interface PageService {
    List<PageDTO> searchByProperties (String pageCode, String pageName, Long menuId, Long status, ObjectResultPage objectResultPage);
    Page save(Page page);
    PageDTO findByPageCode(String pageCode);
    PageDTO findByPageName(String pageName);
    Page findById(Long pageId);
    Page update(Page page);
    void deleteById(Long pageId);
    List<Page> getPageById(Long pageId);
    List<GetRoutesDTO> getRoutes();
    List<PageDTO> getPagePermissionByRoleId(long roleId);
    PageDTO getPageByComponent(String component);
    PageDTO findByPath(String path);

    List<Page> getPageByMenuId(Long menuId);
}
