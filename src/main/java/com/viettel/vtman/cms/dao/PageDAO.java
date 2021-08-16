package com.viettel.vtman.cms.dao;

import com.viettel.vtman.cms.dto.GetRoutesDTO;
import com.viettel.vtman.cms.dto.ObjectResultPage;
import com.viettel.vtman.cms.dto.PageDTO;
import com.viettel.vtman.cms.entity.Page;

import java.util.List;
public interface PageDAO {
    List<PageDTO> searchByProperties (String pageCode, String pageName, Long menuId, Long status, ObjectResultPage objectResultPage);
    Page insert(Page page);
    PageDTO findByPageCode(String pageCode);
    PageDTO findByPageName(String pageName);
    Page findById(Long pageId);
    Page updatePage(Page page);
    void deleteById(Long pageId);
    List<Page> getPageById(Long pageId);
    List<GetRoutesDTO> getRoutes(Long roleId);
    List<Page> getPagePermissionByRoleId(long roleId);
    Page getPageByComponent(String component);
    PageDTO findByPath(String path);
    List<Page> getPageByMenuId(Long menuId);

}
