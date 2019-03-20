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
import uk.gov.companieshouse.api.accounts.model.entity.PreviousPeriodEntity;
import uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.repository.PreviousPeriodRepository;
import uk.gov.companieshouse.api.accounts.service.ResourceService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.transformer.PreviousPeriodTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;
import uk.gov.companieshouse.api.accounts.validation.PreviousPeriodValidator;

@Service
public class PreviousPeriodService implements ResourceService<PreviousPeriod> {

    private PreviousPeriodRepository previousPeriodRepository;

    private PreviousPeriodTransformer previousPeriodTransformer;

    private PreviousPeriodValidator previousPeriodValidator;

    private SmallFullService smallFullService;

    private KeyIdGenerator keyIdGenerator;

    @Autowired
    public PreviousPeriodService(
        PreviousPeriodRepository previousPeriodRepository,
        PreviousPeriodTransformer previousPeriodTransformer,
        PreviousPeriodValidator previousPeriodValidator,
        SmallFullService smallFullService,
        KeyIdGenerator keyIdGenerator) {
        this.previousPeriodRepository = previousPeriodRepository;
        this.previousPeriodTransformer = previousPeriodTransformer;
        this.previousPeriodValidator = previousPeriodValidator;
        this.smallFullService = smallFullService;
        this.keyIdGenerator = keyIdGenerator;
    }

    @Override
    public ResponseObject<PreviousPeriod> create(PreviousPeriod previousPeriod,
        Transaction transaction, String companyAccountId, HttpServletRequest request)
        throws DataException {

        Errors errors = previousPeriodValidator.validatePreviousPeriod(previousPeriod, transaction);

        if (errors.hasErrors()) {
            return new ResponseObject<>(ResponseStatus.VALIDATION_ERROR, errors);
        }

        String selfLink = createSelfLink(transaction, companyAccountId);
        initLinks(previousPeriod, selfLink);
        previousPeriod.setEtag(GenerateEtagUtil.generateEtag());
        previousPeriod.setKind(Kind.PREVIOUS_PERIOD.getValue());
        PreviousPeriodEntity previousPeriodEntity = previousPeriodTransformer
            .transform(previousPeriod);

        previousPeriodEntity.setId(generateID(companyAccountId));

        try {
            previousPeriodRepository.insert(previousPeriodEntity);
        } catch (DuplicateKeyException dke) {
            return new ResponseObject<>(ResponseStatus.DUPLICATE_KEY_ERROR);
        } catch (MongoException e) {
            throw new DataException(e);
        }

        smallFullService
            .addLink(companyAccountId, SmallFullLinkType.PREVIOUS_PERIOD, selfLink, request);

        return new ResponseObject<>(ResponseStatus.CREATED, previousPeriod);
    }

    @Override
    public ResponseObject<PreviousPeriod> find(String companyAccountsId, HttpServletRequest request)
        throws DataException {

        PreviousPeriodEntity previousPeriodEntity;

        try {
            previousPeriodEntity = previousPeriodRepository.findById(generateID(companyAccountsId)).orElse(null);
        } catch (MongoException e) {
            throw new DataException(e);
        }

        if (previousPeriodEntity == null) {
            return new ResponseObject<>(ResponseStatus.NOT_FOUND);
        }

        PreviousPeriod previousPeriod = previousPeriodTransformer.transform(previousPeriodEntity);
        return new ResponseObject<>(ResponseStatus.FOUND, previousPeriod);
    }

    @Override
    public ResponseObject<PreviousPeriod> delete(String companyAccountsId, HttpServletRequest request) throws DataException {
        return null;
    }

    public ResponseObject<PreviousPeriod> update(PreviousPeriod rest, Transaction transaction,
        String companyAccountId, HttpServletRequest request) throws DataException {

        Errors errors = previousPeriodValidator.validatePreviousPeriod(rest, transaction);

        if (errors.hasErrors()) {
            return new ResponseObject<>(ResponseStatus.VALIDATION_ERROR, errors);
        }

        populateMetadata(rest, transaction, companyAccountId);
        PreviousPeriodEntity previousPeriodEntity = previousPeriodTransformer.transform(rest);
        previousPeriodEntity.setId(generateID(companyAccountId));

        try {
            previousPeriodRepository.save(previousPeriodEntity);
        } catch (MongoException e) {
            throw new DataException(e);
        }

        return new ResponseObject<>(ResponseStatus.UPDATED, rest);
    }

    private String generateID(String value) {
        return keyIdGenerator.generate(value + "-" + ResourceName.PREVIOUS_PERIOD.getName());
    }

    public String createSelfLink(Transaction transaction, String companyAccountId) {
        return transaction.getLinks().getSelf() + "/"
            + ResourceName.COMPANY_ACCOUNT.getName() + "/"
            + companyAccountId + "/" + ResourceName.SMALL_FULL.getName() + "/"
            + ResourceName.PREVIOUS_PERIOD.getName();
    }

    private void populateMetadata(PreviousPeriod previousPeriod, Transaction transaction,
        String companyAccountId) {
        Map<String, String> map = new HashMap<>();
        map.put(BasicLinkType.SELF.getLink(), createSelfLink(transaction, companyAccountId));

        previousPeriod.setLinks(map);
        previousPeriod.setEtag(GenerateEtagUtil.generateEtag());
        previousPeriod.setKind(Kind.PREVIOUS_PERIOD.getValue());
    }

    private void initLinks(PreviousPeriod previousPeriod, String link) {
        Map<String, String> map = new HashMap<>();
        map.put(BasicLinkType.SELF.getLink(), link);
        previousPeriod.setLinks(map);
    }
}
