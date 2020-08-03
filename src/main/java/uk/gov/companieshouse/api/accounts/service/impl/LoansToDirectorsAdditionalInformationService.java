package uk.gov.companieshouse.api.accounts.service.impl;

import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.loanstodirectors.AdditionalInformation;
import uk.gov.companieshouse.api.accounts.service.ResourceService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@Service
public class LoansToDirectorsAdditionalInformationService implements ResourceService<AdditionalInformation> {

    @Override
    public ResponseObject<AdditionalInformation> create(AdditionalInformation rest,
            Transaction transaction, String companyAccountId, HttpServletRequest request)
            throws DataException {
        return null;
    }

    @Override
    public ResponseObject<AdditionalInformation> update(AdditionalInformation rest,
            Transaction transaction, String companyAccountId, HttpServletRequest request)
            throws DataException {
        return null;
    }

    @Override
    public ResponseObject<AdditionalInformation> find(String companyAccountsId,
            HttpServletRequest request) throws DataException {
        return null;
    }

    @Override
    public ResponseObject<AdditionalInformation> delete(String companyAccountsId,
            HttpServletRequest request) throws DataException {
        return null;
    }
}
