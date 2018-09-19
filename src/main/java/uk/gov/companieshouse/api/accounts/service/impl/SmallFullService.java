package uk.gov.companieshouse.api.accounts.service.impl;

import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoException;
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
import uk.gov.companieshouse.api.accounts.model.entity.SmallFullEntity;
import uk.gov.companieshouse.api.accounts.model.rest.SmallFull;
import uk.gov.companieshouse.api.accounts.repository.SmallFullRepository;
import uk.gov.companieshouse.api.accounts.service.ParentService;
import uk.gov.companieshouse.api.accounts.service.CompanyAccountService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.transformer.SmallFullTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Service
public class SmallFullService implements
    ParentService<SmallFull> {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(CompanyAccountsApplication.APPLICATION_NAME_SPACE);

    private SmallFullRepository smallFullRepository;

    private SmallFullTransformer smallFullTransformer;

    private CompanyAccountService companyAccountService;

    private KeyIdGenerator keyIdGenerator;

    @Autowired
    public SmallFullService(SmallFullRepository smallFullRepository,
            SmallFullTransformer smallFullTransformer,
            CompanyAccountService companyAccountService,
            KeyIdGenerator keyIdGenerator) {
        this.smallFullRepository = smallFullRepository;
        this.smallFullTransformer = smallFullTransformer;
        this.companyAccountService = companyAccountService;
        this.keyIdGenerator = keyIdGenerator;
    }

    @Override
    public ResponseObject<SmallFull> create(SmallFull smallFull, Transaction transaction,
            String companyAccountId, String requestId)
            throws DataException {
        String selfLink = createSelfLink(transaction, companyAccountId);
        initLinks(smallFull, selfLink);
        smallFull.setEtag(GenerateEtagUtil.generateEtag());
        smallFull.setKind(Kind.SMALL_FULL_ACCOUNT.getValue());
        SmallFullEntity baseEntity = smallFullTransformer.transform(smallFull);

        final Map<String, Object> debugMap = new HashMap<>();
        debugMap.put("transaction_id", transaction.getId());
        debugMap.put("company_accounts_id", companyAccountId);

        String id = generateID(companyAccountId);
        baseEntity.setId(id);
        debugMap.put("id", id);

        try {
            smallFullRepository.insert(baseEntity);
        } catch (DuplicateKeyException dke) {
            LOGGER.errorContext(requestId, dke, debugMap);
            return new ResponseObject<>(ResponseStatus.DUPLICATE_KEY_ERROR, null);
        } catch (MongoException me) {
            DataException dataException = new DataException(
                    "Failed to insert " + ResourceName.SMALL_FULL.getName(), me);
            LOGGER.errorContext(requestId, dataException, debugMap);
            throw dataException;
        }

        companyAccountService.addLink(companyAccountId, LinkType.SMALL_FULL, selfLink);

        return new ResponseObject<>(ResponseStatus.CREATED, smallFull);
    }

    @Override
    public ResponseObject<SmallFull> findById(String id, String requestId) throws DataException {
        SmallFullEntity smallFullEntity;
        try {
            smallFullEntity = smallFullRepository.findById(id).orElse(null);
        } catch (MongoException me) {
            final Map<String, Object> debugMap = new HashMap<>();
            debugMap.put("id", id);
            DataException dataException = new DataException("Failed to find Small full entity", me);
            LOGGER.errorContext(requestId, dataException, debugMap);
            throw dataException;
        }

        if (smallFullEntity == null) {
            return new ResponseObject<>(ResponseStatus.NOT_FOUND);
        }

        SmallFull smallFull = smallFullTransformer.transform(smallFullEntity);
        return new ResponseObject<>(ResponseStatus.FOUND, smallFull);
    }

    @Override
    public void addLink(String id, LinkType linkType, String link, String requestId)
            throws DataException {
        String smallFullId = generateID(id);
        SmallFullEntity smallFullEntity = smallFullRepository.findById(smallFullId)
                .orElseThrow(() -> new DataException(
                        "Failed to add get Small full entity to add link"));
        smallFullEntity.getData().getLinks().put(linkType.getLink(), link);

        try {
            smallFullRepository.save(smallFullEntity);
        } catch (MongoException me) {
            final Map<String, Object> debugMap = new HashMap<>();
            debugMap.put("company_account_id", id);
            debugMap.put("id", smallFullId);
            debugMap.put("link", link);
            debugMap.put("link_type", linkType.getLink());

            DataException dataException = new DataException("Failed to add link to Small full", me);
            LOGGER.errorContext(requestId, dataException, debugMap);
            throw dataException;
        }
    }

    @Override
    public String generateID(String value) {
        return keyIdGenerator.generate(value + "-" + ResourceName.SMALL_FULL.getName());
    }

    public String createSelfLink(Transaction transaction, String companyAccountId) {
        return transaction.getLinks().get(LinkType.SELF.getLink()) + "/"
                + ResourceName.COMPANY_ACCOUNT.getName() + "/"
                + companyAccountId + "/" + ResourceName.SMALL_FULL.getName();
    }

    private void initLinks(SmallFull smallFull, String link) {
        Map<String, String> map = new HashMap<>();
        map.put(LinkType.SELF.getLink(), link);
        smallFull.setLinks(map);
    }
}