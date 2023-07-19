package com.shanjun.ncbi.entrez;

import lombok.Data;

import java.util.List;

@Data
public class ESearchResult {
    private Integer count;
    private Integer retmax;
    private Integer retstart;
    private List<String> idlist;

}
