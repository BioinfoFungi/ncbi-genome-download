package com.shanjun.ncbi.services.impl;

import com.shanjun.ncbi.pojo.Disease;
import com.shanjun.ncbi.repositiory.DiseaseRepository;
import com.shanjun.ncbi.services.IDiseaseService;
import com.wangyang.common.pojo.BaseVo;
import com.wangyang.common.service.AbstractCrudService;
import org.springframework.stereotype.Service;

@Service
public class DiseaseServiceImpl  extends AbstractCrudService<Disease,Disease, BaseVo,Integer>
        implements IDiseaseService {

    DiseaseRepository diseaseRepository;
    public DiseaseServiceImpl(DiseaseRepository diseaseRepository) {
        super(diseaseRepository);
        this.diseaseRepository = diseaseRepository;
    }
}
