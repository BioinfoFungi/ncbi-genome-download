package com.shanjun.ncbi.pojo;

import com.wangyang.common.pojo.BaseEntity;
import lombok.Data;

import javax.persistence.Entity;

@Entity
@Data
public class Gene extends BaseEntity {
    private Integer geneId;
    private String name;
}
