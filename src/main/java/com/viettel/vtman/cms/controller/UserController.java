//package com.viettel.vtman.cms.controller;
//
//import com.viettel.vtman.cms.infrastructure.base.response.Response;
//import com.viettel.vtman.cms.entity.Users;
//import com.viettel.vtman.cms.service.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.Optional;
//
//@RestController
//@RequestMapping("api/users")
//public class UserController {
//
//    @Autowired
//    private UserService userService;
//
//    @GetMapping("/{id}")
//    public Response<Users> GetById(@PathVariable("id") int id) {
//        Response<Users> result = new Response<Users>();
//        try {
//            Optional<Users> user = userService.findById(id);
//            result.setSuccess(true);
//            result.setOutput(user.get());
//        } catch (Exception ex) {
//
//        }
//        return result;
//    }
//
//}
