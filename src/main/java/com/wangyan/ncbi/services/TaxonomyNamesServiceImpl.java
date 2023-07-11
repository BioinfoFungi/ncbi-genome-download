package com.wangyan.ncbi.services;

import com.wangyan.ncbi.pojo.Taxonomy;
import com.wangyan.ncbi.pojo.TaxonomyNames;
import com.wangyan.ncbi.repositiory.TaxonomyNamesRepository;
import com.wangyang.common.pojo.BaseVo;
import com.wangyang.common.repository.BaseRepository;
import com.wangyang.common.service.AbstractCrudService;
import org.springframework.stereotype.Service;

@Service
public class TaxonomyNamesServiceImpl extends AbstractCrudService<TaxonomyNames,TaxonomyNames, BaseVo,Integer>
        implements ITaxonomyNamesService{
    TaxonomyNamesRepository taxonomyNamesRepository;
    public TaxonomyNamesServiceImpl(TaxonomyNamesRepository taxonomyNamesRepository) {
        super(taxonomyNamesRepository);
        this.taxonomyNamesRepository = taxonomyNamesRepository;
    }
}
