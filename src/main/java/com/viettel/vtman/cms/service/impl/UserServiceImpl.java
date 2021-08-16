//package com.viettel.vtman.cms.service.impl;
//
//import com.viettel.vtman.cms.dao.UserDao;
//import com.viettel.vtman.cms.entity.Users;
//import com.viettel.vtman.cms.service.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.Optional;
//
//@Service
//public class UserServiceImpl implements UserService {
//
//    @Autowired
//    private UserDao userDao;
//
//    @Override
//    public Optional<Users> findById(int id) {
//        return userDao.findById(id);
//    }
//
//    @Override
//    public Optional<Users> findByUsername(String userName) {
//        return userDao.findByUserName(userName);
//    }
//}
