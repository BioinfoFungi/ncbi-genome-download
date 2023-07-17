package com.wangyan.ncbi.services.impl;

import com.wangyan.ncbi.pojo.Journal;
import com.wangyan.ncbi.repositiory.JournalRepository;
import com.wangyan.ncbi.services.IJournalService;
import com.wangyang.common.pojo.BaseVo;
import com.wangyang.common.service.AbstractCrudService;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

@Service
public class JournalServiceImpl extends AbstractCrudService<Journal,Journal, BaseVo,Integer>
        implements IJournalService {
    JournalRepository journalRepository;
    public JournalServiceImpl( JournalRepository journalRepository) {
        super(journalRepository);
        this.journalRepository =journalRepository;
    }

    public Journal findByTitle(String title){
        List<Journal> journalList = journalRepository.findAll(new Specification<Journal>() {
            @Override
            public Predicate toPredicate(Root<Journal> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return query.where(criteriaBuilder.equal(root.get("title"),title)).getRestriction();
            }
        });
        if(journalList.size()>0)return journalList.get(0);
        return null;
    }


    @Override
    public Journal findSave(Journal journalInput){
        Journal journal = findByTitle(journalInput.getTitle());
        if(journal==null){
            journal =save(journalInput);
        }
        return journal;
    }
}
