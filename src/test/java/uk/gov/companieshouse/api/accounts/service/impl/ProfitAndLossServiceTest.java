package uk.gov.companieshouse.api.accounts.service.impl;

import com.mongodb.MongoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.verification.VerificationMode;
import org.springframework.dao.DuplicateKeyException;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.Kind;
import uk.gov.companieshouse.api.accounts.ResourceName;
import uk.gov.companieshouse.api.accounts.enumeration.AccountingPeriod;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.CurrentPeriodLinkType;
import uk.gov.companieshouse.api.accounts.links.PreviousPeriodLinkType;
import uk.gov.companieshouse.api.accounts.model.entity.profitloss.ProfitAndLossEntity;
import uk.gov.companieshouse.api.accounts.model.rest.Statement;
import uk.gov.companieshouse.api.accounts.model.rest.profitloss.ProfitAndLoss;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.repository.ProfitAndLossRepository;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transformer.ProfitAndLossTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;
import uk.gov.companieshouse.api.accounts.validation.ProfitAndLossValidator;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.model.transaction.TransactionLinks;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProfitAndLossServiceTest {

    @Mock
    private ProfitAndLossTransformer transformer;

    @Mock
    private ProfitAndLossRepository repository;

    @Mock
    private StatementService statementService;

    @Mock
    private CurrentPeriodService currentPeriodService;

    @Mock
    private PreviousPeriodService previousPeriodService;

    @Mock
    private KeyIdGenerator keyIdGenerator;

    @Mock
    private ProfitAndLoss profitAndLoss;

    @Mock
    private Statement statement;

    @Mock
    private Transaction transaction;

    @Mock
    private TransactionLinks transactionLinks;

    @Mock
    private HttpServletRequest request;

    @Mock
    private ProfitAndLossValidator validator;

    @Mock
    private ProfitAndLossEntity profitAndLossEntity;

    @Mock
    private Errors errors;

    @InjectMocks
    private ProfitAndLossService profitAndLossService;

    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";
    private static final String GENERATED_ID = "generatedId";
    private static final String TRANSACTION_SELF_LINK = "/transactions/123456";
    private static final String CURRENT_PERIOD_URI = TRANSACTION_SELF_LINK + "/company-accounts/" +
                                                        COMPANY_ACCOUNTS_ID + "/small-full/current-period/profit-and-loss";
    private static final String PREVIOUS_PERIOD_URI = TRANSACTION_SELF_LINK + "/company-accounts/" +
                                                        COMPANY_ACCOUNTS_ID + "/small-full/previous-period/profit-and-loss";


    @Test
    @DisplayName("Tests the successful creation of a current period profit and loss resource")
    void createCurrentPeriodProfitAndLossSuccess() throws DataException {

        AccountingPeriod period = AccountingPeriod.CURRENT_PERIOD;

        when(validator.validateProfitLoss(profitAndLoss, COMPANY_ACCOUNTS_ID, request, transaction))
                .thenReturn(errors);
        when(errors.hasErrors()).thenReturn(false);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        when(transformer.transform(profitAndLoss)).thenReturn(profitAndLossEntity);
        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" +
                ResourceName.CURRENT_PERIOD.getName() + "-" +
                ResourceName.PROFIT_LOSS.getName()))
                    .thenReturn(GENERATED_ID);

        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        ResponseObject responseObject = new ResponseObject(ResponseStatus.FOUND);
        when(statementService.find(COMPANY_ACCOUNTS_ID, request)).thenReturn(responseObject);

        ResponseObject<ProfitAndLoss> response =
                profitAndLossService.create(profitAndLoss, transaction, COMPANY_ACCOUNTS_ID, request, period);

        verify(statementService).update(any(Statement.class), eq(transaction), eq(COMPANY_ACCOUNTS_ID), eq(request));
        assertMetaDataSetOnRestObject(true);
        assertIdGeneratedForDatabaseEntity();
        assertRepositoryInsertCalled();
        assertWhetherSmallFullServiceCalledToAddLink(true, true);
        assertEquals(ResponseStatus.CREATED, response.getStatus());
        assertEquals(profitAndLoss, response.getData());
    }

    @Test
    @DisplayName("Tests the successful creation of a previous period profit and loss resource")
    void createPreviousPeriodProfitAndLossSuccess() throws DataException {

        AccountingPeriod period = AccountingPeriod.PREVIOUS_PERIOD;

        when(validator.validateProfitLoss(profitAndLoss, COMPANY_ACCOUNTS_ID, request, transaction))
                .thenReturn(errors);
        when(errors.hasErrors()).thenReturn(false);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        when(transformer.transform(profitAndLoss)).thenReturn(profitAndLossEntity);
        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" +
                ResourceName.PREVIOUS_PERIOD.getName() + "-" +
                ResourceName.PROFIT_LOSS.getName()))
                    .thenReturn(GENERATED_ID);

        ResponseObject<ProfitAndLoss> response =
                profitAndLossService.create(profitAndLoss, transaction, COMPANY_ACCOUNTS_ID, request, period);

        assertMetaDataSetOnRestObject(false);
        assertIdGeneratedForDatabaseEntity();
        assertRepositoryInsertCalled();
        assertWhetherSmallFullServiceCalledToAddLink(false, true);
        assertEquals(ResponseStatus.CREATED, response.getStatus());
        assertEquals(profitAndLoss, response.getData());
    }

    @Test
    @DisplayName("Tests the creation of a profit and loss resource where the repository throws a duplicate key exception")
    void createProfitAndLossDuplicateKeyException() throws DataException {

        AccountingPeriod period = AccountingPeriod.CURRENT_PERIOD;

        when(validator.validateProfitLoss(profitAndLoss, COMPANY_ACCOUNTS_ID, request, transaction))
                .thenReturn(errors);
        when(errors.hasErrors()).thenReturn(false);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);


        when(transformer.transform(profitAndLoss)).thenReturn(profitAndLossEntity);
        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" +
                ResourceName.CURRENT_PERIOD.getName() + "-" +
                ResourceName.PROFIT_LOSS.getName()))
                    .thenReturn(GENERATED_ID);

        when(repository.insert(profitAndLossEntity)).thenThrow(DuplicateKeyException.class);

        ResponseObject<ProfitAndLoss> response =
                profitAndLossService.create(profitAndLoss, transaction, COMPANY_ACCOUNTS_ID, request, period);

        assertWhetherSmallFullServiceCalledToAddLink(true,false);
        assertEquals(ResponseStatus.DUPLICATE_KEY_ERROR, response.getStatus());
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Tests the creation of a profit and loss resource where the repository throws a Mongo exception")
    void createProfitAndLossMongoException() throws DataException {

        AccountingPeriod period = AccountingPeriod.CURRENT_PERIOD;

        when(validator.validateProfitLoss(profitAndLoss, COMPANY_ACCOUNTS_ID, request, transaction))
                .thenReturn(errors);
        when(errors.hasErrors()).thenReturn(false);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);


        when(transformer.transform(profitAndLoss)).thenReturn(profitAndLossEntity);
        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" +
                ResourceName.CURRENT_PERIOD.getName() + "-" +
                ResourceName.PROFIT_LOSS.getName()))
                    .thenReturn(GENERATED_ID);

        when(repository.insert(profitAndLossEntity)).thenThrow(MongoException.class);

        assertThrows(DataException.class, () ->
                profitAndLossService.create(profitAndLoss, transaction, COMPANY_ACCOUNTS_ID, request, period));

        assertWhetherSmallFullServiceCalledToAddLink(true,false);
    }

    @Test
    @DisplayName("Tests the successful update of a current period profit and loss resource")
    void updateCurrentPeriodProfitAndLossSuccess() throws DataException {

        AccountingPeriod period = AccountingPeriod.CURRENT_PERIOD;

        when(validator.validateProfitLoss(profitAndLoss, COMPANY_ACCOUNTS_ID, request, transaction))
                .thenReturn(errors);
        when(errors.hasErrors()).thenReturn(false);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);


        when(transformer.transform(profitAndLoss)).thenReturn(profitAndLossEntity);
        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" +
                ResourceName.CURRENT_PERIOD.getName() + "-" +
                ResourceName.PROFIT_LOSS.getName()))
                    .thenReturn(GENERATED_ID);

        ResponseObject<ProfitAndLoss> response =
                profitAndLossService.update(profitAndLoss, transaction, COMPANY_ACCOUNTS_ID, request, period);

        assertMetaDataSetOnRestObject(true);
        assertIdGeneratedForDatabaseEntity();
        assertRepositoryUpdateCalled();
        assertEquals(ResponseStatus.UPDATED, response.getStatus());
        assertEquals(profitAndLoss, response.getData());
    }

    @Test
    @DisplayName("Tests the successful update of a previous period profit and loss resource")
    void updatePreviousPeriodProfitAndLossSuccess() throws DataException {

        AccountingPeriod period = AccountingPeriod.PREVIOUS_PERIOD;

        when(validator.validateProfitLoss(profitAndLoss, COMPANY_ACCOUNTS_ID, request, transaction))
                .thenReturn(errors);
        when(errors.hasErrors()).thenReturn(false);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);


        when(transformer.transform(profitAndLoss)).thenReturn(profitAndLossEntity);
        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" +
                ResourceName.PREVIOUS_PERIOD.getName() + "-" +
                ResourceName.PROFIT_LOSS.getName()))
                    .thenReturn(GENERATED_ID);

        ResponseObject<ProfitAndLoss> response =
                profitAndLossService.update(profitAndLoss, transaction, COMPANY_ACCOUNTS_ID, request, period);

        assertMetaDataSetOnRestObject(false);
        assertIdGeneratedForDatabaseEntity();
        assertRepositoryUpdateCalled();
        assertEquals(ResponseStatus.UPDATED, response.getStatus());
        assertEquals(profitAndLoss, response.getData());
    }

    @Test
    @DisplayName("Tests the update of a profit and loss resource where the repository throws a Mongo exception")
    void updateProfitAndLossMongoException() throws DataException {

        AccountingPeriod period = AccountingPeriod.CURRENT_PERIOD;

        when(validator.validateProfitLoss(profitAndLoss, COMPANY_ACCOUNTS_ID, request, transaction))
                .thenReturn(errors);
        when(errors.hasErrors()).thenReturn(false);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        when(transformer.transform(profitAndLoss)).thenReturn(profitAndLossEntity);
        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" +
                ResourceName.CURRENT_PERIOD.getName() + "-" +
                ResourceName.PROFIT_LOSS.getName()))
                    .thenReturn(GENERATED_ID);

        when(repository.save(profitAndLossEntity)).thenThrow(MongoException.class);

        assertThrows(DataException.class, () ->
                profitAndLossService.update(profitAndLoss, transaction, COMPANY_ACCOUNTS_ID, request, period));
    }

    @Test
    @DisplayName("Tests the successful retrieval of a profit and loss resource")
    void getProfitAndLossSuccess() throws DataException {

        AccountingPeriod period = AccountingPeriod.CURRENT_PERIOD;

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" +
                ResourceName.CURRENT_PERIOD.getName() + "-" +
                ResourceName.PROFIT_LOSS.getName()))
                    .thenReturn(GENERATED_ID);

        when(repository.findById(GENERATED_ID)).thenReturn(Optional.of(profitAndLossEntity));
        when(transformer.transform(profitAndLossEntity)).thenReturn(profitAndLoss);

        ResponseObject<ProfitAndLoss> response =
                profitAndLossService.find(COMPANY_ACCOUNTS_ID, period);

        assertRepositoryFindByIdCalled();
        assertEquals(ResponseStatus.FOUND, response.getStatus());
        assertEquals(profitAndLoss, response.getData());
    }

    @Test
    @DisplayName("Tests the retrieval of a non-existent profit and loss resource")
    void getProfitAndLossNotFound() throws DataException {

        AccountingPeriod period = AccountingPeriod.CURRENT_PERIOD;

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" +
                ResourceName.CURRENT_PERIOD.getName() + "-" +
                ResourceName.PROFIT_LOSS.getName()))
                    .thenReturn(GENERATED_ID);

        when(repository.findById(GENERATED_ID)).thenReturn(Optional.ofNullable(null));

        ResponseObject<ProfitAndLoss> response =
                profitAndLossService.find(COMPANY_ACCOUNTS_ID, period);

        assertRepositoryFindByIdCalled();
        assertEquals(ResponseStatus.NOT_FOUND, response.getStatus());
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Tests the successful deletion of a current period profit and loss resource")
    void deleteCurrentPeriodProfitAndLossSuccess() throws DataException {

        AccountingPeriod period = AccountingPeriod.CURRENT_PERIOD;

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" +
                ResourceName.CURRENT_PERIOD.getName() + "-" +
                ResourceName.PROFIT_LOSS.getName()))
                    .thenReturn(GENERATED_ID);

        when(repository.existsById(GENERATED_ID)).thenReturn(true);

        ResponseObject<ProfitAndLoss> response =
                profitAndLossService.delete(COMPANY_ACCOUNTS_ID, request, period);

        assertRepositoryDeleteByIdCalled();
        assertWhetherSmallFullServiceCalledToRemoveLink(true,true);
        assertEquals(ResponseStatus.UPDATED, response.getStatus());
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Tests the successful deletion of a previous period profit and loss resource")
    void deletePreviousPeriodProfitAndLossSuccess() throws DataException {

        AccountingPeriod period = AccountingPeriod.PREVIOUS_PERIOD;

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" +
                ResourceName.PREVIOUS_PERIOD.getName() + "-" +
                ResourceName.PROFIT_LOSS.getName()))
                    .thenReturn(GENERATED_ID);

        when(repository.existsById(GENERATED_ID)).thenReturn(true);

        ResponseObject<ProfitAndLoss> response =
                profitAndLossService.delete(COMPANY_ACCOUNTS_ID, request, period);

        assertRepositoryDeleteByIdCalled();
        assertWhetherSmallFullServiceCalledToRemoveLink(false,true);
        assertEquals(ResponseStatus.UPDATED, response.getStatus());
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Tests the deletion of a profit and loss resource where the repository throws a Mongo exception")
    void deleteProfitAndLossMongoException() throws DataException {

        AccountingPeriod period = AccountingPeriod.CURRENT_PERIOD;

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" +
                ResourceName.CURRENT_PERIOD.getName() + "-" +
                ResourceName.PROFIT_LOSS.getName()))
                    .thenReturn(GENERATED_ID);

        when(repository.existsById(GENERATED_ID)).thenReturn(true);
        doThrow(MongoException.class).when(repository).deleteById(GENERATED_ID);

        assertThrows(DataException.class, () ->
                profitAndLossService.delete(COMPANY_ACCOUNTS_ID, request, period));

        assertWhetherSmallFullServiceCalledToRemoveLink(true,false);
    }

    @Test
    @DisplayName("Tests the deletion of a non-existent profit and loss resource")
    void deleteProfitAndLossNotFound() throws DataException {

        AccountingPeriod period = AccountingPeriod.CURRENT_PERIOD;

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" +
                ResourceName.CURRENT_PERIOD.getName() + "-" +
                ResourceName.PROFIT_LOSS.getName()))
                    .thenReturn(GENERATED_ID);

        when(repository.existsById(GENERATED_ID)).thenReturn(false);

        ResponseObject<ProfitAndLoss> response =
                profitAndLossService.delete(COMPANY_ACCOUNTS_ID, request, period);

        verify(repository, never()).deleteById(GENERATED_ID);
        assertWhetherSmallFullServiceCalledToRemoveLink(true, false);
        assertEquals(ResponseStatus.NOT_FOUND, response.getStatus());
        assertNull(response.getData());
    }

    private void assertMetaDataSetOnRestObject(boolean currentPeriod) {
        verify(profitAndLoss, times(1)).setKind((currentPeriod ? Kind.PROFIT_LOSS_CURRENT.getValue() : Kind.PROFIT_LOSS_PREVIOUS.getValue()));
        verify(profitAndLoss, times(1)).setEtag(anyString());
        verify(profitAndLoss, times(1)).setLinks(anyMap());
    }

    private void assertIdGeneratedForDatabaseEntity() {
        verify(profitAndLossEntity, times(1)).setId(GENERATED_ID);
    }

    private void assertRepositoryInsertCalled() {
        verify(repository, times(1)).insert(profitAndLossEntity);
    }

    private void assertRepositoryUpdateCalled() {
        verify(repository, times(1)).save(profitAndLossEntity);
    }

    private void assertRepositoryFindByIdCalled() {
        verify(repository, times(1)).findById(GENERATED_ID);
    }

    private void assertRepositoryDeleteByIdCalled() {
        verify(repository, times(1)).deleteById(GENERATED_ID);
    }

    private void assertWhetherSmallFullServiceCalledToAddLink(boolean currentPeriod, boolean isServiceExpected) throws DataException {

        VerificationMode timesExpected = isServiceExpected ? times(1) : never();
        if (currentPeriod) {
            verify(currentPeriodService, timesExpected)
                    .addLink(COMPANY_ACCOUNTS_ID,
                           CurrentPeriodLinkType.PROFIT_AND_LOSS,
                                    CURRENT_PERIOD_URI, request);
        } else {
            verify(previousPeriodService, timesExpected)
                    .addLink(COMPANY_ACCOUNTS_ID,
                            PreviousPeriodLinkType.PROFIT_AND_LOSS,
                                    PREVIOUS_PERIOD_URI, request);
        }
    }

    private void assertWhetherSmallFullServiceCalledToRemoveLink(boolean currentPeriod, boolean isServiceExpected) throws DataException {

        VerificationMode timesExpected = isServiceExpected ? times(1) : never();
        if (currentPeriod) {
            verify(currentPeriodService, timesExpected)
                    .removeLink(COMPANY_ACCOUNTS_ID,
                            CurrentPeriodLinkType.PROFIT_AND_LOSS,
                                    request);
        } else {
            verify(previousPeriodService, timesExpected)
                    .removeLink(COMPANY_ACCOUNTS_ID,
                            PreviousPeriodLinkType.PROFIT_AND_LOSS,
                                    request);
        }
    }
}
