package com.shanjun.ncbi.pojo;


import com.wangyang.common.pojo.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
@Data
public class TaxonomyMetabolite  extends BaseEntity {
    private Integer  taxonomyId;
    private Integer metaboliteId;
    private Integer literatureId;
    private Boolean isSubstrate;
    private Boolean isMetabolite;
    @Column(columnDefinition = "longtext")
    private String description;



}
