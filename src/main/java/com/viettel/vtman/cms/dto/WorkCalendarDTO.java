package com.viettel.vtman.cms.dto;

import lombok.Data;

import javax.persistence.Column;
import java.util.Date;
import java.util.Objects;

@Data
public class WorkCalendarDTO {
    private Long workCalendarId;

    private String days;

    private String endWorkTimeAM;

    private String beginWorkTimeAM;

    private String endWorkTimePM;

    private String beginWorkTimePM;

    private Date createdDate;

    private Long createdBy;

    private Date updatedDate;

    private Long updatedBy;

    private Long status;

    private String checkDayOff;

    private String isUpdate;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        WorkCalendarDTO that = (WorkCalendarDTO) object;
        return Objects.equals(workCalendarId, that.workCalendarId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(workCalendarId, days, endWorkTimeAM, beginWorkTimeAM, endWorkTimePM, beginWorkTimePM, createdDate, createdBy, updatedDate, updatedBy, status, checkDayOff, isUpdate);
    }
}
