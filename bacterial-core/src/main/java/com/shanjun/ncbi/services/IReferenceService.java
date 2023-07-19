package com.shanjun.ncbi.services;

import com.shanjun.ncbi.pojo.Reference;
import com.wangyang.common.pojo.BaseVo;
import com.wangyang.common.service.ICrudService;

public interface IReferenceService extends ICrudService<Reference,Reference, BaseVo,Integer> {
    Reference findSave(Integer pId, Integer refId);
}
