package uk.gov.companieshouse.api.accounts.service;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.rest.RestObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;

public interface ResourceService<T extends RestObject> {

    ResponseObject<T> create(T rest, Transaction transaction, String companyAccountId,
        HttpServletRequest request)
        throws DataException;

    ResponseObject<T> update(T rest, Transaction transaction, String companyAccountId,
        HttpServletRequest request)
        throws DataException;

    ResponseObject<T> findById(String id, HttpServletRequest request)
        throws DataException;

    ResponseObject<T> delete(String companyAccountsId, HttpServletRequest request)
        throws DataException;

    String generateID(String companyAccountId);

    default Map<String, Object> getDebugMap(Transaction transaction, String companyAccountsId, String id) {

        Map<String, Object> debugMap = new HashMap<>();

        debugMap.put("transaction_id", transaction.getId());
        debugMap.put("company_accounts_id", companyAccountsId);
        debugMap.put("id", id);

        return debugMap;
    }

}