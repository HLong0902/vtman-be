package com.viettel.vtman.cms.controller;

import com.viettel.vtman.cms.dto.PageDTO;
import com.viettel.vtman.cms.dto.PermissionActionDTO;
import com.viettel.vtman.cms.entity.Page;
import com.viettel.vtman.cms.entity.Role;
import com.viettel.vtman.cms.message.CommonController;
import com.viettel.vtman.cms.message.Const;
import com.viettel.vtman.cms.service.PageService;
import com.viettel.vtman.cms.service.PermissionActionService;
import com.viettel.vtman.cms.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@RestController
@RequestMapping("/api/permissionAction")
public class PermissionActionController extends CommonController {

    @Autowired
    private PermissionActionService permissionActionService;

    @Autowired
    private PageService pageService;

    @Autowired
    private RoleService roleService;

    @GetMapping("/byRoleId")
    public ResponseEntity<?> byRoleId(@RequestParam(name = "roleId") long roleId) {
        try {
            List<PageDTO> lstPage = pageService.getPagePermissionByRoleId(roleId);
            Map<Long, Map<String, Object>> result = new LinkedHashMap<>();
            for (PageDTO dto : lstPage) {
                if (!result.containsKey(dto.getPropMenu().getMenuId())) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("menuId", dto.getPropMenu().getMenuId());
                    item.put("menuName", dto.getPropMenu().getMenuName());
                    item.put("permission", new ArrayList<>());

                    result.put(dto.getPropMenu().getMenuId(), item);
                }

                List<Object> lst = (List<Object>) result.get(dto.getPropMenu().getMenuId()).get("permission");
                Map<String, Object> lstItem = new HashMap<>();
                lstItem.put("pageId", dto.getPageId());
                lstItem.put("pageName", dto.getPageName());
                lstItem.put("actions", dto.getPropPermissionAction() == null ? "" : dto.getPropPermissionAction().getActionId());
                lstItem.put("availableActionId", Arrays.asList(Const.PAGE_COMPONENT_GROUP.SYSTEM_PAGE).contains(dto.getComponent()) ? "" : dto.getAvailableActionId());

                lst.add(lstItem);
            }

            return toSuccessResult(result.values());
        } catch (Exception ex) {
            return toExceptionResult(ex.getMessage(), Const.API_RESPONSE.RETURN_CODE_ERROR);
        }
    }

    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody PermissionActionDTO permissionActionDTO) {
        try {
            Page page = pageService.findById(permissionActionDTO.getPageId());
            Role role = roleService.findById(permissionActionDTO.getRoleId());
            if (Arrays.asList(Const.PAGE_COMPONENT_GROUP.SYSTEM_PAGE).contains(page.getComponent())
                    && Arrays.asList(Const.ROLE_GROUP.SYSTEM_ROLE).contains(role.getRoleName())) {
                return toExceptionResult("Không thể sửa quyền của ADMIN với các page cấu hình", Const.API_RESPONSE.RETURN_CODE_ERROR);
            }

            if (StringUtils.isEmpty(permissionActionDTO.getActionId())) {
                if (permissionActionService.deleteByDTO(permissionActionDTO).equals("SUCCESS")) {
                    return toSuccessResult("UPDATE SUCCESS");
                }
            } else if (permissionActionService.updateOrSave(permissionActionDTO) != null) {
                return toSuccessResult("UPDATE SUCCESS");
            }
            return toExceptionResult("UPDATE FAIL", Const.API_RESPONSE.RETURN_CODE_ERROR);
        } catch (Exception ex) {
            return toExceptionResult(ex.getMessage(), Const.API_RESPONSE.RETURN_CODE_ERROR);
        }
    }

}
