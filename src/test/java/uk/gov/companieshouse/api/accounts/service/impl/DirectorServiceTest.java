package uk.gov.companieshouse.api.accounts.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mongodb.MongoException;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.verification.VerificationMode;
import org.springframework.dao.DuplicateKeyException;
import uk.gov.companieshouse.api.accounts.Kind;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.BasicLinkType;
import uk.gov.companieshouse.api.accounts.links.LoansToDirectorsLinkType;
import uk.gov.companieshouse.api.accounts.model.entity.directorsreport.DirectorEntity;
import uk.gov.companieshouse.api.accounts.model.rest.directorsreport.Director;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.loanstodirectors.LoansToDirectors;
import uk.gov.companieshouse.api.accounts.repository.DirectorRepository;
import uk.gov.companieshouse.api.accounts.service.DirectorsReportService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transformer.DirectorTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.model.transaction.TransactionLinks;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DirectorServiceTest {

    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";
    private static final String DIRECTOR_ID = "directorId";
    private static final String TRANSACTION_SELF_LINK = "transactionSelfLink";
    private static final String DIRECTOR_SELF_LINK = "directorSelfLink";
    private static final String LOANS_TO_DIRECTORS_ADDITIONAL_INFO_LINK = "ltdAdditionalInfoLink";

    private static final String URI = "/transactions/transactionId/company-accounts/" +
                                        COMPANY_ACCOUNTS_ID + "/small-full/directors-report" +
                                        "/directors/" + DIRECTOR_ID;

    private static final String DIRECTORS_LINK = TRANSACTION_SELF_LINK + "/company-accounts/" +
                                                COMPANY_ACCOUNTS_ID + "/small-full/directors-report" +
                                                "/directors";

    private static final boolean LOANS_TO_DIRECTORS_EXISTS = true;
    private static final boolean LOANS_TO_DIRECTORS_ABSENT = false;
    private static final boolean LOANS_TO_DIRECTORS_ADDITIONAL_INFO_EXISTS = true;
    private static final boolean LOANS_TO_DIRECTORS_ADDITIONAL_INFO_ABSENT = false;
    private static final boolean DIRECTORS_APPROVAL_EXISTS = true;
    private static final boolean DIRECTORS_APPROVAL_ABSENT = false;
    private static final boolean APPROVAL_EXISTS = true;
    private static final boolean APPROVAL_ABSENT = false;

    @Mock
    private DirectorTransformer transformer;

    @Mock
    private DirectorRepository repository;

    @Mock
    private DirectorsReportService directorsReportService;

    @Mock
    private KeyIdGenerator keyIdGenerator;

    @Mock
    private Director director;

    @Mock
    private Transaction transaction;

    @Mock
    private TransactionLinks transactionLinks;

    @Mock
    private HttpServletRequest request;

    @Mock
    private DirectorEntity directorEntity;

    @Mock
    private Map<String, String> directorLinks;

    @Mock
    private Map<String, String> loansToDirectorLinks;

    @Mock
    private LoanServiceImpl loanService;

    @Mock
    private LoansToDirectorsServiceImpl loansToDirectorsService;

    @Mock
    private LoansToDirectors loansToDirectors;

    @Mock
    private DirectorsApprovalService directorsApprovalService;

    @Mock
    private ApprovalService approvalService;

    @InjectMocks
    private DirectorService directorService;

    @Test
    @DisplayName("Tests the successful creation of a director resource, loans to directors resource absent")
    void createDirectorSuccessLtdAndApprovalsNotPresent() throws DataException {

        when(keyIdGenerator.generateRandom()).thenReturn(DIRECTOR_ID);

        when(transformer.transform(director)).thenReturn(directorEntity);

        expectDirectorsResourceChangedForLoansToDirectors(LOANS_TO_DIRECTORS_ABSENT, LOANS_TO_DIRECTORS_ADDITIONAL_INFO_ABSENT);
        expectDirectorsResourceChangedForApprovals(DIRECTORS_APPROVAL_ABSENT, APPROVAL_ABSENT);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        when(director.getLinks()).thenReturn(directorLinks);
        when(directorLinks.get(BasicLinkType.SELF.getLink())).thenReturn(DIRECTOR_SELF_LINK);

        ResponseObject<Director> response =
                directorService.create(director, transaction, COMPANY_ACCOUNTS_ID, request);

        assertMetaDataSetOnRestObject();
        assertIdGeneratedForDatabaseEntity();
        assertRepositoryInsertCalled();
        assertWhetherDirectorsReportServiceCalledToAddDirector(true);
        assertEquals(ResponseStatus.CREATED, response.getStatus());
        assertEquals(director, response.getData());
        assertWhetherAllLoansDeleted(false);
        assertWhetherLoansToDirectorsDeleted(false);
    }

    @Test
    @DisplayName("Tests the successful creation of a director resource, loans to directors resource exists, LTD additional info present")
    void createDirectorSuccessLtdPresentAdditionalInfoPresent() throws DataException {

        when(keyIdGenerator.generateRandom()).thenReturn(DIRECTOR_ID);

        when(transformer.transform(director)).thenReturn(directorEntity);

        expectDirectorsResourceChangedForLoansToDirectors(LOANS_TO_DIRECTORS_EXISTS, LOANS_TO_DIRECTORS_ADDITIONAL_INFO_EXISTS);
        expectDirectorsResourceChangedForApprovals(DIRECTORS_APPROVAL_ABSENT, APPROVAL_ABSENT);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        when(director.getLinks()).thenReturn(directorLinks);
        when(directorLinks.get(BasicLinkType.SELF.getLink())).thenReturn(DIRECTOR_SELF_LINK);

        ResponseObject<Director> response =
                directorService.create(director, transaction, COMPANY_ACCOUNTS_ID, request);

        assertMetaDataSetOnRestObject();
        assertIdGeneratedForDatabaseEntity();
        assertRepositoryInsertCalled();
        assertWhetherDirectorsReportServiceCalledToAddDirector(true);
        assertEquals(ResponseStatus.CREATED, response.getStatus());
        assertEquals(director, response.getData());
        assertWhetherAllLoansDeleted(true);
        assertWhetherLoansToDirectorsDeleted(false);
    }

    @Test
    @DisplayName("Tests the successful creation of a director resource, loans to directors resource exists, LTD additional info absent")
    void createDirectorSuccessLtdPresentAdditionalInfoNotPresent() throws DataException {

        when(keyIdGenerator.generateRandom()).thenReturn(DIRECTOR_ID);

        when(transformer.transform(director)).thenReturn(directorEntity);

        expectDirectorsResourceChangedForLoansToDirectors(LOANS_TO_DIRECTORS_EXISTS, LOANS_TO_DIRECTORS_ADDITIONAL_INFO_ABSENT);
        expectDirectorsResourceChangedForApprovals(DIRECTORS_APPROVAL_ABSENT, APPROVAL_ABSENT);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        when(director.getLinks()).thenReturn(directorLinks);
        when(directorLinks.get(BasicLinkType.SELF.getLink())).thenReturn(DIRECTOR_SELF_LINK);

        ResponseObject<Director> response =
                directorService.create(director, transaction, COMPANY_ACCOUNTS_ID, request);

        assertMetaDataSetOnRestObject();
        assertIdGeneratedForDatabaseEntity();
        assertRepositoryInsertCalled();
        assertWhetherDirectorsReportServiceCalledToAddDirector(true);
        assertEquals(ResponseStatus.CREATED, response.getStatus());
        assertEquals(director, response.getData());
        assertWhetherAllLoansDeleted(false);
        assertWhetherLoansToDirectorsDeleted(true);
    }

    @Test
    @DisplayName("Tests the successful creation of a director resource, directors approval resource present, accounts approval present")
    void createDirectorSuccessDirectorsApprovalPresentAccountApprovalPresent() throws DataException {

        when(keyIdGenerator.generateRandom()).thenReturn(DIRECTOR_ID);

        when(transformer.transform(director)).thenReturn(directorEntity);

        expectDirectorsResourceChangedForLoansToDirectors(LOANS_TO_DIRECTORS_ABSENT, LOANS_TO_DIRECTORS_ADDITIONAL_INFO_ABSENT);
        expectDirectorsResourceChangedForApprovals(DIRECTORS_APPROVAL_EXISTS, APPROVAL_EXISTS);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        when(director.getLinks()).thenReturn(directorLinks);
        when(directorLinks.get(BasicLinkType.SELF.getLink())).thenReturn(DIRECTOR_SELF_LINK);

        ResponseObject<Director> response =
                directorService.create(director, transaction, COMPANY_ACCOUNTS_ID, request);

        assertMetaDataSetOnRestObject();
        assertIdGeneratedForDatabaseEntity();
        assertRepositoryInsertCalled();
        assertWhetherDirectorsReportServiceCalledToAddDirector(true);
        assertEquals(ResponseStatus.CREATED, response.getStatus());
        assertEquals(director, response.getData());
        assertWhetherDirectorsApprovalDeleted(true);
        assertWhetherAccountsApprovalDeleted(true);
    }

    @Test
    @DisplayName("Tests the successful creation of a director resource, directors approval resource present, accounts approval absent")
    void createDirectorSuccessDirectorsApprovalPresentAccountApprovalAbsent() throws DataException {

        when(keyIdGenerator.generateRandom()).thenReturn(DIRECTOR_ID);

        when(transformer.transform(director)).thenReturn(directorEntity);

        expectDirectorsResourceChangedForLoansToDirectors(LOANS_TO_DIRECTORS_ABSENT, LOANS_TO_DIRECTORS_ADDITIONAL_INFO_ABSENT);
        expectDirectorsResourceChangedForApprovals(DIRECTORS_APPROVAL_EXISTS, APPROVAL_ABSENT);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        when(director.getLinks()).thenReturn(directorLinks);
        when(directorLinks.get(BasicLinkType.SELF.getLink())).thenReturn(DIRECTOR_SELF_LINK);

        ResponseObject<Director> response =
                directorService.create(director, transaction, COMPANY_ACCOUNTS_ID, request);

        assertMetaDataSetOnRestObject();
        assertIdGeneratedForDatabaseEntity();
        assertRepositoryInsertCalled();
        assertWhetherDirectorsReportServiceCalledToAddDirector(true);
        assertEquals(ResponseStatus.CREATED, response.getStatus());
        assertEquals(director, response.getData());
        assertWhetherDirectorsApprovalDeleted(true);
        assertWhetherAccountsApprovalDeleted(false);
    }

    @Test
    @DisplayName("Tests the successful creation of a director resource, directors approval resource absent, accounts approval present")
    void createDirectorSuccessDirectorsApprovalAbsentAccountApprovalPresent() throws DataException {

        when(keyIdGenerator.generateRandom()).thenReturn(DIRECTOR_ID);

        when(transformer.transform(director)).thenReturn(directorEntity);

        expectDirectorsResourceChangedForLoansToDirectors(LOANS_TO_DIRECTORS_ABSENT, LOANS_TO_DIRECTORS_ADDITIONAL_INFO_ABSENT);
        expectDirectorsResourceChangedForApprovals(DIRECTORS_APPROVAL_ABSENT, APPROVAL_EXISTS);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        when(director.getLinks()).thenReturn(directorLinks);
        when(directorLinks.get(BasicLinkType.SELF.getLink())).thenReturn(DIRECTOR_SELF_LINK);

        ResponseObject<Director> response =
                directorService.create(director, transaction, COMPANY_ACCOUNTS_ID, request);

        assertMetaDataSetOnRestObject();
        assertIdGeneratedForDatabaseEntity();
        assertRepositoryInsertCalled();
        assertWhetherDirectorsReportServiceCalledToAddDirector(true);
        assertEquals(ResponseStatus.CREATED, response.getStatus());
        assertEquals(director, response.getData());
        assertWhetherDirectorsApprovalDeleted(false);
        assertWhetherAccountsApprovalDeleted(true);
    }

    @Test
    @DisplayName("Tests the creation of a director resource where the repository throws a duplicate key exception")
    void createDirectorDuplicateKeyException() throws DataException {

        when(keyIdGenerator.generateRandom()).thenReturn(DIRECTOR_ID);

        when(transformer.transform(director)).thenReturn(directorEntity);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        when(repository.insert(directorEntity)).thenThrow(DuplicateKeyException.class);

        ResponseObject<Director> response =
                directorService.create(director, transaction, COMPANY_ACCOUNTS_ID, request);

        assertMetaDataSetOnRestObject();
        assertIdGeneratedForDatabaseEntity();
        assertRepositoryInsertCalled();
        assertWhetherDirectorsReportServiceCalledToAddDirector(false);
        assertEquals(ResponseStatus.DUPLICATE_KEY_ERROR, response.getStatus());
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Tests the creation of a director resource where the repository throws a Mongo exception")
    void createDirectorMongoException() throws DataException {

        when(keyIdGenerator.generateRandom()).thenReturn(DIRECTOR_ID);

        when(transformer.transform(director)).thenReturn(directorEntity);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        when(repository.insert(directorEntity)).thenThrow(MongoException.class);

        assertThrows(DataException.class, () ->
                directorService.create(director, transaction, COMPANY_ACCOUNTS_ID, request));

        assertMetaDataSetOnRestObject();
        assertIdGeneratedForDatabaseEntity();
        assertRepositoryInsertCalled();
        assertWhetherDirectorsReportServiceCalledToAddDirector(false);
    }

    @Test
    @DisplayName("Tests the successful update of a director resource,, loans to directors resource absent")
    void updateDirectorNoLoansToDirectorsSuccess() throws DataException {

        when(request.getRequestURI()).thenReturn(URI);

        when(transformer.transform(director)).thenReturn(directorEntity);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        expectDirectorsResourceChangedForLoansToDirectors(LOANS_TO_DIRECTORS_ABSENT, LOANS_TO_DIRECTORS_ADDITIONAL_INFO_ABSENT);
        expectDirectorsResourceChangedForApprovals(DIRECTORS_APPROVAL_ABSENT, APPROVAL_ABSENT);

        ResponseObject<Director> response =
                directorService.update(director, transaction, COMPANY_ACCOUNTS_ID, request);

        assertMetaDataSetOnRestObject();
        assertIdGeneratedForDatabaseEntity();
        assertRepositoryUpdateCalled();
        assertEquals(ResponseStatus.UPDATED, response.getStatus());
        assertEquals(director, response.getData());
        assertWhetherAllLoansDeleted(false);
        assertWhetherLoansToDirectorsDeleted(false);
    }

    @Test
    @DisplayName("Tests the successful update of a director resource,, loans to directors resource exists, LTD additional info exists")
    void updateDirectorWithLoansToDirectorsAndAdditionalInfoSuccess() throws DataException {

        when(request.getRequestURI()).thenReturn(URI);

        when(transformer.transform(director)).thenReturn(directorEntity);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        expectDirectorsResourceChangedForLoansToDirectors(LOANS_TO_DIRECTORS_EXISTS, LOANS_TO_DIRECTORS_ADDITIONAL_INFO_EXISTS);
        expectDirectorsResourceChangedForApprovals(DIRECTORS_APPROVAL_ABSENT, APPROVAL_ABSENT);

        ResponseObject<Director> response =
                directorService.update(director, transaction, COMPANY_ACCOUNTS_ID, request);

        assertMetaDataSetOnRestObject();
        assertIdGeneratedForDatabaseEntity();
        assertRepositoryUpdateCalled();
        assertEquals(ResponseStatus.UPDATED, response.getStatus());
        assertEquals(director, response.getData());
        assertWhetherAllLoansDeleted(true);
        assertWhetherLoansToDirectorsDeleted(false);
    }

    @Test
    @DisplayName("Tests the successful update of a director resource,, directors approval resource exists, accounts approval absent")
    void updateDirectorSuccessDirectorsApprovalExistsAccountsApprovalAbsent() throws DataException {

        when(request.getRequestURI()).thenReturn(URI);

        when(transformer.transform(director)).thenReturn(directorEntity);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        expectDirectorsResourceChangedForLoansToDirectors(LOANS_TO_DIRECTORS_ABSENT, LOANS_TO_DIRECTORS_ADDITIONAL_INFO_ABSENT);
        expectDirectorsResourceChangedForApprovals(DIRECTORS_APPROVAL_EXISTS, APPROVAL_ABSENT);

        ResponseObject<Director> response =
                directorService.update(director, transaction, COMPANY_ACCOUNTS_ID, request);

        assertMetaDataSetOnRestObject();
        assertIdGeneratedForDatabaseEntity();
        assertRepositoryUpdateCalled();
        assertEquals(ResponseStatus.UPDATED, response.getStatus());
        assertEquals(director, response.getData());
        assertWhetherDirectorsApprovalDeleted(true);
        assertWhetherAccountsApprovalDeleted(false);
    }

    @Test
    @DisplayName("Tests the successful update of a director resource,, directors approval resource absent, accounts approval exists")
    void updateDirectorSuccessDirectorsApprovalAbsentAccountsApprovalExists() throws DataException {

        when(request.getRequestURI()).thenReturn(URI);

        when(transformer.transform(director)).thenReturn(directorEntity);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        expectDirectorsResourceChangedForLoansToDirectors(LOANS_TO_DIRECTORS_EXISTS, LOANS_TO_DIRECTORS_ADDITIONAL_INFO_EXISTS);
        expectDirectorsResourceChangedForApprovals(DIRECTORS_APPROVAL_ABSENT, APPROVAL_EXISTS);

        ResponseObject<Director> response =
                directorService.update(director, transaction, COMPANY_ACCOUNTS_ID, request);

        assertMetaDataSetOnRestObject();
        assertIdGeneratedForDatabaseEntity();
        assertRepositoryUpdateCalled();
        assertEquals(ResponseStatus.UPDATED, response.getStatus());
        assertEquals(director, response.getData());
        assertWhetherDirectorsApprovalDeleted(false);
        assertWhetherAccountsApprovalDeleted(true);
    }

    @Test
    @DisplayName("Tests the update of a director resource where the repository throws a Mongo exception")
    void updateDirectorMongoException() {

        when(request.getRequestURI()).thenReturn(URI);

        when(transformer.transform(director)).thenReturn(directorEntity);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        when(repository.save(directorEntity)).thenThrow(MongoException.class);

        assertThrows(DataException.class, () ->
                directorService.update(director, transaction, COMPANY_ACCOUNTS_ID, request));

        assertMetaDataSetOnRestObject();
        assertIdGeneratedForDatabaseEntity();
        assertRepositoryUpdateCalled();
    }

    @Test
    @DisplayName("Tests the successful retrieval of a director resource")
    void getDirectorSuccess() throws DataException {

        when(request.getRequestURI()).thenReturn(URI);

        when(repository.findById(DIRECTOR_ID)).thenReturn(Optional.of(directorEntity));
        when(transformer.transform(directorEntity)).thenReturn(director);

        ResponseObject<Director> response =
                directorService.find(COMPANY_ACCOUNTS_ID, request);

        assertRepositoryFindByIdCalled();
        assertEquals(ResponseStatus.FOUND, response.getStatus());
        assertEquals(director, response.getData());
    }

    @Test
    @DisplayName("Tests the retrieval of a non-existent director resource")
    void getDirectorNotFound() throws DataException {

        when(request.getRequestURI()).thenReturn(URI);

        when(repository.findById(DIRECTOR_ID)).thenReturn(Optional.empty());

        ResponseObject<Director> response =
                directorService.find(COMPANY_ACCOUNTS_ID, request);

        assertRepositoryFindByIdCalled();
        assertEquals(ResponseStatus.NOT_FOUND, response.getStatus());
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Tests the retrieval of a director resource where the repository throws a Mongo exception")
    void getDirectorMongoException() {

        when(request.getRequestURI()).thenReturn(URI);

        when(repository.findById(DIRECTOR_ID)).thenThrow(MongoException.class);

        assertThrows(DataException.class, () ->
                directorService.find(COMPANY_ACCOUNTS_ID, request));

        assertRepositoryFindByIdCalled();
    }

    @Test
    @DisplayName("Tests the successful retrieval of all directors")
    void getAllDirectorsSuccess() throws DataException {

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        DirectorEntity[] entities = new DirectorEntity[]{directorEntity};
        when(repository.findAllDirectors(DIRECTORS_LINK)).thenReturn(entities);

        Director[] directors = new Director[]{director};
        when(transformer.transform(entities)).thenReturn(directors);

        ResponseObject<Director> response = directorService.findAll(transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(ResponseStatus.FOUND, response.getStatus());
        assertEquals(directors, response.getDataForMultipleResources());
    }

    @Test
    @DisplayName("Tests the retrieval of all directors where none exist")
    void getAllDirectorsNotFound() throws DataException {

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        DirectorEntity[] entities = new DirectorEntity[0];
        when(repository.findAllDirectors(DIRECTORS_LINK)).thenReturn(entities);

        ResponseObject<Director> response = directorService.findAll(transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(ResponseStatus.NOT_FOUND, response.getStatus());
        assertNull(response.getDataForMultipleResources());
    }

    @Test
    @DisplayName("Tests the retrieval of all directors where the repository throws a MongoException")
    void getAllDirectorsThrowsMongoException() {

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        when(repository.findAllDirectors(DIRECTORS_LINK)).thenThrow(MongoException.class);

        assertThrows(DataException.class,
                () -> directorService.findAll(transaction, COMPANY_ACCOUNTS_ID, request));
    }

    @Test
    @DisplayName("Tests the successful deletion of a director resource, loans to directors and approvals resources absent")
    void deleteDirectorNoLoansToDirectorsOrApprovalsSuccess() throws DataException {

        when(request.getRequestURI()).thenReturn(URI);

        when(repository.existsById(DIRECTOR_ID)).thenReturn(true);

        expectDirectorsResourceChangedForLoansToDirectors(LOANS_TO_DIRECTORS_ABSENT, LOANS_TO_DIRECTORS_ADDITIONAL_INFO_ABSENT);
        expectDirectorsResourceChangedForApprovals(DIRECTORS_APPROVAL_ABSENT, APPROVAL_ABSENT);

        ResponseObject<Director> response =
                directorService.delete(COMPANY_ACCOUNTS_ID, request);

        assertRepositoryDeleteByIdCalled();
        assertWhetherDirectorsReportServiceCalledToRemoveDirector(true);
        assertEquals(ResponseStatus.UPDATED, response.getStatus());
        assertNull(response.getData());

        assertWhetherAllLoansDeleted(false);
        assertWhetherLoansToDirectorsDeleted(false);
    }

    @Test
    @DisplayName("Tests the successful deletion of a director resource, loans to directors resource exists, LTD additional info absent")
    void deleteDirectorWithLoansToDirectorsSuccess() throws DataException {

        when(request.getRequestURI()).thenReturn(URI);

        when(repository.existsById(DIRECTOR_ID)).thenReturn(true);

        expectDirectorsResourceChangedForLoansToDirectors(LOANS_TO_DIRECTORS_EXISTS, LOANS_TO_DIRECTORS_ADDITIONAL_INFO_ABSENT);
        expectDirectorsResourceChangedForApprovals(DIRECTORS_APPROVAL_ABSENT, APPROVAL_ABSENT);

        ResponseObject<Director> response =
                directorService.delete(COMPANY_ACCOUNTS_ID, request);

        assertRepositoryDeleteByIdCalled();
        assertWhetherDirectorsReportServiceCalledToRemoveDirector(true);
        assertEquals(ResponseStatus.UPDATED, response.getStatus());
        assertNull(response.getData());

        assertWhetherAllLoansDeleted(false);
        assertWhetherLoansToDirectorsDeleted(true);
    }

    @Test
    @DisplayName("Tests the successful deletion of a director resource, directors approval exists, accounts approval absent")
    void deleteDirectorWithDirectorsApprovalSuccess() throws DataException {

        when(request.getRequestURI()).thenReturn(URI);

        when(repository.existsById(DIRECTOR_ID)).thenReturn(true);

        expectDirectorsResourceChangedForLoansToDirectors(LOANS_TO_DIRECTORS_ABSENT, LOANS_TO_DIRECTORS_ADDITIONAL_INFO_ABSENT);
        expectDirectorsResourceChangedForApprovals(DIRECTORS_APPROVAL_EXISTS, APPROVAL_ABSENT);

        ResponseObject<Director> response =
                directorService.delete(COMPANY_ACCOUNTS_ID, request);

        assertRepositoryDeleteByIdCalled();
        assertWhetherDirectorsReportServiceCalledToRemoveDirector(true);
        assertEquals(ResponseStatus.UPDATED, response.getStatus());
        assertNull(response.getData());

        assertWhetherDirectorsApprovalDeleted(true);
        assertWhetherAccountsApprovalDeleted(false);
    }

    @Test
    @DisplayName("Tests the successful deletion of a director resource, directors approval absent, accounts approval exist")
    void deleteDirectorWithAccountsApprovalSuccess() throws DataException {

        when(request.getRequestURI()).thenReturn(URI);

        when(repository.existsById(DIRECTOR_ID)).thenReturn(true);

        expectDirectorsResourceChangedForLoansToDirectors(LOANS_TO_DIRECTORS_ABSENT, LOANS_TO_DIRECTORS_ADDITIONAL_INFO_ABSENT);
        expectDirectorsResourceChangedForApprovals(DIRECTORS_APPROVAL_ABSENT, APPROVAL_EXISTS);

        ResponseObject<Director> response =
                directorService.delete(COMPANY_ACCOUNTS_ID, request);

        assertRepositoryDeleteByIdCalled();
        assertWhetherDirectorsReportServiceCalledToRemoveDirector(true);
        assertEquals(ResponseStatus.UPDATED, response.getStatus());
        assertNull(response.getData());

        assertWhetherDirectorsApprovalDeleted(false);
        assertWhetherAccountsApprovalDeleted(true);
    }
    
    @Test
    @DisplayName("Tests the deletion of a director resource where the repository throws a Mongo exception")
    void deleteDirectorMongoException() throws DataException {

        when(request.getRequestURI()).thenReturn(URI);

        when(repository.existsById(DIRECTOR_ID)).thenReturn(true);
        doThrow(MongoException.class).when(repository).deleteById(DIRECTOR_ID);

        assertThrows(DataException.class, () ->
                directorService.delete(COMPANY_ACCOUNTS_ID, request));

        assertRepositoryDeleteByIdCalled();
        assertWhetherDirectorsReportServiceCalledToRemoveDirector(false);
    }

    @Test
    @DisplayName("Tests the deletion of a non-existent director resource")
    void deleteDirectorNotFound() throws DataException {

        when(request.getRequestURI()).thenReturn(URI);

        when(repository.existsById(DIRECTOR_ID)).thenReturn(false);

        ResponseObject<Director> response =
                directorService.delete(COMPANY_ACCOUNTS_ID, request);

        verify(repository, never()).deleteById(DIRECTOR_ID);
        assertWhetherDirectorsReportServiceCalledToRemoveDirector(false);
        assertEquals(ResponseStatus.NOT_FOUND, response.getStatus());
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Tests the successful deletion of all directors, loans to directors resource absent")
    void deleteAllDirectorsNoLoansToDirectorsSuccess() throws DataException {

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        expectDirectorsResourceChangedForLoansToDirectors(LOANS_TO_DIRECTORS_ABSENT, LOANS_TO_DIRECTORS_ADDITIONAL_INFO_ABSENT);
        expectDirectorsResourceChangedForApprovals(DIRECTORS_APPROVAL_ABSENT, APPROVAL_ABSENT);

        ResponseObject<Director> response = directorService.deleteAll(transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(ResponseStatus.UPDATED, response.getStatus());
        assertNull(response.getDataForMultipleResources());

        verify(repository, times(1)).deleteAllDirectors(DIRECTORS_LINK);

        assertWhetherAllLoansDeleted(false);
        assertWhetherLoansToDirectorsDeleted(false);
    }

    @Test
    @DisplayName("Tests the successful deletion of all directors, loans to directors resource exists, LTD additional info absent")
    void deleteAllDirectorsWithLoansToDirectorsSuccess() throws DataException {

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        expectDirectorsResourceChangedForLoansToDirectors(LOANS_TO_DIRECTORS_EXISTS, LOANS_TO_DIRECTORS_ADDITIONAL_INFO_ABSENT);
        expectDirectorsResourceChangedForApprovals(DIRECTORS_APPROVAL_ABSENT, APPROVAL_ABSENT);
        
        ResponseObject<Director> response = directorService.deleteAll(transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(ResponseStatus.UPDATED, response.getStatus());
        assertNull(response.getDataForMultipleResources());

        verify(repository, times(1)).deleteAllDirectors(DIRECTORS_LINK);

        assertWhetherAllLoansDeleted(false);
        assertWhetherLoansToDirectorsDeleted(true);
    }

    @Test
    @DisplayName("Tests the deletion of all directors where the repository throws a MongoException")
    void deleteAllDirectorsThrowsMongoException() {

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        doThrow(MongoException.class).when(repository).deleteAllDirectors(DIRECTORS_LINK);

        assertThrows(DataException.class,
                () -> directorService.deleteAll(transaction, COMPANY_ACCOUNTS_ID, request));
    }

    private void assertMetaDataSetOnRestObject() {
        verify(director, times(1)).setKind(Kind.DIRECTORS_REPORT_DIRECTOR.getValue());
        verify(director, times(1)).setEtag(anyString());
        verify(director, times(1)).setLinks(anyMap());
    }

    private void assertIdGeneratedForDatabaseEntity() {
        verify(directorEntity, times(1)).setId(DIRECTOR_ID);
    }

    private void assertRepositoryInsertCalled() {
        verify(repository, times(1)).insert(directorEntity);
    }

    private void assertRepositoryUpdateCalled() {
        verify(repository, times(1)).save(directorEntity);
    }

    private void assertRepositoryFindByIdCalled() {
        verify(repository, times(1)).findById(DIRECTOR_ID);
    }

    private void assertRepositoryDeleteByIdCalled() {
        verify(repository, times(1)).deleteById(DIRECTOR_ID);
    }

    private void assertWhetherDirectorsReportServiceCalledToAddDirector(boolean isServiceExpected) throws DataException {

        VerificationMode timesExpected = isServiceExpected ? times(1) : never();
        verify(directorsReportService, timesExpected)
                .addDirector(COMPANY_ACCOUNTS_ID, DIRECTOR_ID, DIRECTOR_SELF_LINK, request);
    }

    private void assertWhetherDirectorsReportServiceCalledToRemoveDirector(boolean isServiceExpected) throws DataException {

        VerificationMode timesExpected = isServiceExpected ? times(1) : never();
        verify(directorsReportService, timesExpected)
                .removeDirector(COMPANY_ACCOUNTS_ID, DIRECTOR_ID, request);
    }

    private void expectDirectorsResourceChangedForLoansToDirectors(boolean loansToDirectorsExists,
            boolean loansToDirectorsAdditionalInfoExists) throws DataException {
        if (loansToDirectorsExists) {
            when(loansToDirectorsService.find(COMPANY_ACCOUNTS_ID, request)).thenReturn(
                            new ResponseObject<>(ResponseStatus.FOUND, loansToDirectors));
            when(loansToDirectors.getLinks()).thenReturn(loansToDirectorLinks);
            if (loansToDirectorsAdditionalInfoExists) {
                when(loansToDirectorLinks.get(LoansToDirectorsLinkType.ADDITIONAL_INFO.getLink()))
                                .thenReturn(LOANS_TO_DIRECTORS_ADDITIONAL_INFO_LINK);
                when(loanService.deleteAll(transaction, COMPANY_ACCOUNTS_ID, request))
                                .thenReturn(new ResponseObject<>(ResponseStatus.UPDATED));
            } else {
                when(loansToDirectorLinks.get(LoansToDirectorsLinkType.ADDITIONAL_INFO.getLink()))
                                .thenReturn(null);
                when(loansToDirectorsService.delete(COMPANY_ACCOUNTS_ID, request))
                                .thenReturn(new ResponseObject<>(ResponseStatus.UPDATED));
            }
        } else {
            when(loansToDirectorsService.find(COMPANY_ACCOUNTS_ID, request))
                            .thenReturn(new ResponseObject<>(ResponseStatus.NOT_FOUND));
        }
    }

    private void expectDirectorsResourceChangedForApprovals(boolean directorsApprovalExists,
            boolean approvalExists) throws DataException {
        if (directorsApprovalExists) {
            when(directorsApprovalService.find(COMPANY_ACCOUNTS_ID, request)).thenReturn(new ResponseObject<>(ResponseStatus.FOUND));
            when(directorsApprovalService.delete(COMPANY_ACCOUNTS_ID, request)).thenReturn(new ResponseObject<>(ResponseStatus.UPDATED));
        } else {
            when(directorsApprovalService.find(COMPANY_ACCOUNTS_ID, request)).thenReturn(new ResponseObject<>(ResponseStatus.NOT_FOUND));
        }
        if(approvalExists) {
            when(approvalService.find(COMPANY_ACCOUNTS_ID, request)).thenReturn(new ResponseObject<>(ResponseStatus.FOUND));
            when(approvalService.delete(COMPANY_ACCOUNTS_ID, request)).thenReturn(new ResponseObject<>(ResponseStatus.UPDATED));
        } else {
            when(approvalService.find(COMPANY_ACCOUNTS_ID, request)).thenReturn(new ResponseObject<>(ResponseStatus.NOT_FOUND));
        }
    }

    private void assertWhetherAllLoansDeleted(boolean isExpected) throws DataException {
        VerificationMode timesExpected = isExpected ? times(1) : never();
        verify(loanService, timesExpected).deleteAll(transaction, COMPANY_ACCOUNTS_ID, request);
    }

    private void assertWhetherLoansToDirectorsDeleted(boolean isExpected) throws DataException {
        VerificationMode timesExpected = isExpected ? times(1) : never();
        verify(loansToDirectorsService, timesExpected).delete(COMPANY_ACCOUNTS_ID, request);
    }

    private void assertWhetherDirectorsApprovalDeleted(boolean isExpected) throws DataException {
        VerificationMode timesExpected = isExpected ? times(1) : never();
        verify(directorsApprovalService, timesExpected).delete(COMPANY_ACCOUNTS_ID, request);
    }

    private void assertWhetherAccountsApprovalDeleted(boolean isExpected) throws DataException {
        VerificationMode timesExpected = isExpected ? times(1) : never();
        verify(approvalService, timesExpected).delete(COMPANY_ACCOUNTS_ID, request);
    }
}
