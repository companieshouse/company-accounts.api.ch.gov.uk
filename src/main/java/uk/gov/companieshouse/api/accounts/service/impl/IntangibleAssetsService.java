package uk.gov.companieshouse.api.accounts.service.impl;


import com.mongodb.MongoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.GenerateEtagUtil;
import uk.gov.companieshouse.api.accounts.Kind;
import uk.gov.companieshouse.api.accounts.ResourceName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.BasicLinkType;
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;
import uk.gov.companieshouse.api.accounts.model.entity.notes.intangible.IntangibleAssetsEntity;
import uk.gov.companieshouse.api.accounts.model.entity.notes.tangible.TangibleAssetsEntity;
import uk.gov.companieshouse.api.accounts.model.rest.notes.intangible.IntangibleAssets;
import uk.gov.companieshouse.api.accounts.model.rest.notes.tangible.TangibleAssets;
import uk.gov.companieshouse.api.accounts.repository.IntangibleAssetsRepository;
import uk.gov.companieshouse.api.accounts.service.ResourceService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transformer.IntangibleAssetsTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Service
public class IntangibleAssetsService implements ResourceService<IntangibleAssets> {

    private IntangibleAssetsRepository repository;
    private IntangibleAssetsTransformer transformer;
    private SmallFullService smallFullService;
    private KeyIdGenerator keyIdGenerator;

    @Autowired
    public IntangibleAssetsService(IntangibleAssetsRepository repository, IntangibleAssetsTransformer transformer,
                                   SmallFullService smallFullService, KeyIdGenerator keyIdGenerator) {
        this.repository = repository;
        this.transformer = transformer;
        this.smallFullService = smallFullService;
        this.keyIdGenerator = keyIdGenerator;
    }




    @Override
    public ResponseObject<IntangibleAssets> create(IntangibleAssets rest, Transaction transaction, String companyAccountId, HttpServletRequest request) throws DataException {
        setMetadataOnRestObject(rest, transaction, companyAccountId);

        IntangibleAssetsEntity entity = transformer.transform(rest);
        entity.setId(generateID(companyAccountId));

        try {

            repository.insert(entity);
        } catch (DuplicateKeyException e) {

            return new ResponseObject<>(ResponseStatus.DUPLICATE_KEY_ERROR);
        } catch (MongoException e) {

            throw new DataException(e);
        }

        smallFullService.addLink(companyAccountId, SmallFullLinkType.INTANGIBLE_ASSETS_NOTE,
                getSelfLinkFromRestEntity(rest), request);

        return new ResponseObject<>(ResponseStatus.CREATED, rest);
    }

    @Override
    public ResponseObject<IntangibleAssets> update(IntangibleAssets rest, Transaction transaction, String companyAccountId, HttpServletRequest request) throws DataException {
        setMetadataOnRestObject(rest, transaction, companyAccountId);

        IntangibleAssetsEntity entity = transformer.transform(rest);
        entity.setId(generateID(companyAccountId));

        try {

            repository.save(entity);
        } catch (MongoException e) {

            throw new DataException(e);
        }
        return new ResponseObject<>(ResponseStatus.UPDATED, rest);
    }

    @Override
    public ResponseObject<IntangibleAssets> find(String companyAccountsId, HttpServletRequest request) throws DataException {
        IntangibleAssetsEntity entity;

        try {

            entity = repository.findById(generateID(companyAccountsId)).orElse(null);
        } catch (MongoException e) {

            throw new DataException(e);
        }

        if (entity == null) {
            return new ResponseObject<>(ResponseStatus.NOT_FOUND);
        }

        return new ResponseObject<>(ResponseStatus.FOUND, transformer.transform(entity));
    }

    @Override
    public ResponseObject<IntangibleAssets> delete(String companyAccountsId, HttpServletRequest request) throws DataException {
        String intangibleId = generateID(companyAccountsId);

        try {
            if (repository.existsById(intangibleId)) {

                repository.deleteById(intangibleId);

                smallFullService
                        .removeLink(companyAccountsId, SmallFullLinkType.INTANGIBLE_ASSETS_NOTE, request);
                return new ResponseObject<>(ResponseStatus.UPDATED);
            } else {

                return new ResponseObject<>(ResponseStatus.NOT_FOUND);
            }
        } catch (MongoException e) {

            throw new DataException(e);
        }
    }

    private String generateID(String companyAccountId) {

        return keyIdGenerator.generate(companyAccountId + "-" + ResourceName.INTANGIBLE_ASSETS.getName());
    }

    public String getSelfLinkFromRestEntity(IntangibleAssets entity) {

        return entity.getLinks().get(BasicLinkType.SELF.getLink());
    }

    private String generateSelfLink(Transaction transaction, String companyAccountId) {

        return transaction.getLinks().getSelf() + "/"
                + ResourceName.COMPANY_ACCOUNT.getName() + "/" + companyAccountId + "/"
                + ResourceName.SMALL_FULL.getName() + "/notes/"
                + ResourceName.INTANGIBLE_ASSETS.getName();
    }

    private Map<String, String> createLinks(Transaction transaction, String companyAccountsId) {

        Map<String, String> map = new HashMap<>();
        map.put(BasicLinkType.SELF.getLink(), generateSelfLink(transaction, companyAccountsId));
        return map;
    }

    private void setMetadataOnRestObject(IntangibleAssets rest, Transaction transaction,
                                         String companyAccountsId) {

        rest.setLinks(createLinks(transaction, companyAccountsId));
        rest.setEtag(GenerateEtagUtil.generateEtag());
        rest.setKind(Kind.INTANGIBLE_ASSETS_NOTE.getValue());
    }
}
