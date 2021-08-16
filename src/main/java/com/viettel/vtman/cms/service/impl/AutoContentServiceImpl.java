package com.viettel.vtman.cms.service.impl;

import com.viettel.vtman.cms.dao.AutoContentDAO;
import com.viettel.vtman.cms.dto.AutoContentDTO;
import com.viettel.vtman.cms.entity.AutoContent;
import com.viettel.vtman.cms.entity.AutoContentType;
import com.viettel.vtman.cms.service.AutoContentService;
import com.viettel.vtman.cms.service.EmployeeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class AutoContentServiceImpl implements AutoContentService {

    @Autowired
    private AutoContentDAO autoContentDAO;
    @Autowired
    private EmployeeService employeeService;

    @Override
    public List<AutoContentType> getType() {
        List<AutoContentType> res = autoContentDAO.getType();
        return res;
    };

    @Override
    public List<AutoContent> getAutoContentContain(Long autoContentType, String name, int page, int pageSize) throws Exception {
        if (( Objects.isNull(autoContentType) || autoContentType >= 0) && page > 0 && pageSize > 0) {
            return autoContentDAO.getAutoContentContain(autoContentType, name, page, pageSize);
        } else {
            throw new Exception("Invalid request");
        }
    }
    @Override
    public AutoContentDTO getAutoContentById(Long autoContentId) {
        return toAutoContentDTO(autoContentDAO.getAutoContentById(autoContentId).get(0));
    };

    @Override
    public void deleteById(Long autoContentId) {
        autoContentDAO.deleteById(autoContentId);
    }

    @Override
    public List<AutoContent> getAll() {
        return autoContentDAO.getAutoContent();
    }


    @Override
    public String save(AutoContentDTO autoContentDTO) throws Exception {
        try {
            if (saveCheck(autoContentDTO)) {
                if (Objects.isNull(autoContentDTO.getAutoContentType())) {
                    throw new Exception("Automatic Content Type is not found!");
                }
                AutoContent autoContent = toAutoContent(autoContentDTO);
                autoContent.setCreatedBy(employeeService.getOrCreateFromJwt().getEmployeeId());
                autoContent.setCreatedDate(new Date());
                autoContent.setNumberOrder(1L);
                return autoContentDAO.create(autoContent);
            } else {
                throw new Exception("Invalid data!");
            }
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public String update(AutoContentDTO autoContentDTO) throws Exception {
        try {
            if (saveCheck(autoContentDTO)) {
                AutoContent autoContent = toAutoContent(autoContentDTO);
                autoContent.setUpdatedBy(employeeService.getOrCreateFromJwt().getEmployeeId());
                autoContent.setUpdatedDate(new Date());
                return autoContentDAO.edit(autoContent);
            } else {
                throw new Exception("Invalid data!");
            }
        } catch (Exception e) {
            throw e;
        }
    }

    private boolean saveCheck(AutoContentDTO autoContentDTO) {
        for (String msg: autoContentDTO.getAutoContentList()) {
            if (msg.length() > 200) return false;
        }
        return autoContentDTO.getDescription() == null || autoContentDTO.getDescription().length() <= 500;
    }

    private AutoContentDTO toAutoContentDTO(AutoContent input) {
        AutoContentDTO res = new AutoContentDTO();

        res.setAutomaticContentId(input.getAutomaticContentId());
        res.setAutoContentType(input.getAutoContentType());
        res.setCreatedDate(input.getCreatedDate());
        res.setCreatedBy(input.getCreatedBy());
        res.setUpdatedDate(input.getUpdatedDate());
        res.setUpdatedBy(input.getUpdatedBy());
        res.setDescription(input.getDescription());
        res.setNumberOrder(input.getNumberOrder());
        res.setIsActive(input.getIsActive());
        res.setAutoContentList(input.getAutomaticContentName().split("; "));
        return res;
    }


    private AutoContent toAutoContent(AutoContentDTO input) {
        AutoContent res = new AutoContent();

        res.setAutomaticContentId(input.getAutomaticContentId());
        res.setAutoContentType(input.getAutoContentType());
        res.setCreatedDate(input.getCreatedDate());
        res.setCreatedBy(input.getCreatedBy());
        res.setDescription(input.getDescription());
        res.setNumberOrder(input.getNumberOrder());
        res.setIsActive(input.getIsActive());

        List<String> stringList = Arrays.asList(input.getAutoContentList());
        res.setAutomaticContentName(String.join("; ", stringList));
        return res;
    }

    @Override
    public AutoContentDTO getAutoContentByType(Long autoContentType) {
        AutoContentDTO result = null;
        List<AutoContent> lstContent = autoContentDAO.getAutoContentByType(autoContentType);
        if (!CollectionUtils.isEmpty(lstContent)) {
            lstContent = lstContent.stream().peek(item -> {
                String temp = item.getAutomaticContentName().replaceAll("\n", StringUtils.EMPTY);
                item.setAutomaticContentName(temp);
            }).collect(Collectors.toList());
            SecureRandom random = new SecureRandom();
            int index = random.nextInt(lstContent.size());
            result = toAutoContentDTO(lstContent.get(index));
        }
        return result;
    }

}
