package com.shanjun.ncbi.services.impl;

import com.shanjun.ncbi.pojo.DiseaseTaxonomy;
import com.shanjun.ncbi.pojo.GeneMetabolite;
import com.shanjun.ncbi.repositiory.GeneMetaboliteRepository;
import com.shanjun.ncbi.services.IDiseaseTaxonomyService;
import com.shanjun.ncbi.services.IGeneMetaboliteService;
import com.wangyang.common.pojo.BaseVo;
import com.wangyang.common.repository.BaseRepository;
import com.wangyang.common.service.AbstractCrudService;
import org.springframework.stereotype.Service;

@Service
public class GeneMetaboliteServiceImpl  extends AbstractCrudService<GeneMetabolite, GeneMetabolite, BaseVo,Integer>
        implements IGeneMetaboliteService {


    GeneMetaboliteRepository geneMetaboliteRepository;
    public GeneMetaboliteServiceImpl(GeneMetaboliteRepository geneMetaboliteRepository) {
        super(geneMetaboliteRepository);
        this.geneMetaboliteRepository =geneMetaboliteRepository;
    }
}
