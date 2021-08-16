package com.viettel.vtman.cms.dao.impl;

import com.viettel.vtman.cms.base.dao.BaseFWDAOImpl;
import com.viettel.vtman.cms.dao.PermissionActionDAO;
import com.viettel.vtman.cms.dto.PermissionActionDTO;
import com.viettel.vtman.cms.entity.PermissionAction;
import com.viettel.vtman.cms.message.Const;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.*;

@Repository
@Transactional
public class PermissionActionDAOImpl extends BaseFWDAOImpl implements PermissionActionDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<PermissionAction> listPagePermissionByDTO(PermissionActionDTO dto) {
        StringBuilder sql = new StringBuilder();
        Map<String, Object> mapParam = new HashMap<>();

        sql.append(" SELECT new PermissionAction(a, b, c) ");
        sql.append(" FROM PermissionAction a ");
        sql.append(" JOIN Page b ");
        sql.append(" ON a.pageId = b.pageId AND b.status = 1 ");
        sql.append(" JOIN Menu c ");
        sql.append(" ON b.menuId = c.menuId AND c.status = 1 ");
        sql.append(" JOIN Role d ");
        sql.append(" ON a.roleId = d.roleId AND d.status = 1 ");
        sql.append(" WHERE a.status = 1 ");
        sql.append(" AND a.actionId like '%"+ Const.ROLE_PERMISSION.READ +"%' "); // có quyền READ
        sql.append(" AND b.availableActionId like '%"+ Const.ROLE_PERMISSION.READ +"%' "); // có quyền READ
        sql.append(" AND (d.roleName IN :systemRoleGroup OR b.component NOT IN :systemPageGroup OR b.component IS NULL) ");

        if (dto.getRoleId() != null) {
            sql.append(" AND a.roleId = :roleId ");
            mapParam.put("roleId", dto.getRoleId());
        }

        sql.append(" ORDER BY c.numberOrder ASC NULLS LAST, c.menuName ASC NULLS LAST, b.numberOrder ASC NULLS LAST, b.pageName ASC NULLS LAST ");

        Query query = entityManager.createQuery(sql.toString());

        for (Map.Entry<String, Object> param : mapParam.entrySet()) {
            query.setParameter(param.getKey(), param.getValue());
        }
        query.setParameter("systemRoleGroup", Arrays.asList(Const.ROLE_GROUP.SYSTEM_ROLE));
        query.setParameter("systemPageGroup", Arrays.asList(Const.PAGE_COMPONENT_GROUP.SYSTEM_PAGE));

        return (List<PermissionAction>) query.getResultList();
    }

    @Override
    public PermissionAction getByDTO(PermissionActionDTO dto) {
        StringBuilder sql = new StringBuilder();
        List listParam = new ArrayList();

        sql.append(" SELECT a ");
        sql.append(" FROM "+PermissionAction.class.getName()+" a ");
        sql.append(" WHERE a.status = 1 ");

        if (dto.getRoleId() != null) {
            sql.append(" AND a.roleId = ?0 ");
            listParam.add(dto.getRoleId());
        }

        if (dto.getPageId() != null) {
            sql.append(" AND a.pageId = ?1 ");
            listParam.add(dto.getPageId());
        }

        Query query = entityManager.createQuery(sql.toString());

        int paramCount = 0;
        for (Object param : listParam) {
            query.setParameter(paramCount, param);
            paramCount++;
        }

        try {
            return (PermissionAction) query.getSingleResult();
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public PermissionAction updatePermissionAction(PermissionAction permissionAction) {
        if (this.update(permissionAction).equals("SUCCESS")) {
            return permissionAction;
        }
        return null;
    }

    @Override
    public PermissionAction savePermissionAction(PermissionAction permissionAction) {
        if (this.save(permissionAction).equals("SUCCESS")) {
            return permissionAction;
        }
        return null;
    }

    @Override
    public PermissionAction findByPageId(Long pageId) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT pa.PERMISSION_ACTION_ID as permissionActionId ");
        sql.append(" FROM PERMISSION_ACTION pa ");
        sql.append(" JOIN ROLE r ON pa.ROLE_ID = r.ROLE_ID ");
        sql.append(" WHERE pa.PAGE_ID = :pageId  ");
        sql.append(" AND (pa.ACTION_ID IS NOT NULL OR TRIM(pa.ACTION_ID) != '') ");
        SQLQuery q = (SQLQuery) entityManager.createNativeQuery(sql.toString());
        q.setParameter("pageId", pageId);
        q.addScalar("permissionActionId", LongType.INSTANCE);
        q.setResultTransformer(Transformers.aliasToBean(PermissionAction.class));
        List<PermissionAction> list = q.getResultList();
        if (list != null && list.size() > 0)
            return list.get(0);
        return null;
    }

    @Override
    public String deleteByDTO(PermissionActionDTO permissionActionDTO) {
        PermissionAction permissionAction = this.getByDTO(permissionActionDTO);
        return this.delete(permissionAction);
    }
}
