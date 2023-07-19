package com.shanjun.ncbi.entrez;

import lombok.Data;

@Data
public class ESearch {
    private String header;
    private ESearchResult esearchresult;
    private TranslationSet translationset;

}
