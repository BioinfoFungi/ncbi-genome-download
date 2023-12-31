package com.shanjun.ncbi.pojo;

import com.wangyang.common.annotation.QueryField;
import com.wangyang.common.pojo.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity(name = "taxonomy_names")
@Data
public class Taxonomy extends BaseEntity {
    private Integer taxId;
    private String nameTxt;
    private String uniqueName;
    private String nameClass;
    private String chineseName;
    @Column(columnDefinition = "longtext")
    private String function;
    private String pic;
    private Boolean isScientific;
    @QueryField
    private Boolean isCheck;

}
