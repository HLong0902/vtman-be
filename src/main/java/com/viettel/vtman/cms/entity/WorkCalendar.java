package com.viettel.vtman.cms.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "WORK_CALENDAR")
@Data
public class WorkCalendar {

    @Id
    @Column(name = "WORK_CALENDAR_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long workCalendarId;
    @Column(name = "DAYS")
    private String days;

    @Column(name = "END_WORK_TIME_AM")
    private String endWorkTimeAM;

    @Column(name = "BEGIN_WORK_TIME_AM")
    private String beginWorkTimeAM;

    @Column(name = "BEGIN_WORK_TIME_PM")
    private String beginWorkTimePM;

    @Column(name = "END_WORK_TIME_PM")
    private String endWorkTimePM;

    @Column(name = "CREATED_DATE")
    private Date createdDate;
    @Column(name = "CREATED_BY")
    private Long createdBy;
    @Column(name = "UPDATED_DATE")
    private Date updatedDate;
    @Column(name = "UPDATED_BY")
    private Long updatedBy;
    @Column(name = "STATUS")
    private Long status;
    @Column(name = "IS_UPDATE")
    private String isUpdate;

}
