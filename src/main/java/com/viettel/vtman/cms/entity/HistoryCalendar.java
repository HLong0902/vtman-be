package com.viettel.vtman.cms.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "HISTORY_CALENDAR")
@Data
public class HistoryCalendar {
    @Id
    @Column(name = "HISTORY_CALENDAR_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long historyCalendarId;
    @Column(name = "WORK_CALENDAR_ID")
    private Long workCalendarId;
    @Column(name = "CREATE_DATE")
    private Date createDate;
    @Column(name = "STATUS")
    private Long status;
    @Column(name = "DATE")
    private Date date;
    @Column(name = "DATE_STR")
    private String dateStr;

}
