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
import uk.gov.companieshouse.api.accounts.model.entity.notes.fixedassetsinvestments.FixedAssetsInvestmentsEntity;
import uk.gov.companieshouse.api.accounts.model.rest.notes.fixedassetsinvestments.FixedAssetsInvestments;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.repository.FixedAssetsInvestmentsRepository;
import uk.gov.companieshouse.api.accounts.service.ResourceService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.validation.FixedAssetsInvestmentsValidator;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.transformer.FixedAssetsInvestmentsTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;

@Service
public class FixedAssetsInvestmentsService implements ResourceService<FixedAssetsInvestments> {

    private FixedAssetsInvestmentsRepository repository;
    private FixedAssetsInvestmentsTransformer transformer;
    private KeyIdGenerator keyIdGenerator;
    private SmallFullService smallFullService;
    private FixedAssetsInvestmentsValidator validator;

    @Autowired
    public FixedAssetsInvestmentsService(FixedAssetsInvestmentsRepository repository,
            FixedAssetsInvestmentsTransformer transformer,
            KeyIdGenerator keyIdGenerator,
            SmallFullService smallFullService, FixedAssetsInvestmentsValidator validator) {

        this.repository = repository;
        this.transformer = transformer;
        this.keyIdGenerator = keyIdGenerator;
        this.smallFullService = smallFullService;
        this.validator = validator;
    }

    @Override
    public ResponseObject<FixedAssetsInvestments> create(FixedAssetsInvestments rest,
            Transaction transaction,
            String companyAccountId,
            HttpServletRequest request) throws DataException {

        Errors errors = validator.validateSubmission(rest, transaction, companyAccountId, request);

        if (errors.hasErrors()) {

            return new ResponseObject<>(ResponseStatus.VALIDATION_ERROR, errors);
        }

        setMetadataOnRestObject(rest, transaction, companyAccountId);

        FixedAssetsInvestmentsEntity entity = transformer.transform(rest);
        entity.setId(generateID(companyAccountId));

        try {
            repository.insert(entity);
        } catch (DuplicateKeyException e) {
            return new ResponseObject<>(ResponseStatus.DUPLICATE_KEY_ERROR);
        } catch (MongoException e) {
            throw new DataException(e);
        }

        smallFullService.addLink(companyAccountId, SmallFullLinkType.FIXED_ASSETS_INVESTMENTS_NOTE,
                getSelfLinkFromFixedAssetsInvestmentsEntity(entity), request);

        return new ResponseObject<>(ResponseStatus.CREATED, rest);
    }

    @Override
    public ResponseObject<FixedAssetsInvestments> update(FixedAssetsInvestments rest,
            Transaction transaction,
            String companyAccountId,
            HttpServletRequest request) throws DataException {

        Errors errors = validator.validateSubmission(rest, transaction, companyAccountId, request);

        if (errors.hasErrors()) {

            return new ResponseObject<>(ResponseStatus.VALIDATION_ERROR, errors);
        }
        setMetadataOnRestObject(rest, transaction, companyAccountId);

        FixedAssetsInvestmentsEntity entity = transformer.transform(rest);
        entity.setId(generateID(companyAccountId));

        try {
            repository.save(entity);
        } catch (MongoException e) {
            throw new DataException(e);
        }

        return new ResponseObject<>(ResponseStatus.UPDATED, rest);
    }

    @Override
    public ResponseObject<FixedAssetsInvestments> find(String companyAccountsId,
            HttpServletRequest request) throws DataException {

        FixedAssetsInvestmentsEntity entity;

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
    public ResponseObject<FixedAssetsInvestments> delete(String companyAccountsId,
            HttpServletRequest request) throws DataException {

        String fixedAssetsInvestmentsId = generateID(companyAccountsId);

        try {
            if (repository.existsById(fixedAssetsInvestmentsId)) {
                repository.deleteById(fixedAssetsInvestmentsId);

                smallFullService.removeLink(companyAccountsId,
                        SmallFullLinkType.FIXED_ASSETS_INVESTMENTS_NOTE, request);
                return new ResponseObject<>(ResponseStatus.UPDATED);
            } else {
                return new ResponseObject<>(ResponseStatus.NOT_FOUND);
            }
        } catch (MongoException e) {
            throw new DataException(e);
        }
    }

    private String generateID(String companyAccountId) {
        return keyIdGenerator.generate(companyAccountId + "-" + ResourceName.FIXED_ASSETS_INVESTMENTS.getName());
    }

    private void setMetadataOnRestObject(FixedAssetsInvestments rest, Transaction transaction,
            String companyAccountsId) {

        rest.setLinks(createSelfLink(transaction, companyAccountsId));
        rest.setEtag(GenerateEtagUtil.generateEtag());
        rest.setKind(Kind.FIXED_ASSETS_INVESTMENTS_NOTE.getValue());
    }

    private Map<String, String> createSelfLink(Transaction transaction, String companyAccountsId) {

        Map<String, String> map = new HashMap<>();
        map.put(BasicLinkType.SELF.getLink(), generateSelfLink(transaction, companyAccountsId));
        return map;
    }

    private String generateSelfLink(Transaction transaction, String companyAccountId) {

        return transaction.getLinks().getSelf() + "/"
                + ResourceName.COMPANY_ACCOUNT.getName() + "/"
                + companyAccountId + "/" + ResourceName.SMALL_FULL.getName() + "/notes/"
                + ResourceName.FIXED_ASSETS_INVESTMENTS.getName();
    }

    public String getSelfLinkFromFixedAssetsInvestmentsEntity(FixedAssetsInvestmentsEntity entity) {
        return entity.getData().getLinks().get(BasicLinkType.SELF.getLink());
    }
}
