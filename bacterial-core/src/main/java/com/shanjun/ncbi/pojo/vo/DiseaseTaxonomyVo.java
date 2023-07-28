package com.shanjun.ncbi.pojo.vo;

import com.shanjun.ncbi.pojo.Disease;
import com.shanjun.ncbi.pojo.PubMed;
import com.shanjun.ncbi.pojo.Taxonomy;
import lombok.Data;

@Data
public class DiseaseTaxonomyVo {
    private Disease disease;
    private PubMed pubMed;
    private Taxonomy taxonomy;
    private String description;
}
