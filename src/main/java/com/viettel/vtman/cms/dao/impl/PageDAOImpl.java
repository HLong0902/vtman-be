package com.viettel.vtman.cms.dao.impl;

import com.viettel.vtman.cms.base.dao.BaseFWDAOImpl;
import com.viettel.vtman.cms.dao.PageDAO;
import com.viettel.vtman.cms.dto.GetRoutesDTO;
import com.viettel.vtman.cms.dto.ObjectResultPage;
import com.viettel.vtman.cms.dto.PageDTO;
import com.viettel.vtman.cms.entity.Page;
import com.viettel.vtman.cms.message.Const;
import com.viettel.vtman.cms.utils.Common;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DateType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.*;

import static com.viettel.vtman.cms.message.Const.PAGE_COMPONENT_GROUP.SYSTEM_PAGE;
import static com.viettel.vtman.cms.message.Const.ROLE_GROUP.SYSTEM_ROLE;

@Repository
@Transactional
public class PageDAOImpl extends BaseFWDAOImpl<Page> implements PageDAO {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<PageDTO> searchByProperties (String pageCode, String pageName, Long menuId, Long status, ObjectResultPage objectResultPage){
        StringBuilder sqlCount = new StringBuilder();
        StringBuilder sqlSelect = new StringBuilder();
        Map<String, Object> param = new HashMap<>();
        sqlSelect.append("SELECT " +
                "p.PAGE_ID as pageId, " +
                "p.PAGE_CODE as pageCode, " +
                "p.PAGE_NAME as pageName, " +
                "p.MENU_ID as menuId, " +
                "m.MENU_NAME as menuName, " +
                "p.PATH as path, " +
                "p.AVAILABLE_ACTION_ID as availableActionId, " +
                "p.NUMBER_ORDER as numberOrder, " +
                "p.STATUS as status, " +
                "p.CREATED_DATE as createdDate, " +
                "p.UPDATED_DATE as updatedDate, " +
                "p.UPDATED_BY as updatedBy, " +
                "p.CREATED_BY as createdBy, " +
                "p.COMPONENT as component, " +
                "CASE WHEN p.COMPONENT IN :pageGroup THEN 1 ELSE 0 END AS isSystemPage ");

        sqlCount.append("select count(*) ");

        String condition = conditionQuery(param, pageCode, pageName, menuId, status);
        sqlSelect.append(condition);
        sqlCount.append(condition);
        sqlSelect.append(" order by p.NUMBER_ORDER is null, p.NUMBER_ORDER, p.PAGE_NAME ");
        SQLQuery querySelect = (SQLQuery) entityManager.createNativeQuery(sqlSelect.toString());
        SQLQuery queryCount = (SQLQuery) entityManager.createNativeQuery(sqlCount.toString());

        for (Map.Entry<String, Object> params : param.entrySet()){
            querySelect.setParameter(params.getKey(), params.getValue());
            queryCount.setParameter(params.getKey(), params.getValue());
        }
        querySelect.setParameter("pageGroup", Arrays.asList(SYSTEM_PAGE));

        List<Object[]> listResult = querySelect.getResultList();
        querySelect.addScalar("pageId", LongType.INSTANCE);
        querySelect.addScalar("pageCode", StringType.INSTANCE);
        querySelect.addScalar("pageName", StringType.INSTANCE);
        querySelect.addScalar("menuId", LongType.INSTANCE);
        querySelect.addScalar("menuName", StringType.INSTANCE);
        querySelect.addScalar("path", StringType.INSTANCE);
        querySelect.addScalar("availableActionId", StringType.INSTANCE);
        querySelect.addScalar("numberOrder", LongType.INSTANCE);
        querySelect.addScalar("status", LongType.INSTANCE);
        querySelect.addScalar("createdBy", LongType.INSTANCE);
        querySelect.addScalar("createdDate", DateType.INSTANCE);
        querySelect.addScalar("updatedDate", DateType.INSTANCE);
        querySelect.addScalar("updatedBy", LongType.INSTANCE);
        querySelect.addScalar("component", StringType.INSTANCE);
        querySelect.addScalar("isSystemPage", LongType.INSTANCE);

