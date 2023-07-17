package com.wangyan.ncbi.pojo;

import com.wangyang.common.pojo.BaseEntity;
import lombok.Data;

import javax.persistence.Entity;

@Entity
@Data
public class PubMedAuthor extends BaseEntity {

    private Integer pId;
    private Integer authorId;
}
