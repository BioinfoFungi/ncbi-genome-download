package com.shanjun.ncbi.pojo;

import com.wangyang.common.annotation.QueryField;
import com.wangyang.common.pojo.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.Date;

@Entity
@Data
public class PubMed  extends BaseEntity {
    private Integer pId;
    private String keywords;
    private String doi;
    private String pmc;
    private String meshHeading;
    @Column(columnDefinition = "longtext")
    private String articleTitle;
    @Column(columnDefinition = "longtext")
    private String articleAbstract;
    @Column(columnDefinition = "longtext")
    private String articleFullText;
    @QueryField
    private Boolean isEFetch;
    @QueryField
    private Boolean isPMCEFetch;
    private Integer journalId;
    private Date publishDate;


}
