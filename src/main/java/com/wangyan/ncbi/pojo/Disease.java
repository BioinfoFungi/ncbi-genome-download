package com.wangyan.ncbi.pojo;

import com.wangyang.common.pojo.BaseEntity;
import lombok.Data;

import javax.persistence.Entity;

@Entity
@Data
public class Disease extends BaseEntity {
    private String name;
    private String enName;

    public Disease(String name, String enName) {
        this.name = name;
        this.enName = enName;
    }
    public Disease(){}
}
