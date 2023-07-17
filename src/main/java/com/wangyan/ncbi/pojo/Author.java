package com.wangyan.ncbi.pojo;

import com.wangyang.common.pojo.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
@Data
public class Author  extends BaseEntity {
    private String lastName;
    private String foreName;
    private String initials;
    @Column(columnDefinition = "longtext")
    private String affiliation;
}
