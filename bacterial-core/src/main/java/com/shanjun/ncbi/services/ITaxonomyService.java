package com.shanjun.ncbi.services;

import com.shanjun.ncbi.pojo.Taxonomy;
import com.wangyang.common.pojo.BaseVo;
import com.wangyang.common.service.ICrudService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ITaxonomyService extends ICrudService<Taxonomy, Taxonomy, BaseVo,Integer> {

    List<Taxonomy> findByEnName(List<String> enNames);

    List<Taxonomy> findByChName(List<String> chNames);
}
