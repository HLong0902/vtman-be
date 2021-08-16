package com.viettel.vtman.cms.service.impl;

import com.viettel.vtman.cms.dao.EvaluateDAO;
import com.viettel.vtman.cms.entity.Evaluate;
import com.viettel.vtman.cms.message.Const;
import com.viettel.vtman.cms.service.EvaluateService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

@Service
public class EvaluateServiceImpl implements EvaluateService {

    @Autowired
    private EvaluateDAO evaluateDAO;

    @Override
    public String insertEvaluate(Evaluate evaluate) {
        if (Objects.isNull(evaluate) || StringUtils.isEmpty(evaluate.getEvaluateName())) {
            return StringUtils.EMPTY;
        }
        if (StringUtils.isEmpty(evaluate.getEvaluateName().trim()) || evaluate.getEvaluateName().trim().length() < 3) {
            return Const.ERROR_EVALUATE;
        }

        String temp = StringUtils.normalizeSpace(evaluate.getEvaluateName());
        evaluate.setEvaluateName(temp);
        evaluate.setCreatedDate(new Date());
        evaluateDAO.insertEvaluate(evaluate);
        return Const.SUCCESS;
    }

}
