package com.viettel.vtman.cms.dao.impl;

import com.viettel.vtman.cms.base.dao.BaseFWDAOImpl;
import com.viettel.vtman.cms.dao.UserAuthorizationDAO;
import com.viettel.vtman.cms.dto.ObjectResultPage;
import com.viettel.vtman.cms.dto.UserAuthorizationDTO;
import com.viettel.vtman.cms.utils.Common;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Transactional
public class UserAuthorizationDAOImpl extends BaseFWDAOImpl<UserAuthorizationDTO> implements UserAuthorizationDAO {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<UserAuthorizationDTO> getUserAuthorization(String employee, Long roleId, Long departmentId, ObjectResultPage objectResultPage) {
        StringBuilder sqlSelect = new StringBuilder();
        StringBuilder sqlCount = new StringBuilder();
        Map<String, Object> param = new HashMap<>();
        sqlSelect.append("SELECT e.EMPLOYEE_ID as employeeId, ");
        sqlSelect.append("concat(COALESCE(POST_OFFICE_CODE,''), ' - ', COALESCE(EMPLOYEE_CODE,''), ' - ', COALESCE(EMPLOYEE_NAME,'')) as employee, ");
        sqlSelect.append("e.POST_OFFICE_CODE as postOfficeCode, ");
        sqlSelect.append("e.PHONE as phone, ");
        sqlSelect.append("e.EMPLOYEE_CODE as employeeCode, ");
        sqlSelect.append("e.EMPLOYEE_NAME as employeeName, ");
        sqlSelect.append("d.DEPARTMENT_ID as departmentId, ");
        sqlSelect.append("d.DEPARTMENT_NAME as departmentName, ");
        sqlSelect.append("r.ROLE_ID as roleId, ");
        sqlSelect.append("r.ROLE_NAME as roleName, ");
        sqlSelect.append("e.EMAIL as email ");

        sqlCount.append("Select count(*) ");

        String condition = conditionQuery(param, employee, roleId, departmentId);
        sqlSelect.append(condition);
        sqlCount.append(condition);
        sqlSelect.append("ORDER BY e.EMPLOYEE_CODE ");
        SQLQuery queryCount = (SQLQuery) entityManager.createNativeQuery(sqlCount.toString());
        SQLQuery querySelect = (SQLQuery) entityManager.createNativeQuery(sqlSelect.toString());

        for (Map.Entry<String, Object> params : param.entrySet()){
            querySelect.setParameter(params.getKey(), params.getValue());
            queryCount.setParameter(params.getKey(), params.getValue());
        }

        querySelect.addScalar("phone", StringType.INSTANCE);
        querySelect.addScalar("postOfficeCode", StringType.INSTANCE);
        querySelect.addScalar("employeeId", LongType.INSTANCE);
        querySelect.addScalar("employeeCode", StringType.INSTANCE);
        querySelect.addScalar("employeeName", StringType.INSTANCE);
        querySelect.addScalar("departmentId", LongType.INSTANCE);
        querySelect.addScalar("departmentName", StringType.INSTANCE);
        querySelect.addScalar("roleId", LongType.INSTANCE);
        querySelect.addScalar("roleName", StringType.INSTANCE);
        querySelect.addScalar("email", StringType.INSTANCE);
        querySelect.addScalar("employee", StringType.INSTANCE);
        querySelect.setResultTransformer(Transformers.aliasToBean(UserAuthorizationDTO.class));
        querySelect.setFirstResult((objectResultPage.getPage().intValue()-1) * objectResultPage.getPageSize().intValue());
        querySelect.setMaxResults(objectResultPage.getPageSize().intValue());
        List<UserAuthorizationDTO> result = querySelect.list();

        for (UserAuthorizationDTO userAuthorizationDTO: result){
            userAuthorizationDTO.setTotalRecord(Long.parseLong(queryCount.getResultList().get(0).toString()));
            userAuthorizationDTO.setPage(objectResultPage.getPage());
            userAuthorizationDTO.setPageSize(objectResultPage.getPageSize());
        }
        return result;
    }

    @Override
    public List<UserAuthorizationDTO> getByEmployeeId(Long employeeId) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT e.EMPLOYEE_ID as employeeId, e.EMPLOYEE_CODE employeeCode, " +
                "    e.EMPLOYEE_NAME as employeeName, " +
                "    e.ROLE_ID as roleId, " +
                "    r.ROLE_NAME as roleName, " +
                "    e.DEPARTMENT_ID as departmentId, " +
                "    d.DEPARTMENT_NAME as departmentName " +
                " FROM EMPLOYEE e " +
                " LEFT JOIN DEPARTMENT d ON e.DEPARTMENT_ID = d.DEPARTMENT_ID " +
                " LEFT JOIN ROLE r on r.ROLE_ID = e.ROLE_ID " +
                " WHERE e.EMPLOYEE_ID = :employeeId");
        SQLQuery q = (SQLQuery) entityManager.createNativeQuery(sql.toString());
        q.setParameter("employeeId", employeeId);
        q.addScalar("employeeId", LongType.INSTANCE);
        q.addScalar("employeeCode", StringType.INSTANCE);
        q.addScalar("employeeName", StringType.INSTANCE);
        q.addScalar("roleId", LongType.INSTANCE);
        q.addScalar("roleName", StringType.INSTANCE);
        q.addScalar("departmentId", LongType.INSTANCE);
        q.addScalar("departmentName", StringType.INSTANCE);
        q.setResultTransformer(Transformers.aliasToBean(UserAuthorizationDTO.class));
        List<UserAuthorizationDTO> userAuthorizationDTOList = q.getResultList();
        if (userAuthorizationDTOList != null && userAuthorizationDTOList.size() > 0) {
            for (int i = 0; i < userAuthorizationDTOList.size(); i++) {
                userAuthorizationDTOList.get(i).setEmployeeName(userAuthorizationDTOList.get(i).getEmployeeCode() + " - " + userAuthorizationDTOList.get(i).getEmployeeName());
            }
        }
        if (userAuthorizationDTOList != null && userAuthorizationDTOList.size() > 0)
            return userAuthorizationDTOList;
        return null;
    }

    private String conditionQuery(Map<String, Object> param, String employee, Long roleId, Long departmentId){
        StringBuilder sql = new StringBuilder();
        sql.append("FROM EMPLOYEE e LEFT JOIN ROLE r ON e.ROLE_ID = r.ROLE_ID " +
                "LEFT JOIN DEPARTMENT d on e.DEPARTMENT_ID = d.DEPARTMENT_ID and d.STATUS = 1 "+
                "where BINARY 1 = 1 ");
        if (employee != null && !employee.isEmpty()){
            sql.append(" AND UPPER(concat(COALESCE(POST_OFFICE_CODE,''), ' - ', COALESCE(EMPLOYEE_CODE,''), ' - ', COALESCE(EMPLOYEE_NAME,''))) LIKE :employee ");
            param.put("employee", "%" + Common.escapeStringForMySQL(employee.trim().toUpperCase()) + "%");
        }
        if (roleId != null && !"".equals(String.valueOf(roleId))){
            sql.append(" and e.ROLE_ID =:roleId ");
            param.put("roleId", roleId);
        }
        if (departmentId!=null && !"".equals(String.valueOf(departmentId))){
            sql.append(" and e.DEPARTMENT_ID =:departmentId ");
            param.put("departmentId", departmentId);
        }

        return sql.toString();
    }
}
