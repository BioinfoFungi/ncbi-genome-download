package com.wangyan.ncbi.services.impl;

import com.wangyan.ncbi.pojo.Disease;
import com.wangyan.ncbi.pojo.TaxonomyNames;
import com.wangyan.ncbi.repositiory.DiseaseRepository;
import com.wangyan.ncbi.services.IDiseaseService;
import com.wangyan.ncbi.services.ITaxonomyNamesService;
import com.wangyang.common.pojo.BaseVo;
import com.wangyang.common.repository.BaseRepository;
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
