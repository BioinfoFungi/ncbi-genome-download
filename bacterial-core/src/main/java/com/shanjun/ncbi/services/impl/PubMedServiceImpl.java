package com.shanjun.ncbi.services.impl;

import com.shanjun.ncbi.pojo.PubMed;
import com.shanjun.ncbi.repositiory.PubMedRepository;
import com.shanjun.ncbi.services.IPubMedService;
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
public class PubMedServiceImpl extends AbstractCrudService<PubMed,PubMed, BaseVo,Integer>
        implements IPubMedService {

    PubMedRepository pubMedRepository;
    public PubMedServiceImpl(PubMedRepository pubMedRepository) {
        super(pubMedRepository);
        this.pubMedRepository =pubMedRepository;
    }

    @Override
    public List<PubMed> listAllNoEFetch(){
        return pubMedRepository.findAll(new Specification<PubMed>() {
            @Override
            public Predicate toPredicate(Root<PubMed> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return query.where(criteriaBuilder.or(criteriaBuilder.isNull(root.get("isEFetch")),
                        criteriaBuilder.isFalse(root.get("isEFetch")))).getRestriction();
            }
        });
    }

    @Override
    public PubMed findByPMID(Integer pId){
        List<PubMed> pubMedList = pubMedRepository.findAll(new Specification<PubMed>() {
            @Override
            public Predicate toPredicate(Root<PubMed> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return query.where(criteriaBuilder.equal(root.get("pId"),pId)).getRestriction();
            }
        });
        if(pubMedList.size()>0)return pubMedList.get(0);
        return null;
    }
}
