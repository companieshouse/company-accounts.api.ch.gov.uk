package uk.gov.companieshouse.api.accounts.service.impl;

import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.LoansToDirectorsLinkType;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.loanstodirectors.LoansToDirectors;
import uk.gov.companieshouse.api.accounts.service.LoansToDirectorsService;
import uk.gov.companieshouse.api.accounts.service.ParentService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@Service
public class LoansToDirectorsServiceImpl implements ParentService<LoansToDirectors, LoansToDirectorsLinkType>,
        LoansToDirectorsService {

    @Override
    public ResponseObject<LoansToDirectors> create(LoansToDirectors rest, Transaction transaction,
            String companyAccountId, HttpServletRequest request) throws DataException {
        return null;
    }

    @Override
    public ResponseObject<LoansToDirectors> find(String companyAccountsId,
            HttpServletRequest request) throws DataException {
        return null;
    }

    @Override
    public ResponseObject<LoansToDirectors> delete(String companyAccountsId,
            HttpServletRequest request) throws DataException {
        return null;
    }

    @Override
    public void addLink(String id, LoansToDirectorsLinkType linkType, String link,
            HttpServletRequest request) throws DataException {

    }

    @Override
    public void removeLink(String id, LoansToDirectorsLinkType linkType, HttpServletRequest request)
            throws DataException {

    }

    @Override
    public void addLoan(String companyAccountsId, String loanId, String link,
            HttpServletRequest request) throws DataException {

    }

    @Override
    public void removeLoan(String companyAccountsId, String loanId, HttpServletRequest request)
            throws DataException {

    }
}
