package com.viettel.vtman.cms.dao;

import com.viettel.vtman.cms.dto.RoleDTO;
import com.viettel.vtman.cms.entity.Role;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface RoleDao {

    RoleDTO findNameById(Long roleId);

    List<RoleDTO> findAll();
    List<Role> find(String key, int page, int pageSize);
    long count(String key, int page, int pageSize);
    String deleteById(Long roleId);
    String create(Role role);
    String edit(Role role);
    List<Role> getById(Long roleId);
    Role findById(Long roleId);

}
