package uk.gov.companieshouse.api.accounts.service.impl;

import com.mongodb.MongoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;
import uk.gov.companieshouse.api.accounts.ResourceName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.PreviousPeriodLinkType;
import uk.gov.companieshouse.api.accounts.model.entity.PreviousPeriodDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.PreviousPeriodEntity;
import uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.repository.PreviousPeriodRepository;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transformer.PreviousPeriodTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;
import uk.gov.companieshouse.api.accounts.validation.PreviousPeriodValidator;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.model.transaction.TransactionLinks;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PreviousPeriodServiceTest {
/*
    @Mock
    private HttpServletRequest request;

    @Mock
    private PreviousPeriod previousPeriod;

    @Mock
    private Transaction transaction;

    @Mock
    private SmallFullService smallFullService;

    @Mock
    private TransactionLinks transactionLinks;

    @Mock
    private PreviousPeriodRepository previousPeriodRepository;

    @Mock
    private PreviousPeriodValidator previousPeriodValidator;

    @Mock
    private Errors errors;

    @Mock
    private PreviousPeriodEntity previousPeriodEntity;

    @Mock
    private PreviousPeriodTransformer previousPeriodTransformer;

    @Mock
    private DuplicateKeyException duplicateKeyException;

    @Mock
    private MongoException mongoException;

    @Mock
    private KeyIdGenerator keyIdGenerator;

    @Mock
    private PreviousPeriodDataEntity previousPeriodDataEntity;

    @Mock
    private Map<String, String> links;

    @InjectMocks
    private PreviousPeriodService previousPeriodService;

    private static final String SELF_LINK = "self_link";
    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";
    private static final String RESOURCE_ID = "resourceId";
    private static final String PREVIOUS_PERIOD_PROFIT_AND_LOSS = "previousPeriodReportProfitAndLossLink";

    @BeforeEach
    void setUp() {
        when(keyIdGenerator
                .generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.PREVIOUS_PERIOD.getName()))
                        .thenReturn(RESOURCE_ID);
    }

    @Test
    @DisplayName("Tests the successful creation of a previousPeriod resource")
    void canCreatePreviousPeriod() throws DataException {
        when(previousPeriodTransformer.transform(previousPeriod)).thenReturn(previousPeriodEntity);
        when(previousPeriodValidator.validatePreviousPeriod(previousPeriod, transaction)).thenReturn(errors);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(SELF_LINK);

        ResponseObject<PreviousPeriod> result = previousPeriodService
            .create(previousPeriod, transaction, COMPANY_ACCOUNTS_ID, request);
        assertNotNull(result);
        assertEquals(previousPeriod, result.getData());
    }

    @Test
    @DisplayName("Tests the duplicate key when creating a previous period resource")
    void createSmallfullDuplicateKey() throws DataException {
        doReturn(previousPeriodEntity).when(previousPeriodTransformer).transform(ArgumentMatchers
            .any(PreviousPeriod.class));
        when(previousPeriodRepository.insert(previousPeriodEntity))
            .thenThrow(duplicateKeyException);
        when(previousPeriodValidator.validatePreviousPeriod(previousPeriod, transaction)).thenReturn(errors);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(SELF_LINK);

        ResponseObject response = previousPeriodService.create(previousPeriod, transaction, COMPANY_ACCOUNTS_ID, request);
        assertNotNull(response);
        assertEquals(response.getStatus(), ResponseStatus.DUPLICATE_KEY_ERROR);
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Tests the mongo exception when creating a previous period")
    void createSmallfullMongoExceptionFailure() throws DataException {
        doReturn(previousPeriodEntity).when(previousPeriodTransformer).transform(ArgumentMatchers
            .any(PreviousPeriod.class));
        when(previousPeriodValidator.validatePreviousPeriod(previousPeriod, transaction)).thenReturn(errors);
        when(previousPeriodRepository.insert(previousPeriodEntity)).thenThrow(mongoException);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(SELF_LINK);

        assertThrows(DataException.class, () -> previousPeriodService.create(previousPeriod, transaction, COMPANY_ACCOUNTS_ID, request));
    }

    @Test
    @DisplayName("Tests the successful find of a previous period resource")
    void findPreviousPeriod() throws DataException {
        when(previousPeriodRepository.findById(RESOURCE_ID)).thenReturn(Optional.of(previousPeriodEntity));
        when(previousPeriodTransformer.transform(previousPeriodEntity)).thenReturn(previousPeriod);

        ResponseObject<PreviousPeriod> result = previousPeriodService.find(COMPANY_ACCOUNTS_ID, request);

        assertNotNull(result);
        assertEquals(previousPeriod, result.getData());
    }

    @Test
    @DisplayName("Tests the unsuccessful find of a previous period resource")
    void findPreviousPeriodNotFound() throws DataException {
        when(previousPeriodRepository.findById(RESOURCE_ID)).thenReturn(Optional.empty());
        ResponseObject<PreviousPeriod> result = previousPeriodService.find(COMPANY_ACCOUNTS_ID, request);

        assertNotNull(result);
        assertEquals(ResponseStatus.NOT_FOUND, result.getStatus());
        assertNull(result.getData());
    }

    @Test
    @DisplayName("Tests mongo exception thrown on find of a previous period resource")
    void findPreviousPeriodMongoException() {
        when(previousPeriodRepository.findById(RESOURCE_ID)).thenThrow(mongoException);
        Executable executable = () -> previousPeriodService.find(COMPANY_ACCOUNTS_ID, request);

        assertThrows(DataException.class, executable);
    }

    @Test
    @DisplayName("PUT - Success - Previous Period")
    void canUpdatePreviousPeriod() throws DataException {

        when(previousPeriod.getLinks()).thenReturn(links);

        when(previousPeriodTransformer.transform(previousPeriod)).thenReturn(previousPeriodEntity);
        when(previousPeriodValidator.validatePreviousPeriod(previousPeriod, transaction)).thenReturn(errors);

        ResponseObject<PreviousPeriod> result = previousPeriodService.update(previousPeriod, transaction, COMPANY_ACCOUNTS_ID, request);
        assertNotNull(result);
        assertEquals(previousPeriod, result.getData());
    }

    @Test
    @DisplayName("PUT - Failure - Previous Period - Mongo Exception")
    void canUpdatePreviousPeriodFailureMongoException() throws DataException {

        when(previousPeriod.getLinks()).thenReturn(links);

        when(previousPeriodTransformer.transform(previousPeriod)).thenReturn(previousPeriodEntity);
        when(previousPeriodValidator.validatePreviousPeriod(previousPeriod, transaction)).thenReturn(errors);
        when(previousPeriodRepository.save(any())).thenThrow(new MongoException("ERROR"));

        assertThrows(DataException.class, () -> previousPeriodService.update(previousPeriod, transaction, COMPANY_ACCOUNTS_ID, request));
    }

    @Test
    @DisplayName("Add link to current period resource- success")
    void addLinkSuccess() {

        when(previousPeriodRepository.findById(RESOURCE_ID)).thenReturn(Optional.ofNullable(previousPeriodEntity));

        when(previousPeriodEntity.getData()).thenReturn(previousPeriodDataEntity);
        when(previousPeriodDataEntity.getLinks()).thenReturn(links);

        PreviousPeriodLinkType previousPeriodLinkType = PreviousPeriodLinkType.PROFIT_AND_LOSS;

        assertAll(() ->
                previousPeriodService.addLink(
                        COMPANY_ACCOUNTS_ID, previousPeriodLinkType, PREVIOUS_PERIOD_PROFIT_AND_LOSS, request));

        verify(links).put(previousPeriodLinkType.getLink(), PREVIOUS_PERIOD_PROFIT_AND_LOSS);
        verify(previousPeriodRepository).save(previousPeriodEntity);
    }

    @Test
    @DisplayName("Add link - not found")
    void addLinkNotFound() {

        PreviousPeriodEntity previousPeriodEntity = null;

        when(previousPeriodRepository.findById(RESOURCE_ID)).thenReturn(Optional.ofNullable(previousPeriodEntity));

        assertThrows(DataException.class, () ->
                previousPeriodService.addLink(
                        COMPANY_ACCOUNTS_ID, PreviousPeriodLinkType.PROFIT_AND_LOSS, PREVIOUS_PERIOD_PROFIT_AND_LOSS, request));
    }

    @Test
    @DisplayName("Add link - Mongo exception")
    void addLinkMongoException() {

        when(previousPeriodRepository.findById(RESOURCE_ID)).thenThrow(MongoException.class);

        assertThrows(DataException.class, () ->
                previousPeriodService.addLink(
                        COMPANY_ACCOUNTS_ID, PreviousPeriodLinkType.PROFIT_AND_LOSS, PREVIOUS_PERIOD_PROFIT_AND_LOSS, request));
    }
*/
}