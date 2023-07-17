package com.wangyan.ncbi.services.impl;

import com.wangyan.ncbi.pojo.Reference;
import com.wangyan.ncbi.repositiory.ReferenceRepository;
import com.wangyan.ncbi.services.IReferenceService;
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
public class ReferenceServiceImpl extends AbstractCrudService<Reference,Reference, BaseVo,Integer>
        implements IReferenceService {

    ReferenceRepository referenceRepository;
    public ReferenceServiceImpl(ReferenceRepository referenceRepository) {
        super(referenceRepository);
        this.referenceRepository =referenceRepository;
    }

    public Reference findByPIdAndRefId(Integer pId,Integer refId){
        List<Reference> references = referenceRepository.findAll(new Specification<Reference>() {
            @Override
            public Predicate toPredicate(Root<Reference> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return query.where(criteriaBuilder.equal(root.get("pId"),pId),
                        criteriaBuilder.equal(root.get("refId"),refId)).getRestriction();
            }
        });
        if(references.size()>0)return references.get(0);
        return null;
    }

    @Override
    public Reference findSave(Integer pId, Integer refId){
        Reference reference = findByPIdAndRefId(pId, refId);
        if(reference==null){
            reference = new Reference();
            reference.setPId(pId);
            reference.setRefId(refId);
            save(reference);
        }
        return reference;
    }
}
