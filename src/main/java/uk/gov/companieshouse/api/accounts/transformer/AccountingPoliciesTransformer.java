package uk.gov.companieshouse.api.accounts.transformer;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.model.entity.AccountingPoliciesDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.AccountingPoliciesEntity;
import uk.gov.companieshouse.api.accounts.model.rest.AccountingPolicies;

@Component
public class AccountingPoliciesTransformer implements GenericTransformer<AccountingPolicies, AccountingPoliciesEntity> {

    @Override
    public AccountingPoliciesEntity transform(AccountingPolicies rest) {

        AccountingPoliciesDataEntity accountingPoliciesDataEntity = new AccountingPoliciesDataEntity();
        AccountingPoliciesEntity accountingPoliciesEntity = new AccountingPoliciesEntity();

        BeanUtils.copyProperties(rest, accountingPoliciesDataEntity);
        accountingPoliciesEntity.setData(accountingPoliciesDataEntity);

        return accountingPoliciesEntity;
    }

    @Override
    public AccountingPolicies transform(AccountingPoliciesEntity entity) {

        AccountingPolicies accountingPolicies = new AccountingPolicies();
        AccountingPoliciesDataEntity accountingPoliciesDataEntity = entity.getData();

        BeanUtils.copyProperties(accountingPoliciesDataEntity, accountingPolicies);

        return accountingPolicies;
    }
}
