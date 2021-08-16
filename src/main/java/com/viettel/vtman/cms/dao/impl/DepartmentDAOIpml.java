package com.viettel.vtman.cms.dao.impl;

import com.viettel.vtman.cms.base.dao.BaseFWDAOImpl;
import com.viettel.vtman.cms.dao.DepartmentDAO;
import com.viettel.vtman.cms.dto.DepartmentDTO;
import com.viettel.vtman.cms.entity.Department;
import com.viettel.vtman.cms.entity.Menu;
import com.viettel.vtman.cms.utils.Common;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.viettel.vtman.cms.infrastructure.CMSConst.DEFAULT_PAGE_SIZE;

@Repository
@Transactional
public class DepartmentDAOIpml extends BaseFWDAOImpl<Department> implements DepartmentDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<DepartmentDTO> findAll() {
        StringBuilder sqlCount = new StringBuilder();
        Map<String, Object> param = new HashMap<>();
        StringBuilder sqlSelect = new StringBuilder();
        sqlSelect.append(" select a.DEPARTMENT_ID as departmentId,");
        sqlSelect.append("  a.DEPARTMENT_NAME as departmentName");

        sqlSelect.append("  from DEPARTMENT a WHERE a.STATUS = 1 ");
        sqlSelect.append("  order by a.DEPARTMENT_NAME");
        SQLQuery q = (SQLQuery) entityManager.createNativeQuery(sqlSelect.toString());

        q.addScalar("departmentId", LongType.INSTANCE);
        q.addScalar("departmentName", StringType.INSTANCE);

        q.setResultTransformer(Transformers.aliasToBean(DepartmentDTO.class));
        List<DepartmentDTO> list = q.getResultList();

