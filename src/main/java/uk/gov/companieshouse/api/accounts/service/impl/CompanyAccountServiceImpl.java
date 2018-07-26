package uk.gov.companieshouse.api.accounts.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
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

        addKind(companyAccount);
        addEtag(companyAccount);

        CompanyAccountEntity companyAccountEntity = genericTransformer.transform(companyAccount);

        addEntityID(companyAccountEntity);
        addEntityLinks(companyAccountEntity, transaction);

        mongoRepository.insert(companyAccountEntity);

        transactionManager.updateTransaction(transaction.getBody().getId(), request.getHeader("X-Request-Id"), getCompanyAccountSelfLink(companyAccountEntity));

        return genericTransformer.transform(companyAccountEntity);
    }

    public void addEntityLinks(CompanyAccountEntity entity, ResponseEntity<Transaction> transaction) {
        Map<String, String> map = new HashMap<>();
        String selfLink = createCompanyAccountSelfLink(entity, transaction);
        map.put(LinkType.SELF.getLink(), selfLink);
        entity.getData().setLinks(map);
    }

    private String createCompanyAccountSelfLink(CompanyAccountEntity entity, ResponseEntity<Transaction> transaction) {
        return getTransactionSelfLink(transaction) + "/company-accounts/" + entity.getId() + "";
    }

    private String getTransactionSelfLink(ResponseEntity<Transaction> transaction) {
        return transaction.getBody().getLinks().get(LinkType.SELF.getLink());
    }

    @Override
    public void addLinks(CompanyAccount rest) {

    }

    @Override
    public void addKind(CompanyAccount rest) {
        rest.setKind("company-accounts");
    }

    public void addEntityID(CompanyAccountEntity entity) {
        entity.setId(UUID.randomUUID().toString());
    }

    private static HttpServletRequest getRequestFromContext() {
        return ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
    }

    private static ResponseEntity<Transaction> getTransactionFromSession(HttpServletRequest request) {
        return (ResponseEntity) request.getSession().getAttribute("transaction");
    }

    private String getCompanyAccountSelfLink(CompanyAccountEntity companyAccountEntity) {
        return companyAccountEntity.getData().getLinks().get(LinkType.SELF.getLink());
    }
}