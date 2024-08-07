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
import uk.gov.companieshouse.api.accounts.links.PreviousPeriodLinkType;
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;
import uk.gov.companieshouse.api.accounts.model.entity.PreviousPeriodEntity;
import uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.repository.PreviousPeriodRepository;
import uk.gov.companieshouse.api.accounts.service.ParentService;
import uk.gov.companieshouse.api.accounts.service.ResourceService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transformer.PreviousPeriodTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;
import uk.gov.companieshouse.api.accounts.validation.PreviousPeriodValidator;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Service
public class PreviousPeriodService implements ResourceService<PreviousPeriod>, ParentService<PreviousPeriod,
        PreviousPeriodLinkType> {

    private PreviousPeriodRepository previousPeriodRepository;

    private PreviousPeriodTransformer previousPeriodTransformer;

    private PreviousPeriodValidator previousPeriodValidator;

    private SmallFullService smallFullService;

    private KeyIdGenerator keyIdGenerator;

    @Autowired
    public PreviousPeriodService(PreviousPeriodRepository previousPeriodRepository,
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
                                                 Transaction transaction,
                                                 String companyAccountId,
                                                 HttpServletRequest request) throws DataException {
        Errors errors = previousPeriodValidator.validatePreviousPeriod(previousPeriod, transaction);

        if (errors.hasErrors()) {
            return new ResponseObject<>(ResponseStatus.VALIDATION_ERROR, errors);
        }

        Map<String, String> links = createLinks(transaction, companyAccountId);

        populateMetadata(previousPeriod, links);
        PreviousPeriodEntity previousPeriodEntity = previousPeriodTransformer.transform(previousPeriod);

        previousPeriodEntity.setId(generateID(companyAccountId));

        try {
            previousPeriodRepository.insert(previousPeriodEntity);
        } catch (DuplicateKeyException dke) {
            return new ResponseObject<>(ResponseStatus.DUPLICATE_KEY_ERROR);
        } catch (MongoException e) {
            throw new DataException(e);
        }

        smallFullService.addLink(companyAccountId, SmallFullLinkType.PREVIOUS_PERIOD,
                    previousPeriod.getLinks().get(BasicLinkType.SELF.getLink()), request);

        return new ResponseObject<>(ResponseStatus.CREATED, previousPeriod);
    }

    @Override
    public ResponseObject<PreviousPeriod> find(String companyAccountsId,
                                               HttpServletRequest request) throws DataException {
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
    public ResponseObject<PreviousPeriod> delete(String companyAccountsId,
                                                 HttpServletRequest request) throws DataException {
        return null;
    }

    @Override
    public void addLink(String id,
                        PreviousPeriodLinkType linkType,
                        String link,
                        HttpServletRequest request) throws DataException {
        String previousPeriodId = generateID(id);
        try {
        PreviousPeriodEntity previousPeriodEntity = previousPeriodRepository.findById(previousPeriodId)
                .orElseThrow(() -> new DataException(
                        "Failed to get previous period entity to add link"));
        previousPeriodEntity.getData().getLinks().put(linkType.getLink(), link);

            previousPeriodRepository.save(previousPeriodEntity);
        } catch (MongoException e) {
            throw new DataException(e);
        }
    }

    @Override
    public void removeLink(String id,
                           PreviousPeriodLinkType linkType,
                           HttpServletRequest request) throws DataException {
        String smallFullId = generateID(id);
        PreviousPeriodEntity previousPeriodEntity = previousPeriodRepository.findById(smallFullId)
                .orElseThrow(() -> new DataException(
                        "Failed to get previous period entity from which to remove link"));
        previousPeriodEntity.getData().getLinks().remove(linkType.getLink());

        try {
            previousPeriodRepository.save(previousPeriodEntity);
        } catch (MongoException e) {
            throw new DataException(e);
        }
    }

    public ResponseObject<PreviousPeriod> update(PreviousPeriod rest,
                                                 Transaction transaction,
                                                 String companyAccountId,
                                                 HttpServletRequest request) throws DataException {
        Errors errors = previousPeriodValidator.validatePreviousPeriod(rest, transaction);

        if (errors.hasErrors()) {
            return new ResponseObject<>(ResponseStatus.VALIDATION_ERROR, errors);
        }

        try {
            PreviousPeriodEntity originalEntity = previousPeriodRepository.findById(generateID(companyAccountId))
                            .orElseThrow(() -> new DataException("No previous period found to update")); // We should never get here

            Map<String, String> links = originalEntity.getData().getLinks();
            populateMetadata(rest, links);

            PreviousPeriodEntity previousPeriodEntity = previousPeriodTransformer.transform(rest);
            previousPeriodEntity.setId(generateID(companyAccountId));

            previousPeriodRepository.save(previousPeriodEntity);
        } catch (MongoException e) {
            throw new DataException(e);
        }

        return new ResponseObject<>(ResponseStatus.UPDATED, rest);
    }

    private String generateID(String value) {
        return keyIdGenerator.generate(value + "-" + ResourceName.PREVIOUS_PERIOD.getName());
    }

    private void populateMetadata(PreviousPeriod previousPeriod, Map<String, String> links) {
        previousPeriod.setLinks(links);
        previousPeriod.setEtag(GenerateEtagUtil.generateEtag());
        previousPeriod.setKind(Kind.PREVIOUS_PERIOD.getValue());
    }

    private Map<String, String> createLinks(Transaction transaction, String companyAccountsId) {
        Map<String, String> links = new HashMap<>();
        links.put(BasicLinkType.SELF.getLink(), createSelfLink(transaction, companyAccountsId));
        return links;
    }

    public String createSelfLink(Transaction transaction, String companyAccountId) {
        return transaction.getLinks().getSelf() + "/"
                + ResourceName.COMPANY_ACCOUNT.getName() + "/"
                + companyAccountId + "/" + ResourceName.SMALL_FULL.getName() + "/"
                + ResourceName.PREVIOUS_PERIOD.getName();
    }
}
