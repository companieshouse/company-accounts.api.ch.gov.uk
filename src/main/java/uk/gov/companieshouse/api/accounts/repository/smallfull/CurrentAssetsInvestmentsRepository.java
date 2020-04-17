package uk.gov.companieshouse.api.accounts.repository.smallfull;

import org.springframework.stereotype.Repository;
import uk.gov.companieshouse.api.accounts.enumeration.AccountingNoteType;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.currentassetsinvestments.CurrentAssetsInvestmentsEntity;
import uk.gov.companieshouse.api.accounts.repository.AccountsNoteRepository;

@Repository
public interface CurrentAssetsInvestmentsRepository extends AccountsNoteRepository<CurrentAssetsInvestmentsEntity> {

    @Override
    default AccountingNoteType getAccountsNote() {
        return AccountingNoteType.SMALL_FULL_CURRENT_ASSETS_INVESTMENTS;
    }

}
