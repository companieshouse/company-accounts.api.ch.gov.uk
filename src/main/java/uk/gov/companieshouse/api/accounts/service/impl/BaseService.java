package uk.gov.companieshouse.api.accounts.service.impl;

import com.mongodb.MongoException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.repository.MongoRepository;
import uk.gov.companieshouse.GenerateEtagUtil;
import uk.gov.companieshouse.api.accounts.Kind;
import uk.gov.companieshouse.api.accounts.ResourceName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.BasicLinkType;
import uk.gov.companieshouse.api.accounts.links.LinkType;
import uk.gov.companieshouse.api.accounts.model.entity.BaseEntity;
import uk.gov.companieshouse.api.accounts.model.rest.RestObject;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.ParentService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transformer.GenericTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;
import uk.gov.companieshouse.api.accounts.validation.Validator;
import uk.gov.companieshouse.api.model.transaction.Transaction;

public class BaseService<R extends RestObject, E extends BaseEntity, L extends LinkType> {

    private MongoRepository<E, String> repository;

    private GenericTransformer<R, E> transformer;

    private Validator<R> validator;

    private KeyIdGenerator keyIdGenerator;

    private ParentService<?, L> parentService;

    private L linkType;

    private Kind kind;

    private ResourceName resourceName;

    public BaseService(MongoRepository<E, String> repository,
                       GenericTransformer<R, E> transformer,
                       Validator<R> validator,
                       KeyIdGenerator keyIdGenerator,
                       ParentService<?, L> parentService,
                       L linkType,
                       Kind kind,
                       ResourceName resourceName) {

        this.repository = repository;
        this.transformer = transformer;
        this.validator = validator;
        this.keyIdGenerator = keyIdGenerator;
        this.parentService = parentService;
        this.linkType = linkType;
        this.kind = kind;
        this.resourceName = resourceName;
    }

    public BaseService(MongoRepository<E, String> repository,
                       GenericTransformer<R, E> transformer,
                       KeyIdGenerator keyIdGenerator,
                       ParentService<?, L> parentService,
                       L linkType,
                       Kind kind,
                       ResourceName resourceName) {

        this.repository = repository;
        this.transformer = transformer;
        this.keyIdGenerator = keyIdGenerator;
        this.parentService = parentService;
        this.linkType = linkType;
        this.kind = kind;
        this.resourceName = resourceName;
    }

    public ResponseObject<R> create(R rest, Transaction transaction, String companyAccountId, HttpServletRequest request, String selfLink) throws DataException {

        if (validator != null) {
            Errors errors = validator
                    .validateSubmission(rest, transaction, companyAccountId, request);
            if (errors.hasErrors()) {
                return new ResponseObject<>(ResponseStatus.VALIDATION_ERROR, errors);
            }
        }

        setMetadataOnRestObject(rest, selfLink);

        E entity = transformer.transform(rest);
        entity.setId(generateID(companyAccountId));

        try {
            repository.insert(entity);
        } catch (DuplicateKeyException e) {
            return new ResponseObject<>(ResponseStatus.DUPLICATE_KEY_ERROR);
        } catch (MongoException e) {
            throw new DataException(e);
        }

        parentService.addLink(companyAccountId, linkType, getSelfLink(rest), request);

        return new ResponseObject<>(ResponseStatus.CREATED, rest);
    }

    public ResponseObject<R> update(R rest, Transaction transaction, String companyAccountId, HttpServletRequest request, String selfLink) throws DataException {

        if (validator != null) {
            Errors errors = validator
                    .validateSubmission(rest, transaction, companyAccountId, request);
            if (errors.hasErrors()) {
                return new ResponseObject<>(ResponseStatus.VALIDATION_ERROR, errors);
            }
        }

        setMetadataOnRestObject(rest, selfLink);

        E entity = transformer.transform(rest);
        entity.setId(generateID(companyAccountId));

        try {
            repository.save(entity);
        } catch (MongoException e) {
            throw new DataException(e);
        }

        return new ResponseObject<>(ResponseStatus.UPDATED, rest);
    }

    public ResponseObject<R> find(String companyAccountsId) throws DataException {

        E entity;

        try {
            entity = repository.findById(generateID(companyAccountsId)).orElse(null);
        } catch (MongoException e) {
            throw new DataException(e);
        }

        if (entity == null) {
            return new ResponseObject<>(ResponseStatus.NOT_FOUND);
        }

        R rest = transformer.transform(entity);

        return new ResponseObject<>(ResponseStatus.FOUND, rest);
    }

    public ResponseObject<R> delete(String companyAccountsId, HttpServletRequest request) throws DataException {

        String id = generateID(companyAccountsId);

        try {
            if (repository.existsById(id)) {
                repository.deleteById(id);
                parentService.removeLink(companyAccountsId, linkType, request);
                return new ResponseObject<>(ResponseStatus.UPDATED);
            } else {
                return new ResponseObject<>(ResponseStatus.NOT_FOUND);
            }
        } catch (MongoException e) {
            throw new DataException(e);
        }
    }

    private String generateID(String companyAccountId) {
        return keyIdGenerator.generate(companyAccountId + "-" + resourceName.getName());
    }

    private void setMetadataOnRestObject(RestObject rest, String selfLink) {

        rest.setLinks(createLinks(selfLink));
        rest.setEtag(GenerateEtagUtil.generateEtag());
        rest.setKind(kind.getValue());
    }

    private Map<String, String> createLinks(String selfLink) {

        Map<String, String> map = new HashMap<>();
        map.put(BasicLinkType.SELF.getLink(), selfLink);
        return map;
    }

    private String getSelfLink(R rest) {
        return rest.getLinks().get(BasicLinkType.SELF.getLink());
    }
}
