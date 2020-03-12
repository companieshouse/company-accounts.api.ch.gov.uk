package uk.gov.companieshouse.api.accounts.repository.smallfull;

import org.springframework.stereotype.Repository;
import uk.gov.companieshouse.api.accounts.enumeration.AccountingNoteType;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.offbalancesheetarrangements.OffBalanceSheetArrangementsEntity;
import uk.gov.companieshouse.api.accounts.repository.AccountsNoteRepository;

@Repository
public interface OffBalanceSheetArrangementsRepository  extends AccountsNoteRepository<OffBalanceSheetArrangementsEntity> {

    @Override
    default AccountingNoteType getAccountsNote() {
        return AccountingNoteType.SMALL_FULL_OFF_BALANCE_SHEET_ARRANGEMENTS;
    }
}