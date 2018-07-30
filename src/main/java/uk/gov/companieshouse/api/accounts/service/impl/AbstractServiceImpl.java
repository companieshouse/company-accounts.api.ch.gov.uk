package uk.gov.companieshouse.api.accounts.service.impl;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
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

    public MongoRepository mongoRepository;

    public GenericTransformer<C, E> genericTransformer;

    public AbstractServiceImpl(MongoRepository mongoRepository,
            GenericTransformer<C, E> genericTransformer) {
        this.mongoRepository = mongoRepository;
        this.genericTransformer = genericTransformer;
    }

    @Override
    public C save(C rest, String companyAccountId) throws NoSuchAlgorithmException {
        addEtag(rest);
        addKind(rest);
        addLinks(rest);
        E baseEntity = genericTransformer.transform(rest);
        baseEntity.setId(generateID(companyAccountId));
        mongoRepository.save(baseEntity);

        return rest;
    }

    @Override
    public void addEtag(C rest) {
        rest.setEtag(GenerateEtagUtil.generateEtag());
    }

    @Override
    public String generateID(String value) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        String unencryptedId = value + getResourceName();
        byte[] id = digest.digest(
                unencryptedId.getBytes(StandardCharsets.UTF_8));
        return Base64.getUrlEncoder().encodeToString(id);
    }
}