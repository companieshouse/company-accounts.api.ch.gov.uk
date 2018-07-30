package uk.gov.companieshouse.api.accounts.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.LinkType;
import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountEntity;
import uk.gov.companieshouse.api.accounts.model.rest.CompanyAccount;
import uk.gov.companieshouse.api.accounts.service.CompanyAccountService;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.transaction.TransactionManager;
import uk.gov.companieshouse.api.accounts.transformer.GenericTransformer;

@Service
public class CompanyAccountServiceImpl extends
        AbstractServiceImpl<CompanyAccount, CompanyAccountEntity> implements CompanyAccountService {

    @Autowired
    private TransactionManager transactionManager;

    @Autowired
    public CompanyAccountServiceImpl(
            @Qualifier("companyAccountRepository") MongoRepository<CompanyAccountEntity, String> mongoRepository,
            @Qualifier("companyAccountTransformer") GenericTransformer<CompanyAccount, CompanyAccountEntity> transformer) {
        super(mongoRepository, transformer);
    }

    /**
     * {@inheritDoc}
     */
    public CompanyAccount createCompanyAccount(CompanyAccount companyAccount) {
        HttpServletRequest request = getRequestFromContext();
        ResponseEntity<Transaction> transaction = getTransactionFromSession(request);

        String id = UUID.randomUUID().toString();
        String companyAccountLink = createSelfLink(transaction, id);
        addKind(companyAccount);
        addEtag(companyAccount);
        addSelfLinks(companyAccount, companyAccountLink);

        CompanyAccountEntity companyAccountEntity = genericTransformer.transform(companyAccount);

        companyAccountEntity.setId(id);
        mongoRepository.insert(companyAccountEntity);

        transactionManager.updateTransaction(transaction.getBody().getId(), request.getHeader("X-Request-Id"), getCompanyAccountSelfLink(companyAccountEntity));

        return companyAccount;
    }

    public void addSelfLinks(CompanyAccount companyAccount, String companyAccountLink) {
        Map<String, String> map = new HashMap<>();
        map.put(LinkType.SELF.getLink(), companyAccountLink);
        companyAccount.setLinks(map);
    }

    private String createSelfLink(ResponseEntity<Transaction> transaction, String id) {
        return getTransactionSelfLink(transaction) + "/company-accounts/" + id;
    }

    private String getTransactionSelfLink(ResponseEntity<Transaction> transaction) {
        return transaction.getBody().getLinks().get(LinkType.SELF.getLink());
    }

    @Override
    public void addLinks(CompanyAccount rest) {

    }

    @Override
    public String getResourceName() {
        return "company-account";
    }

    @Override
    public void addKind(CompanyAccount rest) {
        rest.setKind("company-accounts");
    }

    @Override
    public void addID(CompanyAccountEntity entity) {

    }

    private static HttpServletRequest getRequestFromContext() {
        return ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
    }

    private static ResponseEntity<Transaction> getTransactionFromSession(HttpServletRequest request) {
        return (ResponseEntity) request.getSession().getAttribute(AttributeName.TRANSACTION.getValue());
    }

    private String getCompanyAccountSelfLink(CompanyAccountEntity companyAccountEntity) {
        return companyAccountEntity.getData().getLinks().get(LinkType.SELF.getLink());
    }

    @Override
    public String generateID(String value) {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().encodeToString(bytes);
    }
}