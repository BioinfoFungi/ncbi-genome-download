package com.shanjun.ncbi.services;

import com.shanjun.ncbi.pojo.PubMed;
import com.wangyang.common.pojo.BaseVo;
import com.wangyang.common.service.ICrudService;

import java.util.List;

public interface IPubMedService  extends ICrudService<PubMed,PubMed, BaseVo,Integer> {
    List<PubMed> listAllNoEFetch();

    List<PubMed> listAllNoPMCEFetch();

    PubMed findByPMID(Integer pId);
}
