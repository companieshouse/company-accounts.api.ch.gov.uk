package uk.gov.companieshouse.api.accounts.service;

import javax.servlet.http.HttpServletRequest;
import uk.gov.companieshouse.api.accounts.enumeration.AccountingNoteType;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.rest.Note;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.model.transaction.Transaction;

public interface NoteService {

    ResponseObject<Note> create(Note note, AccountingNoteType accountingNoteType, Transaction transaction,
            String companyAccountsId, HttpServletRequest request) throws DataException;

    ResponseObject<Note> update(Note note, AccountingNoteType accountingNoteType, Transaction transaction,
            String companyAccountsId, HttpServletRequest request) throws DataException;

    ResponseObject<Note> find(AccountingNoteType accountingNoteType, String companyAccountsId)
            throws DataException;

    ResponseObject<Note> delete(AccountingNoteType accountingNoteType, String companyAccountsId,
            HttpServletRequest request) throws DataException;
}
