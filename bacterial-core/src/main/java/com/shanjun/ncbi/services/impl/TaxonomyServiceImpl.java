package com.shanjun.ncbi.services.impl;

import com.shanjun.ncbi.pojo.Taxonomy;
import com.shanjun.ncbi.repositiory.TaxonomyRepository;
import com.shanjun.ncbi.services.ITaxonomyService;
import com.wangyang.common.pojo.BaseVo;
import com.wangyang.common.service.AbstractCrudService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

@Service
public class TaxonomyServiceImpl extends AbstractCrudService<Taxonomy, Taxonomy, BaseVo,Integer>
        implements ITaxonomyService {
    TaxonomyRepository taxonomyNamesRepository;
    public TaxonomyServiceImpl(TaxonomyRepository taxonomyNamesRepository) {
        super(taxonomyNamesRepository);
        this.taxonomyNamesRepository = taxonomyNamesRepository;
    }

//    @Override
//    public Page<Taxonomy> pageBy(Pageable pageable, Taxonomy taxonomy, String keywords) {
//        Page<Taxonomy> taxonomies = taxonomyNamesRepository.findAll(new Specification<Taxonomy>() {
//            @Override
//            public Predicate toPredicate(Root<Taxonomy> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
//
//                return query.where(criteriaBuilder.equal(root.get("nameClass"),"scientific name")).getRestriction();
//            }
//        },pageable);
//        return taxonomies;
//    }

    @Override
    public List<Taxonomy> findByEnName(List<String> enNames){
        return taxonomyNamesRepository.findAll(new Specification<Taxonomy>() {
            @Override
            public Predicate toPredicate(Root<Taxonomy> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return query.where(criteriaBuilder.isTrue(root.get("isScientific")),
                        criteriaBuilder.in(root.get("nameTxt")).value(enNames) ).getRestriction();
            }
        });
    }

    @Override
    public List<Taxonomy> findByChName(List<String> chNames){
        return taxonomyNamesRepository.findAll(new Specification<Taxonomy>() {
            @Override
            public Predicate toPredicate(Root<Taxonomy> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return query.where(criteriaBuilder.isTrue(root.get("isScientific")),
                        criteriaBuilder.in(root.get("chineseName")).value(chNames) ).getRestriction();
            }
        });
    }
}
