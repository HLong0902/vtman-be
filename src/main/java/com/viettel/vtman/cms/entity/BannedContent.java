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
@Table(name = "CONTENT_BANNED")
@NoArgsConstructor
public class BannedContent implements Serializable {
    @Id
    @Column(name = "CONTENT_BANNED_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bannedContentId;
    @Column(name = "CONTENT_BANNED_NAME")
    private String bannedContentName;
    @Column(name = "DESCRIPTION")
    private String description;
    @Column(name = "CREATED_DATE")
    private Date createdDate;
    @Column(name = "CREATED_BY")
    private Long createdBy;
    @Column(name = "UPDATED_DATE")
    private Date updatedDate;
    @Column(name = "UPDATED_BY")
    private Long updatedBy;
}
