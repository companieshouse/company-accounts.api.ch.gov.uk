package uk.gov.companieshouse.api.accounts.repository;

import org.springframework.stereotype.Repository;
import uk.gov.companieshouse.api.accounts.enumeration.AccountingNoteType;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.employees.EmployeesEntity;

@Repository
public interface EmployeesRepository extends AccountsNoteRepository<EmployeesEntity> {

    @Override
    default AccountingNoteType getAccountsNote() {
        return AccountingNoteType.SMALL_FULL_EMPLOYEES;
    }
}
