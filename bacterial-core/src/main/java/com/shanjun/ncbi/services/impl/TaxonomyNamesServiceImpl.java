package com.shanjun.ncbi.services.impl;

import com.shanjun.ncbi.pojo.TaxonomyNames;
import com.shanjun.ncbi.repositiory.TaxonomyNamesRepository;
import com.shanjun.ncbi.services.ITaxonomyNamesService;
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
public class TaxonomyNamesServiceImpl extends AbstractCrudService<TaxonomyNames,TaxonomyNames, BaseVo,Integer>
        implements ITaxonomyNamesService {
    TaxonomyNamesRepository taxonomyNamesRepository;
    public TaxonomyNamesServiceImpl(TaxonomyNamesRepository taxonomyNamesRepository) {
        super(taxonomyNamesRepository);
        this.taxonomyNamesRepository = taxonomyNamesRepository;
    }


    @Override
    public List<TaxonomyNames> findByEnName(List<String> enNames){
        return taxonomyNamesRepository.findAll(new Specification<TaxonomyNames>() {
            @Override
            public Predicate toPredicate(Root<TaxonomyNames> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return query.where(criteriaBuilder.isTrue(root.get("isScientific")),
                        criteriaBuilder.in(root.get("nameTxt")).value(enNames) ).getRestriction();
            }
        });
    }

    @Override
    public List<TaxonomyNames> findByChName(List<String> chNames){
        return taxonomyNamesRepository.findAll(new Specification<TaxonomyNames>() {
            @Override
            public Predicate toPredicate(Root<TaxonomyNames> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return query.where(criteriaBuilder.isTrue(root.get("isScientific")),
                        criteriaBuilder.in(root.get("chineseName")).value(chNames) ).getRestriction();
            }
        });
    }
}
