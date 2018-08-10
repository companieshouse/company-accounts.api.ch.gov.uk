package uk.gov.companieshouse.api.accounts.service.impl;

import static uk.gov.companieshouse.api.accounts.service.response.ResponseStatus.ID_GENERATION_ERROR;

import com.mongodb.MongoException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Optional;
import org.springframework.dao.DuplicateKeyException;
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
public abstract class AbstractServiceImpl<C extends RestObject, E extends BaseEntity> implements
        AbstractService<C, E> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompanyAccountsApplication.APPLICATION_NAME_SPACE);

    public MongoRepository mongoRepository;

    public GenericTransformer<C, E> genericTransformer;

    public AbstractServiceImpl(MongoRepository mongoRepository,
            GenericTransformer<C, E> genericTransformer) {
        this.mongoRepository = mongoRepository;
        this.genericTransformer = genericTransformer;
    }

    @Override
    public ResponseObject<C> save(C rest, String companyAccountId) {
        addEtag(rest);
        addKind(rest);
        addLinks(rest);
        E baseEntity = genericTransformer.transform(rest);
        try {
        baseEntity.setId(generateID(companyAccountId));
            mongoRepository.insert(baseEntity);
        } catch (DuplicateKeyException exp) {
            LOGGER.error(exp);
            return new ResponseObject(ResponseStatus.DUPLICATE_KEY_ERROR);
        } catch (MongoException mongoExp) {
            LOGGER.error(mongoExp);
            return new ResponseObject(ResponseStatus.MONGO_ERROR);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error(e);
            return new ResponseObject(ID_GENERATION_ERROR);
        }

        mongoRepository.save(baseEntity);

        return new ResponseObject(ResponseStatus.SUCCESS_CREATED, rest);
    }

    @Override
    public E findById(String id) {
        Optional<E> optional = (Optional<E>) mongoRepository.findById(id);
        if (optional.isPresent()) {
            return optional.get();
        }
        return null;
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