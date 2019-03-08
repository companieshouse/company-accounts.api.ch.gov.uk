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
import uk.gov.companieshouse.api.accounts.model.entity.notes.creditorswithinoneyear.CreditorsWithinOneYearEntity;
import uk.gov.companieshouse.api.accounts.model.rest.notes.creditorswithinoneyear.CreditorsWithinOneYear;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.repository.CreditorsWithinOneYearRepository;
import uk.gov.companieshouse.api.accounts.service.ResourceService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.transformer.CreditorsWithinOneYearTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;
import uk.gov.companieshouse.api.accounts.validation.CreditorsWithinOneYearValidator;

@Service
public class CreditorsWithinOneYearService implements ResourceService<CreditorsWithinOneYear> {

    private CreditorsWithinOneYearRepository repository;
    private CreditorsWithinOneYearTransformer transformer;
    private CreditorsWithinOneYearValidator validator;
    private KeyIdGenerator keyIdGenerator;
    private SmallFullService smallFullService;

    @Autowired
    public CreditorsWithinOneYearService(CreditorsWithinOneYearRepository repository,
            CreditorsWithinOneYearTransformer transformer,
            KeyIdGenerator keyIdGenerator,
            SmallFullService smallFullService, CreditorsWithinOneYearValidator validator) {

        this.repository = repository;
        this.transformer = transformer;
        this.keyIdGenerator = keyIdGenerator;
        this.smallFullService = smallFullService;
        this.validator = validator;
    }

    @Override
    public ResponseObject<CreditorsWithinOneYear> create(CreditorsWithinOneYear rest,
            Transaction transaction,
            String companyAccountId,
            HttpServletRequest request) throws DataException {


        Errors errors = validator.validateIfEmptyResource(rest, request, companyAccountId);

        if (!errors.hasErrors()) {

            errors = validator.validateCreditorsWithinOneYear(rest, transaction, companyAccountId, request);
        }
        if (errors.hasErrors()) {

            return new ResponseObject<>(ResponseStatus.VALIDATION_ERROR, errors);
        }

        setMetadataOnRestObject(rest, transaction, companyAccountId);

        CreditorsWithinOneYearEntity entity = transformer.transform(rest);
        entity.setId(generateID(companyAccountId));

        try {
            repository.insert(entity);
        } catch (DuplicateKeyException e) {
            return new ResponseObject<>(ResponseStatus.DUPLICATE_KEY_ERROR);
        } catch (MongoException e) {
            throw new DataException(e);
        }

        smallFullService.addLink(companyAccountId, SmallFullLinkType.CREDITORS_WITHIN_ONE_YEAR_NOTE,
                getSelfLinkFromCreditorsWithinOneYearEntity(entity), request);

        return new ResponseObject<>(ResponseStatus.CREATED, rest);
    }

    @Override
    public ResponseObject<CreditorsWithinOneYear> update(CreditorsWithinOneYear rest,
            Transaction transaction,
            String companyAccountId,
            HttpServletRequest request) throws DataException {

        Errors errors = validator.validateCreditorsWithinOneYear(rest, transaction, companyAccountId, request);

        if (errors.hasErrors()) {

            return new ResponseObject<>(ResponseStatus.VALIDATION_ERROR, errors);
        }

        setMetadataOnRestObject(rest, transaction, companyAccountId);

        CreditorsWithinOneYearEntity entity = transformer.transform(rest);
        entity.setId(generateID(companyAccountId));

        try {
            repository.save(entity);
        } catch (MongoException e) {
            throw new DataException(e);
        }

        return new ResponseObject<>(ResponseStatus.UPDATED, rest);
    }

    @Override
    public ResponseObject<CreditorsWithinOneYear> findById(String id,
            HttpServletRequest request) throws DataException {

        CreditorsWithinOneYearEntity entity;

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
    public ResponseObject<CreditorsWithinOneYear> delete(String companyAccountsId,
                                                             HttpServletRequest request) throws DataException {

        String creditorsWithinOneYearId = generateID(companyAccountsId);

        try {
            if (repository.existsById(creditorsWithinOneYearId)) {
                repository.deleteById(creditorsWithinOneYearId);

                smallFullService.removeLink(companyAccountsId,
                        SmallFullLinkType.CREDITORS_WITHIN_ONE_YEAR_NOTE, request);
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
        return keyIdGenerator.generate(companyAccountId + "-" + ResourceName.CREDITORS_WITHIN_ONE_YEAR.getName());
    }

    private void setMetadataOnRestObject(CreditorsWithinOneYear rest, Transaction transaction,
            String companyAccountsId) {

        rest.setLinks(createSelfLink(transaction, companyAccountsId));
        rest.setEtag(GenerateEtagUtil.generateEtag());
        rest.setKind(Kind.CREDITORS_DUE_WITHIN_ONE_YEAR_NOTE.getValue());
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
                + ResourceName.CREDITORS_WITHIN_ONE_YEAR.getName();
    }

    public String getSelfLinkFromCreditorsWithinOneYearEntity(CreditorsWithinOneYearEntity entity) {
        return entity.getData().getLinks().get(BasicLinkType.SELF.getLink());
    }
}