        querySelect.setResultTransformer(Transformers.aliasToBean(PageDTO.class));
        querySelect.setFirstResult((objectResultPage.getPage().intValue()-1) * objectResultPage.getPageSize().intValue());
        querySelect.setMaxResults(objectResultPage.getPageSize().intValue());

        List<PageDTO> result = querySelect.list();
        for (PageDTO pageDTO : result){
            pageDTO.setTotalRecord(Long.parseLong(queryCount.getResultList().get(0).toString()));
            pageDTO.setPage(objectResultPage.getPage());
            pageDTO.setPageSize(objectResultPage.getPageSize());
        }

        return result;
    }

    private String conditionQuery(Map<String, Object> param, String pageCode, String pageName, Long menuId, Long status){
        StringBuilder sql = new StringBuilder();
        sql.append("FROM PAGE p " +
                "join MENU m on p.MENU_ID = m.MENU_ID " +
                "where 1=1 ");
        if (pageCode!=null && !StringUtils.isEmpty(pageCode)){
            sql.append(" and upper(p.PAGE_CODE) like :pageCode ");
            param.put("pageCode", "%" + Common.escapeStringForMySQL(pageCode.trim().toUpperCase()) + "%");
        }
        if (pageName!=null && !StringUtils.isEmpty(pageName)){
            sql.append(" and upper(p.PAGE_NAME) like :pageName ");
            param.put("pageName", "%" + Common.escapeStringForMySQL(pageName.trim().toUpperCase()) + "%");
        }
        if (menuId != null ){
            sql.append(" and m.MENU_ID =:menuId ");
            param.put("menuId", menuId);
        }
        if (status != null ){
            sql.append(" and p.STATUS =:status ");
            param.put("status", status);
        }
        return sql.toString();
    }


    @Override
    public Page insert(Page page) {
        this.save(page);
        return page;
    }

    public Page findById(Long pageId){
        return this.get(Page.class, pageId);
    }

    @Override
    public Page updatePage(Page page){
        this.update(page);
        return page;
    }

