package com.shanjun.ncbi.services.impl;


import com.shanjun.ncbi.pojo.Journal;
import com.shanjun.ncbi.pojo.TaxonomyMetabolite;
import com.shanjun.ncbi.repositiory.TaxonomyMetaboliteRepository;
import com.shanjun.ncbi.services.IJournalService;
import com.shanjun.ncbi.services.ITaxonomyMetaboliteService;
import com.wangyang.common.pojo.BaseVo;
import com.wangyang.common.repository.BaseRepository;
import com.wangyang.common.service.AbstractCrudService;
import org.springframework.stereotype.Service;

@Service
public class TaxonomyMetaboliteServiceImpl  extends AbstractCrudService<TaxonomyMetabolite,TaxonomyMetabolite, BaseVo,Integer>
        implements ITaxonomyMetaboliteService {

    TaxonomyMetaboliteRepository taxonomyMetaboliteRepository;


    public TaxonomyMetaboliteServiceImpl(TaxonomyMetaboliteRepository taxonomyMetaboliteRepository) {
        super(taxonomyMetaboliteRepository);
        this.taxonomyMetaboliteRepository =taxonomyMetaboliteRepository;
    }


}
