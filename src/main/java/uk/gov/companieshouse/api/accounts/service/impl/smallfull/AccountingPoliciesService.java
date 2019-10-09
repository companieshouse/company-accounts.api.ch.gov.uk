package uk.gov.companieshouse.api.accounts.service.impl.smallfull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.accounts.Kind;
import uk.gov.companieshouse.api.accounts.ResourceName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;
import uk.gov.companieshouse.api.accounts.model.entity.AccountingPoliciesEntity;
import uk.gov.companieshouse.api.accounts.model.rest.AccountingPolicies;
import uk.gov.companieshouse.api.accounts.repository.AccountingPoliciesRepository;
import uk.gov.companieshouse.api.accounts.service.ResourceService;
import uk.gov.companieshouse.api.accounts.service.impl.BaseService;
import uk.gov.companieshouse.api.accounts.service.impl.SmallFullService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.transformer.AccountingPoliciesTransformer;
import uk.gov.companieshouse.api.accounts.utility.SelfLinkGenerator;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;

import javax.servlet.http.HttpServletRequest;

@Service
public class AccountingPoliciesService implements ResourceService<AccountingPolicies> {

    private BaseService<AccountingPolicies, AccountingPoliciesEntity, SmallFullLinkType> baseService;

    @Autowired
    public AccountingPoliciesService(AccountingPoliciesRepository repository,
                                     AccountingPoliciesTransformer transformer,
                                     SmallFullService smallFullService,
                                     KeyIdGenerator keyIdGenerator) {

        this.baseService =
                new BaseService<>(
                        repository,
                        transformer,
                        keyIdGenerator,
                        smallFullService,
                        SmallFullLinkType.ACCOUNTING_POLICY_NOTE,
                        Kind.POLICY_NOTE,
                        ResourceName.ACCOUNTING_POLICIES
                );
    }

    @Override
    public ResponseObject<AccountingPolicies> create(
            AccountingPolicies rest, Transaction transaction, String companyAccountId, HttpServletRequest request)
                throws DataException {

        String selfLink =
                SelfLinkGenerator.generateSelfLink(
                        transaction, companyAccountId, ResourceName.SMALL_FULL, true, ResourceName.ACCOUNTING_POLICIES);

        return baseService.create(rest, transaction, companyAccountId, request, selfLink);
    }

    @Override
    public ResponseObject<AccountingPolicies> update(
            AccountingPolicies rest, Transaction transaction, String companyAccountId, HttpServletRequest request)
                throws DataException {

        String selfLink =
                SelfLinkGenerator.generateSelfLink(
                        transaction, companyAccountId, ResourceName.SMALL_FULL, true, ResourceName.ACCOUNTING_POLICIES);

        return baseService.update(rest, transaction, companyAccountId, request, selfLink);
    }

    @Override
    public ResponseObject<AccountingPolicies> find(String companyAccountsId, HttpServletRequest request) throws DataException {

        return baseService.find(companyAccountsId);
    }

    @Override
    public ResponseObject<AccountingPolicies> delete(String companyAccountsId, HttpServletRequest request) throws DataException {

        return baseService.delete(companyAccountsId, request);
    }
}
