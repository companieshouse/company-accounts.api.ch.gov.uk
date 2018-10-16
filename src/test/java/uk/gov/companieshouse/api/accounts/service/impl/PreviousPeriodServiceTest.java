package uk.gov.companieshouse.api.accounts.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import com.mongodb.MongoException;
import java.security.MessageDigest;
import java.util.Optional;
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
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.entity.PreviousPeriodEntity;
import uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod;
import uk.gov.companieshouse.api.accounts.repository.PreviousPeriodRepository;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.transformer.PreviousPeriodTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PreviousPeriodServiceTest {

    @Mock
    private PreviousPeriod previousPeriod;

    @Mock
    private Transaction transaction;

    @Mock
    private PreviousPeriodRepository previousPeriodRepository;

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


    @Test
    @DisplayName("Tests the successful creation of a previousPeriod resource")
    public void canCreatePreviousPeriod() throws DataException {
        when(previousPeriodTransformer.transform(previousPeriod)).thenReturn(previousPeriodEntity);
        ResponseObject<PreviousPeriod> result = previousPeriodService
            .create(previousPeriod, transaction, "", "");
        assertNotNull(result);
        assertEquals(previousPeriod, result.getData());
    }

    @Test
    @DisplayName("Tests the duplicate key when creating a previous period resource")
    public void createSmallfullDuplicateKey() throws DataException {
        doReturn(previousPeriodEntity).when(previousPeriodTransformer).transform(ArgumentMatchers
            .any(PreviousPeriod.class));
        when(previousPeriodRepository.insert(previousPeriodEntity))
            .thenThrow(duplicateKeyException);
        ResponseObject response = previousPeriodService.create(previousPeriod, transaction, "", "");
        assertNotNull(response);
        assertEquals(response.getStatus(), ResponseStatus.DUPLICATE_KEY_ERROR);
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Tests the mongo exception when creating a previous period")
    void createSmallfullMongoExceptionFailure() throws DataException {
        doReturn(previousPeriodEntity).when(previousPeriodTransformer).transform(ArgumentMatchers
            .any(PreviousPeriod.class));
        when(previousPeriodRepository.insert(previousPeriodEntity)).thenThrow(mongoException);
        Executable executable = () -> {
            previousPeriodService.create(previousPeriod, transaction, "", "");
        };
        assertThrows(DataException.class, executable);
    }

    @Test
    @DisplayName("Tests the successful find of a previous period resource")
    public void findPreviousPeriod() throws DataException {
        when(previousPeriodRepository.findById("")).thenReturn(Optional.ofNullable(previousPeriodEntity));
        when(previousPeriodTransformer.transform(previousPeriodEntity)).thenReturn(previousPeriod);

        ResponseObject<PreviousPeriod> result = previousPeriodService.findById("", "");

        assertNotNull(result);
        assertEquals(previousPeriod, result.getData());
    }

    @Test
    @DisplayName("Tests the unsuccessful find of a previous period resource")
    public void findPreviousPeriodNotFound() throws DataException {
        when(previousPeriodRepository.findById("")).thenReturn(Optional.empty());
        ResponseObject<PreviousPeriod> result = previousPeriodService.findById("", "");

        assertNotNull(result);
        assertEquals(ResponseStatus.NOT_FOUND, result.getStatus());
        assertNull(result.getData());
    }

    @Test
    @DisplayName("Tests mongo exception thrown on find of a previous period resource")
    public void findPreviousPeriodMongoException() throws DataException {
        when(previousPeriodRepository.findById("")).thenThrow(mongoException);
        Executable executable = () -> previousPeriodService.findById("", "");

        assertThrows(DataException.class, executable);
    }
}
