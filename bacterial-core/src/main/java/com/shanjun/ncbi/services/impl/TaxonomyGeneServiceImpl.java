package com.shanjun.ncbi.services.impl;

import com.shanjun.ncbi.pojo.TaxonomyGene;
import com.shanjun.ncbi.pojo.TaxonomyMetabolite;
import com.shanjun.ncbi.repositiory.TaxonomyGeneRepository;
import com.shanjun.ncbi.services.ITaxonomyGeneService;
import com.shanjun.ncbi.services.ITaxonomyMetaboliteService;
import com.wangyang.common.pojo.BaseVo;
import com.wangyang.common.repository.BaseRepository;
import com.wangyang.common.service.AbstractCrudService;
import org.springframework.stereotype.Service;

@Service
public class TaxonomyGeneServiceImpl  extends AbstractCrudService<TaxonomyGene, TaxonomyGene, BaseVo,Integer>
        implements ITaxonomyGeneService {

    TaxonomyGeneRepository taxonomyGeneRepository;
    public TaxonomyGeneServiceImpl(TaxonomyGeneRepository taxonomyGeneRepository) {
        super(taxonomyGeneRepository);
        this.taxonomyGeneRepository = taxonomyGeneRepository;
    }
}
