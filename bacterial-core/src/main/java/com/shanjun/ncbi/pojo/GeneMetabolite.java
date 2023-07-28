package com.shanjun.ncbi.pojo;

import com.wangyang.common.pojo.BaseEntity;
import lombok.Data;

import javax.persistence.Entity;

@Entity
@Data
public class GeneMetabolite extends BaseEntity {
    private Integer geneId;
    private Integer metaboliteId;

}
