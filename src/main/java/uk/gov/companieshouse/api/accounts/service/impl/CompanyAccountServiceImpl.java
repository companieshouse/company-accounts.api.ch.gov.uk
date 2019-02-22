package uk.gov.companieshouse.api.accounts.service.impl;

import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoException;

import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.GenerateEtagUtil;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.accounts.Kind;
import uk.gov.companieshouse.api.accounts.ResourceName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.exception.PatchException;
import uk.gov.companieshouse.api.accounts.links.CompanyAccountLinkType;
import uk.gov.companieshouse.api.accounts.links.TransactionLinkType;
import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountEntity;
import uk.gov.companieshouse.api.accounts.model.rest.CompanyAccount;
import uk.gov.companieshouse.api.accounts.repository.CompanyAccountRepository;
import uk.gov.companieshouse.api.accounts.sdk.ApiClientService;
import uk.gov.companieshouse.api.accounts.service.CompanyAccountService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.model.transaction.Resource;
import uk.gov.companieshouse.api.accounts.transformer.CompanyAccountTransformer;
import uk.gov.companieshouse.sdk.manager.ApiSdkManager;

@Service
public class CompanyAccountServiceImpl implements CompanyAccountService {

    @Autowired
    private CompanyAccountRepository companyAccountRepository;

    @Autowired
    private CompanyAccountTransformer companyAccountTransformer;

    @Autowired
    private ApiClientService apiClientService;

    /**
     * {@inheritDoc}
     */
    public ResponseObject<CompanyAccount> create(CompanyAccount companyAccount,
        Transaction transaction, HttpServletRequest request)
        throws PatchException, DataException {

        String id = generateID();
        String companyAccountLink = createSelfLink(transaction, id);
        companyAccount.setKind(Kind.COMPANY_ACCOUNTS.getValue());
        addEtag(companyAccount);
        addLinks(companyAccount, companyAccountLink, transaction);

        CompanyAccountEntity companyAccountEntity = companyAccountTransformer
            .transform(companyAccount);

        companyAccountEntity.setId(id);

        try {
            companyAccountRepository.insert(companyAccountEntity);
        } catch (DuplicateKeyException dke) {
            return new ResponseObject<>(ResponseStatus.DUPLICATE_KEY_ERROR);
        } catch (MongoException e) {
            throw new DataException(e);
        }

        try {
            InternalApiClient internalApiClient = apiClientService.getInternalApiClient(request.getHeader(ApiSdkManager.getEricPassthroughTokenHeader()));
            transaction.setResources(createResourceMap(companyAccountLink));

            internalApiClient.privateTransaction().patch("/private/transactions/" + transaction.getId(), transaction).execute();
        } catch (IOException | URIValidationException e) {

            PatchException patchException = new PatchException("Failed to patch transaction", e);
            throw patchException;
        }

        return new ResponseObject<>(ResponseStatus.CREATED, companyAccount);
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

    private void addLinks(CompanyAccount companyAccount, String companyAccountLink, Transaction transaction) {
        Map<String, String> map = new HashMap<>();
        map.put(CompanyAccountLinkType.SELF.getLink(), companyAccountLink);
        map.put(CompanyAccountLinkType.TRANSACTION.getLink(), getTransactionSelfLink(transaction));
        companyAccount.setLinks(map);
    }

    private String createSelfLink(Transaction transaction, String id) {
        return getTransactionSelfLink(transaction) + "/" + ResourceName.COMPANY_ACCOUNT.getName()
            + "/" + id;
    }

    private String getTransactionSelfLink(Transaction transaction) {
        return transaction.getLinks().getSelf();
    }

    private void addEtag(CompanyAccount rest) {
        rest.setEtag(GenerateEtagUtil.generateEtag());
    }

    public String generateID() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().encodeToString(bytes);
    }

    public ResponseObject<CompanyAccount> findById(String id, HttpServletRequest request)
        throws DataException {

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

    /**
     * Creates the resources map for the patching of the transaction
     *
     * @param link - the link in which to add to the resource
     */
    private Map<String, Resource> createResourceMap(String link) {
        Resource resource = new Resource();
        resource.setKind(Kind.COMPANY_ACCOUNTS.getValue());

        Map<String, String> links = new HashMap<>();
        links.put(TransactionLinkType.RESOURCE.getLink(), link);
        resource.setLinks(links);
        resource.setUpdatedAt(LocalDateTime.now());

        Map<String, Resource> resourceMap = new HashMap<>();
        resourceMap.put(link, resource);
        return resourceMap;
    }
}