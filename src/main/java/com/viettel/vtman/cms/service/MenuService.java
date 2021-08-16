package com.viettel.vtman.cms.service;

import com.viettel.vtman.cms.dto.MenuDTO;
import com.viettel.vtman.cms.entity.Menu;

import java.util.List;

public interface MenuService {
    List<Menu> getMenu(String searchKeyword, int page, int pageSize);
    Menu getMenuById(Long menuId);
    String create(Menu menu);
    String edit(Menu menu);
    void deleteById(Long menuId);
    List<Menu> findAll();
    List<MenuDTO> checkDuplicate(MenuDTO menuDTO);
    MenuDTO findByMenuPath (String menuPath);
    Menu updateMenu(Menu menu);
}
