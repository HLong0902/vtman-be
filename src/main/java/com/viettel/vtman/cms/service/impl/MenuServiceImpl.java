package com.viettel.vtman.cms.service.impl;

import com.viettel.vtman.cms.dao.MenuDAO;
import com.viettel.vtman.cms.dto.MenuDTO;
import com.viettel.vtman.cms.entity.Menu;
import com.viettel.vtman.cms.service.EmployeeService;
import com.viettel.vtman.cms.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class MenuServiceImpl implements MenuService {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private MenuDAO menuDAO;

    @Override
    public List<Menu> getMenu(String searchKeyword, int page, int pageSize) {
        return menuDAO.getMenu(searchKeyword, page, pageSize);
    }

    @Override
    public Menu getMenuById(Long menuId) {
        return menuDAO.getMenuById(menuId);
    }

    @Override
    public String create(Menu menu) {
        menu.setCreatedDate(new Date());
        menu.setCreatedBy(employeeService.getOrCreateFromJwt().getEmployeeId());
        menu.setStatus(1L);
        return menuDAO.create(menu);
    }

    @Override
    public String edit(Menu menu) {
        Menu menu1 = getMenuById(menu.getMenuId());

        menu1.setMenuName(menu.getMenuName());
        menu1.setDescription(menu.getDescription());
        menu1.setIcon(menu.getIcon());
        menu1.setNumberOrder(menu.getNumberOrder());
        menu1.setMenuPath(menu.getMenuPath());
        menu1.setUpdatedBy(employeeService.getOrCreateFromJwt().getEmployeeId());

        menu1.setUpdatedDate(new Date());

        return menuDAO.edit(menu1);
    }

    @Override
    public void deleteById(Long menuId) {
        menuDAO.deleteById(menuId);
    }

    @Override
    public List<Menu> findAll(){
        return menuDAO.findAll();
    }

    @Override
    public List<MenuDTO> checkDuplicate(MenuDTO menuDTO) {
        return menuDAO.checkDuplicate(menuDTO);
    }
    @Override
    public MenuDTO findByMenuPath (String menuPath) {
        MenuDTO result = menuDAO.findByMenuPath(menuPath);
        if (Objects.isNull(result)) {
            return null;
        }
        return result;
    }

    @Override
    public Menu updateMenu(Menu menu) {
        return menuDAO.updateMenu(menu);
    }

}
