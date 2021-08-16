package com.viettel.vtman.cms.dao;

import com.viettel.vtman.cms.entity.TokenFcm;

import java.util.List;

public interface TokenFcmDAO {
    Long insertToken(TokenFcm tokenFcm);
    String updateToken(TokenFcm tokenFcm);
    TokenFcm findByToken(String token);
    TokenFcm findByEmployeeId(Long employeeId);
    TokenFcm findByTokenOrEmployeeId(String token, Long employeeId);
    List<String> getAllToken(List<Long> employeeIds);
}
