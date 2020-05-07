package uk.gov.companieshouse.api.accounts.repository.smallfull;

import org.springframework.stereotype.Repository;
import uk.gov.companieshouse.api.accounts.enumeration.AccountingNoteType;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.accountingpolicies.AccountingPoliciesEntity;
import uk.gov.companieshouse.api.accounts.repository.AccountsNoteRepository;

@Repository
public interface AccountingPoliciesRepository extends AccountsNoteRepository<AccountingPoliciesEntity> {

    @Override
    default AccountingNoteType getAccountsNote() {
        return AccountingNoteType.SMALL_FULL_ACCOUNTING_POLICIES;
    }

}
