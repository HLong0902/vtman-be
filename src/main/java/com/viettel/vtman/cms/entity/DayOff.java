package com.viettel.vtman.cms.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "DAY_OFF")
@Data
public class DayOff {


    @Id
    @Column(name = "DAY_OFF_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dayOffId;
    @Column(name = "DATE")
    private Date date;
    @Column(name = "CREATED_DATE")
    private Date createdDate;
    @Column(name = "CREATED_BY")
    private Long createdBy;
    @Column(name = "UPDATED_DATE")
    private Date updatedDate;
    @Column(name = "UPDATED_BY")
    private Long updatedBy;
    @Column(name = "DATE_STR")
    private String dateStr;

}
