package com.wangyan.ncbi.pojo;

import com.wangyang.common.pojo.BaseEntity;
import lombok.Data;

import javax.persistence.Entity;

@Entity(name = "taxonomy_nodes")
@Data
public class TaxonomyNodes extends BaseEntity {
    private String  rank;
}
