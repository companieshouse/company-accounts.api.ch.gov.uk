package uk.gov.companieshouse.api.accounts.service.impl;

import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import uk.gov.companieshouse.GenerateEtagUtil;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.LinkType;
import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountEntity;
import uk.gov.companieshouse.api.accounts.model.rest.CompanyAccount;
import uk.gov.companieshouse.api.accounts.repository.CompanyAccountRepository;
import uk.gov.companieshouse.api.accounts.service.CompanyAccountService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.transaction.TransactionManager;
import uk.gov.companieshouse.api.accounts.transformer.CompanyAccountTransformer;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Service
public class CompanyAccountServiceImpl implements CompanyAccountService {

    @Autowired
    private TransactionManager transactionManager;

    @Autowired
    private CompanyAccountRepository companyAccountRepository;

    @Autowired
    private CompanyAccountTransformer companyAccountTransformer;

    private static final Logger LOGGER = LoggerFactory.getLogger("company-accounts.api.ch.gov.uk");

    /**
     * {@inheritDoc}
     */
    public ResponseObject createCompanyAccount(CompanyAccount companyAccount) {
        HttpServletRequest request = getRequestFromContext();
        Transaction transaction = getTransactionFromSession(request);

        String id = generateID();
        String companyAccountLink = createSelfLink(transaction, id);
        addKind(companyAccount);
        addEtag(companyAccount);
        addLinks(companyAccount, companyAccountLink);

        CompanyAccountEntity companyAccountEntity = companyAccountTransformer.transform(companyAccount);

        companyAccountEntity.setId(id);

        try {
            companyAccountRepository.insert(companyAccountEntity);
            transactionManager.updateTransaction(transaction.getId(),
                    request.getHeader("X-Request-Id"),
                    companyAccountLink);
        } catch (DuplicateKeyException dke) {
            LOGGER.error(dke);
            return new ResponseObject(ResponseStatus.DUPLICATE_KEY_ERROR);
        } catch (MongoException me) {
            LOGGER.error(me);
            return new ResponseObject(ResponseStatus.MONGO_ERROR);
        } catch (Exception ex) {
            LOGGER.error(ex);
            return new ResponseObject(ResponseStatus.TRANSACTION_PATCH_ERROR);
        }

        return new ResponseObject(ResponseStatus.SUCCESS, companyAccount);
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

    private static HttpServletRequest getRequestFromContext() {
        return ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
    }

    private static Transaction getTransactionFromSession(HttpServletRequest request) {
        return (Transaction) request.getSession().getAttribute(AttributeName.TRANSACTION.getValue());
    }

    public String generateID() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().encodeToString(bytes);
    }
}