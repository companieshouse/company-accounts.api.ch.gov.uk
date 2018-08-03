package uk.gov.companieshouse.api.accounts.service;

import uk.gov.companieshouse.api.accounts.model.filing.Filing;

import java.io.IOException;

public interface FilingService {

    /**
     *
     * @param transactionId
     * @param accountsId
     * @return
     * @throws IOException
     */
    Filing generateAccountFiling(String transactionId, String accountsId) throws IOException;
}