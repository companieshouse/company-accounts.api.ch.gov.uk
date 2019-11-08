package uk.gov.companieshouse.api.accounts.repository.smallfull;

import org.springframework.stereotype.Repository;
import uk.gov.companieshouse.api.accounts.enumeration.AccountsResource;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.stocks.StocksEntity;
import uk.gov.companieshouse.api.accounts.repository.AccountsResourceRepository;

@Repository
public interface StocksRepository extends AccountsResourceRepository<StocksEntity> {

    @Override
    default AccountsResource getAccountsResource() {
        return AccountsResource.SMALL_FULL_STOCKS;
    }
}
