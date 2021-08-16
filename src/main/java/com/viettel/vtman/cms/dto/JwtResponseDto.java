package com.viettel.vtman.cms.dto;

import java.io.Serializable;

public class JwtResponseDto implements Serializable {

    private final String jwtToken;

    public JwtResponseDto(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public String getToken() {
        return this.jwtToken;
    }
}
