package com.wangyan.ncbi.services.impl;

import com.wangyan.ncbi.pojo.TaxonomyNames;
import com.wangyan.ncbi.pojo.TaxonomyNodes;
import com.wangyan.ncbi.repositiory.TaxonomyNodesRepository;
import com.wangyan.ncbi.services.ITaxonomyNamesService;
import com.wangyan.ncbi.services.ITaxonomyNodesServices;
import com.wangyang.common.pojo.BaseVo;
import com.wangyang.common.repository.BaseRepository;
import com.wangyang.common.service.AbstractCrudService;
import org.springframework.stereotype.Service;

@Service
public class TaxonomyNodesServicesImpl extends AbstractCrudService<TaxonomyNodes,TaxonomyNodes, BaseVo,Integer>
        implements ITaxonomyNodesServices {

    TaxonomyNodesRepository taxonomyNodesRepository;
    public TaxonomyNodesServicesImpl(TaxonomyNodesRepository taxonomyNodesRepository) {
        super(taxonomyNodesRepository);
        this.taxonomyNodesRepository = taxonomyNodesRepository;
    }
}
