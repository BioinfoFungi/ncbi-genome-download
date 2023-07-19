package com.shanjun.ncbi.entrez;

import com.shanjun.ncbi.pojo.Author;
import com.shanjun.ncbi.pojo.Journal;
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
