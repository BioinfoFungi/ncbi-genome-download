package com.wangyan.ncbi.entrez;

import com.wangyan.ncbi.pojo.Author;
import com.wangyan.ncbi.pojo.Journal;
import com.wangyan.ncbi.pojo.Reference;
import lombok.Data;

import java.util.List;

@Data
public class EFetch {
    private Integer pId;
    private String doi;
    private String pmc;
    private String meshHeading;
    private String articleTitle;
    private String articleAbstract;

    private Journal journal;
    private List<Author> authors;
    private List<Integer> referencePids;

    private String publishDate;

}
