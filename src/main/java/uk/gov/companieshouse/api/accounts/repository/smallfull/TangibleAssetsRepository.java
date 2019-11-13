package uk.gov.companieshouse.api.accounts.repository.smallfull;

import org.springframework.stereotype.Repository;
import uk.gov.companieshouse.api.accounts.enumeration.AccountsResource;
import uk.gov.companieshouse.api.accounts.model.entity.notes.tangible.TangibleAssetsEntity;
import uk.gov.companieshouse.api.accounts.repository.AccountsResourceRepository;

@Repository
public interface TangibleAssetsRepository extends AccountsResourceRepository<TangibleAssetsEntity> {

    @Override
    default AccountsResource getAccountsResource() { return AccountsResource.SMALL_FULL_TANGIBLE_ASSETS; }
}
