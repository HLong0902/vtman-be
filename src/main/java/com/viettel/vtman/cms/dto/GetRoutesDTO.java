package com.viettel.vtman.cms.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GetRoutesDTO {
    private String path;
    private String pageName;
    private Boolean extract = true;
    private String component;
    private String actionId;
    private List<Long> permissions;
}
