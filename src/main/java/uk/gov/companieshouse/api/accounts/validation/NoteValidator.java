package uk.gov.companieshouse.api.accounts.validation;

import jakarta.servlet.http.HttpServletRequest;
import uk.gov.companieshouse.api.accounts.enumeration.AccountingNoteType;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.rest.Note;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.model.transaction.Transaction;

public interface NoteValidator<N extends Note> {

    Errors validateSubmission(N note, Transaction transaction, String companyAccountId, HttpServletRequest request)
            throws DataException;

    AccountingNoteType getAccountingNoteType();
}
