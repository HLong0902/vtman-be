package com.viettel.vtman.cms.dao.impl;

import com.viettel.vtman.cms.base.dao.BaseFWDAOImpl;
import com.viettel.vtman.cms.dao.HistoryFaqDetailDAO;
import com.viettel.vtman.cms.dto.HistoryFaqCMSDTO;
import com.viettel.vtman.cms.dto.HistoryFaqDetailCMSDTO;
import com.viettel.vtman.cms.dto.HistoryFaqDetailDTO;
import com.viettel.vtman.cms.entity.HistoryFaqDetail;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DateType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.hibernate.type.TimestampType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Transactional
public class HistoryFaqDetailDAOImpl extends BaseFWDAOImpl<HistoryFaqDetail> implements HistoryFaqDetailDAO {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<HistoryFaqDetailCMSDTO> detail(HistoryFaqDetailCMSDTO historyFaqDetailCMSDTO) {
        StringBuilder sqlCount = new StringBuilder();
        Map<String, Object> param = new HashMap<>();
        StringBuilder sqlSelect = new StringBuilder();
        sqlSelect.append(" select h.* from (");
        sqlSelect.append(" select a.HISTORY_FAQ_ID as hisFaqId,");
        sqlSelect.append("  a.HISTORY_FAQ_DETAIL_ID as hisFaqDetailId,");
        sqlSelect.append("  a.HISTORY_FAQ_DETAIL_NAME as hisFaqDetailName,");
        sqlSelect.append("  a.HISTORY_FAQ_DETAIL_NAME as historyFaqName,");
        sqlSelect.append("  null as TOPIC_ID,");
        sqlSelect.append("  a.ANSWER as answer,");
        sqlSelect.append("  a.ANSWER_EMPLOYEE_ID as answerEmployeeId,");
        sqlSelect.append("  null as status,");
//        sqlSelect.append("  null as DESCRIPTION,");
        sqlSelect.append("  a.CREATED_DATE as createdDate,");
        sqlSelect.append("  a.CREATED_BY as createdBy,");
        sqlSelect.append("  a.ANSWER_DATE as answerDate,");
        sqlSelect.append("  (select e.EMPLOYEE_NAME  from  EMPLOYEE e where a.CREATED_BY = e.EMPLOYEE_ID  ) as employeeNameQuestion, ");
        sqlSelect.append("  (select e.EMPLOYEE_CODE  from  EMPLOYEE e where a.CREATED_BY = e.EMPLOYEE_ID  ) as employeeCodeQuestion, ");
        sqlSelect.append("  (select e.POST_OFFICE_CODE  from  EMPLOYEE e where a.CREATED_BY = e.EMPLOYEE_ID  ) as employeePostOfficeCode, ");
        sqlSelect.append("  (select e.EMPLOYEE_NAME  from  EMPLOYEE e where a.ANSWER_EMPLOYEE_ID = e.EMPLOYEE_ID  ) as employeeNameAnswer, ");
        sqlSelect.append("  (select e.EMPLOYEE_CODE  from  EMPLOYEE e where a.ANSWER_EMPLOYEE_ID = e.EMPLOYEE_ID  ) as employeeCodeAnswer, ");
        sqlSelect.append("  (select e.POST_OFFICE_CODE  from  EMPLOYEE e where a.ANSWER_EMPLOYEE_ID = e.EMPLOYEE_ID  ) as employeePostOfficeCodeAnswer, ");
        sqlSelect.append("dp.DEPARTMENT_NAME as departmentNameAnswer, ");
        sqlSelect.append("dp1.DEPARTMENT_NAME as departmentNameQuestion, ");
        sqlSelect.append("tp.TOPIC_NAME as topicName, ");
        sqlSelect.append("hf.HISTORY_FAQ_CODE as historyFaqCode ");
        sqlCount.append("Select count(*) from ( ");
        String condition = conditionQuery(param, historyFaqDetailCMSDTO);
        sqlSelect.append(condition);

