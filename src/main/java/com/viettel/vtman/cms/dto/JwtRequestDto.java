package com.viettel.vtman.cms.dto;

import java.io.Serializable;

public class JwtRequestDto implements Serializable {

    private String username;
    private String password;

    public JwtRequestDto(String username, String password) {
        this.setUsername(username);
        this.setPassword(password);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