        return list;
    }

    @Override
    public List<Department> searchByDto(DepartmentDTO dto) {
        Map<String, Object> mapParam = new HashMap<>();
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT a ");
        sql.append(conditionByDto(dto, mapParam));
        sql.append(" ORDER BY a.departmentName ASC ");

        Query query = entityManager.createQuery(sql.toString());
        for (Map.Entry<String, Object> entry : mapParam.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }

        int pageIndex = dto.getPageIndex() == null ? 1 : dto.getPageIndex();
        int pageSize = dto.getPageSize() == null ? DEFAULT_PAGE_SIZE : dto.getPageSize();
        query.setFirstResult((pageIndex - 1) * pageSize);
        query.setMaxResults(pageSize);

        return query.getResultList();
    }

    @Override
    public long countByDto(DepartmentDTO dto) {
        Map<String, Object> mapParam = new HashMap<>();
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT COUNT(a.departmentId) ");
        sql.append(conditionByDto(dto, mapParam));

        Query query = entityManager.createQuery(sql.toString());
        for (Map.Entry<String, Object> entry : mapParam.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }

        return (Long) query.getSingleResult();
    }

    private StringBuilder conditionByDto(DepartmentDTO dto, Map<String, Object> mapParam) {
        StringBuilder sql = new StringBuilder();
        sql.append(" FROM Department a ");
        sql.append(" WHERE a.departmentId IS NOT NULL ");

        if (dto.getDepartmentId() != null) {
            sql.append(" AND a.departmentId = :departmentId ");
            mapParam.put("departmentId", dto.getDepartmentId());
        }
        if (!StringUtils.isEmpty(dto.getDepartmentName())) {
            sql.append(" AND UPPER(a.departmentName) LIKE :departmentName ");
            mapParam.put("departmentName", "%" + Common.escapeStringForMySQL(dto.getDepartmentName().trim().toUpperCase()) + "%");
        }
        if (!StringUtils.isEmpty(dto.getDepartmentCode())) {
            sql.append(" AND UPPER(a.departmentCode) LIKE :departmentCode ");
            mapParam.put("departmentCode", "%" + Common.escapeStringForMySQL(dto.getDepartmentCode().trim().toUpperCase()) + "%");
        }
        if (!StringUtils.isEmpty(dto.getDescription())) {
            sql.append(" AND UPPER(a.description) LIKE :description ");
            mapParam.put("description", "%" + Common.escapeStringForMySQL(dto.getDescription().trim().toUpperCase()) + "%");
        }
        if (dto.getStatus() != null) {
            sql.append(" AND a.status = :status ");
            mapParam.put("status", dto.getStatus());
        }

        if (!StringUtils.isEmpty(dto.getDepartmentCodeUniqueCheck())) {
            sql.append(" AND CAST(UPPER(a.departmentCode) as binary) = CAST(:departmentCodeUniqueCheck as binary) ");
            mapParam.put("departmentCodeUniqueCheck", Common.escapeStringForMySQL(dto.getDepartmentCodeUniqueCheck().toUpperCase()));
        }

        if (!StringUtils.isEmpty(dto.getDepartmentNameUniqueCheck())) {
            sql.append(" AND CAST(UPPER(a.departmentName) as binary) = CAST(:departmentNameUniqueCheck as binary) ");
            mapParam.put("departmentNameUniqueCheck", Common.escapeStringForMySQL(dto.getDepartmentNameUniqueCheck().toUpperCase()));
        }

        if (Objects.nonNull(dto.getDepartmentIdUniqueCheck())) {
            sql.append(" AND a.departmentId != :departmentIdUniqueCheck ");
            mapParam.put("departmentIdUniqueCheck", dto.getDepartmentIdUniqueCheck());
        }

        return sql;
    }

    @Override
    public void deleteByDepartmentId(Long departmentId) {
        this.deleteById(departmentId, Department.class, "departmentId");
    }

    @Override
    public String updateByEntity(Department department) {
        return this.update(department);
    }

    @Override
    public String createByEntity(Department department) {
        return this.save(department);
    }

    @Override
    public boolean checkSafeDelete(Long departmentId) {
        StringBuilder sqlA = new StringBuilder();
        sqlA.append(" SELECT COUNT(a) ");
        sqlA.append(" FROM Topic a ");
        sqlA.append(" WHERE a.departmentId = :departmentId ");

        StringBuilder sqlB = new StringBuilder();
        sqlB.append(" SELECT COUNT(a) ");
        sqlB.append(" FROM Employee a ");
        sqlB.append(" WHERE a.departmentId = :departmentId ");

        Query queryA = entityManager.createQuery(sqlA.toString());
        queryA.setParameter("departmentId", departmentId);
        Query queryB = entityManager.createQuery(sqlB.toString());
        queryB.setParameter("departmentId", departmentId);

        return ((Long) queryA.getSingleResult()).compareTo(0L) == 0 && ((Long) queryB.getSingleResult()).compareTo(0L) == 0;
    }

    @Override
    public List<DepartmentDTO> findAllStatus() {
        StringBuilder sqlCount = new StringBuilder();
        Map<String, Object> param = new HashMap<>();
        StringBuilder sqlSelect = new StringBuilder();
        sqlSelect.append(" select a.DEPARTMENT_ID as departmentId,");
        sqlSelect.append("  a.DEPARTMENT_NAME as departmentName");

        sqlSelect.append("  from DEPARTMENT a");
        sqlSelect.append("  where 1=1 order by a.DEPARTMENT_NAME");
        SQLQuery q = (SQLQuery) entityManager.createNativeQuery(sqlSelect.toString());

        q.addScalar("departmentId", LongType.INSTANCE);
        q.addScalar("departmentName", StringType.INSTANCE);

        q.setResultTransformer(Transformers.aliasToBean(DepartmentDTO.class));
        List<DepartmentDTO> list = q.getResultList();

        return list;
    }

    @Override
    public  Department getDepartmentById(Long departmentId){
        if (this.findByProperties(Department.class, "departmentId", departmentId).isEmpty()){
            return null;
        }
        return this.findByProperties(Department.class, "departmentId", departmentId).get(0);
    }
}
