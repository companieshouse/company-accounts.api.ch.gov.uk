package uk.gov.companieshouse.api.accounts.service.impl;

import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.entity.PreviousPeriodEntity;
import uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod;
import uk.gov.companieshouse.api.accounts.repository.PreviousPeriodRespository;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.transformer.PreviousPeriodTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;

import java.security.MessageDigest;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PreviousPeriodServiceTest {

    @Mock
    private PreviousPeriod previousPeriod;

    @Mock
    private Transaction transaction;

    @Mock
    private PreviousPeriodRespository previousPeriodRepository;

    @Mock
    private SmallFullService smallFullService;

    @Mock
    private MessageDigest messageDigest;

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

    @InjectMocks
    private PreviousPeriodService previousPeriodService;

    public void setUpCreate() {
    }

    @Test
    @DisplayName("Tests the successful creation of a previousPeriod resource")
    public void canCreatePreviousPeriod() throws DataException {
        setUpCreate();
        when(previousPeriodTransformer.transform(previousPeriod)).thenReturn(previousPeriodEntity);
        ResponseObject<PreviousPeriod> result = previousPeriodService
            .create(previousPeriod, transaction, "", "");
        assertNotNull(result);
        assertEquals(previousPeriod, result.getData());
    }

    @Test
    @DisplayName("Tests the duplicate key when creating a previous period resource")
    public void createSmallfullDuplicateKey() throws DataException {
        setUpCreate();
        doReturn(previousPeriodEntity).when(previousPeriodTransformer).transform(ArgumentMatchers
            .any(PreviousPeriod.class));
        when(previousPeriodRepository.insert(previousPeriodEntity)).thenThrow(duplicateKeyException);
        ResponseObject response = previousPeriodService.create(previousPeriod, transaction, "", "");
        assertNotNull(response);
        assertEquals(response.getStatus(), ResponseStatus.DUPLICATE_KEY_ERROR);
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Tests the mongo exception when creating a previous period")
    void createSmallfullMongoExceptionFailure() throws DataException {
        setUpCreate();
        doReturn(previousPeriodEntity).when(previousPeriodTransformer).transform(ArgumentMatchers
            .any(PreviousPeriod.class));
        when(previousPeriodRepository.insert(previousPeriodEntity)).thenThrow(mongoException);
        Executable executable = () -> {
            previousPeriodService.create(previousPeriod, transaction, "", "");
        };
        assertThrows(DataException.class, executable);
    }


}
