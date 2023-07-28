package com.shanjun.ncbi.pojo;

import com.wangyang.common.pojo.BaseEntity;
import lombok.Data;

import javax.persistence.Entity;

@Entity
@Data
public class TaxonomyGene  extends BaseEntity {

    private Integer taxonomyId;
    private Integer geneId;
}
