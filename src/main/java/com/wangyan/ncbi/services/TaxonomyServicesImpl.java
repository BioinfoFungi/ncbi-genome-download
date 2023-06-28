package com.wangyan.ncbi.services;

import com.wangyan.ncbi.pojo.Taxonomy;
import com.wangyan.ncbi.repositiory.TaxonomyRepository;
import com.wangyang.common.pojo.BaseVo;
import com.wangyang.common.repository.BaseRepository;
import com.wangyang.common.service.AbstractCrudService;
import org.springframework.stereotype.Service;

@Service
public class TaxonomyServicesImpl extends AbstractCrudService<Taxonomy,Taxonomy, BaseVo,Integer>
        implements ITaxonomyServices{

    TaxonomyRepository taxonomyRepository;
    public TaxonomyServicesImpl( TaxonomyRepository taxonomyRepository) {
        super(taxonomyRepository);
        this.taxonomyRepository =taxonomyRepository;
    }
}
