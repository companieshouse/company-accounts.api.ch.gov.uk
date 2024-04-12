package uk.gov.companieshouse.api.accounts.service;

import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;

import jakarta.servlet.http.HttpServletRequest;

public interface ValidationStatusService {

    Errors getValidationErrors(String companyAccountsId, HttpServletRequest request) throws DataException;
}