        sqlSelect.append(" UNION ALL ");
        sqlSelect.append("select dt.HISTORY_FAQ_ID as hisFaqId , ");
        sqlSelect.append("null as  HISTORY_FAQ_DETAIL_ID, ");
        sqlSelect.append("dt.HISTORY_FAQ_NAME as  hisFaqDetailName, ");
        sqlSelect.append("dt.HISTORY_FAQ_NAME as  historyFaqName, ");
        sqlSelect.append("dt.TOPIC_ID,");
        sqlSelect.append("dt.ANSWER,");
        sqlSelect.append("null as ANSWER_EMPLOYEE_ID,");
//        sqlSelect.append("dt.NUMBER_ORDER,");
//        sqlSelect.append("dt.DESCRIPTION,");
        sqlSelect.append("dt.STATUS,");
        sqlSelect.append("dt.CREATED_DATE,");
        sqlSelect.append("dt.CREATED_BY,");
        sqlSelect.append("dt.ANSWER_DATE,");
        sqlSelect.append("  (select e.EMPLOYEE_NAME  from  EMPLOYEE e where dt.CREATED_BY = e.EMPLOYEE_ID  ) as employeeNameQuestion, ");
        sqlSelect.append("  (select e.EMPLOYEE_CODE  from  EMPLOYEE e where dt.CREATED_BY = e.EMPLOYEE_ID  ) as employeeCodeQuestion, ");
        sqlSelect.append("  (select e.POST_OFFICE_CODE  from  EMPLOYEE e where dt.CREATED_BY = e.EMPLOYEE_ID  ) as employeePostOfficeCode, ");
        sqlSelect.append("  (select e.EMPLOYEE_NAME  from  EMPLOYEE e where dt.ANSWER_EMPLOYEE_ID = e.EMPLOYEE_ID  ) as employeeNameAnswer, ");
        sqlSelect.append("  (select e.EMPLOYEE_CODE  from  EMPLOYEE e where dt.ANSWER_EMPLOYEE_ID = e.EMPLOYEE_ID  ) as employeeCodeAnswer, ");
        sqlSelect.append("  (select e.POST_OFFICE_CODE  from  EMPLOYEE e where dt.ANSWER_EMPLOYEE_ID = e.EMPLOYEE_ID  ) as employeePostOfficeCodeAnswer, ");
        sqlSelect.append("dp.DEPARTMENT_NAME as departmentNameAnswer, ");
        sqlSelect.append("dp1.DEPARTMENT_NAME as departmentNameQuestion,  ");
        sqlSelect.append("tp1.TOPIC_NAME as topicName, ");
        sqlSelect.append("dt.HISTORY_FAQ_CODE as historyFaqCode ");
        String condition1 = conditionQuery1(param, historyFaqDetailCMSDTO);
        sqlSelect.append(condition1.toString());
        sqlSelect.append(" ) h ");
        sqlSelect.append(" order by h.hisFaqDetailId ");
        sqlCount.append(sqlSelect).append(") a");

        SQLQuery q = (SQLQuery) entityManager.createNativeQuery(sqlSelect.toString());
        SQLQuery queryCount = (SQLQuery) entityManager.createNativeQuery(sqlCount.toString());
        for (Map.Entry<String, Object> params : param.entrySet()) {
            q.setParameter(params.getKey(), params.getValue());
            queryCount.setParameter(params.getKey(), params.getValue());
        }
        List<Object[]> listResult = q.getResultList();
        q.addScalar("hisFaqId", LongType.INSTANCE);
        q.addScalar("historyFaqCode", StringType.INSTANCE);
        q.addScalar("hisFaqDetailId", LongType.INSTANCE);
        q.addScalar("hisFaqDetailName", StringType.INSTANCE);
        q.addScalar("historyFaqName", StringType.INSTANCE);
        q.addScalar("createdDate", TimestampType.INSTANCE);
        q.addScalar("answer", StringType.INSTANCE);
        q.addScalar("answerEmployeeId", LongType.INSTANCE);
        q.addScalar("status", LongType.INSTANCE);
        q.addScalar("topicName", StringType.INSTANCE);
        q.addScalar("departmentNameAnswer", StringType.INSTANCE);
        q.addScalar("departmentNameQuestion", StringType.INSTANCE);
        q.addScalar("createdBy", LongType.INSTANCE);
        q.addScalar("answerDate", TimestampType.INSTANCE);
        q.addScalar("employeeNameAnswer", StringType.INSTANCE);
        q.addScalar("employeeCodeAnswer", StringType.INSTANCE);
        q.addScalar("employeePostOfficeCodeAnswer", StringType.INSTANCE);
        q.addScalar("employeeNameQuestion", StringType.INSTANCE);
        q.addScalar("employeeCodeQuestion", StringType.INSTANCE);
        q.addScalar("employeePostOfficeCode", StringType.INSTANCE);
        q.setResultTransformer(Transformers.aliasToBean(HistoryFaqDetailCMSDTO.class));
        q.setFirstResult((historyFaqDetailCMSDTO.getPage()- 1) * historyFaqDetailCMSDTO.getPageSize());
        q.setMaxResults(historyFaqDetailCMSDTO.getPageSize());
        List<HistoryFaqDetailCMSDTO> result = q.list();
        if (String.valueOf(historyFaqDetailCMSDTO.getPage()) !=null && historyFaqDetailCMSDTO.getPage() > 0
                && String.valueOf(historyFaqDetailCMSDTO.getPageSize()) !=null && historyFaqDetailCMSDTO.getPageSize() > 0) {
            for (HistoryFaqDetailCMSDTO dto : result) {
                dto.setTotalRecord(Integer.parseInt(queryCount.getResultList().get(0).toString()));
                dto.setPage(historyFaqDetailCMSDTO.getPage());
                dto.setPageSize(historyFaqDetailCMSDTO.getPageSize());
            }
        }

