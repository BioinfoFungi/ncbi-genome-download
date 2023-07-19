package com.shanjun.ncbi.services;

import com.shanjun.ncbi.entrez.EFetch;
import org.dom4j.DocumentException;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public interface INCBIService {
    void initTaxonomyDB();

    List<EFetch> spiderEFetch(Set<Integer> ids, Integer retMax) throws DocumentException, IOException;
}
