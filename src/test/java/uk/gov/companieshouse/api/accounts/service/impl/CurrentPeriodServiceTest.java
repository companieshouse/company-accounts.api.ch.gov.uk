package uk.gov.companieshouse.api.accounts.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import com.mongodb.MongoException;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;
import uk.gov.companieshouse.api.accounts.ResourceName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.entity.CurrentPeriodEntity;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.repository.CurrentPeriodRepository;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.transformer.CurrentPeriodTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;
import uk.gov.companieshouse.api.accounts.validation.CurrentPeriodValidator;
import uk.gov.companieshouse.api.model.transaction.TransactionLinks;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class CurrentPeriodServiceTest {


    @Mock
    private HttpServletRequest request;

    @Mock
    private CurrentPeriod currentPeriod;

    @Mock
    private Transaction transaction;

    @Mock
    private TransactionLinks transactionLinks;

    @Mock
    private CurrentPeriodRepository currentPeriodRepository;

    @Mock
    private SmallFullService smallFullService;

    @Mock
    private CurrentPeriodEntity currentPeriodEntity;

    @Mock
    private CurrentPeriodValidator currentPeriodValidator;

    @Mock
    private Errors errors;

    @Mock
    private CurrentPeriodTransformer currentPeriodTransformer;

    @Mock
    private DuplicateKeyException duplicateKeyException;

    @Mock
    private MongoException mongoException;

    @Mock
    private KeyIdGenerator keyIdGenerator;

    @InjectMocks
    private CurrentPeriodService currentPeriodService;

    private static final String SELF_LINK = "self_link";
    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";
    private static final String RESOURCE_ID = "resourceId";

    @BeforeEach
    public void setUp() {
        when(keyIdGenerator
                .generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.CURRENT_PERIOD.getName()))
                        .thenReturn(RESOURCE_ID);
    }

    @Test
    @DisplayName("Tests the successful creation of a currentPeriod resource")
    public void canCreateCurrentPeriod() throws DataException {

        when(currentPeriodValidator.validateCurrentPeriod(currentPeriod, transaction)).thenReturn(errors);
        when(currentPeriodTransformer.transform(currentPeriod)).thenReturn(currentPeriodEntity);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(SELF_LINK);

        ResponseObject<CurrentPeriod> result = currentPeriodService
            .create(currentPeriod, transaction, COMPANY_ACCOUNTS_ID, request);
        assertNotNull(result);
        assertEquals(currentPeriod, result.getData());
    }

    @Test
    @DisplayName("Tests the duplicate key when creating a current period resource")
    public void createSmallfullDuplicateKey() throws DataException {

        when(currentPeriodValidator.validateCurrentPeriod(currentPeriod, transaction)).thenReturn(errors);
        doReturn(currentPeriodEntity).when(currentPeriodTransformer).transform(ArgumentMatchers
            .any(CurrentPeriod.class));
        when(currentPeriodRepository.insert(currentPeriodEntity)).thenThrow(duplicateKeyException);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(SELF_LINK);

        ResponseObject response = currentPeriodService.create(currentPeriod, transaction, COMPANY_ACCOUNTS_ID, request);
        assertNotNull(response);
        assertEquals(response.getStatus(), ResponseStatus.DUPLICATE_KEY_ERROR);
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Tests the mongo exception when creating a current period")
    void createSmallfullMongoExceptionFailure() throws DataException {

        when(currentPeriodValidator.validateCurrentPeriod(currentPeriod, transaction)).thenReturn(errors);
        doReturn(currentPeriodEntity).when(currentPeriodTransformer).transform(ArgumentMatchers
            .any(CurrentPeriod.class));
        when(currentPeriodRepository.insert(currentPeriodEntity)).thenThrow(mongoException);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(SELF_LINK);

        Executable executable = () -> {
            currentPeriodService.create(currentPeriod, transaction, COMPANY_ACCOUNTS_ID, request);
        };
        assertThrows(DataException.class, executable);
    }

    @Test
    @DisplayName("Tests the successful find of a currentPeriod resource")
    public void findCurrentPeriod() throws DataException {
        when(currentPeriodRepository.findById(RESOURCE_ID))
            .thenReturn(Optional.ofNullable(currentPeriodEntity));
        when(currentPeriodTransformer.transform(currentPeriodEntity)).thenReturn(currentPeriod);
        ResponseObject<CurrentPeriod> result = currentPeriodService
            .find(COMPANY_ACCOUNTS_ID, request);
        assertNotNull(result);
        assertEquals(currentPeriod, result.getData());
    }

    @Test
    @DisplayName("Tests mongo exception thrown on find of a currentPeriod resource")
    public void findCurrentPeriodMongoException()  {
        when(currentPeriodRepository.findById(RESOURCE_ID)).thenThrow(mongoException);
        Executable executable = () -> {
            currentPeriodService.find(COMPANY_ACCOUNTS_ID, request);
        };
        assertThrows(DataException.class, executable);
    }
}