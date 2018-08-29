package uk.gov.companieshouse.api.accounts.service.impl;

import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.GenerateEtagUtil;
import uk.gov.companieshouse.api.accounts.CompanyAccountsApplication;
import uk.gov.companieshouse.api.accounts.LinkType;
import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountEntity;
import uk.gov.companieshouse.api.accounts.model.rest.CompanyAccount;
import uk.gov.companieshouse.api.accounts.repository.CompanyAccountRepository;
import uk.gov.companieshouse.api.accounts.service.CompanyAccountService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transaction.PatchException;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.transaction.TransactionManager;
import uk.gov.companieshouse.api.accounts.transformer.CompanyAccountTransformer;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Service
public class CompanyAccountServiceImpl implements CompanyAccountService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompanyAccountsApplication.APPLICATION_NAME_SPACE);
    @Autowired
    private TransactionManager transactionManager;
    @Autowired
    private CompanyAccountRepository companyAccountRepository;
    @Autowired
    private CompanyAccountTransformer companyAccountTransformer;

    /**
     * {@inheritDoc}
     */
    public ResponseObject<CompanyAccount> createCompanyAccount(CompanyAccount companyAccount,
            Transaction transaction, String requestId) {
        String id = generateID();
        String companyAccountLink = createSelfLink(transaction, id);
        addKind(companyAccount);
        addEtag(companyAccount);
        addLinks(companyAccount, companyAccountLink);

        CompanyAccountEntity companyAccountEntity = companyAccountTransformer
                .transform(companyAccount);

        companyAccountEntity.setId(id);

        companyAccountRepository.insert(companyAccountEntity);

        transactionManager.updateTransaction(transaction.getId(), requestId, companyAccountLink);

        return new ResponseObject<>(ResponseStatus.SUCCESS_CREATED, companyAccount);
    }

    private void addLinks(CompanyAccount companyAccount, String companyAccountLink) {
        Map<String, String> map = new HashMap<>();
        map.put(LinkType.SELF.getLink(), companyAccountLink);
        companyAccount.setLinks(map);
    }

    private String createSelfLink(Transaction transaction, String id) {
        return getTransactionSelfLink(transaction) + "/company-accounts/" + id;
    }

    private String getTransactionSelfLink(Transaction transaction) {
        return transaction.getLinks().get(LinkType.SELF.getLink());
    }

    private void addKind(CompanyAccount rest) {
        rest.setKind("company-accounts#company-accounts");
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

    public CompanyAccountEntity findById(String id) {
        return companyAccountRepository.findById(id).orElse(null);
    }
}