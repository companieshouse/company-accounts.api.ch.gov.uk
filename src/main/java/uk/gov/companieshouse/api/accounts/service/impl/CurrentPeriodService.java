package uk.gov.companieshouse.api.accounts.service.impl;

import com.mongodb.MongoException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.GenerateEtagUtil;
import uk.gov.companieshouse.api.accounts.CompanyAccountsApplication;
import uk.gov.companieshouse.api.accounts.Kind;
import uk.gov.companieshouse.api.accounts.ResourceName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.BasicLinkType;
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;
import uk.gov.companieshouse.api.accounts.links.TransactionLinkType;
import uk.gov.companieshouse.api.accounts.model.entity.CurrentPeriodEntity;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.repository.CurrentPeriodRepository;
import uk.gov.companieshouse.api.accounts.service.ResourceService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.transformer.CurrentPeriodTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;
import uk.gov.companieshouse.api.accounts.validation.CurrentPeriodValidator;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Service
public class CurrentPeriodService implements
    ResourceService<CurrentPeriod> {

    private static final Logger LOGGER = LoggerFactory
        .getLogger(CompanyAccountsApplication.APPLICATION_NAME_SPACE);

    private CurrentPeriodRepository currentPeriodRepository;

    private CurrentPeriodTransformer currentPeriodTransformer;

    private CurrentPeriodValidator currentPeriodValidator;

    private SmallFullService smallFullService;

    private KeyIdGenerator keyIdGenerator;

    @Autowired
    public CurrentPeriodService(
        CurrentPeriodRepository currentPeriodRepository,
        CurrentPeriodTransformer currentPeriodTransformer,
        CurrentPeriodValidator currentPeriodValidator,
        SmallFullService smallFullService,
        KeyIdGenerator keyIdGenerator) {
        this.currentPeriodRepository = currentPeriodRepository;
        this.currentPeriodTransformer = currentPeriodTransformer;
        this.currentPeriodValidator = currentPeriodValidator;
        this.smallFullService = smallFullService;
        this.keyIdGenerator = keyIdGenerator;
    }

    @Override
    public ResponseObject<CurrentPeriod> create(CurrentPeriod currentPeriod,
        Transaction transaction, String companyAccountId, HttpServletRequest request)
        throws DataException {

        final Map<String, Object> debugMap = new HashMap<>();
        debugMap.put("transaction_id", transaction.getId());
        debugMap.put("company_accounts_id", companyAccountId);
        Errors errors = currentPeriodValidator.validateCurrentPeriod(currentPeriod);

        if (errors.hasErrors()) {
            DataException dataException = new DataException(
                "Failed to validate " + ResourceName.CURRENT_PERIOD.getName());
            LOGGER.errorRequest(request, dataException, debugMap);
            return new ResponseObject<>(ResponseStatus.VALIDATION_ERROR, errors);
        }

        populateMetadata(currentPeriod, transaction, companyAccountId);
        CurrentPeriodEntity currentPeriodEntity = currentPeriodTransformer.transform(currentPeriod);

        String id = generateID(companyAccountId);
        currentPeriodEntity.setId(id);
        debugMap.put("id", id);

        try {
            currentPeriodRepository.insert(currentPeriodEntity);
        } catch (DuplicateKeyException dke) {
            LOGGER.errorRequest(request, dke, debugMap);
            return new ResponseObject<>(ResponseStatus.DUPLICATE_KEY_ERROR);
        } catch (MongoException me) {
            DataException dataException = new DataException(
                "Failed to insert " + ResourceName.SMALL_FULL.getName(), me);
            LOGGER.errorRequest(request, dataException, debugMap);
            throw dataException;
        }

        smallFullService
            .addLink(companyAccountId, SmallFullLinkType.CURRENT_PERIOD,
                currentPeriod.getLinks().get(BasicLinkType.SELF.getLink()), request);

        return new ResponseObject<>(ResponseStatus.CREATED, currentPeriod);
    }

    @Override
    public ResponseObject<CurrentPeriod> update(CurrentPeriod rest, Transaction transaction,
        String companyAccountId, HttpServletRequest request) throws DataException {

        String id = generateID(companyAccountId);

        final Map<String, Object> debugMap = new HashMap<>();
        debugMap.put("transaction_id", transaction.getId());
        debugMap.put("company_accounts_id", companyAccountId);
        debugMap.put("id", id);
        Errors errors = currentPeriodValidator.validateCurrentPeriod(rest);

        if (errors.hasErrors()) {
            DataException dataException = new DataException(
                "Failed to validate " + ResourceName.CURRENT_PERIOD.getName());
            LOGGER.errorRequest(request, dataException, debugMap);
            return new ResponseObject<>(ResponseStatus.VALIDATION_ERROR, errors);
        }

        populateMetadata(rest, transaction, companyAccountId);
        CurrentPeriodEntity currentPeriodEntity = currentPeriodTransformer.transform(rest);
        currentPeriodEntity.setId(id);

        try {
            currentPeriodRepository.save(currentPeriodEntity);
        } catch (MongoException me) {
            DataException dataException = new DataException(
                "Failed to update " + ResourceName.CURRENT_PERIOD.getName(), me);
            LOGGER.errorRequest(request, dataException, debugMap);
            throw dataException;
        }

        return new ResponseObject<>(ResponseStatus.UPDATED, rest);
    }

    @Override
    public ResponseObject<CurrentPeriod> findById(String id, HttpServletRequest request)
        throws DataException {

        CurrentPeriodEntity currentPeriodEntity;
        try {
            currentPeriodEntity = currentPeriodRepository.findById(id).orElse(null);
        } catch (MongoException me) {
            final Map<String, Object> debugMap = new HashMap<>();
            debugMap.put("id", id);
            DataException dataException = new DataException("Failed to find Current period", me);
            LOGGER.errorRequest(request, dataException, debugMap);
            throw dataException;
        }

        if (currentPeriodEntity == null) {
            return new ResponseObject<>(ResponseStatus.NOT_FOUND);
        }
        CurrentPeriod currentPeriod = currentPeriodTransformer.transform(currentPeriodEntity);
        return new ResponseObject<>(ResponseStatus.FOUND, currentPeriod);
    }

    @Override
    public ResponseObject<CurrentPeriod> delete(String companyAccountsId, HttpServletRequest request) throws DataException {
        return null;
    }

    @Override
    public String generateID(String value) {
        return keyIdGenerator.generate(value + "-" + ResourceName.CURRENT_PERIOD.getName());
    }

    private void populateMetadata(CurrentPeriod currentPeriod, Transaction transaction,
        String companyAccountId) {
        Map<String, String> map = new HashMap<>();
        map.put(BasicLinkType.SELF.getLink(), createSelfLink(transaction, companyAccountId));

        currentPeriod.setLinks(map);
        currentPeriod.setEtag(GenerateEtagUtil.generateEtag());
        currentPeriod.setKind(Kind.CURRENT_PERIOD.getValue());
    }

    private String createSelfLink(Transaction transaction, String companyAccountId) {
        return transaction.getLinks().getSelf() + "/"
            + ResourceName.COMPANY_ACCOUNT.getName() + "/"
            + companyAccountId + "/" + ResourceName.SMALL_FULL.getName() + "/"
            + ResourceName.CURRENT_PERIOD.getName();
    }
}