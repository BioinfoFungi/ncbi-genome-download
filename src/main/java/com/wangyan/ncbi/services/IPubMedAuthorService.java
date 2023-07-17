package com.wangyan.ncbi.services;

import com.wangyan.ncbi.pojo.PubMed;
import com.wangyan.ncbi.pojo.PubMedAuthor;
import com.wangyang.common.pojo.BaseVo;
import com.wangyang.common.service.ICrudService;

public interface IPubMedAuthorService extends ICrudService<PubMedAuthor,PubMedAuthor, BaseVo,Integer> {
    PubMedAuthor findSave(Integer pId, Integer authorId);
}
