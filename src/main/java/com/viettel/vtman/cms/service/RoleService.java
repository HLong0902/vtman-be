package com.viettel.vtman.cms.service;

import com.viettel.vtman.cms.dto.RoleDTO;
import com.viettel.vtman.cms.entity.Role;

import java.util.List;

public interface RoleService {

    RoleDTO findNameById(Long roleId);

    List<RoleDTO> findAll();
    List<Role> find(String key, int page, int pageSize);
    long count(String key, int page, int pageSize);
    String deleteById(Long roleId);
    String save(Role role);
    String update(Role role);
    List<Role> getById(Long roleId);
    Role findById(Long roleId);

}
