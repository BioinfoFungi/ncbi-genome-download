package com.wangyan.ncbi.entrez;

import lombok.Data;

import java.util.List;

@Data
public class ESearch {
    private String header;
    private ESearchResult esearchresult;
    private TranslationSet translationset;

}
