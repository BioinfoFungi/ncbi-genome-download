package com.shanjun.ncbi.services.impl;

import com.shanjun.ncbi.pojo.TaxonomyNodes;
import com.shanjun.ncbi.repositiory.TaxonomyNodesRepository;
import com.shanjun.ncbi.services.ITaxonomyNodesServices;
import com.wangyang.common.pojo.BaseVo;
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
