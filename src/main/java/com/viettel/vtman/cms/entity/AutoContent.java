package com.viettel.vtman.cms.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "AUTOMATIC_CONTENT")
@NoArgsConstructor
public class AutoContent implements Serializable {
    @Id
    @Column(name = "AUTOMATIC_CONTENT_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long automaticContentId;
    @Column(name = "AUTOMATIC_CONTENT_NAME")
    private String automaticContentName;
    @Column(name = "AUTOMATIC_CONTENT_TYPE")
    private Long autoContentType;
    @Column(name = "NUMBER_ORDER")
    private Long numberOrder;
    @Column(name = "DESCRIPTION")
    private String description;
    @Column(name = "STATUS")
    private Long isActive;
    @Column(name = "CREATED_DATE")
    private Date createdDate;
    @Column(name = "CREATED_BY")
    private Long createdBy;
    @Column(name = "UPDATED_DATE")
    private Date updatedDate;
    @Column(name = "UPDATED_BY")
    private Long updatedBy;
}
