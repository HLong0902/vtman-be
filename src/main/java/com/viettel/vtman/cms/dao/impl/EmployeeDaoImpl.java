package com.viettel.vtman.cms.dao.impl;

import com.viettel.vtman.cms.base.dao.BaseFWDAOImpl;
import com.viettel.vtman.cms.dao.EmployeeDAO;
import com.viettel.vtman.cms.dto.EmployeeDto;
import com.viettel.vtman.cms.entity.Employee;
import com.viettel.vtman.cms.message.Const;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
@Transactional
public class EmployeeDaoImpl extends BaseFWDAOImpl<Employee> implements EmployeeDAO {

    private static final Logger LOGGER = LogManager.getLogger(EmployeeDaoImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public EmployeeDto findNameById(Long employeeId) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select a.EMPLOYEE_NAME as employeeName,");
        sql.append(" a.EMPLOYEE_CODE  as employeeCode,");
        sql.append(" a.EMPLOYEE_ID  as employeeId,");
        sql.append(" a.POST_OFFICE_CODE  as postOfficeCode ");
        sql.append("  from EMPLOYEE a");
        sql.append("  where a.EMPLOYEE_ID = :employeeId");
        SQLQuery q = (SQLQuery) entityManager.createNativeQuery(sql.toString());
        q.setParameter("employeeId", employeeId);
        q.addScalar("employeeName", StringType.INSTANCE);
        q.addScalar("employeeCode", StringType.INSTANCE);
        q.addScalar("employeeId", LongType.INSTANCE);
        q.addScalar("postOfficeCode", StringType.INSTANCE);
        q.setResultTransformer(Transformers.aliasToBean(EmployeeDto.class));
        List<EmployeeDto> list = q.getResultList();
        if (list != null && list.size() > 0)
            return list.get(0);
        return null;
    }

    @Override
    public List<EmployeeDto> findAllById(Long departmentId) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select a.EMPLOYEE_CODE as employeeCode,");
        sql.append("  a.EMPLOYEE_NAME as employeeName,");
        sql.append("  a.EMPLOYEE_ID as employeeId, ");
        sql.append("  a.POST_OFFICE_CODE as postOfficeCode ");

        sql.append("  from EMPLOYEE a");
        sql.append("  where a.STATUS = 1 AND a.DEPARTMENT_ID = :departmentId order by a.EMPLOYEE_CODE ");
        SQLQuery q = (SQLQuery) entityManager.createNativeQuery(sql.toString());
        q.setParameter("departmentId", departmentId);
        q.addScalar("employeeCode", StringType.INSTANCE);
        q.addScalar("employeeName", StringType.INSTANCE);
        q.addScalar("employeeId", LongType.INSTANCE);
        q.addScalar("postOfficeCode", StringType.INSTANCE);
        q.setResultTransformer(Transformers.aliasToBean(EmployeeDto.class));
        List<EmployeeDto> list = q.getResultList();

