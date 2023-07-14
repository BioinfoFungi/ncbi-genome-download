package com.wangyan.ncbi.pojo;

import com.wangyang.common.pojo.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
@Data
public class PubMed  extends BaseEntity {
    private Integer pId;
    private String keywords;
    private String doi;
    private String pmc;
    private String meshHeading;
    private String articleTitle;
    @Column(columnDefinition = "longtext")
    private String articleAbstract;
}
