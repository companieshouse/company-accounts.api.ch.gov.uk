package uk.gov.companieshouse.api.accounts.repository.smallfull;

import org.springframework.stereotype.Repository;
import uk.gov.companieshouse.api.accounts.enumeration.AccountsResource;
import uk.gov.companieshouse.api.accounts.model.entity.notes.employees.EmployeesEntity;
import uk.gov.companieshouse.api.accounts.repository.AccountsResourceRepository;

@Repository
public interface EmployeesRepository extends AccountsResourceRepository<EmployeesEntity> {

    @Override
    default AccountsResource getAccountsResource() {
        return AccountsResource.SMALL_FULL_EMPLOYEES;
    }
}
