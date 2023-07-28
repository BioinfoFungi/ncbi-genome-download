package com.shanjun.ncbi.services.impl;

import com.shanjun.ncbi.pojo.Gene;
import com.shanjun.ncbi.pojo.PubMedAuthor;
import com.shanjun.ncbi.repositiory.GeneRepository;
import com.shanjun.ncbi.services.IGeneService;
import com.shanjun.ncbi.services.IPubMedAuthorService;
import com.wangyang.common.pojo.BaseVo;
import com.wangyang.common.repository.BaseRepository;
import com.wangyang.common.service.AbstractCrudService;
import org.springframework.stereotype.Service;

@Service
public class GeneServiceImpl extends AbstractCrudService<Gene,Gene, BaseVo,Integer>
        implements IGeneService {


    GeneRepository geneRepository;

    public GeneServiceImpl(GeneRepository geneRepository) {
        super(geneRepository);
        this.geneRepository =geneRepository;
    }
}
