package com.viettel.vtman.cms.controller;

import com.viettel.vtman.cms.dto.ActionDTO;
import com.viettel.vtman.cms.dto.EmployeeDto;
import com.viettel.vtman.cms.dto.GetRoutesDTO;
import com.viettel.vtman.cms.dto.ObjectResultPage;
import com.viettel.vtman.cms.dto.PageDTO;
import com.viettel.vtman.cms.entity.Menu;
import com.viettel.vtman.cms.entity.Page;
import com.viettel.vtman.cms.entity.PermissionAction;
import com.viettel.vtman.cms.message.CommonController;
import com.viettel.vtman.cms.message.Const;
import com.viettel.vtman.cms.service.ActionService;
import com.viettel.vtman.cms.service.EmployeeService;
import com.viettel.vtman.cms.service.MenuService;
import com.viettel.vtman.cms.service.PageService;
import com.viettel.vtman.cms.service.PermissionActionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@RestController
@RequestMapping(value = "/api/page")
public class PageController extends CommonController {
    private static final Logger LOGGER = LogManager.getLogger(PageController.class);
    @Autowired
    private PageService pageService;

    @Autowired
    private MenuService menuService;

    @Autowired
    private ActionService actionService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private PermissionActionService permissionActionService;
    @GetMapping(value = "/search")
    public ResponseEntity<?> searchByProperties (@RequestParam("pageCode") String pageCode,
                                                 @RequestParam("pageName") String pageName,
                                                 @RequestParam("menuId") Long menuId,
                                                 @RequestParam("status") Long status,
                                                 @RequestParam("page") Long page,
                                                 @RequestParam("pageSize") Long pageSize){
        try {
            pageCode = URLDecoder.decode(pageCode, StandardCharsets.UTF_8.name());
            pageName = URLDecoder.decode(pageName, StandardCharsets.UTF_8.name());
            ObjectResultPage objectResultPage = new ObjectResultPage();
            objectResultPage.setPage(page);
            objectResultPage.setPageSize(pageSize);

            ActionDTO dto = new ActionDTO();
            List<ActionDTO> actionDTOList = null;

            List<PageDTO> pageDTOList = pageService.searchByProperties(pageCode, pageName, menuId, status, objectResultPage);

            for (PageDTO obj : pageDTOList) {
                String result = obj.getAvailableActionId();
                if (result != null && !StringUtils.isEmpty(result)) {
                    if (result.contains(",")) {
                        String splitData[] = result.split(",");
                        if (splitData.length > 0) {
                            actionDTOList = new ArrayList<>();

                            for (int i = 0; i < splitData.length; i++) {
                                dto = actionService.findNameById(Long.parseLong(splitData[i].trim()));
                                if (dto != null) {
                                    actionDTOList.add(dto);
                                }
                            }
                            if (actionDTOList != null && actionDTOList.size() > 0) {
                                obj.setActionName(setCode(actionDTOList));
                            }
                        }
                    } else {
                        actionDTOList = new ArrayList<>();
                        dto = actionService.findNameById(Long.parseLong(result));
                        actionDTOList.add(dto);
                        obj.setActionName(setCode(actionDTOList));
                    }
                }
            }

            PageDTO data = new PageDTO();
            data.setData(pageDTOList);
            data.setTotalRecord(objectResultPage.getTotalRecord());
            data.setStart(1L);
            return toSuccessResult(pageDTOList);
        }catch (Exception e){
            LOGGER.error("PageController Search Page Error: " + e.getMessage());
            return toExceptionResult(e.getMessage(), Const.API_RESPONSE.RETURN_CODE_ERROR);
        }
    }

    public String setCode(List<ActionDTO> list) {
        String code = "";
        try {
            for (ActionDTO listResult : list) {
                if (null != listResult.getActionId()) {
                    if (!StringUtils.isEmpty(code)) {
                        code = code + "; ";
                    } else {
                        code = code + "";
                    }
                }
                if (null != listResult.getActionName()) {
                    if (!StringUtils.isEmpty(code)) {
                        code = code + listResult.getActionName();
                    } else {
                        code = code + "" + listResult.getActionName();
                    }
                }

            }
        } catch (Exception e) {
            LOGGER.error("PageController SetCode Error: " + e.getMessage());
        }
        return code;
    }
    
