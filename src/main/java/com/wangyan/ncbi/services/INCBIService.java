package com.wangyan.ncbi.services;

import com.wangyan.ncbi.entrez.EFetch;
import org.dom4j.DocumentException;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public interface INCBIService {
    List<EFetch> spiderEFetch(Set<Integer> ids, Integer retMax) throws DocumentException, IOException;
}
