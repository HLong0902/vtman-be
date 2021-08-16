package com.viettel.vtman.cms.dao.impl;

import com.viettel.vtman.cms.base.dao.BaseFWDAOImpl;
import com.viettel.vtman.cms.dao.FunctionConfigDAO;
import com.viettel.vtman.cms.dto.FunctionConfigDTO;
import com.viettel.vtman.cms.entity.FunctionConfig;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DoubleType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional
public class FunctionConfigDAOImpl extends BaseFWDAOImpl<FunctionConfig> implements FunctionConfigDAO {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<FunctionConfigDTO> displayAll() {
        List<FunctionConfigDTO> functionConfigDTOList = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT " +
                "s.DEPARTMENT_ID as departmentId,\n" +
                "d.DEPARTMENT_NAME as departmentName,\n" +
                "s.EMPLOYEE_ID as employeeId,\n" +
                "e.EMPLOYEE_NAME as employeeName,\n" +
                "s.MAXIMUM_RESPONSE_TIME as maximumResponseTime,\n" +
                "s.RESPONSE_REMINDING_TIME as responseRemindingTime,\n" +
                "s.MAXIMUM_WAITING_TIME as maximumWaitingTime,\n" +
                "s.REMINDING_WAITING_TIME as remindingWaitingTime,\n" +
                "s.MAXIMUM_QA_SESSION as maximumQASession ,\n" +
                "s.ANSWER_KPI_PERCENT as answerKPIPercent\n" +
                "FROM FUNCTION_CONFIG s\n" +
                "join DEPARTMENT d on s.DEPARTMENT_ID = d.DEPARTMENT_ID\n" +
                "join EMPLOYEE e on s.EMPLOYEE_ID = e.EMPLOYEE_ID\n" +
                "where d.STATUS = 1 and e.STATUS = 1");
        SQLQuery query = (SQLQuery) entityManager.createNativeQuery(sql.toString());

        query.addScalar("departmentId", LongType.INSTANCE);
        query.addScalar("departmentName", StringType.INSTANCE);
        query.addScalar("employeeId", StringType.INSTANCE);
        query.addScalar("employeeName", StringType.INSTANCE);
        query.addScalar("maximumResponseTime", LongType.INSTANCE);
        query.addScalar("responseRemindingTime", LongType.INSTANCE);
        query.addScalar("maximumWaitingTime", DoubleType.INSTANCE);
        query.addScalar("remindingWaitingTime", DoubleType.INSTANCE);
        query.addScalar("maximumQASession", LongType.INSTANCE);
        query.addScalar("answerKPIPercent", LongType.INSTANCE);

        query.setResultTransformer(Transformers.aliasToBean(FunctionConfigDTO.class));
        functionConfigDTOList = query.list();


        return functionConfigDTOList;
    }

    @Override
    public FunctionConfig insert(FunctionConfig functionConfig) {
        this.save(functionConfig);
        return functionConfig;
    }

    @Override
    public FunctionConfig findById(Long functionConfigId) {
        return this.get(FunctionConfig.class, functionConfigId);
    }

    @Override
    public FunctionConfig updateFunctionConfig(FunctionConfig functionConfig) {
        this.update(functionConfig);
        return functionConfig;
    }

    @Override
    public List<FunctionConfig> findAll() {
        return this.getAll(FunctionConfig.class);
    }
}
