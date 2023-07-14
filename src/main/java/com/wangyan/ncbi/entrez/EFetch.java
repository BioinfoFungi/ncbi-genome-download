package com.wangyan.ncbi.entrez;

import lombok.Data;

@Data
public class EFetch {
    private Integer pId;
    private String doi;
    private String pmc;
    private String meshHeading;
    private String articleTitle;
    private String articleAbstract;


}
