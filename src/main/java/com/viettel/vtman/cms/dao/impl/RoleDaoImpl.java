package com.viettel.vtman.cms.dao.impl;

import com.viettel.vtman.cms.base.dao.BaseFWDAOImpl;
import com.viettel.vtman.cms.dao.RoleDao;
import com.viettel.vtman.cms.dto.RoleDTO;
import com.viettel.vtman.cms.entity.Role;
import com.viettel.vtman.cms.utils.Common;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
@Transactional
public class RoleDaoImpl extends BaseFWDAOImpl<Role> implements RoleDao {
    @PersistenceContext
    private EntityManager entityManager;

    public RoleDTO findNameById(Long roleId){
        StringBuilder sql = new StringBuilder();
        sql.append(" select r.ROLE_ID as roleId, r.ROLE_NAME as roleName ");
        sql.append(" from ROLE r ");
        sql.append(" where r.ROLE_ID = :roleId ");
        SQLQuery query = (SQLQuery) entityManager.createNativeQuery(sql.toString());
        query.setParameter("roleId", roleId);
        query.addScalar("roleId", LongType.INSTANCE);
        query.addScalar("roleName", StringType.INSTANCE);
        query.setResultTransformer(Transformers.aliasToBean(RoleDTO.class));
        List<RoleDTO> list = query.getResultList();
        if (list != null && list.size() > 0)
            return list.get(0);
        return null;
    }

    public List<RoleDTO> findAll() {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT NEW " + RoleDTO.class.getName() + "(a) ");
        sql.append(" FROM Role a ");
        sql.append(" ORDER BY a.roleName ASC NULLS LAST ");
        javax.persistence.Query query = entityManager.createQuery(sql.toString());
        List<RoleDTO> list = query.getResultList();
        if (list != null && list.size() > 0)
            return list;
        return null;
    }

    @Override
    public List<Role> find(String key, int page, int pageSize) {
        Query query = this.getSession().createQuery("FROM Role t WHERE UPPER(t.roleName) LIKE :key order by t.roleName asc");
        query.setParameter("key", "%" + Common.escapeStringForMySQL(key.trim().toUpperCase()) + "%");
        query.setFirstResult((page - 1) * pageSize);
        query.setMaxResults(pageSize);

        return query.list();
    }

    @Override
    public long count(String key, int page, int pageSize) {
        Query count = this.getSession().createQuery("SELECT COUNT(*) FROM Role t WHERE UPPER(t.roleName) LIKE :key");
        count.setParameter("key", "%" + Common.escapeStringForMySQL(key.trim().toUpperCase()) + "%");

        return Long.parseLong(count.getSingleResult().toString());
    }

    @Override
    public String deleteById(Long roleId) {
        Query query = this.getSession().createQuery(" SELECT 1 FROM Employee t WHERE t.roleId = :roleId ");
        query.setParameter("roleId",roleId);
        query.setMaxResults(1);
        if (query.list().isEmpty()) {
            this.deleteById(roleId, Role.class, "roleId");
            return "SUCCESS";
        } else return "FAILURE";
    }

    @Override
    public List<Role> getById(Long roleId) {
        return this.findByProperties(Role.class,  "roleId", roleId);
    }

    @Override
    public String create(Role role) {
        Query count = this.getSession().createQuery("SELECT COUNT(*) FROM Role t WHERE BINARY(UPPER(t.roleName)) = BINARY(UPPER(:roleName)) ");
        count.setParameter("roleName", role.getRoleName());
        if ((Long) count.list().get(0) == 0L) {
            return this.save(role);
        } else {
            return "Dữ liệu đã tồn tại trong hệ thống";
        }
    }
    @Override
    public String edit(Role role) {
        Query count = this.getSession().createQuery("SELECT COUNT(*) FROM Role t WHERE BINARY(UPPER(t.roleName)) = BINARY(UPPER(:roleName)) AND t.roleId != :roleId");
        count.setParameter("roleName", role.getRoleName());
        count.setParameter("roleId", role.getRoleId());
        if ((Long) count.list().get(0) == 0L)
            return this.update(role);
        else
            return "Dữ liệu đã tồn tại trong hệ thống";
    }

    @Override
    public Role findById(Long roleId) {
        return this.get(Role.class, roleId);
    }

}
