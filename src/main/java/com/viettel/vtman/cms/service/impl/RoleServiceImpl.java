package com.viettel.vtman.cms.service.impl;

import com.viettel.vtman.cms.dao.RoleDao;
import com.viettel.vtman.cms.dto.RoleDTO;
import com.viettel.vtman.cms.entity.Role;
import com.viettel.vtman.cms.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {
    @Autowired
    private RoleDao roleDao;

    @Override
    public RoleDTO findNameById(Long roleId) {
        return roleDao.findNameById(roleId);
    }

    @Override
    public List<RoleDTO> findAll() {
        return roleDao.findAll();
    }

    @Override
    public List<Role> find(String key, int page, int pageSize) {
        return roleDao.find(key, page, pageSize);
    }

    @Override
    public long count(String key, int page, int pageSize) {
        return roleDao.count(key, page, pageSize);
    }

    @Override
    public String deleteById(Long roleId) {
        return roleDao.deleteById(roleId);
    }

    @Override
    public String save(Role role) {
        role.setCreatedDate(new Date());
        return roleDao.create(role);
    }

    @Override
    public String update(Role role) {
        role.setUpdatedDate(new Date());
        return roleDao.edit(role);
    }
    @Override
    public List<Role> getById(Long roleId){
        return roleDao.getById(roleId);
    }

    @Override
    public Role findById(Long roleId) {
        return roleDao.findById(roleId);
    }
}
