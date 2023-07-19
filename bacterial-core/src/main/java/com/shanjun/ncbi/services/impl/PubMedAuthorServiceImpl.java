package com.shanjun.ncbi.services.impl;

import com.shanjun.ncbi.pojo.PubMedAuthor;
import com.shanjun.ncbi.repositiory.PubMedAuthorRepository;
import com.shanjun.ncbi.services.IPubMedAuthorService;
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
public class PubMedAuthorServiceImpl extends AbstractCrudService<PubMedAuthor,PubMedAuthor, BaseVo,Integer>
        implements IPubMedAuthorService {

    PubMedAuthorRepository pubMedAuthorRepository;

    public PubMedAuthorServiceImpl(PubMedAuthorRepository pubMedAuthorRepository) {
        super(pubMedAuthorRepository);
        this.pubMedAuthorRepository = pubMedAuthorRepository;
    }


    public PubMedAuthor findByPIdAndAuthorId(Integer pId, Integer authorId){
        List<PubMedAuthor> pubMedAuthors = pubMedAuthorRepository.findAll(new Specification<PubMedAuthor>() {
            @Override
            public Predicate toPredicate(Root<PubMedAuthor> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return query.where(criteriaBuilder.equal(root.get("pId"),pId),
                        criteriaBuilder.equal(root.get("authorId"),authorId)
                ).getRestriction();
            }
        });
        if(pubMedAuthors.size()>0)return pubMedAuthors.get(0);
        return null;
    }

    @Override
    public PubMedAuthor findSave(Integer pId, Integer authorId){
        PubMedAuthor pubMedAuthor = findByPIdAndAuthorId(pId, authorId);
        if(pubMedAuthor==null){
            pubMedAuthor = new PubMedAuthor();
            pubMedAuthor.setAuthorId(authorId);
            pubMedAuthor.setPId(pId);

            pubMedAuthor = save(pubMedAuthor);
        }
        return pubMedAuthor;
    }
}
