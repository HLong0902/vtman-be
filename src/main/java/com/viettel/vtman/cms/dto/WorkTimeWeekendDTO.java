package com.viettel.vtman.cms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkTimeWeekendDTO {

    int beginHourAm;
    int beginMinuteAm;
    int endHourAm;
    int endMinuteAm;
    int beginHourPm;
    int beginMinutePm;
    int endHourPm;
    int endMinutePm;
    int second;

}
