package com.viettel.vtman.cms.service.impl;

import com.viettel.vtman.cms.dao.PageDAO;
import com.viettel.vtman.cms.dto.EmployeeDto;
import com.viettel.vtman.cms.dto.GetRoutesDTO;
import com.viettel.vtman.cms.dto.ObjectResultPage;
import com.viettel.vtman.cms.dto.PageDTO;
import com.viettel.vtman.cms.entity.Page;
import com.viettel.vtman.cms.service.EmployeeService;
import com.viettel.vtman.cms.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class PageServiceImpl implements PageService {
    @Autowired
    private PageDAO pageDAO;

    @Autowired
    private EmployeeService employeeService;

    @Override
    public List<PageDTO> searchByProperties (String pageCode, String pageName, Long menuId, Long status, ObjectResultPage objectResultPage){
        return pageDAO.searchByProperties(pageCode, pageName, menuId, status, objectResultPage);
    }

    @Override
    public Page save(Page page) {
        return pageDAO.insert(page);
    }

    @Override
    public PageDTO findByPageCode(String pageCode) {
        PageDTO result = pageDAO.findByPageCode(pageCode);
        if (Objects.isNull(result)) {
            return null;
        }
        return pageDAO.findByPageCode(pageCode);
    }

    @Override
    public PageDTO findByPageName(String pageName) {
        PageDTO result = pageDAO.findByPageName(pageName);
        if (Objects.isNull(result)){
            return null;
        }
        return pageDAO.findByPageName(pageName);
    }

    public Page findById(Long pageId){
        return pageDAO.findById(pageId);
    }

    @Override
    public Page update(Page page) {
        return pageDAO.updatePage(page);
    }

    @Override
    public void deleteById(Long pageId){
        pageDAO.deleteById(pageId);
    }

    @Override
    public List<Page> getPageById(Long pageId){
        return pageDAO.getPageById(pageId);
    }

    @Override
    public List<GetRoutesDTO> getRoutes() {
        EmployeeDto employeeDto = employeeService.getOrCreateFromJwt();

        return pageDAO.getRoutes(employeeDto.getRoleId());
    }

    @Override
    public List<PageDTO> getPagePermissionByRoleId(long roleId) {
        return pageDAO.getPagePermissionByRoleId(roleId).stream().map(e -> new PageDTO(e)).collect(Collectors.toList());
    }

    @Override
    public PageDTO getPageByComponent(String component) {
        return new PageDTO(pageDAO.getPageByComponent(component));
    }

    @Override
    public PageDTO findByPath(String path) {
        PageDTO result = pageDAO.findByPath(path);
        if (Objects.isNull(result)) {
            return null;
        }
        return result;
    }

    @Override
    public  List<Page> getPageByMenuId(Long menuId){
        return pageDAO.getPageByMenuId(menuId);
    }
}
