package uk.gov.companieshouse.api.accounts.service.impl;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.GenerateEtagUtil;
import uk.gov.companieshouse.api.accounts.model.entity.BaseEntity;
import uk.gov.companieshouse.api.accounts.model.rest.RestObject;
import uk.gov.companieshouse.api.accounts.service.AbstractService;
import uk.gov.companieshouse.api.accounts.transformer.GenericTransformer;

@Service
public abstract class AbstractServiceImpl<C extends RestObject, E extends BaseEntity> implements
        AbstractService<C, E> {

    protected MongoRepository mongoRepository;

    protected GenericTransformer<C, E> genericTransformer;

    public AbstractServiceImpl(MongoRepository mongoRepository,
            GenericTransformer<C, E> genericTransformer) {
        this.mongoRepository = mongoRepository;
        this.genericTransformer = genericTransformer;
    }

    @Override
    public C save(C rest) {
        addEtag(rest);
        addKind(rest);
        addLinks(rest);
        E baseEntity = genericTransformer.transform(rest);
        addID(baseEntity);
        mongoRepository.save(baseEntity);
        return rest;
    }

    @Override
    public void addEtag(C rest) {
        rest.setEtag(GenerateEtagUtil.generateEtag());
    }

    @Override
    public void addID(BaseEntity entity) {
        entity.setId("generated id");
    }
}