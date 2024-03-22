package uk.gov.companieshouse.api.accounts.service.impl;

import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoException;

import uk.gov.companieshouse.GenerateEtagUtil;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.accounts.Kind;
import uk.gov.companieshouse.api.accounts.ResourceName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.exception.PatchException;
import uk.gov.companieshouse.api.accounts.exception.ServiceException;
import uk.gov.companieshouse.api.accounts.links.CompanyAccountLinkType;
import uk.gov.companieshouse.api.accounts.links.TransactionLinkType;
import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountEntity;
import uk.gov.companieshouse.api.accounts.model.rest.CompanyAccount;
import uk.gov.companieshouse.api.accounts.repository.CompanyAccountRepository;
import uk.gov.companieshouse.api.accounts.sdk.ApiClientService;
import uk.gov.companieshouse.api.accounts.service.CompanyAccountService;
import uk.gov.companieshouse.api.accounts.service.TransactionService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transformer.CompanyAccountTransformer;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.transaction.Resource;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.sdk.manager.ApiSdkManager;

@Service
public class CompanyAccountServiceImpl implements CompanyAccountService {

    @Autowired
    private CompanyAccountRepository companyAccountRepository;

    @Autowired
    private CompanyAccountTransformer companyAccountTransformer;

    @Autowired
    private ApiClientService apiClientService;

    @Autowired
    private TransactionService transactionService;

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseObject<CompanyAccount> create(CompanyAccount companyAccount,
                                                 Transaction transaction,
                                                 HttpServletRequest request) throws PatchException, DataException {
        try {
            String id = generateID();
            setMetadataOnRestObject(companyAccount, transaction, id);

            CompanyAccountEntity companyAccountEntity = companyAccountTransformer.transform(companyAccount);
            companyAccountEntity.setId(id);

            companyAccountRepository.insert(companyAccountEntity);

            InternalApiClient internalApiClient = apiClientService.getInternalApiClient(
                    request.getHeader(ApiSdkManager.getEricPassthroughTokenHeader()));

            boolean isPayableTransaction = !transactionService.getPayableResources(transaction).isEmpty();

            transaction.setResources(createTransactionResourceMap(companyAccount, isPayableTransaction));

            internalApiClient.privateTransaction()
                    .patch("/private/transactions/" + transaction.getId(), transaction).execute();

        } catch (DuplicateKeyException dke) {
            return new ResponseObject<>(ResponseStatus.DUPLICATE_KEY_ERROR);
        } catch (ServiceException | MongoException e) {
            throw new DataException(e);
        } catch (IOException | URIValidationException e) {
            throw new PatchException("Failed to patch transaction", e);
        }

        return new ResponseObject<>(ResponseStatus.CREATED, companyAccount);
    }

    @Override
    public ResponseObject<CompanyAccount> findById(String id, HttpServletRequest request) throws DataException {

        CompanyAccountEntity companyAccountEntity;

        try {
            companyAccountEntity = companyAccountRepository.findById(id).orElse(null);
        } catch (MongoException e) {
            throw new DataException(e);
        }

        if (companyAccountEntity == null) {
            return new ResponseObject<>(ResponseStatus.NOT_FOUND);
        }

        CompanyAccount companyAccount = companyAccountTransformer.transform(companyAccountEntity);
        return new ResponseObject<>(ResponseStatus.FOUND, companyAccount);
    }

    @Override
    public void addLink(String id, CompanyAccountLinkType linkType, String link) {
        CompanyAccountEntity companyAccountEntity = companyAccountRepository.findById(id)
                .orElseThrow(() -> new MongoException(
                        "Failed to add link to Company account entity"));

        CompanyAccountDataEntity companyAccountDataEntity = companyAccountEntity.getData();
        Map<String, String> map = companyAccountDataEntity.getLinks();
        map.put(linkType.getLink(), link);

        companyAccountDataEntity.setLinks(map);
        companyAccountRepository.save(companyAccountEntity);
    }

    @Override
    public void removeLink(String id, CompanyAccountLinkType linkType) {
        CompanyAccountEntity companyAccountEntity = companyAccountRepository.findById(id)
                .orElseThrow(() -> new MongoException(
                        "Failed to find company accounts entity with id " + id +
                                " from which to remove link: " + linkType.getLink()));

        companyAccountEntity.getData().getLinks().remove(linkType.getLink());
        companyAccountRepository.save(companyAccountEntity);
    }

    private void setMetadataOnRestObject(CompanyAccount rest,
                                         Transaction transaction,
                                         String companyAccountsId) {
        rest.setLinks(createLinks(transaction, companyAccountsId));
        rest.setEtag(GenerateEtagUtil.generateEtag());
        rest.setKind(Kind.COMPANY_ACCOUNTS.getValue());
    }

    private Map<String, String> createLinks(Transaction transaction, String companyAccountsId) {
        Map<String, String> map = new HashMap<>();
        map.put(CompanyAccountLinkType.SELF.getLink(), createSelfLink(transaction, companyAccountsId));
        map.put(CompanyAccountLinkType.TRANSACTION.getLink(), getTransactionSelfLink(transaction));
        return map;
    }

    private String createSelfLink(Transaction transaction, String id) {
        return getTransactionSelfLink(transaction) + "/" + ResourceName.COMPANY_ACCOUNT.getName()
                + "/" + id;
    }

    private String getTransactionSelfLink(Transaction transaction) {
        return transaction.getLinks().getSelf();
    }

    private String getSelfLink(CompanyAccount companyAccount) {
        return companyAccount.getLinks().get(CompanyAccountLinkType.SELF.getLink());
    }

    public String generateID() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().encodeToString(bytes);
    }

    private Map<String, Resource> createTransactionResourceMap(CompanyAccount companyAccount, boolean isPayableTransaction) {
        String selfLink = getSelfLink(companyAccount);

        Resource resource = new Resource();
        resource.setKind(Kind.COMPANY_ACCOUNTS.getValue());

        Map<String, String> links = new HashMap<>();
        links.put(TransactionLinkType.RESOURCE.getLink(), selfLink);
        links.put(TransactionLinkType.VALIDATION_STATUS.getLink(), selfLink + "/validate");

        if (isPayableTransaction) {
            links.put(TransactionLinkType.COSTS.getLink(), selfLink + "/costs");
        }

        resource.setLinks(links);
        resource.setUpdatedAt(LocalDateTime.now());

        Map<String, Resource> resourceMap = new HashMap<>();
        resourceMap.put(selfLink, resource);
        return resourceMap;
    }
}