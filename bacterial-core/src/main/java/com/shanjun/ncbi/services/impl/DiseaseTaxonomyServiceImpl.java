package com.shanjun.ncbi.services.impl;

import com.shanjun.ncbi.pojo.DiseaseTaxonomy;
import com.shanjun.ncbi.repositiory.DiseaseTaxonomyRepository;
import com.shanjun.ncbi.services.IDiseaseTaxonomyService;
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
public class DiseaseTaxonomyServiceImpl extends AbstractCrudService<DiseaseTaxonomy, DiseaseTaxonomy, BaseVo,Integer>
        implements IDiseaseTaxonomyService  {

    DiseaseTaxonomyRepository diseaseTaxonomyRepository;
    public DiseaseTaxonomyServiceImpl(DiseaseTaxonomyRepository diseaseTaxonomyRepository) {
        super(diseaseTaxonomyRepository);
        this.diseaseTaxonomyRepository =diseaseTaxonomyRepository;
    }


    @Override
    public List<DiseaseTaxonomy> listByTaxonomyId(Integer taxonomyId){
        return diseaseTaxonomyRepository.findAll(new Specification<DiseaseTaxonomy>() {
            @Override
            public Predicate toPredicate(Root<DiseaseTaxonomy> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return query.where( criteriaBuilder.equal(root.get("taxonomyId"),taxonomyId) ).getRestriction();
            }
        });
    }
    @Override
    public List<DiseaseTaxonomy> listByDiseaseId(Integer diseaseId){
        return diseaseTaxonomyRepository.findAll(new Specification<DiseaseTaxonomy>() {
            @Override
            public Predicate toPredicate(Root<DiseaseTaxonomy> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return query.where( criteriaBuilder.equal(root.get("diseaseId"),diseaseId) ).getRestriction();
            }
        });
    }
}
