package uk.gov.companieshouse.api.accounts.repository.smallfull;

import org.springframework.stereotype.Repository;
import uk.gov.companieshouse.api.accounts.enumeration.AccountingNoteType;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.fixedassetsinvestments.FixedAssetsInvestmentsEntity;
import uk.gov.companieshouse.api.accounts.repository.AccountsNoteRepository;

@Repository
public interface FixedAssetsInvestmentsRepository extends AccountsNoteRepository<FixedAssetsInvestmentsEntity> {

    @Override
    default AccountingNoteType getAccountsNote() {
        return AccountingNoteType.SMALL_FULL_FIXED_ASSETS_INVESTMENTS;
    }
}
