package com.shanjun.ncbi.services;

import com.shanjun.ncbi.pojo.Disease;
import com.shanjun.ncbi.pojo.DiseaseTaxonomy;
import com.wangyang.common.pojo.BaseVo;
import com.wangyang.common.service.ICrudService;

import java.util.List;

public interface IDiseaseTaxonomyService extends ICrudService<DiseaseTaxonomy,DiseaseTaxonomy, BaseVo,Integer> {
    List<DiseaseTaxonomy> listByTaxonomyId(Integer taxonomyId);

    List<DiseaseTaxonomy> listByDiseaseId(Integer diseaseId);
}
