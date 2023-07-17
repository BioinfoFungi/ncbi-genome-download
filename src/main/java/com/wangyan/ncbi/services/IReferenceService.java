package com.wangyan.ncbi.services;

import com.wangyan.ncbi.pojo.Journal;
import com.wangyan.ncbi.pojo.Reference;
import com.wangyang.common.pojo.BaseVo;
import com.wangyang.common.service.ICrudService;

public interface IReferenceService extends ICrudService<Reference,Reference, BaseVo,Integer> {
    Reference findSave(Integer pId, Integer refId);
}
