package com.viettel.vtman.cms.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity(name = "POSITION")
@Data
public class Position {
    @Id
    @Column(name = "POSITION_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long positionId;
    @Column(name = "POSITION_NAME")
    private String positionName;
    @Column(name = "CREATED_DATE")
    private Date createdDate;
    @Column(name = "CREATED_BY")
    private Long createdBy;
    @Column(name = "UPDATED_DATE")
    private Date updatedDate;
    @Column(name = "UPDATED_BY")
    private Long updatedBy;
    @Column(name = "POSITION_CODE")
    private String positionCode;
    @Column(name = "STATUS")
    private Long status;

}
