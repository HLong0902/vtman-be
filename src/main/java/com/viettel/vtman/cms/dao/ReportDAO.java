package com.viettel.vtman.cms.dao;

import com.viettel.vtman.cms.dto.ObjectResult;
import com.viettel.vtman.cms.dto.ReportSearchDTO;

import java.util.HashMap;
import java.util.List;

public interface ReportDAO {

    ObjectResult getAnswerPercentReport(ReportSearchDTO dto);
    List<HashMap<String, Object>> getTopicReport(ReportSearchDTO reportSearchDTO);
    ObjectResult getRatingReport(ReportSearchDTO reportSearchDTO);
    List<HashMap<String, Object>> getQuestionReport(ReportSearchDTO reportSearchDTO);
    List<HashMap<String, Object>> getTopics();

}
