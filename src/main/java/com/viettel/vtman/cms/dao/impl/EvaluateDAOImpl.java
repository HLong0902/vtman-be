package com.viettel.vtman.cms.dao.impl;

import com.viettel.vtman.cms.base.dao.BaseFWDAOImpl;
import com.viettel.vtman.cms.dao.EvaluateDAO;
import com.viettel.vtman.cms.entity.Evaluate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class EvaluateDAOImpl extends BaseFWDAOImpl<Evaluate> implements EvaluateDAO {

    @Override
    public Evaluate insertEvaluate(Evaluate evaluate) {
        this.save(evaluate);
        return evaluate;
    }
}
