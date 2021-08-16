package com.viettel.vtman.cms.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "ACTION")
public class Action {
    @Id
    @Column(name = "ACTION_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long actionId;

    @Column(name = "ACTION_NAME")
    private String actionName;

    @Column(name = "STATUS")
    private Long status;

    @Column(name = "CREATED_DATE")
    private Date createdDate;

    @Column(name = "CREATED_BY")
    private Long createdBy;

    @Column(name = "UPDATED_DATE")
    private Date updatedDate;

    @Column(name = "UPDATED_BY")
    private Long updatedBy;
}
