package com.wangyan.ncbi.pojo;

import com.wangyang.common.pojo.BaseEntity;
import lombok.Data;

import javax.persistence.Entity;

@Entity(name = "taxonomy_names")
@Data
public class TaxonomyNames  extends BaseEntity {
    private Integer taxId;
    private String nameTxt;
    private String uniqueName;
    private String nameClass;
}
