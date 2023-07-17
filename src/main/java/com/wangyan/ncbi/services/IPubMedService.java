package com.wangyan.ncbi.services;

import com.wangyan.ncbi.pojo.Disease;
import com.wangyan.ncbi.pojo.PubMed;
import com.wangyang.common.pojo.BaseVo;
import com.wangyang.common.service.ICrudService;

import java.util.List;

public interface IPubMedService  extends ICrudService<PubMed,PubMed, BaseVo,Integer> {
    List<PubMed> listAllNoEFetch();
}
