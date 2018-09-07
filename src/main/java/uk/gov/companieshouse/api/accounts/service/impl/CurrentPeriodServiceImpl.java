package uk.gov.companieshouse.api.accounts.service.impl;

import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.GenerateEtagUtil;
import uk.gov.companieshouse.api.accounts.CompanyAccountsApplication;
import uk.gov.companieshouse.api.accounts.Kind;
import uk.gov.companieshouse.api.accounts.LinkType;
import uk.gov.companieshouse.api.accounts.ResourceName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.entity.CurrentPeriodDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.CurrentPeriodEntity;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.repository.CurrentPeriodRepository;
import uk.gov.companieshouse.api.accounts.service.CurrentPeriodService;
import uk.gov.companieshouse.api.accounts.service.SmallFullService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.transformer.CurrentPeriodTransformer;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Service
public class CurrentPeriodServiceImpl implements CurrentPeriodService {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(CompanyAccountsApplication.APPLICATION_NAME_SPACE);

    private CurrentPeriodRepository currentPeriodRepository;

    private CurrentPeriodTransformer currentPeriodTransformer;

    private SmallFullService smallFullService;

    private MessageDigest messageDigest;

    @Autowired
    public CurrentPeriodServiceImpl(
            CurrentPeriodRepository currentPeriodRepository,
            CurrentPeriodTransformer currentPeriodTransformer,
            SmallFullService smallFullService) {
        this.currentPeriodRepository = currentPeriodRepository;
        this.currentPeriodTransformer = currentPeriodTransformer;
        this.smallFullService = smallFullService;
    }

    @Override
    public ResponseObject<CurrentPeriod> create(CurrentPeriod currentPeriod,
            Transaction transaction,
            String companyAccountId, String requestId)
            throws DataException {

        String selfLink = createSelfLink(transaction, companyAccountId);
        initLinks(currentPeriod, selfLink);
        currentPeriod.setEtag(GenerateEtagUtil.generateEtag());
        currentPeriod.setKind(Kind.CURRENT_PERIOD.getValue());
        CurrentPeriodEntity currentPeriodEntity = currentPeriodTransformer.transform(currentPeriod);

        final Map<String, Object> debugMap = new HashMap<>();
        debugMap.put("transaction_id", transaction.getId());
        debugMap.put("company_accounts_id", companyAccountId);

        String id = generateID(companyAccountId);
        currentPeriodEntity.setId(id);
        debugMap.put("id", id);

        try {
            currentPeriodRepository.insert(currentPeriodEntity);
        } catch (DuplicateKeyException dke) {
            LOGGER.errorContext(requestId, dke, debugMap);
            return new ResponseObject<>(ResponseStatus.DUPLICATE_KEY_ERROR, null);
        } catch (MongoException me) {
            DataException dataException = new DataException(
                    "Failed to insert " + ResourceName.SMALL_FULL.getName(), me);
            LOGGER.errorContext(requestId, dataException, debugMap);
            throw dataException;
        }

        smallFullService.addLink(companyAccountId, LinkType.SMALL_FULL, selfLink, requestId);

        return new ResponseObject<>(ResponseStatus.CREATED, currentPeriod);
    }

    @Override
    public ResponseObject<CurrentPeriod> findById(String id, String requestId)
            throws DataException {
        CurrentPeriodEntity currentPeriodEntity;
        try {
            currentPeriodEntity = currentPeriodRepository.findById(id).orElse(null);
        } catch (MongoException me) {
            final Map<String, Object> debugMap = new HashMap<>();
            debugMap.put("id", id);
            DataException dataException = new DataException("Failed to find Current period", me);
            LOGGER.errorContext(requestId, dataException, debugMap);
            throw dataException;
        }

        if (currentPeriodEntity == null) {
            return new ResponseObject<>(ResponseStatus.NOT_FOUND);
        }
        CurrentPeriod currentPeriod = currentPeriodTransformer.transform(currentPeriodEntity);
        return new ResponseObject<>(ResponseStatus.FOUND, currentPeriod);
    }

    @Override
    public void addLink(String id, LinkType linkType, String link, String requestId)
            throws DataException {
        CurrentPeriodEntity currentPeriodEntity = currentPeriodRepository.findById(id)
                .orElseThrow(() -> new DataException(
                        "Failed to add get Current period entity to add link"));
        CurrentPeriodDataEntity currentPeriodDataEntity = currentPeriodEntity.getData();
        Map<String, String> map = currentPeriodDataEntity.getLinks();
        map.put(linkType.getLink(), link);
        currentPeriodDataEntity.setLinks(map);
        currentPeriodEntity.setData(currentPeriodDataEntity);
        try {
            currentPeriodRepository.save(currentPeriodEntity);
        } catch (MongoException me) {
            final Map<String, Object> debugMap = new HashMap<>();
            debugMap.put("id", id);
            debugMap.put("link", link);
            debugMap.put("link_type", linkType.getLink());

            DataException dataException = new DataException(
                    "Failed to add link to Small full", me);
            LOGGER.errorContext(requestId, dataException, debugMap);
            throw dataException;
        }
    }

    @Override
    public String generateID(String value) {
        String unencryptedId = value + "-" + ResourceName.CURRENT_PERIOD.getName();
        byte[] id = messageDigest.digest(
                unencryptedId.getBytes(StandardCharsets.UTF_8));
        return Base64.getUrlEncoder().encodeToString(id);
    }

    @Override
    public String createSelfLink(Transaction transaction, String companyAccountId) {
        return transaction.getLinks().get(LinkType.SELF.getLink()) + "/"
                + ResourceName.COMPANY_ACCOUNT.getName() + "/"
                + companyAccountId + "/" + ResourceName.SMALL_FULL.getName() + "/"
                + ResourceName.CURRENT_PERIOD.getName();
    }

    private void initLinks(CurrentPeriod currentPeriod, String link) {
        Map<String, String> map = new HashMap<>();
        map.put(LinkType.SELF.getLink(), link);
        currentPeriod.setLinks(map);
    }

    @Autowired
    public void setMessageDigest(MessageDigest messageDigest) {
        this.messageDigest = messageDigest;
    }
}