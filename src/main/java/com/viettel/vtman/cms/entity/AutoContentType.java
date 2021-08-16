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

@Data
@Entity
@Table(name = "AUTO_CONTENT_TYPE")
@NoArgsConstructor
public class AutoContentType implements Serializable {
    @Id
    @Column(name = "AUTO_CONTENT_TYPE")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long automaticContentType;
    @Column(name = "NAME")
    private String name;
}