        return result;
    }

    @Override
    public HistoryFaqDetail findById(Long id) {
        return this.get(HistoryFaqDetail.class, id);
    }

    @Override
    public String updateHisFaqDetail(HistoryFaqDetail historyFaqDetail) {
        return this.update(historyFaqDetail);
    }

    private String conditionQuery(Map<String, Object> param, HistoryFaqDetailCMSDTO historyFaqDTO) {
        StringBuilder sql = new StringBuilder();
        sql.append(" from HISTORY_FAQ_DETAIL a");
        sql.append(" left join EMPLOYEE k on a.ANSWER_EMPLOYEE_ID = k.EMPLOYEE_ID");
        sql.append(" left join DEPARTMENT dp on k.DEPARTMENT_ID = dp.DEPARTMENT_ID");
        sql.append(" left join EMPLOYEE k1 on a.CREATED_BY = k1.EMPLOYEE_ID");
        sql.append(" left join DEPARTMENT dp1 on k1.DEPARTMENT_ID = dp1.DEPARTMENT_ID");
        sql.append(" left join HISTORY_FAQ hf  on a.HISTORY_FAQ_ID = hf.HISTORY_FAQ_ID");
        sql.append(" left join TOPIC tp  on hf.TOPIC_ID = tp.TOPIC_ID");
        sql.append(" where 1=1");

        if (historyFaqDTO.getHistoryFaqId() != null) {
            sql.append(" AND a.HISTORY_FAQ_ID  =:historyFaqId");
            param.put("historyFaqId", historyFaqDTO.getHistoryFaqId());
        }
        return sql.toString();
    }

    private String conditionQuery1(Map<String, Object> param, HistoryFaqDetailCMSDTO historyFaqDTO) {
        StringBuilder sql = new StringBuilder();
        sql.append(" from HISTORY_FAQ dt");
        sql.append(" left join EMPLOYEE ep1 on dt.CREATED_BY = ep1.EMPLOYEE_ID");
        sql.append(" left join DEPARTMENT dp1 on ep1.DEPARTMENT_ID = dp1.DEPARTMENT_ID");
        sql.append(" left join EMPLOYEE ep on dt.ANSWER_EMPLOYEE_ID = ep.EMPLOYEE_ID");
        sql.append(" left join DEPARTMENT dp on ep.DEPARTMENT_ID = dp.DEPARTMENT_ID");
        sql.append(" left join TOPIC tp1 on dt.TOPIC_ID = tp1.TOPIC_ID");
        sql.append(" where 1=1");

        if (historyFaqDTO.getHistoryFaqId() != null) {
            sql.append(" AND dt.HISTORY_FAQ_ID  =:historyFaqId");
            param.put("historyFaqId", historyFaqDTO.getHistoryFaqId());
        }
        return sql.toString();
    }

    @Override
    public String insertHisFaqDetail(HistoryFaqDetail hisFaqDetail) {
        return this.save(hisFaqDetail);
    }

}
