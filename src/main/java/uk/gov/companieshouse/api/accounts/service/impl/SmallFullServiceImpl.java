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
import uk.gov.companieshouse.api.accounts.model.entity.SmallFullDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.SmallFullEntity;
import uk.gov.companieshouse.api.accounts.model.rest.SmallFull;
import uk.gov.companieshouse.api.accounts.repository.SmallFullRepository;
import uk.gov.companieshouse.api.accounts.service.CompanyAccountService;
import uk.gov.companieshouse.api.accounts.service.SmallFullService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.transformer.SmallFullTransformer;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Service
public class SmallFullServiceImpl implements
        SmallFullService {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(CompanyAccountsApplication.APPLICATION_NAME_SPACE);

    private SmallFullRepository smallFullRepository;

    private SmallFullTransformer smallFullTransformer;

    private CompanyAccountService companyAccountService;

    private MessageDigest messageDigest;

    @Autowired
    public SmallFullServiceImpl(SmallFullRepository smallFullRepository,
            SmallFullTransformer smallFullTransformer,
            CompanyAccountService companyAccountService) {
        this.smallFullRepository = smallFullRepository;
        this.smallFullTransformer = smallFullTransformer;
        this.companyAccountService = companyAccountService;
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

        try {
            baseEntity.setId(generateID(companyAccountId));
            smallFullRepository.insert(baseEntity);
            companyAccountService.addLink(companyAccountId, LinkType.SMALL_FULL, selfLink);
        } catch (DuplicateKeyException dke) {
            LOGGER.errorContext(requestId, dke, debugMap);
            return new ResponseObject<>(ResponseStatus.DUPLICATE_KEY_ERROR, null);
        } catch (MongoException me) {
            DataException dataException = new DataException(
                    "Failed to insert " + ResourceName.SMALL_FULL.getName(), me);
            LOGGER.errorContext(requestId, dataException, debugMap);
            throw dataException;
        }

        return new ResponseObject<>(ResponseStatus.CREATED, smallFull);
    }

    @Override
    public ResponseObject<SmallFull> findById(String id) {
        SmallFullEntity smallFullEntity = smallFullRepository.findById(id).orElse(null);
        if (smallFullEntity == null) {
            return new ResponseObject<>(ResponseStatus.NOT_FOUND);
        }
        SmallFull smallFull = smallFullTransformer.transform(smallFullEntity);
        return new ResponseObject<>(ResponseStatus.FOUND, smallFull);
    }

    @Override
    public String generateID(String value) {
        String unencryptedId = value + "-" + ResourceName.SMALL_FULL.getName();
        byte[] id = messageDigest.digest(
                unencryptedId.getBytes(StandardCharsets.UTF_8));
        return Base64.getUrlEncoder().encodeToString(id);
    }

    @Override
    public void initLinks(SmallFull smallFull, String link) {
        Map<String, String> map = new HashMap<>();
        map.put(LinkType.SELF.getLink(), link);
        smallFull.setLinks(map);
    }

    @Override
    public String createSelfLink(Transaction transaction, String companyAccountId) {
        return transaction.getLinks().get(LinkType.SELF.getLink()) + "/company-account/"
                + companyAccountId + "/" + ResourceName.SMALL_FULL.getName();
    }

    @Override
    public void addLink(String id, LinkType linkType, String link) {
        SmallFullEntity smallFullEntity = smallFullRepository.findById(id)
                .orElseThrow(() -> new MongoException(
                        "Failed to add link of type to Small full entity"));
        SmallFullDataEntity smallFullDataEntity = smallFullEntity.getData();
        Map<String, String> map = smallFullDataEntity.getLinks();
        map.put(linkType.getLink(), link);
        smallFullDataEntity.setLinks(map);
        smallFullEntity.setData(smallFullDataEntity);
        smallFullRepository.save(smallFullEntity);
    }

    @Autowired
    public void setMessageDigest(MessageDigest messageDigest) {
        this.messageDigest = messageDigest;
    }
}