package uk.gov.companieshouse.api.accounts.service.impl;

import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.GenerateEtagUtil;
import uk.gov.companieshouse.api.accounts.CompanyAccountsApplication;
import uk.gov.companieshouse.api.accounts.model.entity.BaseEntity;
import uk.gov.companieshouse.api.accounts.model.rest.RestObject;
import uk.gov.companieshouse.api.accounts.service.AbstractService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transformer.GenericTransformer;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Service
public abstract class AbstractServiceImpl<T extends RestObject, U extends BaseEntity> implements
        AbstractService<T, U> {
    private static final Logger LOGGER = LoggerFactory.getLogger(CompanyAccountsApplication.APPLICATION_NAME_SPACE);

    private MongoRepository<U, String> mongoRepository;

    private GenericTransformer<T, U> genericTransformer;

    private MessageDigest messageDigest;

    public AbstractServiceImpl(MongoRepository<U, String> mongoRepository,
            GenericTransformer<T, U> genericTransformer) {
        this.mongoRepository = mongoRepository;
        this.genericTransformer = genericTransformer;
    }

    @Override
    public ResponseObject<T> save(T rest, String companyAccountId) {
        addEtag(rest);
        addKind(rest);
        U baseEntity = genericTransformer.transform(rest);

        try{
        baseEntity.setId(generateID(companyAccountId));
            mongoRepository.insert(baseEntity);
        } catch (DuplicateKeyException exp) {
            LOGGER.error(exp);
            return new ResponseObject(ResponseStatus.DUPLICATE_KEY_ERROR);
        } catch (MongoException mongoExp) {
            LOGGER.error(mongoExp);
            return new ResponseObject(ResponseStatus.MONGO_ERROR);
        }

        return new ResponseObject(ResponseStatus.SUCCESS_CREATED, rest);
    }

    @Override
    public U findById(String id) {
        return mongoRepository.findById(id).orElse(null);
    }

    @Override
    public void addEtag(T rest) {
        rest.setEtag(GenerateEtagUtil.generateEtag());
    }

    @Override
    public String generateID(String value) {
        String unencryptedId = value + getResourceName();
        byte[] id = messageDigest.digest(
                unencryptedId.getBytes(StandardCharsets.UTF_8));
        return Base64.getUrlEncoder().encodeToString(id);
    }

    @Autowired
    public void setMessageDigest(MessageDigest messageDigest) {
        this.messageDigest = messageDigest;
    }
}