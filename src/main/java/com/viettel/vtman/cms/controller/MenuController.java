package com.viettel.vtman.cms.controller;


import com.viettel.vtman.cms.dto.EmployeeDto;
import com.viettel.vtman.cms.dto.MenuDTO;
import com.viettel.vtman.cms.dto.PermissionActionDTO;
import com.viettel.vtman.cms.entity.Menu;
import com.viettel.vtman.cms.entity.Page;
import com.viettel.vtman.cms.message.CommonController;
import com.viettel.vtman.cms.message.Const;
import com.viettel.vtman.cms.service.EmployeeService;
import com.viettel.vtman.cms.service.MenuService;
import com.viettel.vtman.cms.service.PageService;
import com.viettel.vtman.cms.service.PermissionActionService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@RestController
@RequestMapping(value = "/api/menu")
@RequiredArgsConstructor
public class MenuController extends CommonController {
    private static final Logger LOGGER = LogManager.getLogger(MenuController.class);

    @Autowired
    private MenuService menuService;

    @Autowired
    private PermissionActionService permissionActionService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private PageService pageService;

    private static Object count = 0;
    public static void setCount(Object count) {
        MenuController.count = count;
    }

    @GetMapping({"/", ""})
    public ResponseEntity<?> getAutoContentByType(@RequestParam(name = "keyword") String searchKeyword,
                                                  @RequestParam(name = "page") int page,
                                                  @RequestParam(name = "pageSize") int pageSize) {
        try {
            List<Menu> result = menuService.getMenu(searchKeyword, page, pageSize);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Access-Control-Expose-Headers", "count");
            headers.add("count", MenuController.count.toString());
            return new ResponseEntity<>(result, headers, HttpStatus.OK);
        } catch (Exception ex) {
            LOGGER.error("MenuController Get AutoContent By Type Error: " + ex.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/detail")
    public ResponseEntity<?> getMenuById(@RequestParam(name = "menuId") Long menuId){
        Menu result = menuService.getMenuById(menuId);
        return new ResponseEntity<>(result,HttpStatus.OK);
    }

    @GetMapping("/delete")
    public ResponseEntity<?> delete(@RequestParam(name = "menuId") Long menuId) {
        try {
            List<Page> lstPage = pageService.getPageByMenuId(menuId);
            Menu result = menuService.getMenuById(menuId);
            if(result!=null){
                if (lstPage == null) {
                    menuService.deleteById(menuId);
                } else {
                    return toSuccessResultTopicId(result);
                }
            }
            else{
                return toExceptionResult("err", Const.API_RESPONSE.TOPIC_ID_NOT_FOUND);
            }
            return toSuccessResult(null);

        } catch (Exception ex) {
            LOGGER.error("MenuController Delete Error: " + ex.getMessage());
            return toExceptionResult(ex.getMessage(), Const.API_RESPONSE.RETURN_CODE_ERROR);
        }
    }


    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody MenuDTO menuDTO) throws Exception {
        Menu menu = new Menu();

        try{
            MenuDTO checkMenuPath = new MenuDTO();
            EmployeeDto employeeDto = employeeService.getOrCreateFromJwt();
            if (Objects.isNull(menuDTO)){
                return toExceptionResult("DATA_IS_NULL", Const.API_RESPONSE.RETURN_CODE_ERROR);
            }
            List<MenuDTO> listCheckDuplicate = menuService.checkDuplicate(menuDTO);
            StringBuilder duplicateWarningMgs = new StringBuilder();
            if (listCheckDuplicate.size() > 0) {
                duplicateWarningMgs.append("Tên Menu đã tồn tại");
            }

            if (duplicateWarningMgs.length() > 0) {
                return toExceptionResult(duplicateWarningMgs.toString(), Const.API_RESPONSE.DUPLICATE_MENU_NAME);
            }

            checkMenuPath = menuService.findByMenuPath(menuDTO.getMenuPath());
            if (checkMenuPath != null) {
                return toExceptionResult("DUPLICATED_MENU_PATH: " + checkMenuPath.getMenuPath(), Const.API_RESPONSE.DUPLICATE_MENU_PATH);
            }

            menu.setMenuName(menuDTO.getMenuName());
            menu.setDescription(menuDTO.getDescription());
            menu.setMenuPath(menuDTO.getMenuPath());
            menu.setCreatedDate(new Date());
            menu.setCreatedBy(employeeDto.getEmployeeId());
            menu.setIcon(menuDTO.getIcon());
            menu.setStatus(1L);
            menu.setNumberOrder(menuDTO.getNumberOrder());
            menu.setPropPage(menu.getPropPage());
            String result = menuService.create(menu);
            if (result.equals(Const.SUCCESS)){
                return toSuccessResult(menu);
            }
            else {
                return toExceptionResult(result, Const.API_RESPONSE.RETURN_CODE_ERROR);
            }
        }catch (Exception e){
            LOGGER.error("MenuController Create Error: " + e.getMessage());
            return toExceptionResult("FAIL", Const.API_RESPONSE.RETURN_CODE_ERROR);
        }
    }

    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody MenuDTO menuDTO) throws Exception {
        try {
            MenuDTO checkMenuPath = new MenuDTO();
            Menu result = new Menu();
            Menu menu = menuService.getMenuById(menuDTO.getMenuId());
            if (!Objects.nonNull(menu)){
                return toExceptionResult("MENU_ID_NOT_FOUND", Const.API_RESPONSE.RETURN_CODE_ERROR);
            }
            EmployeeDto employeeDto = employeeService.getOrCreateFromJwt();
            List<MenuDTO> listCheckDuplicate = menuService.checkDuplicate(menuDTO);
            if (listCheckDuplicate.size()==0 || (listCheckDuplicate.size() == 1 && listCheckDuplicate.get(0).getMenuId().equals(menu.getMenuId()))){
                menu.setMenuName(menuDTO.getMenuName());
                menu.setDescription(menuDTO.getDescription());
                menu.setMenuPath(menuDTO.getMenuPath());
                menu.setUpdatedDate(new Date());
                menu.setUpdatedBy(employeeDto.getEmployeeId());
                menu.setIcon(menuDTO.getIcon());
                menu.setStatus(1L);
                menu.setNumberOrder(menuDTO.getNumberOrder());
                menu.setPropPage(menu.getPropPage());
            }
            else{
                StringBuilder duplicateWarningMgs = new StringBuilder();
                if (listCheckDuplicate.size() > 0) {
                    duplicateWarningMgs.append("Tên Menu đã tồn tại");
                }

                if (duplicateWarningMgs.length() > 0) {
                    return toExceptionResult(duplicateWarningMgs.toString(), Const.API_RESPONSE.DUPLICATE_MENU_NAME);
                }
                menu.setMenuName(menuDTO.getMenuName());
                menu.setDescription(menuDTO.getDescription());
                menu.setMenuPath(menuDTO.getMenuPath());
                menu.setCreatedDate(new Date());
                menu.setCreatedBy(employeeDto.getEmployeeId());
                menu.setIcon(menuDTO.getIcon());
                menu.setStatus(1L);
                menu.setNumberOrder(menuDTO.getNumberOrder());
                menu.setPropPage(menu.getPropPage());
            }
            checkMenuPath = menuService.findByMenuPath(menuDTO.getMenuPath());
            if (checkMenuPath != null){
                if (!menuDTO.getMenuId().equals(checkMenuPath.getMenuId())) {
                    return toExceptionResult("DUPLICATED_MENU_PATH: " + checkMenuPath.getMenuPath(), Const.API_RESPONSE.DUPLICATE_MENU_PATH);
                }
            }
            result = menuService.updateMenu(menu);
            return toSuccessResult(result);
        }catch (Exception e){
            LOGGER.error("MenuController Update Error: " + e.getMessage());
            return toExceptionResult(e.getMessage(), Const.API_RESPONSE.RETURN_CODE_ERROR);
        }
    }

    @GetMapping("/loadSideMenu")
    public ResponseEntity<?> loadSideMenu() {
        EmployeeDto employeeDto = employeeService.getOrCreateFromJwt();

        if (employeeDto == null) {
            return toExceptionResult("Dữ liệu tài khoản người dùng có lỗi", Const.API_RESPONSE.RETURN_CODE_ERROR);
        }

        PermissionActionDTO searchDTO = new PermissionActionDTO();
        searchDTO.setRoleId(employeeDto.getRoleId());

        List<PermissionActionDTO> dtoList = permissionActionService.getPermissionActionByDTO(searchDTO);

        Map<Long, Map<String, Object>> result = new LinkedHashMap<>();
        for (PermissionActionDTO dto : dtoList) {
            if (!result.containsKey(dto.getPropMenu().getMenuId())) {
                Map<String, Object> item = new HashMap<>();
                item.put("_tag", "CSidebarNavDropdown");
                item.put("name", dto.getPropMenu().getMenuName());
                item.put("route", dto.getPropMenu().getMenuPath());
                item.put("icon", dto.getPropMenu().getIcon());
                item.put("_children", new ArrayList<>());

                result.put(dto.getPropMenu().getMenuId(), item);
            }

            List<Object> lst = (List<Object>) result.get(dto.getPropMenu().getMenuId()).get("_children");
            Map<String, Object> lstItem = new HashMap<>();
            lstItem.put("_tag", "CSidebarNavItem");
            lstItem.put("name", dto.getPropPage().getPageName());
            lstItem.put("to", dto.getPropMenu().getMenuPath() + dto.getPropPage().getPath());

            lst.add(lstItem);
        }

        return new ResponseEntity<>(result.values(), HttpStatus.OK);
    }

}
