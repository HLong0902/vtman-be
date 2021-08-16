package com.viettel.vtman.cms.dto;

import lombok.Data;

import java.util.Date;

@Data
public class ListDayInWeekDTO {
    private Date minDate;
    private Date maxDate;
}