        return list;
    }

    @Override
    public List<Employee> findAll() {
        return this.getAll(Employee.class);
    }

    @Override
    public Employee findByUserId(Long userId) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT a ");
        sql.append(" FROM Employee a ");
        sql.append(" WHERE a.userId = :userId ");

        Query query = entityManager.createQuery(sql.toString());
        query.setParameter("userId", userId);

        try {
            return (Employee) query.getSingleResult();
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public Employee findByEmployeeCode(String employeeCode) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT EMPLOYEE_ID as employeeId, ");
        sql.append("EMPLOYEE_NAME as employeeName, ");
        sql.append("DEPARTMENT_ID as departmentId, ");
        sql.append("STATUS as status, ");
        sql.append("DESCRIPTION as description, ");
        sql.append("CREATED_DATE as createdDate, ");
        sql.append("CREATED_BY as createdBy, ");
        sql.append("UPDATED_DATE as updatedDate, ");
        sql.append("UPDATED_BY as updatedBy, ");
        sql.append("EMAIL as email, ");
        sql.append("EMPLOYEE_CODE as employeeCode, ");
        sql.append("POSITION_ID as positionId, ");
        sql.append("USER_ID as userId, ");
        sql.append("ROLE_ID as roleId, ");
        sql.append("USER_NAME as userName ");
        sql.append("FROM EMPLOYEE ");
        sql.append("WHERE EMPLOYEE_CODE = :employeeCode ");

        SQLQuery query = this.getSession().createSQLQuery(sql.toString());
        query.addScalar("employeeId", LongType.INSTANCE);
        query.addScalar("employeeName", StringType.INSTANCE);
        query.addScalar("departmentId", LongType.INSTANCE);
        query.addScalar("status", LongType.INSTANCE);
        query.addScalar("description", StringType.INSTANCE);
        query.addScalar("createdDate", DateType.INSTANCE);
        query.addScalar("createdBy", LongType.INSTANCE);
        query.addScalar("updatedDate", DateType.INSTANCE);
        query.addScalar("updatedBy", LongType.INSTANCE);
        query.addScalar("email", StringType.INSTANCE);
        query.addScalar("employeeCode", StringType.INSTANCE);
        query.addScalar("positionId", LongType.INSTANCE);
        query.addScalar("userId", LongType.INSTANCE);
        query.addScalar("roleId", LongType.INSTANCE);
        query.addScalar("userName", StringType.INSTANCE);

        query.setResultTransformer(Transformers.aliasToBean(Employee.class));
        query.setParameter("employeeCode", employeeCode);

        return (Employee) query.uniqueResult();
    }

    @Override
    public Employee createFromJwt(Map<String, Object> jwt) {
        Employee employee = new Employee();
        employee.setEmployeeCode(StringUtils.isEmpty(jwt.get("ma_nhanvien")) ? String.valueOf(jwt.get("username")) : String.valueOf(jwt.get("ma_nhanvien")));
        employee.setEmployeeName(jwt.get("lastname") + " " + jwt.get("firstname"));
        employee.setEmail(String.valueOf(jwt.get("email")));
        employee.setPositionId(0L);
        employee.setDepartmentId(0L);
        employee.setStatus(1L);
        employee.setUserId(Long.valueOf(String.valueOf(jwt.get("userId"))));
        employee.setUserName(String.valueOf(jwt.get("username")));
        employee.setPhone(String.valueOf(jwt.get("phone")));
        employee.setPostOfficeCode(String.valueOf(jwt.get("mabuucuc")));
        employee.setRoleId(0L);

        if ("SUCCESS".equals(this.save(employee))) {
            return employee;
        }

        return null;
    }

    @Override
    public Employee createFromJwtApp(Map<String, Object> jwt, String employeeCode) {
        Employee employee = new Employee();
        employee.setEmployeeCode(employeeCode);
        employee.setEmployeeName(jwt.get("name").toString());
        employee.setPositionId(0L);
        employee.setDepartmentId(1L); // fix cung = 1
        employee.setStatus(1L);
        employee.setUserId(Long.valueOf(jwt.get("userid").toString()));
        employee.setUserName(String.valueOf(jwt.get("username")));
        employee.setRoleId(7L);
        employee.setPostOfficeCode(String.valueOf(jwt.get("ma_buucuc")));
        employee.setPhone(String.valueOf(jwt.get("phone")));
        employee.setCreatedDate(new Date());

        try {
            if (Const.SUCCESS.equals(this.save(employee))) {
                return employee;
            }
        } catch (Exception e) {
            LOGGER.error("createEmployeeApp error: " + e.getMessage());
        }

        return null;
    }

    @Override
    public Employee findById(Long employeeId) {
        return this.get(Employee.class, employeeId);
    }

    @Override
    public List<Employee> findAllSortByName() {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT e.POST_OFFICE_CODE as postOfficeCode, e.EMPLOYEE_ID as employeeId, e.EMPLOYEE_CODE as employeeCode, e.EMPLOYEE_NAME as employeeName FROM EMPLOYEE e order by EMPLOYEE_CODE, EMPLOYEE_NAME");
        SQLQuery q = (SQLQuery) entityManager.createNativeQuery(sql.toString());
        q.addScalar("employeeId", LongType.INSTANCE);
        q.addScalar("employeeCode", StringType.INSTANCE);
        q.addScalar("employeeName", StringType.INSTANCE);
        q.addScalar("postOfficeCode", StringType.INSTANCE);
        q.setResultTransformer(Transformers.aliasToBean(Employee.class));
        List<Employee> list = q.getResultList();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
//                if(list.get(i).getPostOfficeCode() == null || "".equals(list.get(i).getPostOfficeCode())){
//                    list.get(i).setEmployeeName(list.get(i).getEmployeeCode() + " - " + list.get(i).getEmployeeName());
//                }else{
//                    list.get(i).setEmployeeName(list.get(i).getPostOfficeCode() + " - " +  list.get(i).getEmployeeCode() + " - " + list.get(i).getEmployeeName());
//                }
                String employeeName = StringUtils.isEmpty(list.get(i).getPostOfficeCode()) ? "" : list.get(i).getPostOfficeCode();
                if (!StringUtils.isEmpty(list.get(i).getEmployeeCode())) {
                    employeeName += employeeName.length() > 0 ? " - " + list.get(i).getEmployeeCode() : list.get(i).getEmployeeCode();
                }
                if (!StringUtils.isEmpty(list.get(i).getEmployeeName())) {
                    employeeName += employeeName.length() > 0 ? " - " + list.get(i).getEmployeeName() : list.get(i).getEmployeeName();
                }
                list.get(i).setEmployeeName(employeeName);
            }
        }
        return list;
    }

    @Override
    public Employee findByEmployeeId(Long employeeId){
        return this.get(Employee.class, employeeId);
    }

    @Override
    public Employee updateEmployee(Employee employee){
        this.update(employee);
        return  employee;
    }

    @Override
    public EmployeeDto findByRoleId(Long roleId) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select e.EMPLOYEE_NAME as employeeName, ");
        sql.append(" e.EMPLOYEE_CODE  as employeeCode, ");
        sql.append(" e.EMPLOYEE_ID  as employeeId, ");
        sql.append(" e.ROLE_ID as roleId ");
        sql.append("  from EMPLOYEE e ");
        sql.append("  where e.ROLE_ID = :roleId ");
        SQLQuery q = (SQLQuery) entityManager.createNativeQuery(sql.toString());
        q.setParameter("roleId", roleId);
        q.addScalar("employeeName", StringType.INSTANCE);
        q.addScalar("employeeCode", StringType.INSTANCE);
        q.addScalar("employeeId", LongType.INSTANCE);
        q.addScalar("roleId", LongType.INSTANCE);
        q.setResultTransformer(Transformers.aliasToBean(EmployeeDto.class));
        List<EmployeeDto> list = q.getResultList();
        if (list != null && list.size() > 0)
            return list.get(0);
        return null;
    }

    @Override
    public List<Employee> findByIds(List<Long> employeeIds) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT EMPLOYEE_ID as employeeId, ");
        sql.append("EMPLOYEE_NAME as employeeName, ");
        sql.append("DEPARTMENT_ID as departmentId, ");
        sql.append("EMPLOYEE_CODE as employeeCode, ");
        sql.append("USER_ID as userId, ");
        sql.append("USER_NAME as userName ");
        sql.append("FROM EMPLOYEE ");
        sql.append("WHERE EMPLOYEE_ID IN (:employeeIds) ");

        SQLQuery query = this.getSession().createSQLQuery(sql.toString());
        query.addScalar("employeeId", LongType.INSTANCE);
        query.addScalar("employeeName", StringType.INSTANCE);
        query.addScalar("departmentId", LongType.INSTANCE);
        query.addScalar("employeeCode", StringType.INSTANCE);
        query.addScalar("userId", LongType.INSTANCE);
        query.addScalar("userName", StringType.INSTANCE);

        query.setResultTransformer(Transformers.aliasToBean(Employee.class));
        query.setParameterList("employeeIds", employeeIds);

        List<Employee> result = query.list();
        return result;
    }
}
