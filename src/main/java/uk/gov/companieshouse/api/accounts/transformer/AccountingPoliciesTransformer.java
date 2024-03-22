package uk.gov.companieshouse.api.accounts.transformer;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.enumeration.AccountingNoteType;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.accountingpolicies.AccountingPoliciesDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.accountingpolicies.AccountingPoliciesEntity;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.accountingpolicies.AccountingPolicies;

@Component
public class AccountingPoliciesTransformer implements NoteTransformer<AccountingPolicies, AccountingPoliciesEntity> {

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

    @Override
    public AccountingNoteType getAccountingNoteType() {
        return AccountingNoteType.SMALL_FULL_ACCOUNTING_POLICIES;
    }
}
