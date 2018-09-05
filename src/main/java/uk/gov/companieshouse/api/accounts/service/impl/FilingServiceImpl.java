package uk.gov.companieshouse.api.accounts.service.impl;


import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountEntity;
import uk.gov.companieshouse.api.accounts.model.filing.Filing;
import uk.gov.companieshouse.api.accounts.service.FilingService;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;

@Service
public class FilingServiceImpl implements FilingService {

    /**
     * {@inheritDoc}
     */
    @Override
    public Filing generateAccountFiling(Transaction transaction,
        CompanyAccountEntity companyAccountEntity) {
        return new Filing();
    }
}