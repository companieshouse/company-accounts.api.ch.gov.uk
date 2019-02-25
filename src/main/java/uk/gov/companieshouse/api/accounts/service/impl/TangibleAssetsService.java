package uk.gov.companieshouse.api.accounts.service.impl;

import com.mongodb.MongoException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.GenerateEtagUtil;
import uk.gov.companieshouse.api.accounts.Kind;
import uk.gov.companieshouse.api.accounts.ResourceName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.BasicLinkType;
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;
import uk.gov.companieshouse.api.accounts.model.entity.notes.tangible.TangibleAssetsEntity;
import uk.gov.companieshouse.api.accounts.model.rest.notes.tangible.TangibleAssets;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.repository.TangibleAssetsRepository;
import uk.gov.companieshouse.api.accounts.service.ResourceService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.transformer.TangibleAssetsTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;
import uk.gov.companieshouse.api.accounts.validation.TangibleAssetsValidator;

@Service
public class TangibleAssetsService implements ResourceService<TangibleAssets> {

    private TangibleAssetsRepository repository;
    private TangibleAssetsTransformer transformer;
    private TangibleAssetsValidator validator;
    private SmallFullService smallFullService;
    private KeyIdGenerator keyIdGenerator;

    @Autowired
    public TangibleAssetsService(TangibleAssetsRepository repository, TangibleAssetsTransformer transformer,
            TangibleAssetsValidator validator, SmallFullService smallFullService, KeyIdGenerator keyIdGenerator) {

        this.repository = repository;
        this.transformer = transformer;
        this.validator = validator;
        this.smallFullService = smallFullService;
        this.keyIdGenerator = keyIdGenerator;
    }

    @Override
    public ResponseObject<TangibleAssets> create(TangibleAssets rest, Transaction transaction,
            String companyAccountsId, HttpServletRequest request) throws DataException {

        Errors errors = validator.validateTangibleAssets(rest, transaction, companyAccountsId, request);
        if (errors.hasErrors()) {
            return new ResponseObject<>(ResponseStatus.VALIDATION_ERROR, errors);
        }

        setMetadataOnRestObject(rest, transaction, companyAccountsId);

        TangibleAssetsEntity entity = transformer.transform(rest);
        entity.setId(generateID(companyAccountsId));

        try {

            repository.insert(entity);
        } catch (DuplicateKeyException e) {

            return new ResponseObject<>(ResponseStatus.DUPLICATE_KEY_ERROR);
        } catch (MongoException e) {

            throw new DataException(e);
        }

        smallFullService.addLink(companyAccountsId, SmallFullLinkType.TANGIBLE_ASSETS_NOTE,
                getSelfLinkFromRestEntity(rest), request);

        return new ResponseObject<>(ResponseStatus.CREATED, rest);
    }

    @Override
    public ResponseObject<TangibleAssets> update(TangibleAssets rest, Transaction transaction,
            String companyAccountsId, HttpServletRequest request) throws DataException {

        Errors errors = validator.validateTangibleAssets(rest, transaction, companyAccountsId, request);
        if (errors.hasErrors()) {
            return new ResponseObject<>(ResponseStatus.VALIDATION_ERROR, errors);
        }

        setMetadataOnRestObject(rest, transaction, companyAccountsId);

        TangibleAssetsEntity entity = transformer.transform(rest);
        entity.setId(generateID(companyAccountsId));

        try {

            repository.save(entity);
        } catch (MongoException e) {

            throw new DataException(e);
        }
        return new ResponseObject<>(ResponseStatus.UPDATED, rest);
    }

    @Override
    public ResponseObject<TangibleAssets> findById(String id, HttpServletRequest request)
            throws DataException {

        TangibleAssetsEntity entity;

        try {

            entity = repository.findById(id).orElse(null);
        } catch (MongoException e) {

            throw new DataException(e);
        }

        if (entity == null) {
            return new ResponseObject<>(ResponseStatus.NOT_FOUND);
        }

        return new ResponseObject<>(ResponseStatus.FOUND, transformer.transform(entity));
    }

    @Override
    public ResponseObject<TangibleAssets> delete(String companyAccountsId, HttpServletRequest request)
            throws DataException {

        String tangibleId = generateID(companyAccountsId);

        try {
            if (repository.existsById(tangibleId)) {

                repository.deleteById(tangibleId);

                smallFullService
                        .removeLink(companyAccountsId, SmallFullLinkType.TANGIBLE_ASSETS_NOTE, request);
                return new ResponseObject<>(ResponseStatus.UPDATED);
            } else {

                return new ResponseObject<>(ResponseStatus.NOT_FOUND);
            }
        } catch (MongoException e) {

            throw new DataException(e);
        }
    }

    @Override
    public String generateID(String companyAccountId) {

        return keyIdGenerator.generate(companyAccountId + "-" + ResourceName.TANGIBLE_ASSETS.getName());
    }

    public String getSelfLinkFromRestEntity(TangibleAssets entity) {

        return entity.getLinks().get(BasicLinkType.SELF.getLink());
    }

    private String generateSelfLink(Transaction transaction, String companyAccountId) {

        return transaction.getLinks().getSelf() + "/"
                + ResourceName.COMPANY_ACCOUNT.getName() + "/" + companyAccountId + "/"
                + ResourceName.SMALL_FULL.getName() + "/notes/"
                + ResourceName.TANGIBLE_ASSETS.getName();
    }

    private Map<String, String> createLinks(Transaction transaction, String companyAccountsId) {

        Map<String, String> map = new HashMap<>();
        map.put(BasicLinkType.SELF.getLink(), generateSelfLink(transaction, companyAccountsId));
        return map;
    }

    private void setMetadataOnRestObject(TangibleAssets rest, Transaction transaction,
                                         String companyAccountsId) {

        rest.setLinks(createLinks(transaction, companyAccountsId));
        rest.setEtag(GenerateEtagUtil.generateEtag());
        rest.setKind(Kind.TANGIBLE_ASSETS_NOTE.getValue());
    }
}
