package uk.gov.companieshouse.api.accounts.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.verification.VerificationModeFactory;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.verification.VerificationMode;
import org.springframework.dao.DuplicateKeyException;

import com.mongodb.MongoException;

import uk.gov.companieshouse.api.accounts.Kind;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.BasicLinkType;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.loanstodirectors.LoanEntity;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.loanstodirectors.Loan;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.repository.smallfull.LoanRepository;
import uk.gov.companieshouse.api.accounts.service.LoansToDirectorsService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transformer.LoanTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;
import uk.gov.companieshouse.api.accounts.validation.LoanValidator;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.model.transaction.TransactionLinks;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LoanServiceImplTest {

    @Mock
    private LoanTransformer transformer;

    @Mock
    private LoanRepository repository;

    @Mock
    private LoansToDirectorsServiceImpl loansToDirectorsService;

    @Mock
    private KeyIdGenerator keyIdGenerator;

    @Mock
    private Loan loan;

    @Mock
    private Transaction transaction;

    @Mock
    private TransactionLinks transactionLinks;

    @Mock
    private HttpServletRequest request;

    @Mock
    private LoanEntity loanEntity;

    @Mock
    private LoanValidator loanValidator;

    @Mock
    private Errors errors;
    
    @Mock
    private Map<String, String> links;

    @InjectMocks
    private LoanServiceImpl loanService;

    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";
    private static final String LOAN_ID = "loanId";
    private static final String TRANSACTION_SELF_LINK = "transactionSelfLink";
    private static final String LOAN_SELF_LINK = "loanSelfLink";

    private static final String URI = "/transactions/transactionId/company-accounts/" +
            COMPANY_ACCOUNTS_ID + "/small-full/notes/loans-to-directors/loans/" +
             LOAN_ID;

    private static final String LOAN_LINK = TRANSACTION_SELF_LINK + "/company-accounts/" +
            COMPANY_ACCOUNTS_ID + "/small-full/notes/loans-to-directors/loans";

    @Test
    @DisplayName("Tests the successful creation of a loan resource")
    void createLoanSuccess() throws DataException {

        when(loanValidator.validateLoan(loan, transaction, COMPANY_ACCOUNTS_ID, request)).thenReturn(errors);
        when(errors.hasErrors()).thenReturn(false);

        when(keyIdGenerator.generateRandom()).thenReturn(LOAN_ID);

        when(transformer.transform(loan)).thenReturn(loanEntity);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        when(loan.getLinks()).thenReturn(links);
        when(links.get(BasicLinkType.SELF.getLink())).thenReturn(LOAN_SELF_LINK);

        ResponseObject<Loan> response =
                loanService.create(loan, transaction, COMPANY_ACCOUNTS_ID, request);

        assertMetaDataSetOnRestObject();
        assertIdGeneratedForDatabaseEntity();
        assertRepositoryInsertCalled();
        assertWhetherLoansToDirectorsServiceCalledToAddLoan(true);
        assertEquals(ResponseStatus.CREATED, response.getStatus());
        assertEquals(loan, response.getData());
    }

    @Test
    @DisplayName("Tests the creation of a loan resource where the repository throws a duplicate key exception")
    void createLoanDuplicateKeyException() throws DataException {

        when(loanValidator.validateLoan(loan, transaction, COMPANY_ACCOUNTS_ID, request)).thenReturn(errors);
        when(errors.hasErrors()).thenReturn(false);

        when(keyIdGenerator.generateRandom()).thenReturn(LOAN_ID);

        when(transformer.transform(loan)).thenReturn(loanEntity);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        when(repository.insert(loanEntity)).thenThrow(DuplicateKeyException.class);

        ResponseObject<Loan> response =
                loanService.create(loan, transaction, COMPANY_ACCOUNTS_ID, request);

        assertMetaDataSetOnRestObject();
        assertIdGeneratedForDatabaseEntity();
        assertRepositoryInsertCalled();
        assertWhetherLoansToDirectorsServiceCalledToAddLoan(false);
        assertEquals(ResponseStatus.DUPLICATE_KEY_ERROR, response.getStatus());
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Tests the creation of a loan resource where the repository throws a Mongo exception")
    void createLoanMongoException() throws DataException {

        when(loanValidator.validateLoan(loan, transaction, COMPANY_ACCOUNTS_ID, request)).thenReturn(errors);
        when(errors.hasErrors()).thenReturn(false);
        
        when(keyIdGenerator.generateRandom()).thenReturn(LOAN_ID);

        when(transformer.transform(loan)).thenReturn(loanEntity);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        when(repository.insert(loanEntity)).thenThrow(MongoException.class);

        assertThrows(DataException.class, () ->
                loanService.create(loan, transaction, COMPANY_ACCOUNTS_ID, request));

        assertMetaDataSetOnRestObject();
        assertIdGeneratedForDatabaseEntity();
        assertRepositoryInsertCalled();
        assertWhetherLoansToDirectorsServiceCalledToAddLoan(false);
    }

    @Test
    @DisplayName("Tests the creation of a loan resource where the validator returns errors")
    void createLoanWithValidationErrors() throws DataException {

    	when(loanValidator.validateLoan(loan, transaction, COMPANY_ACCOUNTS_ID, request)).thenReturn(errors);
        when(errors.hasErrors()).thenReturn(true);
        
        ResponseObject<Loan> response =
                loanService.create(loan, transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(ResponseStatus.VALIDATION_ERROR, response.getStatus());
        assertNull(response.getData());
        assertNotNull(response.getErrors());
    }

    @Test
    @DisplayName("Tests the successful update of a loan resource")
    void updateLoanSuccess() throws DataException {

        when(loanValidator.validateLoan(loan, transaction, COMPANY_ACCOUNTS_ID, request)).thenReturn(errors);

        when(request.getRequestURI()).thenReturn(URI);

        when(transformer.transform(loan)).thenReturn(loanEntity);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        ResponseObject<Loan> response =
                loanService.update(loan, transaction, COMPANY_ACCOUNTS_ID, request);

        assertMetaDataSetOnRestObject();
        assertIdGeneratedForDatabaseEntity();
        assertRepositoryUpdateCalled();
        assertEquals(ResponseStatus.UPDATED, response.getStatus());
        assertEquals(loan, response.getData());
    }

    @Test
    @DisplayName("Tests the update of a loan resource where the repository throws a Mongo exception")
    void updateLoanMongoException() throws DataException {

        when(loanValidator.validateLoan(loan, transaction, COMPANY_ACCOUNTS_ID, request)).thenReturn(errors);
        when(request.getRequestURI()).thenReturn(URI);

        when(transformer.transform(loan)).thenReturn(loanEntity);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        when(repository.save(loanEntity)).thenThrow(MongoException.class);

        assertThrows(DataException.class, () ->
                loanService.update(loan, transaction, COMPANY_ACCOUNTS_ID, request));

        assertMetaDataSetOnRestObject();
        assertIdGeneratedForDatabaseEntity();
        assertRepositoryUpdateCalled();
    }

    @Test
    @DisplayName("Tests the update of a loan resource where the validator returns errors")
    void updateLoanWithValidationErrors() throws DataException {

    	when(loanValidator.validateLoan(loan, transaction, COMPANY_ACCOUNTS_ID, request)).thenReturn(errors);
        when(errors.hasErrors()).thenReturn(true);
        
        ResponseObject<Loan> response =
                loanService.update(loan, transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(ResponseStatus.VALIDATION_ERROR, response.getStatus());
        assertNull(response.getData());
        assertNotNull(response.getErrors());
    }

    @Test
    @DisplayName("Tests the successful retrieval of a loan resource")
    void getLoanSuccess() throws DataException {

        when(request.getRequestURI()).thenReturn(URI);

        when(repository.findById(LOAN_ID)).thenReturn(Optional.of(loanEntity));
        when(transformer.transform(loanEntity)).thenReturn(loan);

        ResponseObject<Loan> response =
                loanService.find(COMPANY_ACCOUNTS_ID, request);

        assertRepositoryFindByIdCalled();
        assertEquals(ResponseStatus.FOUND, response.getStatus());
        assertEquals(loan, response.getData());
    }

    @Test
    @DisplayName("Tests the retrieval of a non-existent loan resource")
    void getLoanNotFound() throws DataException {

        when(request.getRequestURI()).thenReturn(URI);

        when(repository.findById(LOAN_ID)).thenReturn(Optional.empty());

        ResponseObject<Loan> response =
                loanService.find(COMPANY_ACCOUNTS_ID, request);

        assertRepositoryFindByIdCalled();
        assertEquals(ResponseStatus.NOT_FOUND, response.getStatus());
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Tests the retrieval of a loan resource where the repository throws a Mongo exception")
    void getLoanMongoException() {

        when(request.getRequestURI()).thenReturn(URI);

        when(repository.findById(LOAN_ID)).thenThrow(MongoException.class);

        assertThrows(DataException.class, () ->
                loanService.find(COMPANY_ACCOUNTS_ID, request));

        assertRepositoryFindByIdCalled();
    }

    @Test
    @DisplayName("Tests the successful retrieval of all loans")
    void getAllLoansSuccess() throws DataException {

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        LoanEntity[] entities = new LoanEntity[]{loanEntity};
        when(repository.findAllLoans(LOAN_LINK)).thenReturn(entities);

        Loan[] directors = new Loan[]{loan};
        when(transformer.transform(entities)).thenReturn(directors);

        ResponseObject<Loan> response = loanService.findAll(transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(ResponseStatus.FOUND, response.getStatus());
        assertEquals(directors, response.getDataForMultipleResources());
    }

    @Test
    @DisplayName("Tests the retrieval of all loans where none exist")
    void getAllLoansNotFound() throws DataException {

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        LoanEntity[] entities = new LoanEntity[0];
        when(repository.findAllLoans(LOAN_LINK)).thenReturn(entities);

        ResponseObject<Loan> response = loanService.findAll(transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(ResponseStatus.NOT_FOUND, response.getStatus());
        assertNull(response.getDataForMultipleResources());
    }

    @Test
    @DisplayName("Tests the retrieval of all loans where the repository throws a MongoException")
    void getAllLoansThrowsMongoException() {

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        when(repository.findAllLoans(LOAN_LINK)).thenThrow(MongoException.class);

        assertThrows(DataException.class,
                () -> loanService.findAll(transaction, COMPANY_ACCOUNTS_ID, request));
    }

    @Test
    @DisplayName("Tests the successful deletion of a loan resource")
    void deleteLoanSuccess() throws DataException {

        when(request.getRequestURI()).thenReturn(URI);

        when(repository.existsById(LOAN_ID)).thenReturn(true);

        ResponseObject<Loan> response =
                loanService.delete(COMPANY_ACCOUNTS_ID, request);

        assertRepositoryDeleteByIdCalled();
        assertWhetherLoansToDirectorsServiceCalledToRemoveLoan(true);
        assertEquals(ResponseStatus.UPDATED, response.getStatus());
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Tests the deletion of a loan resource where the repository throws a Mongo exception")
    void deleteLoanMongoException() throws DataException {

        when(request.getRequestURI()).thenReturn(URI);

        when(repository.existsById(LOAN_ID)).thenReturn(true);
        doThrow(MongoException.class).when(repository).deleteById(LOAN_ID);

        assertThrows(DataException.class, () ->
                loanService.delete(COMPANY_ACCOUNTS_ID, request));

        assertRepositoryDeleteByIdCalled();
        assertWhetherLoansToDirectorsServiceCalledToRemoveLoan(false);
    }

    @Test
    @DisplayName("Tests the deletion of a non-existent loan resource")
    void deleteLoanNotFound() throws DataException {

        when(request.getRequestURI()).thenReturn(URI);

        when(repository.existsById(LOAN_ID)).thenReturn(false);

        ResponseObject<Loan> response =
                loanService.delete(COMPANY_ACCOUNTS_ID, request);

        verify(repository, never()).deleteById(LOAN_ID);
        assertWhetherLoansToDirectorsServiceCalledToRemoveLoan(false);
        assertEquals(ResponseStatus.NOT_FOUND, response.getStatus());
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Tests the successful deletion of all loans")
    void deleteAllLoansSuccess() throws DataException {

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        ResponseObject<Loan> response = loanService.deleteAll(transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(ResponseStatus.UPDATED, response.getStatus());
        assertNull(response.getDataForMultipleResources());

        verify(repository, times(1)).deleteAllLoans(LOAN_LINK);
    }

    @Test
    @DisplayName("Tests the deletion of all loans where the repository throws a MongoException")
    void deleteAllLoansThrowsMongoException() {

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        doThrow(MongoException.class).when(repository).deleteAllLoans(LOAN_LINK);

        assertThrows(DataException.class,
                () -> loanService.deleteAll(transaction, COMPANY_ACCOUNTS_ID, request));
    }

    private void assertMetaDataSetOnRestObject() {
        verify(loan, times(1)).setKind(Kind.LOANS_TO_DIRECTORS_LOANS.getValue());
        verify(loan, times(1)).setEtag(anyString());
        verify(loan, times(1)).setLinks(anyMap());
    }

    private void assertIdGeneratedForDatabaseEntity() {
        verify(loanEntity, times(1)).setId(LOAN_ID);
    }

    private void assertRepositoryInsertCalled() {
        verify(repository, times(1)).insert(loanEntity);
    }

    private void assertRepositoryUpdateCalled() {
        verify(repository, times(1)).save(loanEntity);
    }

    private void assertRepositoryFindByIdCalled() {
        verify(repository, times(1)).findById(LOAN_ID);
    }

    private void assertRepositoryDeleteByIdCalled() {
        verify(repository, times(1)).deleteById(LOAN_ID);
    }

    private void assertWhetherLoansToDirectorsServiceCalledToAddLoan(boolean isServiceExpected) throws DataException {

        VerificationMode timesExpected = isServiceExpected ? times(1) : never();
        verify(loansToDirectorsService, timesExpected)
                .addLoan(COMPANY_ACCOUNTS_ID, LOAN_ID, LOAN_SELF_LINK, request);
    }

    private void assertWhetherLoansToDirectorsServiceCalledToRemoveLoan(boolean isServiceExpected) throws DataException {

        VerificationMode timesExpected = isServiceExpected ? VerificationModeFactory.times(1) : never();
        verify(loansToDirectorsService, timesExpected)
                .removeLoan(COMPANY_ACCOUNTS_ID, LOAN_ID, request);
    }
}