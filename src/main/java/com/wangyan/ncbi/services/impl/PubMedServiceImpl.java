package com.wangyan.ncbi.services.impl;

import com.wangyan.ncbi.pojo.Disease;
import com.wangyan.ncbi.pojo.PubMed;
import com.wangyan.ncbi.repositiory.PubMedRepository;
import com.wangyan.ncbi.services.IDiseaseService;
import com.wangyan.ncbi.services.IPubMedService;
import com.wangyang.common.pojo.BaseVo;
import com.wangyang.common.repository.BaseRepository;
import com.wangyang.common.service.AbstractCrudService;
import org.springframework.stereotype.Service;

@Service
public class PubMedServiceImpl extends AbstractCrudService<PubMed,PubMed, BaseVo,Integer>
        implements IPubMedService {

    PubMedRepository pubMedRepository;
    public PubMedServiceImpl(PubMedRepository pubMedRepository) {
        super(pubMedRepository);
        this.pubMedRepository =pubMedRepository;
    }
}
