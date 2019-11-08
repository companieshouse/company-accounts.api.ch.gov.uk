package uk.gov.companieshouse.api.accounts.service;

import com.mongodb.MongoException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.GenerateEtagUtil;
import uk.gov.companieshouse.api.accounts.enumeration.AccountsResource;
import uk.gov.companieshouse.api.accounts.enumeration.Resource;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.BasicLinkType;
import uk.gov.companieshouse.api.accounts.links.LinkType;
import uk.gov.companieshouse.api.accounts.model.entity.BaseEntity;
import uk.gov.companieshouse.api.accounts.model.rest.Rest;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.parent.ParentResourceFactory;
import uk.gov.companieshouse.api.accounts.repository.AccountsResourceRepositoryFactory;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transformer.AccountsResourceTransformerFactory;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;
import uk.gov.companieshouse.api.accounts.validation.AccountsResourceValidator;
import uk.gov.companieshouse.api.accounts.validation.AccountsResourceValidatorFactory;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@Service
public class AccountsResourceService {

    @Autowired
    private KeyIdGenerator keyIdGenerator;

    @Autowired
    private AccountsResourceValidatorFactory<Rest> validatorFactory;

    @Autowired
    private AccountsResourceTransformerFactory<Rest, BaseEntity> transformerFactory;

    @Autowired
    private AccountsResourceRepositoryFactory<BaseEntity> repositoryFactory;

    @Autowired
    private ParentResourceFactory<LinkType> parentResourceFactory;

    public ResponseObject<Rest> create(Rest rest, AccountsResource accountsResource, Transaction transaction, String companyAccountId, HttpServletRequest request) throws DataException {

        AccountsResourceValidator<Rest> validator = validatorFactory.getValidator(accountsResource);
        if (validator != null) {

            Errors errors = validator.validateSubmission(rest, transaction, companyAccountId, request);
            if (errors.hasErrors()) {
                return new ResponseObject<>(ResponseStatus.VALIDATION_ERROR, errors);
            }
        }

        setMetadataOnRest(rest, request.getRequestURI(), accountsResource);

        BaseEntity entity = transformerFactory.getTransformer(accountsResource).transform(rest);
        entity.setId(generateID(companyAccountId, accountsResource.getResource()));

        try {
            repositoryFactory.getRepository(accountsResource).insert(entity);
        } catch (DuplicateKeyException e) {
            return new ResponseObject<>(ResponseStatus.DUPLICATE_KEY_ERROR);
        } catch (MongoException e) {
            throw new DataException(e);
        }

        parentResourceFactory.getParentResource(accountsResource.getParent())
                .addLink(companyAccountId, accountsResource.getLinkType(), getSelfLink(rest), request);

        return new ResponseObject<>(ResponseStatus.CREATED, rest);
    }

    public ResponseObject<Rest> update(Rest rest, AccountsResource accountsResource, Transaction transaction, String companyAccountId, HttpServletRequest request) throws DataException {

        AccountsResourceValidator<Rest> validator = validatorFactory.getValidator(accountsResource);
        if (validator != null) {

            Errors errors = validator.validateSubmission(rest, transaction, companyAccountId, request);
            if (errors.hasErrors()) {
                return new ResponseObject<>(ResponseStatus.VALIDATION_ERROR, errors);
            }
        }

        setMetadataOnRest(rest, request.getRequestURI(), accountsResource);

        BaseEntity entity = transformerFactory.getTransformer(accountsResource).transform(rest);
        entity.setId(generateID(companyAccountId, accountsResource.getResource()));

        try {
            repositoryFactory.getRepository(accountsResource).save(entity);
        } catch (MongoException e) {
            throw new DataException(e);
        }

        return new ResponseObject<>(ResponseStatus.UPDATED, rest);
    }

    public ResponseObject<Rest> find(AccountsResource accountsResource, String companyAccountsId) throws DataException {

        BaseEntity entity;

        try {
            entity = repositoryFactory.getRepository(accountsResource)
                            .findById(generateID(companyAccountsId, accountsResource.getResource()))
                                    .orElse(null);
        } catch (MongoException e) {
            throw new DataException(e);
        }

        if (entity == null) {
            return new ResponseObject<>(ResponseStatus.NOT_FOUND);
        }

        Rest rest = transformerFactory.getTransformer(accountsResource).transform(entity);

        return new ResponseObject<>(ResponseStatus.FOUND, rest);
    }

    public ResponseObject<Rest> delete(AccountsResource accountsResource, String companyAccountsId, HttpServletRequest request) throws DataException {

        String id = generateID(companyAccountsId, accountsResource.getResource());

        try {
            if (repositoryFactory.getRepository(accountsResource).existsById(id)) {
                repositoryFactory.getRepository(accountsResource).deleteById(id);
                parentResourceFactory.getParentResource(accountsResource.getParent())
                        .removeLink(companyAccountsId, accountsResource.getLinkType(), request);
                return new ResponseObject<>(ResponseStatus.UPDATED);
            } else {
                return new ResponseObject<>(ResponseStatus.NOT_FOUND);
            }
        } catch (MongoException e) {
            throw new DataException(e);
        }
    }

    private String generateID(String companyAccountId, Resource resource) {
        return keyIdGenerator.generate(companyAccountId + "-" + resource.getName());
    }

    private void setMetadataOnRest(Rest rest, String selfLink, AccountsResource accountsResource) {

        rest.setLinks(createLinks(selfLink));
        rest.setEtag(GenerateEtagUtil.generateEtag());
        rest.setKind(accountsResource.getKind());
    }

    private Map<String, String> createLinks(String selfLink) {

        Map<String, String> map = new HashMap<>();
        map.put(BasicLinkType.SELF.getLink(), selfLink);
        return map;
    }

    private String getSelfLink(Rest rest) {
        return rest.getLinks().get(BasicLinkType.SELF.getLink());
    }
}