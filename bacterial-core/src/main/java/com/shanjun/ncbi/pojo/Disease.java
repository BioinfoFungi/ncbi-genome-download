package com.shanjun.ncbi.pojo;

import com.wangyang.common.pojo.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
@Data
public class Disease extends BaseEntity {
    private String name;
    private String enName;
    private String abbreviation;
    @Column(columnDefinition = "longtext")
    private String description;

    public Disease(String name, String enName) {
        this.name = name;
        this.enName = enName;
    }
    public Disease(){}
}
