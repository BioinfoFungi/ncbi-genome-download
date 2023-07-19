package com.shanjun.ncbi.services;

import com.shanjun.ncbi.pojo.TaxonomyNames;
import com.wangyang.common.pojo.BaseVo;
import com.wangyang.common.service.ICrudService;

import java.util.List;

public interface ITaxonomyNamesService extends ICrudService<TaxonomyNames,TaxonomyNames, BaseVo,Integer> {
    List<TaxonomyNames> findByEnName(List<String> enNames);

    List<TaxonomyNames> findByChName(List<String> chNames);
}
