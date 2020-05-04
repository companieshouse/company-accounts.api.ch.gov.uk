package uk.gov.companieshouse.api.accounts.repository.smallfull;

import org.springframework.stereotype.Repository;
import uk.gov.companieshouse.api.accounts.enumeration.AccountingNoteType;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.creditorsaftermorethanoneyear.CreditorsAfterMoreThanOneYearEntity;
import uk.gov.companieshouse.api.accounts.repository.AccountsNoteRepository;

@Repository
public interface CreditorsAfterOneYearRepository extends AccountsNoteRepository<CreditorsAfterMoreThanOneYearEntity> {

    @Override
    default AccountingNoteType getAccountsNote() {
        return AccountingNoteType.SMALL_FULL_CREDITORS_AFTER;
    }
}
