package com.viettel.vtman.cms.dao.impl;

import com.viettel.vtman.cms.base.dao.BaseFWDAOImpl;
import com.viettel.vtman.cms.controller.ReportController;
import com.viettel.vtman.cms.dao.FunctionConfigDAO;
import com.viettel.vtman.cms.dao.ReportDAO;
import com.viettel.vtman.cms.dto.ObjectResult;
import com.viettel.vtman.cms.dto.ReportSearchDTO;
import com.viettel.vtman.cms.entity.AutoContent;
import com.viettel.vtman.cms.entity.Department;
import com.viettel.vtman.cms.entity.FunctionConfig;
import com.viettel.vtman.cms.entity.Topic;
import com.viettel.vtman.cms.infrastructure.CMSConst;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReportDAOImpl extends BaseFWDAOImpl<AutoContent> implements ReportDAO {
    private static final Logger LOGGER = LogManager.getLogger(ReportDAOImpl.class);
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private FunctionConfigDAO functionConfigDAO;

    @Override
    public ObjectResult getAnswerPercentReport(ReportSearchDTO dto) {
//        Query kpiQuery = this.getSession().createQuery("SELECT answerKPIPercent FROM FUNCTION_CONFIG");
//        Long kpi = (Long) (kpiQuery.list().get(0));
//
//        StringBuilder sql = new StringBuilder("SELECT NEW MAP (d.departmentName AS department,");
//        sql.append(" COUNT(*) AS total,COUNT(h.answer) AS answered, ");
//        sql.append(" ROUND(COUNT(h.answer)*100.0/count(*)) AS percent, ");
//        sql.append(" (CASE WHEN COUNT(h.answer)/count(*)*100 >= :kpi");
//        sql.append(" THEN 'Đạt' ELSE 'Không đạt' END) AS kpi) FROM HistoryFaq ");
//        sql.append(" h LEFT JOIN ");
//        sql.append(Department.class.getName());
//        sql.append(" d ON d.departmentId = h.departmentId ");
//        sql.append(conditionQuery(reportSearchDTO));
//        sql.append(" GROUP BY h.departmentId ORDER BY d.departmentName ");
        ObjectResult result = new ObjectResult();

        FunctionConfig functionConfig = functionConfigDAO.findAll().stream().findFirst().orElse(null);
        if (Objects.isNull(functionConfig) || Objects.isNull(functionConfig.getAnswerKPIPercent())) {
            result.setErrorMgs("Chưa cài đặt Cấu hình chức năng");
            return result;
        }

        Map<String, Object> mapParam = new HashMap<>();
        StringBuilder sqlList = new StringBuilder();
        StringBuilder sqlCount = new StringBuilder();
        StringBuilder countCondition = new StringBuilder();
        if (Objects.nonNull(dto.getFromDate())) {
            countCondition.append(" AND createdDate >= :fromDate ");
            Calendar calFrom = Calendar.getInstance();
            calFrom.setTime(dto.getFromDate());
            calFrom.set(Calendar.MILLISECOND, 0);
            calFrom.set(Calendar.SECOND, 0);
            calFrom.set(Calendar.MINUTE, 0);
            calFrom.set(Calendar.HOUR_OF_DAY, 0);
            mapParam.put("fromDate", calFrom.getTime());
        }
        if (Objects.nonNull(dto.getToDate())) {
            countCondition.append(" AND createdDate <= :toDate ");
            Calendar calTo = Calendar.getInstance();
            calTo.setTime(dto.getToDate());
            calTo.set(Calendar.MILLISECOND, 999);
            calTo.set(Calendar.SECOND, 59);
            calTo.set(Calendar.MINUTE, 59);
            calTo.set(Calendar.HOUR_OF_DAY, 23);
            mapParam.put("toDate", calTo.getTime());
        }
        if (Objects.nonNull(dto.getTopicId())) {
            countCondition.append(" AND topicId = :topicId ");
            mapParam.put("topicId", dto.getTopicId());
        }
        countCondition.append(" AND STATUS != 5 ");

        sqlList.append(" SELECT NEW MAP( ");
        sqlList.append("    a.departmentName AS department ");
        sqlList.append("    , (SELECT COUNT(historyFaqId) FROM HistoryFaq WHERE departmentId = a.departmentId " + countCondition + " ) AS total ");
        sqlList.append("    , (SELECT COUNT(historyFaqId) FROM HistoryFaq WHERE departmentId = a.departmentId AND answerEmployeeId IS NOT NULL " + countCondition + " ) AS answered ");
        sqlList.append(" ) ");
        sqlList.append(" FROM Department a ");
        sqlList.append(" WHERE a.departmentId IS NOT NULL ");

        sqlCount.append(" SELECT COUNT(a) ");
        sqlCount.append(" FROM Department a ");
        sqlCount.append(" WHERE a.departmentId IS NOT NULL ");

        if (Objects.nonNull(dto.getDepartmentId())) {
            sqlList.append(" AND a.departmentId = :departmentId ");
            sqlCount.append(" AND a.departmentId = :departmentId ");
            mapParam.put("departmentId", dto.getDepartmentId());
        }

        sqlList.append(" ORDER BY a.departmentName ASC NULLS LAST ");

        javax.persistence.Query queryList = entityManager.createQuery(sqlList.toString());
        javax.persistence.Query queryCount = entityManager.createQuery(sqlCount.toString());

        for (Map.Entry<String, Object> entry : mapParam.entrySet()) {
            queryList.setParameter(entry.getKey(), entry.getValue());
        }
        if (Objects.nonNull(dto.getDepartmentId())) {
            queryCount.setParameter("departmentId", dto.getDepartmentId());
        }

        if (Objects.nonNull(dto.getPage())) {
            int pageSize = Objects.nonNull(dto.getPageSize()) ? dto.getPageSize() : CMSConst.DEFAULT_PAGE_SIZE;
            queryList.setFirstResult(pageSize * (dto.getPage() - 1));
            queryList.setMaxResults(pageSize);
            result.setPage(dto.getPage());
            result.setPageSize(pageSize);
        }

        result.setListRecord(((List<HashMap<String, Object>>) queryList.getResultList()).stream().map(e -> {
            long percent = Math.round((Long.parseLong(e.get("total").toString()) > 0L ? Double.parseDouble(e.get("answered").toString()) / Double.parseDouble(e.get("total").toString()) : -1) * 100);
            e.put("percent", percent < 0 ? "--" : percent);
            e.put("kpi", percent >= functionConfig.getAnswerKPIPercent() ? "Đạt" : (percent >= 0 ? "Không đạt" : "--"));
            return e;
        }).collect(Collectors.toList()));
        result.setTotalRecord(Integer.parseInt(queryCount.getSingleResult().toString()));

        return result;
    }

    @Override
    public ObjectResult getRatingReport(ReportSearchDTO dto) {

//        StringBuilder sql = new StringBuilder("SELECT NEW MAP (d.departmentName AS department, ");
//        sql.append("COUNT(*) AS total,COUNT(h.answer) AS answered, COUNT(h.rating) AS rated,");
//        sql.append(" AVG(h.rating) as rating) FROM HistoryFaq h LEFT JOIN ");
//        sql.append(Department.class.getName());
//        sql.append(" d ON d.departmentId = h.departmentId ");
//        sql.append(conditionQuery(reportSearchDTO));
//        sql.append(" GROUP BY h.departmentId ORDER BY d.departmentName");
//        Query query = this.getSession().createQuery(sql.toString());
//        addParams(query, reportSearchDTO);
//
//        reportSearchDTO.setPage(null);
//        reportSearchDTO.setPageSize(null);
//
//        Query cntQuery = this.getSession().createQuery("SELECT COUNT(DISTINCT h.departmentId) FROM HistoryFaq h " + conditionQuery(reportSearchDTO));
//        addParams(cntQuery, reportSearchDTO);
//        ReportController.count = cntQuery.list().get(0);
//
//        return query.list();

        ObjectResult result = new ObjectResult();

        Map<String, Object> mapParam = new HashMap<>();
        StringBuilder sqlList = new StringBuilder();
        StringBuilder sqlCount = new StringBuilder();
        StringBuilder countCondition = new StringBuilder();

        if (Objects.nonNull(dto.getFromDate())) {
            countCondition.append(" AND createdDate >= :fromDate ");
            Calendar calFrom = Calendar.getInstance();
            calFrom.setTime(dto.getFromDate());
            calFrom.set(Calendar.MILLISECOND, 0);
            calFrom.set(Calendar.SECOND, 0);
            calFrom.set(Calendar.MINUTE, 0);
            calFrom.set(Calendar.HOUR_OF_DAY, 0);
            mapParam.put("fromDate", calFrom.getTime());
        }
        if (Objects.nonNull(dto.getToDate())) {
            countCondition.append(" AND createdDate <= :toDate ");
            Calendar calTo = Calendar.getInstance();
            calTo.setTime(dto.getToDate());
            calTo.set(Calendar.MILLISECOND, 999);
            calTo.set(Calendar.SECOND, 59);
            calTo.set(Calendar.MINUTE, 59);
            calTo.set(Calendar.HOUR_OF_DAY, 23);
            mapParam.put("toDate", calTo.getTime());
        }
        if (Objects.nonNull(dto.getTopicId())) {
            countCondition.append(" AND topicId = :topicId ");
            mapParam.put("topicId", dto.getTopicId());
        }
        countCondition.append(" AND STATUS != 5 ");

        sqlList.append(" SELECT NEW MAP( ");
        sqlList.append("    a.departmentName AS department ");
        sqlList.append("    , (SELECT COUNT(historyFaqId) FROM HistoryFaq WHERE departmentId = a.departmentId " + countCondition + " ) AS total ");
        sqlList.append("    , (SELECT COUNT(historyFaqId) FROM HistoryFaq WHERE departmentId = a.departmentId AND answerEmployeeId IS NOT NULL " + countCondition + " ) AS answered ");
        sqlList.append("    , (SELECT COUNT(historyFaqId) FROM HistoryFaq WHERE departmentId = a.departmentId AND rating IS NOT NULL " + countCondition + " ) AS rated ");
        sqlList.append("    , (SELECT AVG(rating) FROM HistoryFaq WHERE departmentId = a.departmentId AND rating IS NOT NULL " + countCondition + " ) AS rating ");
        sqlList.append(" ) ");
        sqlList.append(" FROM Department a ");
        sqlList.append(" WHERE a.departmentId IS NOT NULL ");

        sqlCount.append(" SELECT COUNT(a) ");
        sqlCount.append(" FROM Department a ");
        sqlCount.append(" WHERE a.departmentId IS NOT NULL ");

        if (Objects.nonNull(dto.getDepartmentId())) {
            sqlList.append(" AND a.departmentId = :departmentId ");
            sqlCount.append(" AND a.departmentId = :departmentId ");
            mapParam.put("departmentId", dto.getDepartmentId());
        }

        sqlList.append(" ORDER BY a.departmentName ASC NULLS LAST ");

        javax.persistence.Query queryList = entityManager.createQuery(sqlList.toString());
        javax.persistence.Query queryCount = entityManager.createQuery(sqlCount.toString());

        for (Map.Entry<String, Object> entry : mapParam.entrySet()) {
            queryList.setParameter(entry.getKey(), entry.getValue());
        }
        if (Objects.nonNull(dto.getDepartmentId())) {
            queryCount.setParameter("departmentId", dto.getDepartmentId());
        }

        if (Objects.nonNull(dto.getPageSize()) && Objects.nonNull(dto.getPage())) {
            queryList.setMaxResults(dto.getPageSize());
            queryList.setFirstResult((dto.getPage() - 1) * dto.getPageSize());
        }

        result.setListRecord(((List<Map<String, Object>>) queryList.getResultList()).stream().map(e -> {
            if (Objects.nonNull(e.get("rating"))) {
                e.put("rating", Math.round(10 * Double.parseDouble(e.get("rating").toString())) / 10D);
            } else {
                e.put("rating", "--");
            }
            return e;
        }).collect(Collectors.toList()));
        result.setTotalRecord(Integer.parseInt(queryCount.getSingleResult().toString()));

        return result;
    }

    @Override
    public List<HashMap<String, Object>> getTopicReport(ReportSearchDTO reportSearchDTO) {
        StringBuilder sql = new StringBuilder("SELECT NEW MAP(d.departmentName AS department, t.topicName AS topic,");
        Query query;
        try {
            sql.append(" COUNT(*)  AS total, COUNT(h.answer) AS answered,");
            sql.append(" SUM( CASE WHEN h.status = 3 THEN 1 ELSE 0 END) AS expired,");
            sql.append(" COUNT(h.rating) AS rated) FROM HistoryFaq h LEFT JOIN ");
            sql.append(Department.class.getName());
            sql.append(" d ON d.departmentId = h.departmentId LEFT JOIN ");
            sql.append(Topic.class.getName());
            sql.append(" t ON t.topicId = h.topicId ");
            sql.append(conditionQuery(reportSearchDTO));
            sql.append(" GROUP BY h.departmentId, h.topicId,d.departmentName,t.topicName  ORDER BY  t.topicName  asc nulls last ");
            query = this.getSession().createQuery(sql.toString());
            addParams(query, reportSearchDTO);
            reportSearchDTO.setPage(null);
            reportSearchDTO.setPageSize(null);
            Query cntQuery = this.getSession().createQuery("SELECT 1 FROM HistoryFaq h "
                    + conditionQuery(reportSearchDTO) + " GROUP BY h.departmentId, h.topicId ");
            addParams(cntQuery, reportSearchDTO);
            ReportController.count = cntQuery.list().size();
            return query.list();
        } catch (Exception ex) {
            LOGGER.error("query report topic error: " + ex.getMessage());
            return Collections.EMPTY_LIST;
        }
    }

    @Override
    public List<HashMap<String, Object>> getTopics() {
        StringBuilder sql = new StringBuilder("SELECT NEW MAP(t.topicName AS topic, t.topicId AS id) FROM " + Topic.class.getName() + " t ORDER BY t.topicName ");
        Query query = this.getSession().createQuery(sql.toString());
        return query.list();
    }

    @Override
    public List<HashMap<String, Object>> getQuestionReport(ReportSearchDTO reportSearchDTO) {
        StringBuilder sql = new StringBuilder("SELECT NEW MAP (d.departmentName AS department, t.topicName AS topic,");
        sql.append("h.historyFaqName AS question,h.answer AS answer, ");
//        sql.append(" CASE WHEN e.postOfficeCode IS NULL THEN CONCAT(e.employeeCode,' - ', e.employeeName) ");
//        sql.append(" ELSE CONCAT(e.postOfficeCode, ' - ', e.employeeCode, ' - ', e.employeeName) END AS answerEmp, ");
//        sql.append(" CASE WHEN a.postOfficeCode IS NULL THEN concat(COALESCE(a.employeeCode,''), ' - ', COALESCE(a.employeeName,'')) ");
//        sql.append(" ELSE concat(COALESCE(a.postOfficeCode,''), ' - ', COALESCE(a.employeeCode,''), ' - ', COALESCE(a.employeeName,'')) END as employee) ");
        sql.append(" concat(COALESCE(e.postOfficeCode, ''), CASE WHEN COALESCE(e.postOfficeCode, '') != '' AND COALESCE(e.employeeCode, '') != '' THEN ' - ' ELSE '' END, COALESCE(e.employeeCode,''), CASE WHEN (COALESCE(e.postOfficeCode, '') != '' OR COALESCE(e.employeeCode, '') != '') AND COALESCE(e.employeeName, '') != '' THEN ' - ' ELSE '' END, COALESCE(e.employeeName,'')) AS answerEmp, ");
        sql.append(" concat(COALESCE(a.postOfficeCode, ''), CASE WHEN COALESCE(a.postOfficeCode, '') != '' AND COALESCE(a.employeeCode, '') != '' THEN ' - ' ELSE '' END, COALESCE(a.employeeCode,''), CASE WHEN (COALESCE(a.postOfficeCode, '') != '' OR COALESCE(a.employeeCode, '') != '') AND COALESCE(a.employeeName,'') != '' THEN ' - ' ELSE '' END, COALESCE(a.employeeName,'')) AS employee) ");
        sql.append(" FROM HistoryFaq h LEFT JOIN ");
        sql.append(Department.class.getName());
        sql.append(" d ON d.departmentId = h.departmentId LEFT JOIN ");
        sql.append(Topic.class.getName());
        sql.append(" t ON t.topicId = h.topicId LEFT JOIN Employee e ON h.answerEmployeeId = e.employeeId");
        sql.append(" LEFT JOIN Employee a ON h.createdBy = a.employeeId ");
        sql.append(conditionQuery(reportSearchDTO));
        sql.append(" ORDER BY d.departmentName, t.topicName, h.historyFaqName ");
        Query query = this.getSession().createQuery(sql.toString());
        addParams(query, reportSearchDTO);

        reportSearchDTO.setPage(null);
        reportSearchDTO.setPageSize(null);

        Query cntQuery = this.getSession().createQuery("SELECT COUNT(*) FROM HistoryFaq h " + conditionQuery(reportSearchDTO));
        addParams(cntQuery, reportSearchDTO);
        ReportController.count = cntQuery.list().get(0);
        return query.list();
    }


    private String conditionQuery(ReportSearchDTO reportSearchDTO) {
        StringBuilder condition = new StringBuilder(" WHERE h.status != 5");
        if (reportSearchDTO.getDepartmentId() != null) condition.append(" AND h.departmentId = :departmentId");
        if (reportSearchDTO.getTopicId() != null) condition.append(" AND h.topicId = :topicId");
        if (reportSearchDTO.getFromDate() != null) condition.append(" AND h.createdDate >= :fromDate");
        if (reportSearchDTO.getToDate() != null) condition.append(" AND h.createdDate <= :toDate");
        return condition.toString();
    }

    private void addParams(Query query, ReportSearchDTO reportSearchDTO) {
        if (reportSearchDTO.getDepartmentId() != null)
            query.setParameter("departmentId", reportSearchDTO.getDepartmentId());
        if (reportSearchDTO.getTopicId() != null) query.setParameter("topicId", reportSearchDTO.getTopicId());
        if (reportSearchDTO.getFromDate() != null) query.setParameter("fromDate", reportSearchDTO.getFromDate());
        if (reportSearchDTO.getToDate() != null) query.setParameter("toDate", reportSearchDTO.getToDate());
        Integer page = reportSearchDTO.getPage();
        Integer pageSize = reportSearchDTO.getPageSize();
        if (page != null && pageSize != null) {
            query.setFirstResult((page - 1) * pageSize);
            query.setMaxResults(pageSize);
        }
    }
}
