package com.shanjun.ncbi.services;

import com.shanjun.ncbi.entrez.EFetch;
import com.shanjun.ncbi.pojo.PubMed;
import org.dom4j.DocumentException;
import org.springframework.scheduling.annotation.Async;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Set;

public interface INCBIService {
    void initTaxonomyDB();

    List<EFetch> spiderEFetch(Set<Integer> ids, Integer retMax) ;

    List<EFetch>    spiderPMCEFetch(Set<Integer> ids, Integer retMax) ;

    @Async
    void runPMC(Integer batchSize);


    void runPMC(List<PubMed> pubMedList, Integer batchSize);
    @Async
    void runPubmed(Integer batchSize);


    void runPubmed(List<PubMed> pubMedList, Integer batchSize);
}
