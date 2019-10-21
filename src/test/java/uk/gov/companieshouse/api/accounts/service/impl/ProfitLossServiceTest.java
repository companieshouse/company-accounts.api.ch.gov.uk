package uk.gov.companieshouse.api.accounts.service.impl;

import com.mongodb.MongoException;
import java.util.Optional;
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
import uk.gov.companieshouse.api.accounts.ResourceName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;
import uk.gov.companieshouse.api.accounts.model.entity.profitloss.ProfitLossEntity;
import uk.gov.companieshouse.api.accounts.model.rest.profitloss.ProfitLoss;
import uk.gov.companieshouse.api.accounts.repository.ProfitLossRepository;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transformer.ProfitLossTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.model.transaction.TransactionLinks;

import javax.servlet.http.HttpServletRequest;

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

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProfitLossServiceTest {

    @Mock
    private ProfitLossTransformer transformer;

    @Mock
    private ProfitLossRepository repository;

    @Mock
    private SmallFullService smallFullService;

    @Mock
    private KeyIdGenerator keyIdGenerator;

    @Mock
    private ProfitLoss profitLoss;

    @Mock
    private Transaction transaction;

    @Mock
    private TransactionLinks transactionLinks;

    @Mock
    private HttpServletRequest request;

    @Mock
    private ProfitLossEntity profitLossEntity;

    @InjectMocks
    private ProfitLossService profitLossService;

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

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        when(request.getRequestURI()).thenReturn(CURRENT_PERIOD_URI);

        when(transformer.transform(profitLoss)).thenReturn(profitLossEntity);
        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" +
                ResourceName.CURRENT_PERIOD.getName() + "-" +
                ResourceName.PROFIT_LOSS.getName()))
                    .thenReturn(GENERATED_ID);

        ResponseObject<ProfitLoss> response =
                profitLossService.create(profitLoss, transaction, COMPANY_ACCOUNTS_ID, request);

        assertMetaDataSetOnRestObject(true);
        assertIdGeneratedForDatabaseEntity();
        assertRepositoryInsertCalled();
        assertWhetherSmallFullServiceCalledToAddLink(true, true);
        assertEquals(ResponseStatus.CREATED, response.getStatus());
        assertEquals(profitLoss, response.getData());
    }

    @Test
    @DisplayName("Tests the successful creation of a previous period profit and loss resource")
    void createPreviousPeriodProfitAndLossSuccess() throws DataException {

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        when(request.getRequestURI()).thenReturn(PREVIOUS_PERIOD_URI);

        when(transformer.transform(profitLoss)).thenReturn(profitLossEntity);
        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" +
                ResourceName.PREVIOUS_PERIOD.getName() + "-" +
                ResourceName.PROFIT_LOSS.getName()))
                    .thenReturn(GENERATED_ID);

        ResponseObject<ProfitLoss> response =
                profitLossService.create(profitLoss, transaction, COMPANY_ACCOUNTS_ID, request);

        assertMetaDataSetOnRestObject(false);
        assertIdGeneratedForDatabaseEntity();
        assertRepositoryInsertCalled();
        assertWhetherSmallFullServiceCalledToAddLink(false, true);
        assertEquals(ResponseStatus.CREATED, response.getStatus());
        assertEquals(profitLoss, response.getData());
    }

    @Test
    @DisplayName("Tests the creation of a profit and loss resource where the repository throws a duplicate key exception")
    void createProfitAndLossDuplicateKeyException() throws DataException {

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        when(request.getRequestURI()).thenReturn(CURRENT_PERIOD_URI);

        when(transformer.transform(profitLoss)).thenReturn(profitLossEntity);
        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" +
                ResourceName.CURRENT_PERIOD.getName() + "-" +
                ResourceName.PROFIT_LOSS.getName()))
                    .thenReturn(GENERATED_ID);

        when(repository.insert(profitLossEntity)).thenThrow(DuplicateKeyException.class);

        ResponseObject<ProfitLoss> response =
                profitLossService.create(profitLoss, transaction, COMPANY_ACCOUNTS_ID, request);

        assertWhetherSmallFullServiceCalledToAddLink(true,false);
        assertEquals(ResponseStatus.DUPLICATE_KEY_ERROR, response.getStatus());
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Tests the creation of a profit and loss resource where the repository throws a Mongo exception")
    void createProfitAndLossMongoException() throws DataException {

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        when(request.getRequestURI()).thenReturn(CURRENT_PERIOD_URI);

        when(transformer.transform(profitLoss)).thenReturn(profitLossEntity);
        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" +
                ResourceName.CURRENT_PERIOD.getName() + "-" +
                ResourceName.PROFIT_LOSS.getName()))
                    .thenReturn(GENERATED_ID);

        when(repository.insert(profitLossEntity)).thenThrow(MongoException.class);

        assertThrows(DataException.class, () ->
                profitLossService.create(profitLoss, transaction, COMPANY_ACCOUNTS_ID, request));

        assertWhetherSmallFullServiceCalledToAddLink(true,false);
    }

    @Test
    @DisplayName("Tests the successful update of a current period profit and loss resource")
    void updateCurrentPeriodProfitAndLossSuccess() throws DataException {

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        when(request.getRequestURI()).thenReturn(CURRENT_PERIOD_URI);

        when(transformer.transform(profitLoss)).thenReturn(profitLossEntity);
        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" +
                ResourceName.CURRENT_PERIOD.getName() + "-" +
                ResourceName.PROFIT_LOSS.getName()))
                    .thenReturn(GENERATED_ID);

        ResponseObject<ProfitLoss> response =
                profitLossService.update(profitLoss, transaction, COMPANY_ACCOUNTS_ID, request);

        assertMetaDataSetOnRestObject(true);
        assertIdGeneratedForDatabaseEntity();
        assertRepositoryUpdateCalled();
        assertEquals(ResponseStatus.UPDATED, response.getStatus());
        assertEquals(profitLoss, response.getData());
    }

    @Test
    @DisplayName("Tests the successful update of a previous period profit and loss resource")
    void updatePreviousPeriodProfitAndLossSuccess() throws DataException {

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        when(request.getRequestURI()).thenReturn(PREVIOUS_PERIOD_URI);

        when(transformer.transform(profitLoss)).thenReturn(profitLossEntity);
        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" +
                ResourceName.PREVIOUS_PERIOD.getName() + "-" +
                ResourceName.PROFIT_LOSS.getName()))
                    .thenReturn(GENERATED_ID);

        ResponseObject<ProfitLoss> response =
                profitLossService.update(profitLoss, transaction, COMPANY_ACCOUNTS_ID, request);

        assertMetaDataSetOnRestObject(false);
        assertIdGeneratedForDatabaseEntity();
        assertRepositoryUpdateCalled();
        assertEquals(ResponseStatus.UPDATED, response.getStatus());
        assertEquals(profitLoss, response.getData());
    }

    @Test
    @DisplayName("Tests the update of a profit and loss resource where the repository throws a Mongo exception")
    void updateProfitAndLossMongoException() {

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        when(request.getRequestURI()).thenReturn(CURRENT_PERIOD_URI);

        when(transformer.transform(profitLoss)).thenReturn(profitLossEntity);
        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" +
                ResourceName.CURRENT_PERIOD.getName() + "-" +
                ResourceName.PROFIT_LOSS.getName()))
                    .thenReturn(GENERATED_ID);

        when(repository.save(profitLossEntity)).thenThrow(MongoException.class);

        assertThrows(DataException.class, () ->
                profitLossService.update(profitLoss, transaction, COMPANY_ACCOUNTS_ID, request));
    }

    @Test
    @DisplayName("Tests the successful retrieval of a profit and loss resource")
    void getProfitAndLossSuccess() throws DataException {

        when(request.getRequestURI()).thenReturn(CURRENT_PERIOD_URI);

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" +
                ResourceName.CURRENT_PERIOD.getName() + "-" +
                ResourceName.PROFIT_LOSS.getName()))
                    .thenReturn(GENERATED_ID);

        when(repository.findById(GENERATED_ID)).thenReturn(Optional.of(profitLossEntity));
        when(transformer.transform(profitLossEntity)).thenReturn(profitLoss);

        ResponseObject<ProfitLoss> response =
                profitLossService.find(COMPANY_ACCOUNTS_ID, request);

        assertRepositoryFindByIdCalled();
        assertEquals(ResponseStatus.FOUND, response.getStatus());
        assertEquals(profitLoss, response.getData());
    }

    @Test
    @DisplayName("Tests the retrieval of a non-existent profit and loss resource")
    void getProfitAndLossNotFound() throws DataException {

        when(request.getRequestURI()).thenReturn(CURRENT_PERIOD_URI);

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" +
                ResourceName.CURRENT_PERIOD.getName() + "-" +
                ResourceName.PROFIT_LOSS.getName()))
                    .thenReturn(GENERATED_ID);

        when(repository.findById(GENERATED_ID)).thenReturn(Optional.ofNullable(null));

        ResponseObject<ProfitLoss> response =
                profitLossService.find(COMPANY_ACCOUNTS_ID, request);

        assertRepositoryFindByIdCalled();
        assertEquals(ResponseStatus.NOT_FOUND, response.getStatus());
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Tests the successful deletion of a current period profit and loss resource")
    void deleteCurrentPeriodProfitAndLossSuccess() throws DataException {

        when(request.getRequestURI()).thenReturn(CURRENT_PERIOD_URI);

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" +
                ResourceName.CURRENT_PERIOD.getName() + "-" +
                ResourceName.PROFIT_LOSS.getName()))
                    .thenReturn(GENERATED_ID);

        when(repository.existsById(GENERATED_ID)).thenReturn(true);

        ResponseObject<ProfitLoss> response =
                profitLossService.delete(COMPANY_ACCOUNTS_ID, request);

        assertRepositoryDeleteByIdCalled();
        assertWhetherSmallFullServiceCalledToRemoveLink(true,true);
        assertEquals(ResponseStatus.UPDATED, response.getStatus());
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Tests the successful deletion of a previous period profit and loss resource")
    void deletePreviousPeriodProfitAndLossSuccess() throws DataException {

        when(request.getRequestURI()).thenReturn(PREVIOUS_PERIOD_URI);

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" +
                ResourceName.PREVIOUS_PERIOD.getName() + "-" +
                ResourceName.PROFIT_LOSS.getName()))
                    .thenReturn(GENERATED_ID);

        when(repository.existsById(GENERATED_ID)).thenReturn(true);

        ResponseObject<ProfitLoss> response =
                profitLossService.delete(COMPANY_ACCOUNTS_ID, request);

        assertRepositoryDeleteByIdCalled();
        assertWhetherSmallFullServiceCalledToRemoveLink(false,true);
        assertEquals(ResponseStatus.UPDATED, response.getStatus());
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Tests the deletion of a profit and loss resource where the repository throws a Mongo exception")
    void deleteProfitAndLossMongoException() throws DataException {

        when(request.getRequestURI()).thenReturn(CURRENT_PERIOD_URI);

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" +
                ResourceName.CURRENT_PERIOD.getName() + "-" +
                ResourceName.PROFIT_LOSS.getName()))
                    .thenReturn(GENERATED_ID);

        when(repository.existsById(GENERATED_ID)).thenReturn(true);
        doThrow(MongoException.class).when(repository).deleteById(GENERATED_ID);

        assertThrows(DataException.class, () ->
                profitLossService.delete(COMPANY_ACCOUNTS_ID, request));

        assertWhetherSmallFullServiceCalledToRemoveLink(true,false);
    }

    @Test
    @DisplayName("Tests the deletion of a non-existent profit and loss resource")
    void deleteProfitAndLossNotFound() throws DataException {

        when(request.getRequestURI()).thenReturn(CURRENT_PERIOD_URI);

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" +
                ResourceName.CURRENT_PERIOD.getName() + "-" +
                ResourceName.PROFIT_LOSS.getName()))
                    .thenReturn(GENERATED_ID);

        when(repository.existsById(GENERATED_ID)).thenReturn(false);

        ResponseObject<ProfitLoss> response =
                profitLossService.delete(COMPANY_ACCOUNTS_ID, request);

        verify(repository, never()).deleteById(GENERATED_ID);
        assertWhetherSmallFullServiceCalledToRemoveLink(true, false);
        assertEquals(ResponseStatus.NOT_FOUND, response.getStatus());
        assertNull(response.getData());
    }

    private void assertMetaDataSetOnRestObject(boolean currentPeriod) {
        verify(profitLoss, times(1)).setKind((currentPeriod ? Kind.PROFIT_LOSS_CURRENT.getValue() : Kind.PROFIT_LOSS_PREVIOUS.getValue()));
        verify(profitLoss, times(1)).setEtag(anyString());
        verify(profitLoss, times(1)).setLinks(anyMap());
    }

    private void assertIdGeneratedForDatabaseEntity() {
        verify(profitLossEntity, times(1)).setId(GENERATED_ID);
    }

    private void assertRepositoryInsertCalled() {
        verify(repository, times(1)).insert(profitLossEntity);
    }

    private void assertRepositoryUpdateCalled() {
        verify(repository, times(1)).save(profitLossEntity);
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
            verify(smallFullService, timesExpected)
                    .addLink(COMPANY_ACCOUNTS_ID,
                           SmallFullLinkType.CURRENT_PERIOD_PROFIT_LOSS,
                                    CURRENT_PERIOD_URI, request);
        } else {
            verify(smallFullService, timesExpected)
                    .addLink(COMPANY_ACCOUNTS_ID,
                            SmallFullLinkType.PREVIOUS_PERIOD_PROFIT_LOSS,
                                    PREVIOUS_PERIOD_URI, request);
        }
    }

    private void assertWhetherSmallFullServiceCalledToRemoveLink(boolean currentPeriod, boolean isServiceExpected) throws DataException {

        VerificationMode timesExpected = isServiceExpected ? times(1) : never();
        if (currentPeriod) {
            verify(smallFullService, timesExpected)
                    .removeLink(COMPANY_ACCOUNTS_ID,
                            SmallFullLinkType.CURRENT_PERIOD_PROFIT_LOSS,
                                    request);
        } else {
            verify(smallFullService, timesExpected)
                    .removeLink(COMPANY_ACCOUNTS_ID,
                            SmallFullLinkType.PREVIOUS_PERIOD_PROFIT_LOSS,
                                    request);
        }
    }
}
