package com.viettel.vtman.cms.service.impl;

import com.viettel.vtman.cms.dao.BannedContentDAO;
import com.viettel.vtman.cms.dto.BannedContentDTO;
import com.viettel.vtman.cms.entity.BannedContent;
import com.viettel.vtman.cms.service.BannedContentService;
import com.viettel.vtman.cms.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class BannedContentServiceImpl implements BannedContentService {
    @Autowired
    private BannedContentDAO bannedContentDAO;
    @Autowired
    private EmployeeService employeeService;

    @Override
    public List<BannedContent> find(String name, int page, int pageSize) throws Exception {
        if (page > 0 && pageSize > 0) {
            return bannedContentDAO.find(name, page, pageSize);
        } else {
            throw new Exception("Invalid request");
        }
    }

    @Override
    public String save(BannedContent bannedContent) throws Exception {
        if (saveCheck(bannedContent)) {
            bannedContent.setCreatedDate(new Date());
            bannedContent.setCreatedBy(employeeService.getOrCreateFromJwt().getEmployeeId());
            return bannedContentDAO.create(bannedContent);
        } else {
            return "Dữ liệu không hợp lệ.";
        }
    }

    @Override
    public String update(BannedContent bannedContent) throws Exception {
        if (saveCheck(bannedContent)) {
            BannedContent bannedContent1 = findById(bannedContent.getBannedContentId());
            bannedContent1.setBannedContentName(bannedContent.getBannedContentName());
            bannedContent1.setDescription(bannedContent.getDescription());
            bannedContent1.setUpdatedDate(new Date());
            bannedContent1.setUpdatedBy(employeeService.getOrCreateFromJwt().getEmployeeId());
            return bannedContentDAO.edit(bannedContent1);
        } else {
            return "Dữ liệu không hợp lệ.";
        }
    }

    @Override
    public BannedContent findById(Long bannedContentId) throws Exception {
        return bannedContentDAO.findById(bannedContentId);
    }

    @Override
    public void deleteById(Long bannedContentId) {
        bannedContentDAO.deleteById(bannedContentId);
    }
    private boolean saveCheck(BannedContent bannedContent) {
        return      bannedContent.getBannedContentName().length() <= 200
                ||  bannedContent.getDescription().length() <= 200;
    }

    @Override
    public Boolean checkContentBanned(String answer){
        List<BannedContent> bannedContents = bannedContentDAO.getBannedContent();
        for (BannedContent bannedContent: bannedContents) {
            if (answer.contains(bannedContent.getBannedContentName())){
                return true;
            }
        }
        return false;
    }

    @Override
    public BannedContentDTO findByName(String bannedContentName) {
        return bannedContentDAO.findByName(bannedContentName);
    }

    @Override
    public BannedContent updateData(BannedContent bannedContent) {
        return bannedContentDAO.updateData(bannedContent);
    }
}