    @DeleteMapping(value = "/deletePage")
    public ResponseEntity<?> deletePage(@RequestParam("pageId") Long pageId){
        try {
            Page result = pageService.findById(pageId);
            PermissionAction result2 = permissionActionService.findByPageId(pageId);
            if (result !=null && Arrays.asList(Const.PAGE_COMPONENT_GROUP.SYSTEM_PAGE).contains(result.getComponent()))  {
                return toExceptionResult("Không được xóa System page", Const.API_RESPONSE.RETURN_CODE_ERROR);
            }
            if (!Objects.nonNull(result)){
                return toExceptionResult("PAGE_ID_NOT_FOUND", Const.API_RESPONSE.PAGE_ID_NOT_FOUND);
            }else {
                if (Objects.isNull(result2)){
                    pageService.deleteById(pageId);
                }
                else {
                    return toExceptionResult("PAGE_ID_IS_USED", Const.API_RESPONSE.PAGE_ID_IS_USED);
                }
            }
            return toSuccessResult(null);
        }catch (Exception exception){
            LOGGER.error("PageController Delete Page Error: " + exception.getMessage());
            return toExceptionResult("FAIL", Const.API_RESPONSE.RETURN_CODE_ERROR);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createPage(@RequestBody PageDTO pageDTO) throws Exception{
        if (Arrays.asList(Const.PAGE_COMPONENT_GROUP.SYSTEM_PAGE).contains(pageDTO.getComponent())) {
            return toExceptionResult("Không được tạo System page", Const.API_RESPONSE.RETURN_CODE_ERROR);
        }

        Page result = new Page();
        Page data = new Page();
        try {
            PageDTO checkDuplicatePageCode = new PageDTO();
            checkDuplicatePageCode = pageService.findByPageCode(pageDTO.getPageCode());
            PageDTO checkDuplicatePageName = new PageDTO();
            checkDuplicatePageName = pageService.findByPageName(pageDTO.getPageName());
            PageDTO checkDuplicatePath = new PageDTO();
            checkDuplicatePath = pageService.findByPath(pageDTO.getPath());

            if (checkDuplicatePageCode != null) {
                return toExceptionResult("DUPLICATED_PAGE_CODE: " + checkDuplicatePageCode.getPageCode(), Const.API_RESPONSE.DUPLICATE_PAGE_CODE);
            } else if (checkDuplicatePageName != null) {
                return toExceptionResult("DUPLICATED_PAGE_NAME: " + checkDuplicatePageName.getPageName(), Const.API_RESPONSE.DUPLICATE_PAGE_NAME);
            } else if (checkDuplicatePath != null) {
                return toExceptionResult("DUPLICATED_PATH: " + checkDuplicatePath.getPath(), Const.API_RESPONSE.DUPLICATE_PATH);
            } else {
                data.setPageCode(pageDTO.getPageCode());
                data.setPageName(pageDTO.getPageName());
                data.setMenuId(pageDTO.getMenuId());
                data.setNumberOrder(pageDTO.getNumberOrder());
                data.setPath(pageDTO.getPath());
                data.setStatus(pageDTO.getStatus());
                data.setAvailableActionId(pageDTO.getAvailableActionId());
                data.setComponent(pageDTO.getComponent());
                data.setCreatedDate(new Date());
                data.setCreatedBy(pageDTO.getCreatedBy());
                result = pageService.save(data);
            }
        } catch (Exception e) {
            LOGGER.error("PageController Create Page Error: " + e.getMessage());
            return toExceptionResult(e.getMessage(), Const.API_RESPONSE.RETURN_CODE_ERROR);
        }
        return toSuccessResult(result);
    }

    @PutMapping(value = "/update")
    public ResponseEntity<?> updatePage (@RequestParam ("pageId") Long pageId,
                                         @RequestBody PageDTO page) throws Exception{
        Page data = new Page();
        Page result = new Page();
        boolean flag = true;
        EmployeeDto employeeDto = employeeService.getOrCreateFromJwt();
        try {
            Date date = new Date();
            PageDTO checkDupplicateCode = new PageDTO();
            PageDTO checkDupplicateName = new PageDTO();
            PageDTO checkDuplicatePath = new PageDTO();
            checkDupplicateCode = pageService.findByPageCode(page.getPageCode());
            checkDupplicateName = pageService.findByPageName(page.getPageName());
            checkDuplicatePath = pageService.findByPath(page.getPath());
            if (checkDupplicateCode!=null){
                if (Arrays.asList(Const.PAGE_COMPONENT_GROUP.SYSTEM_PAGE).contains(checkDupplicateCode.getComponent())) {
                    if (!StringUtils.isEmpty(page.getComponent()) && !page.getComponent().equals(checkDupplicateCode.getComponent())) {
                        return toExceptionResult("Không được sửa Component của System page", Const.API_RESPONSE.RETURN_CODE_ERROR);
                    }
                }

                flag = false;
                if (!pageId.equals(checkDupplicateCode.getPageId())){
                    checkDupplicateCode.setMessageError(Const.API_RESPONSE.DUPLICATE_PAGE_CODE);
                    return toExceptionResult("DUPLICATED_PAGE_CODE: " + checkDupplicateCode.getPageCode(),Const.API_RESPONSE.DUPLICATE_PAGE_CODE);
                }  else {
                    result = pageService.findById(pageId);
                    result.setPageCode(page.getPageCode());
                    result.setPageName(page.getPageName());
                    result.setMenuId(page.getMenuId());
                    result.setNumberOrder(page.getNumberOrder());
                    result.setPath(page.getPath());
                    result.setAvailableActionId(page.getAvailableActionId());
                    result.setStatus(page.getStatus());
                    result.setComponent(page.getComponent());
                    result.setUpdatedBy(employeeDto.getEmployeeId());
                    result.setUpdatedDate(date);

//                    data = pageService.update(result);
                }
            }
            if (checkDupplicateName!=null){
                if (Arrays.asList(Const.PAGE_COMPONENT_GROUP.SYSTEM_PAGE).contains(checkDupplicateName.getComponent())) {
                    if (!StringUtils.isEmpty(page.getComponent()) && !page.getComponent().equals(checkDupplicateName.getComponent())) {
                        return toExceptionResult("Không được sửa Component của System page", Const.API_RESPONSE.RETURN_CODE_ERROR);
                    }
                }

                flag = false;
                if (!pageId.equals(checkDupplicateName.getPageId())){
                    checkDupplicateName.setMessageError(Const.API_RESPONSE.DUPLICATE_PAGE_NAME);
                    return toExceptionResult("DUPLICATED_PAGE_NAME: " + checkDupplicateName.getPageName(), Const.API_RESPONSE.DUPLICATE_PAGE_NAME);
                }
                else {
                    result = pageService.findById(pageId);
                    result.setPageCode(page.getPageCode());
                    result.setPageName(page.getPageName());
                    result.setMenuId(page.getMenuId());
                    result.setNumberOrder(page.getNumberOrder());
                    result.setPath(page.getPath());
                    result.setAvailableActionId(page.getAvailableActionId());
                    result.setStatus(page.getStatus());
                    result.setComponent(page.getComponent());
                    result.setUpdatedBy(employeeDto.getEmployeeId());
                    result.setUpdatedDate(date);

//                    data = pageService.update(result);
                }
            }
            if (checkDuplicatePath != null) {
                if (Arrays.asList(Const.PAGE_COMPONENT_GROUP.SYSTEM_PAGE).contains(checkDuplicatePath.getComponent())) {
                    if (!StringUtils.isEmpty(page.getComponent()) && !page.getComponent().equals(checkDuplicatePath.getComponent())) {
                        return toExceptionResult("Không được sửa Component của System page", Const.API_RESPONSE.RETURN_CODE_ERROR);
                    }
                }

                flag = false;
                if (!pageId.equals(checkDuplicatePath.getPageId())){
                    checkDuplicatePath.setMessageError(Const.API_RESPONSE.DUPLICATE_PATH);
                    return toExceptionResult("DUPLICATED_PATH: " + checkDuplicatePath.getPath(),Const.API_RESPONSE.DUPLICATE_PATH);
                }  else {
                    result = pageService.findById(pageId);
                    result.setPageCode(page.getPageCode());
                    result.setPageName(page.getPageName());
                    result.setMenuId(page.getMenuId());
                    result.setNumberOrder(page.getNumberOrder());
                    result.setPath(page.getPath());
                    result.setAvailableActionId(page.getAvailableActionId());
                    result.setStatus(page.getStatus());
                    result.setComponent(page.getComponent());
                    result.setUpdatedBy(employeeDto.getEmployeeId());
                    result.setUpdatedDate(date);

//                    data = pageService.update(result);
                }
            }
            if (flag) {
                result = pageService.findById(pageId);
                if (Arrays.asList(Const.PAGE_COMPONENT_GROUP.SYSTEM_PAGE).contains(result.getComponent())) {
                    if (!StringUtils.isEmpty(page.getComponent()) && !page.getComponent().equals(result.getComponent())) {
                        return toExceptionResult("Không được sửa Component của System page", Const.API_RESPONSE.RETURN_CODE_ERROR);
                    }
                }

                result.setPageCode(page.getPageCode());
                result.setPageName(page.getPageName());
                result.setMenuId(page.getMenuId());
                result.setNumberOrder(page.getNumberOrder());
                result.setPath(page.getPath());
                result.setAvailableActionId(page.getAvailableActionId());
                result.setStatus(page.getStatus());
                result.setComponent(page.getComponent());
                result.setUpdatedBy(employeeDto.getEmployeeId());
                result.setUpdatedDate(date);

//                data = pageService.update(result);
            }
        }catch (Exception exception){
            LOGGER.error("PageController Update Page Error: " + exception.getMessage());
            return toExceptionResult(exception.getMessage(), Const.API_RESPONSE.RETURN_CODE_ERROR);
        }
        data = pageService.update(result);
        return toSuccessResult(data);
    }

    @GetMapping(value = "/getById")
    public ResponseEntity<?> getPageById(@RequestParam("pageId") Long pageId){
        List<Page> pageList;
        try{
            pageList = pageService.getPageById(pageId);
        } catch (Exception exception){
            LOGGER.error("PageController Get By Id Error: " + exception.getMessage());
            return toExceptionResult(exception.getMessage(), Const.API_RESPONSE.RETURN_CODE_ERROR);
        }
        return toSuccessResult(pageList);
    }

    @GetMapping(value = "/menu")
    public ResponseEntity<?> getMenu(){
        try{
            List<Menu> menuList = menuService.findAll();
            return toSuccessResult(menuList);
        } catch (Exception e){
            LOGGER.error("PageController Get Menu Error: " + e.getMessage());
            return toExceptionResult(e.getMessage(), Const.API_RESPONSE.RETURN_CODE_ERROR);
        }
    }

    @GetMapping(value = "/getRoutes")
    public ResponseEntity<?> getRoutes(){
        try{
            List<GetRoutesDTO> getRoutesDTOList = pageService.getRoutes();
            for (GetRoutesDTO dto : getRoutesDTOList) {
                dto.setPermissions(new ArrayList<>());
                dto.getPermissions().addAll(Arrays.stream(dto.getActionId().split(",")).map(e -> Long.valueOf(e)).collect(Collectors.toList()));
            }
            return toSuccessResult(getRoutesDTOList);
        } catch (Exception e){
            LOGGER.error("PageController Get Routes Error: " + e.getMessage());
            return toExceptionResult(e.getMessage(), Const.API_RESPONSE.RETURN_CODE_ERROR);
        }
    }

}
