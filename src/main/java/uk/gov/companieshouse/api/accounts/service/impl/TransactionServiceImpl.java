package uk.gov.companieshouse.api.accounts.service.impl;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.accounts.PayableResource;
import uk.gov.companieshouse.api.accounts.exception.ServiceException;
import uk.gov.companieshouse.api.accounts.service.CompanyService;
import uk.gov.companieshouse.api.accounts.service.TransactionService;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private CompanyService companyService;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<PayableResource> getPayableResources(Transaction transaction) throws ServiceException {

        List<PayableResource> payableResources = new ArrayList<>();

        if(companyService.isCIC(transaction)) {
            payableResources.add(PayableResource.CIC);
        }

        return payableResources;
    }
}
