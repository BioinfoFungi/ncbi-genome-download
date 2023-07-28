package com.shanjun.ncbi.pojo;

import com.wangyang.common.pojo.BaseEntity;
import lombok.Data;

import javax.persistence.Entity;

@Entity
@Data
public class Metabolite extends BaseEntity {
    private Integer pubChemId;
    private String name;

}
