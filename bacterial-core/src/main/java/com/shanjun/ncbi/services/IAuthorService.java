package com.shanjun.ncbi.services;

import com.shanjun.ncbi.pojo.Author;
import com.wangyang.common.pojo.BaseVo;
import com.wangyang.common.service.ICrudService;

public interface IAuthorService  extends ICrudService<Author,Author, BaseVo,Integer> {
    Author findSave(Author authorInput);
}