    @Override
    public PageDTO findByPageCode(String pageCode) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT p.PAGE_CODE as pageCode, ");
        sql.append("p.PAGE_NAME as pageName, ");
        sql.append("p.PAGE_ID as pageId ");
        sql.append("FROM PAGE p ");
        sql.append("WHERE BINARY UPPER(p.PAGE_CODE) = UPPER(:pageCode) ");
        SQLQuery q = (SQLQuery) entityManager.createNativeQuery(sql.toString());
        q.setParameter("pageCode", Common.escapeStringForMySQL(pageCode));
        q.addScalar("pageCode", StringType.INSTANCE);
        q.addScalar("pageName", StringType.INSTANCE);
        q.addScalar("pageId", LongType.INSTANCE);
        q.setResultTransformer(Transformers.aliasToBean(PageDTO.class));
        List<PageDTO> list = q.getResultList();
        if (list != null && list.size() > 0)
            return list.get(0);
        return null;
    }

    @Override
    public PageDTO findByPageName(String pageName) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT p.PAGE_CODE as pageCode, ");
        sql.append("p.PAGE_NAME as pageName, ");
        sql.append("p.PAGE_ID as pageId ");
        sql.append("FROM PAGE p ");
        sql.append("WHERE BINARY UPPER(p.PAGE_NAME) = UPPER(:pageName) ");
        SQLQuery q = (SQLQuery) entityManager.createNativeQuery(sql.toString());
        q.setParameter("pageName", Common.escapeStringForMySQL(pageName));
        q.addScalar("pageCode", StringType.INSTANCE);
        q.addScalar("pageName", StringType.INSTANCE);
        q.addScalar("pageId", LongType.INSTANCE);
        q.setResultTransformer(Transformers.aliasToBean(PageDTO.class));
        List<PageDTO> list = q.getResultList();
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    public void deleteById(Long pageId){
        this.deleteById(pageId, Page.class, "pageId");
    }

    @Override
    public List<Page> getPageById(Long pageId){
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT p.PAGE_CODE as pageCode, ");
        sql.append("p.PAGE_NAME as pageName, ");
        sql.append("p.PAGE_ID as pageId, ");
        sql.append("p.MENU_ID as menuId, ");
        sql.append("p.NUMBER_ORDER as numberOrder, ");
        sql.append("p.PATH as path, ");
        sql.append("p.AVAILABLE_ACTION_ID as availableActionId, ");
        sql.append("p.STATUS as status, ");
        sql.append("p.CREATED_DATE as createdDate, ");
        sql.append("p.CREATED_BY as createdBy, ");
        sql.append("p.UPDATED_DATE as updatedDate, ");
        sql.append("p.UPDATED_BY as updatedBy, ");
        sql.append("p.COMPONENT as component, ");
        sql.append("CASE WHEN p.COMPONENT IN :pageGroup THEN 1 ELSE 0 END AS isSystemPage ");
        sql.append("FROM PAGE p ");
        sql.append("WHERE p.PAGE_ID = :pageId ");
        SQLQuery q = (SQLQuery) entityManager.createNativeQuery(sql.toString());
        q.setParameter("pageId", pageId);
        q.setParameter("pageGroup", Arrays.asList(SYSTEM_PAGE));
        q.addScalar("pageCode", StringType.INSTANCE);
        q.addScalar("pageName", StringType.INSTANCE);
        q.addScalar("pageId", LongType.INSTANCE);
        q.addScalar("menuId", LongType.INSTANCE);
        q.addScalar("numberOrder", LongType.INSTANCE);
        q.addScalar("path", StringType.INSTANCE);
        q.addScalar("availableActionId", StringType.INSTANCE);
        q.addScalar("status", LongType.INSTANCE);
        q.addScalar("createdDate", DateType.INSTANCE);
        q.addScalar("createdBy", LongType.INSTANCE);
        q.addScalar("updatedDate", DateType.INSTANCE);
        q.addScalar("updatedBy", LongType.INSTANCE);
        q.addScalar("component", StringType.INSTANCE);
        q.addScalar("isSystemPage", LongType.INSTANCE);
        q.setResultTransformer(Transformers.aliasToBean(Page.class));
        List<Page> pageList = q.getResultList();
        if (pageList != null && pageList.size() > 0)
            return pageList;
        return null;
    }

    @Override
    public List<GetRoutesDTO> getRoutes(Long roleId) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT p.PAGE_NAME as pageName,");
        sql.append("p.COMPONENT as component,");
        sql.append("CONCAT(m.MENU_PATH, p.PATH) as path, ");
        sql.append("a.ACTION_ID as actionId ");
        sql.append("FROM PERMISSION_ACTION a ");
        sql.append("JOIN PAGE p ON a.PAGE_ID = p.PAGE_ID ");
        sql.append("JOIN MENU m ON p.MENU_ID = m.MENU_ID ");
        sql.append("where a.ROLE_ID = :roleId AND m.MENU_PATH is not null and p.PATH is not null ");
        sql.append(" AND a.ACTION_ID like '%"+ Const.ROLE_PERMISSION.READ +"%' "); // có quyền READ
        SQLQuery q = (SQLQuery) entityManager.createNativeQuery(sql.toString());
        q.setParameter("roleId", roleId);
        q.addScalar("pageName", StringType.INSTANCE);
        q.addScalar("component", StringType.INSTANCE);
        q.addScalar("path", StringType.INSTANCE);
        q.addScalar("actionId", StringType.INSTANCE);
        q.setResultTransformer(Transformers.aliasToBean(GetRoutesDTO.class));
        List<GetRoutesDTO> getRoutesDTOList = q.getResultList();
        if (getRoutesDTOList !=null && getRoutesDTOList.size()>0){
            return getRoutesDTOList;
        }
        return new ArrayList<>();
    }

    @Override
    public List<Page> getPagePermissionByRoleId(long roleId) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT NEW Page(a, b ,c) ");
        sql.append(" FROM Page a ");
        sql.append(" JOIN Menu b ");
        sql.append(" ON a.menuId = b.menuId AND b.status = 1 ");
        sql.append(" LEFT JOIN PermissionAction c ");
        sql.append(" ON a.pageId = c.pageId AND c.status = 1 AND c.roleId = :roleId ");
        sql.append(" LEFT JOIN Role d ");
        sql.append(" ON c.roleId = d.roleId AND d.status = 1 ");
        sql.append(" WHERE a.status = 1 ");
        sql.append(" AND (d.roleName IN :systemRoleGroup OR a.component NOT IN :systemPageGroup OR a.component IS NULL) ");
        sql.append(" ORDER BY b.numberOrder ASC NULLS LAST, b.menuName ASC NULLS LAST, a.numberOrder ASC NULLS LAST, a.pageName ASC NULLS LAST ");

        Query query = entityManager.createQuery(sql.toString());
        query.setParameter("roleId", roleId);
        query.setParameter("systemRoleGroup", Arrays.asList(SYSTEM_ROLE));
        query.setParameter("systemPageGroup", Arrays.asList(SYSTEM_PAGE));

        return (List<Page>) query.getResultList();
    }

    @Override
    public Page getPageByComponent(String component) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT NEW Page(a, b) ");
        sql.append(" FROM Page a ");
        sql.append(" INNER JOIN Menu b ");
        sql.append(" ON a.menuId = b.menuId AND b.status = 1 ");
        sql.append(" WHERE a.status = 1 ");
        sql.append(" AND a.component = :component ");

        Query query = entityManager.createQuery(sql.toString());
        query.setParameter("component", component);

        List<Page> result = query.getResultList();
        if (result.isEmpty()) {
            return null;
        }
        return result.get(0);
    }

    @Override
    public PageDTO findByPath(String path) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT ");
        sql.append(" p.PAGE_ID as pageId, ");
        sql.append(" p.PAGE_NAME as pageName, ");
        sql.append(" p.PATH as path ");
        sql.append(" FROM PAGE p ");
        sql.append(" WHERE BINARY UPPER(p.PATH) = UPPER(:path) ");
        SQLQuery q = (SQLQuery) entityManager.createNativeQuery(sql.toString());
        q.setParameter("path", Common.escapeStringForMySQL(path));
        q.addScalar("pageId", LongType.INSTANCE);
        q.addScalar("pageName", StringType.INSTANCE);
        q.addScalar("path", StringType.INSTANCE);
        q.setResultTransformer(Transformers.aliasToBean(PageDTO.class));
        List<PageDTO> list = q.getResultList();
        if (list != null && list.size() > 0)
            return list.get(0);
        return null;
    }

    public  List<Page> getPageByMenuId(Long menuId){
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT p.PAGE_CODE as pageCode, ");
        sql.append("p.PAGE_NAME as pageName, ");
        sql.append("p.PAGE_ID as pageId, ");
        sql.append("p.MENU_ID as menuId, ");
        sql.append("p.NUMBER_ORDER as numberOrder, ");
        sql.append("p.PATH as path, ");
        sql.append("p.AVAILABLE_ACTION_ID as availableActionId, ");
        sql.append("p.STATUS as status, ");
        sql.append("p.CREATED_DATE as createdDate, ");
        sql.append("p.CREATED_BY as createdBy, ");
        sql.append("p.UPDATED_DATE as updatedDate, ");
        sql.append("p.UPDATED_BY as updatedBy, ");
        sql.append("p.COMPONENT as component ");
        sql.append("FROM PAGE p ");
        sql.append("WHERE p.MENU_ID = :menuId ");
        SQLQuery q = (SQLQuery) entityManager.createNativeQuery(sql.toString());
        q.setParameter("menuId", menuId);
        q.addScalar("pageCode", StringType.INSTANCE);
        q.addScalar("pageName", StringType.INSTANCE);
        q.addScalar("pageId", LongType.INSTANCE);
        q.addScalar("menuId", LongType.INSTANCE);
        q.addScalar("numberOrder", LongType.INSTANCE);
        q.addScalar("path", StringType.INSTANCE);
        q.addScalar("availableActionId", StringType.INSTANCE);
        q.addScalar("status", LongType.INSTANCE);
        q.addScalar("createdDate", DateType.INSTANCE);
        q.addScalar("createdBy", LongType.INSTANCE);
        q.addScalar("updatedDate", DateType.INSTANCE);
        q.addScalar("updatedBy", LongType.INSTANCE);
        q.addScalar("component", StringType.INSTANCE);
        q.setResultTransformer(Transformers.aliasToBean(Page.class));
        List<Page> pageList = q.getResultList();
        if (pageList != null && pageList.size() > 0)
            return pageList;
        return null;
    }
}
