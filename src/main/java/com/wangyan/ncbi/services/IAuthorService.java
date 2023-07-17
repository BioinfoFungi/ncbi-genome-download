package com.wangyan.ncbi.services;

import com.wangyan.ncbi.pojo.Author;
import com.wangyan.ncbi.pojo.Disease;
import com.wangyang.common.pojo.BaseVo;
import com.wangyang.common.service.ICrudService;

public interface IAuthorService  extends ICrudService<Author,Author, BaseVo,Integer> {
    Author findSave(Author authorInput);
}
