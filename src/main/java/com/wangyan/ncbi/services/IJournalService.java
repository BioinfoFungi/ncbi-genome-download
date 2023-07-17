package com.wangyan.ncbi.services;

import com.wangyan.ncbi.pojo.Author;
import com.wangyan.ncbi.pojo.Journal;
import com.wangyang.common.pojo.BaseVo;
import com.wangyang.common.service.ICrudService;

public interface IJournalService extends ICrudService<Journal,Journal, BaseVo,Integer> {
    Journal findSave(Journal journalInput);
}
