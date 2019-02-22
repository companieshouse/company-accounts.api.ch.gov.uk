package uk.gov.companieshouse.api.accounts.service.impl;

import com.mongodb.MongoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.GenerateEtagUtil;
import uk.gov.companieshouse.api.accounts.CompanyAccountsApplication;
import uk.gov.companieshouse.api.accounts.Kind;
import uk.gov.companieshouse.api.accounts.ResourceName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.BasicLinkType;
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;
import uk.gov.companieshouse.api.accounts.model.entity.AccountingPoliciesEntity;
import uk.gov.companieshouse.api.accounts.model.rest.AccountingPolicies;
import uk.gov.companieshouse.api.accounts.repository.AccountingPoliciesRepository;
import uk.gov.companieshouse.api.accounts.service.ResourceService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.transformer.AccountingPoliciesTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Service
public class AccountingPoliciesService implements ResourceService<AccountingPolicies> {

    private AccountingPoliciesRepository repository;

    private AccountingPoliciesTransformer transformer;

    private SmallFullService smallFullService;

    private KeyIdGenerator keyIdGenerator;

    @Autowired
    public AccountingPoliciesService(AccountingPoliciesRepository repository,
                              AccountingPoliciesTransformer transformer,
                              SmallFullService smallFullService,
                              KeyIdGenerator keyIdGenerator) {

        this.repository = repository;
        this.transformer = transformer;
        this.smallFullService = smallFullService;
        this.keyIdGenerator = keyIdGenerator;
    }

    @Override
    public ResponseObject<AccountingPolicies> create(AccountingPolicies rest, Transaction transaction,
                                                     String companyAccountsId, HttpServletRequest request)
                                                    throws DataException {

        setMetadataOnRestObject(rest, transaction, companyAccountsId);

        AccountingPoliciesEntity entity = transformer.transform(rest);
        entity.setId(generateID(companyAccountsId));

        try {

            repository.insert(entity);
        } catch (DuplicateKeyException e) {

            return new ResponseObject<>(ResponseStatus.DUPLICATE_KEY_ERROR);
        } catch (MongoException e) {

            throw new DataException(e);
        }

        smallFullService
                .addLink(companyAccountsId, SmallFullLinkType.ACCOUNTING_POLICY_NOTE,
                        getSelfLinkFromAccountingPolicy(entity), request);

        return new ResponseObject<>(ResponseStatus.CREATED, rest);
    }

    @Override
    public ResponseObject<AccountingPolicies> update(AccountingPolicies rest, Transaction transaction,
                                                     String companyAccountsId, HttpServletRequest request)
                                                    throws DataException {

        setMetadataOnRestObject(rest, transaction, companyAccountsId);

        AccountingPoliciesEntity entity = transformer.transform(rest);
        entity.setId(generateID(companyAccountsId));

        try {

            repository.save(entity);
        } catch (MongoException e) {

            throw new DataException(e);
        }

        return new ResponseObject<>(ResponseStatus.UPDATED, rest);
    }

    @Override
    public ResponseObject<AccountingPolicies> findById(String id, HttpServletRequest request) throws DataException {

        AccountingPoliciesEntity entity;

        try {

            entity = repository.findById(id).orElse(null);
        } catch (MongoException e) {

            throw new DataException(e);
        }

        if (entity == null) {

            return new ResponseObject<>(ResponseStatus.NOT_FOUND);
        }

        return new ResponseObject<>(ResponseStatus.FOUND, transformer.transform(entity));
    }

    @Override
    public ResponseObject<AccountingPolicies> delete(String companyAccountsId, HttpServletRequest request) throws DataException {
        return null;
    }

    @Override
    public String generateID(String companyAccountId) {

        return keyIdGenerator.generate(companyAccountId + "-" + ResourceName.ACCOUNTING_POLICIES.getName());
    }

    private String getSelfLink(Transaction transaction, String companyAccountId) {

        return transaction.getLinks().getSelf() + "/"
                + ResourceName.COMPANY_ACCOUNT.getName() + "/"
                + companyAccountId + "/" + ResourceName.SMALL_FULL.getName() + "/"
                + "notes/"
                + ResourceName.ACCOUNTING_POLICIES.getName();
    }

    private void setLinksOnAccountingPolicies(AccountingPolicies rest, String link) {
        
        Map<String, String> map = new HashMap<>();
        map.put(BasicLinkType.SELF.getLink(), link);
        rest.setLinks(map);
    }

    public String getSelfLinkFromAccountingPolicy(AccountingPoliciesEntity entity) {

        return entity.getData().getLinks().get(BasicLinkType.SELF.getLink());
    }

    private void setMetadataOnRestObject(AccountingPolicies rest, Transaction transaction, String companyAccountsId) {

        setLinksOnAccountingPolicies(rest, getSelfLink(transaction, companyAccountsId));
        rest.setEtag(GenerateEtagUtil.generateEtag());
        rest.setKind(Kind.POLICY_NOTE.getValue());
    }
}
