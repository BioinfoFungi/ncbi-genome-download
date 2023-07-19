package com.shanjun.ncbi.services.impl;

import com.shanjun.ncbi.pojo.Taxonomy;
import com.shanjun.ncbi.repositiory.TaxonomyRepository;
import com.shanjun.ncbi.services.ITaxonomyServices;
import com.wangyang.common.pojo.BaseVo;
import com.wangyang.common.service.AbstractCrudService;
import org.springframework.stereotype.Service;

@Service
public class TaxonomyServicesImpl extends AbstractCrudService<Taxonomy,Taxonomy, BaseVo,Integer>
        implements ITaxonomyServices {

    TaxonomyRepository taxonomyRepository;
    public TaxonomyServicesImpl( TaxonomyRepository taxonomyRepository) {
        super(taxonomyRepository);
        this.taxonomyRepository =taxonomyRepository;
    }
}
