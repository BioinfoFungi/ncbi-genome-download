package com.shanjun.ncbi.pojo;


import com.wangyang.common.pojo.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
@Data
public class DiseaseTaxonomy extends BaseEntity {
    private Integer diseaseId;
    private Integer taxonomyId;
    private Integer literatureId;

    @Column(columnDefinition = "longtext")
    private String description;

}
