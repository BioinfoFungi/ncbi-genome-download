package com.wangyan.ncbi.services.impl;

import com.wangyan.ncbi.pojo.Author;
import com.wangyan.ncbi.repositiory.AuthorRepository;
import com.wangyan.ncbi.services.IAuthorService;
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
public class AuthorServiceImpl extends AbstractCrudService<Author,Author, BaseVo,Integer>
        implements IAuthorService {
    AuthorRepository authorRepository;
    public AuthorServiceImpl(AuthorRepository authorRepository) {
        super(authorRepository);
        this.authorRepository = authorRepository;
    }

    public Author findByAll(Author author){
        List<Author> authorList = authorRepository.findAll(new Specification<Author>() {
            @Override
            public Predicate toPredicate(Root<Author> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return query.where(criteriaBuilder.equal(root.get("lastName"),author.getLastName()),
                        criteriaBuilder.equal(root.get("foreName"),author.getForeName()),
                        criteriaBuilder.equal(root.get("initials"),author.getInitials()),
                        criteriaBuilder.equal(root.get("affiliation"),author.getAffiliation())
                        ).getRestriction();
            }
        });
        if(authorList.size()>0)authorList.get(0);
        return null;
    }


    @Override
    public Author findSave(Author authorInput){
        Author author = findByAll(authorInput);
        if(author==null){
            author = save(authorInput);
        }
        return author;
    }
}
