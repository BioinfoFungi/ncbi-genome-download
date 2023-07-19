package com.shanjun.ncbi.services;

import com.shanjun.ncbi.pojo.Journal;
import com.wangyang.common.pojo.BaseVo;
import com.wangyang.common.service.ICrudService;

public interface IJournalService extends ICrudService<Journal,Journal, BaseVo,Integer> {
    Journal findSave(Journal journalInput);
}
