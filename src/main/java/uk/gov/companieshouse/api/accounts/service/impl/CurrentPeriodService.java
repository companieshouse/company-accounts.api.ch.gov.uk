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
import uk.gov.companieshouse.api.accounts.links.CurrentPeriodLinkType;
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;
import uk.gov.companieshouse.api.accounts.model.entity.CurrentPeriodEntity;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.repository.CurrentPeriodRepository;
import uk.gov.companieshouse.api.accounts.service.ParentService;
import uk.gov.companieshouse.api.accounts.service.ResourceService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transformer.CurrentPeriodTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;
import uk.gov.companieshouse.api.accounts.validation.CurrentPeriodValidator;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Service
public class CurrentPeriodService implements
    ResourceService<CurrentPeriod>, ParentService<CurrentPeriod,
        CurrentPeriodLinkType> {

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

        Errors errors = currentPeriodValidator.validateCurrentPeriod(currentPeriod, transaction);

        if (errors.hasErrors()) {
            return new ResponseObject<>(ResponseStatus.VALIDATION_ERROR, errors);
        }

        populateMetadata(currentPeriod, transaction, companyAccountId);
        CurrentPeriodEntity currentPeriodEntity = currentPeriodTransformer.transform(currentPeriod);

        currentPeriodEntity.setId(generateID(companyAccountId));

        try {
            currentPeriodRepository.insert(currentPeriodEntity);
        } catch (DuplicateKeyException dke) {
            return new ResponseObject<>(ResponseStatus.DUPLICATE_KEY_ERROR);
        } catch (MongoException e) {
            throw new DataException(e);
        }

        smallFullService
            .addLink(companyAccountId, SmallFullLinkType.CURRENT_PERIOD,
                currentPeriod.getLinks().get(BasicLinkType.SELF.getLink()), request);

        return new ResponseObject<>(ResponseStatus.CREATED, currentPeriod);
    }

    @Override
    public ResponseObject<CurrentPeriod> update(CurrentPeriod rest, Transaction transaction,
        String companyAccountId, HttpServletRequest request) throws DataException {

        Errors errors = currentPeriodValidator.validateCurrentPeriod(rest, transaction);

        if (errors.hasErrors()) {
            return new ResponseObject<>(ResponseStatus.VALIDATION_ERROR, errors);
        }

        populateMetadata(rest, transaction, companyAccountId);
        CurrentPeriodEntity currentPeriodEntity = currentPeriodTransformer.transform(rest);
        currentPeriodEntity.setId(generateID(companyAccountId));

        try {
            currentPeriodRepository.save(currentPeriodEntity);
        } catch (MongoException e) {
            throw new DataException(e);
        }

        return new ResponseObject<>(ResponseStatus.UPDATED, rest);
    }

    @Override
    public ResponseObject<CurrentPeriod> find(String companyAccountsId, HttpServletRequest request)
        throws DataException {

        CurrentPeriodEntity currentPeriodEntity;
        try {
            currentPeriodEntity = currentPeriodRepository.findById(generateID(companyAccountsId)).orElse(null);
        } catch (MongoException e) {
            throw new DataException(e);
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
    public void addLink(String id, CurrentPeriodLinkType linkType, String link, HttpServletRequest request) throws DataException {

        String smallFullId = generateID(id);
        try {
        CurrentPeriodEntity currentPeriodEntity = currentPeriodRepository.findById(smallFullId)
                .orElseThrow(() -> new DataException(
                        "Failed to get current period entity to add link"));
        currentPeriodEntity.getData().getLinks().put(linkType.getLink(), link);


            currentPeriodRepository.save(currentPeriodEntity);
        } catch (MongoException e) {
            throw new DataException(e);
        }

    }

    @Override
    public void removeLink(String id, CurrentPeriodLinkType linkType, HttpServletRequest request) throws DataException {

        String currentPeriodId = generateID(id);
        CurrentPeriodEntity currentPeriodEntity = currentPeriodRepository.findById(currentPeriodId)
                .orElseThrow(() -> new DataException(
                        "Failed to get current period entity from which to remove link"));
        currentPeriodEntity.getData().getLinks().remove(linkType.getLink());

        try {
            currentPeriodRepository.save(currentPeriodEntity);
        } catch (MongoException e) {
            throw new DataException(e);
        }
    }

    private String generateID(String value) {
        return keyIdGenerator.generate(value + "-" + ResourceName.CURRENT_PERIOD.getName());
    }

    private void populateMetadata(CurrentPeriod currentPeriod, Transaction transaction,
        String companyAccountId) {

        if (currentPeriod.getLinks() != null) {
            Map<String, String> map = new HashMap<>();
            map.put(BasicLinkType.SELF.getLink(), createSelfLink(transaction, companyAccountId));

            currentPeriod.setLinks(map);
        }
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