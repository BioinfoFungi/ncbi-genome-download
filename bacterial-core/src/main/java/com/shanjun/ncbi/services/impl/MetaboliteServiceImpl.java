package com.shanjun.ncbi.services.impl;

import com.shanjun.ncbi.pojo.Gene;
import com.shanjun.ncbi.pojo.Metabolite;
import com.shanjun.ncbi.repositiory.MetaboliteRepository;
import com.shanjun.ncbi.services.IGeneService;
import com.shanjun.ncbi.services.IMetaboliteService;
import com.wangyang.common.pojo.BaseVo;
import com.wangyang.common.repository.BaseRepository;
import com.wangyang.common.service.AbstractCrudService;
import org.springframework.stereotype.Service;

@Service
public class MetaboliteServiceImpl extends AbstractCrudService<Metabolite,Metabolite, BaseVo,Integer>
        implements IMetaboliteService {

    MetaboliteRepository metaboliteRepository;
    public MetaboliteServiceImpl(MetaboliteRepository metaboliteRepository) {
        super(metaboliteRepository);
        this.metaboliteRepository = metaboliteRepository;
    }
}
