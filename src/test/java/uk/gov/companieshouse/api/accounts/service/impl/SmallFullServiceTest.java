package uk.gov.companieshouse.api.accounts.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoException;
import java.security.MessageDigest;
import java.util.Optional;
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
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.entity.SmallFullEntity;
import uk.gov.companieshouse.api.accounts.model.rest.SmallFull;
import uk.gov.companieshouse.api.accounts.repository.SmallFullRepository;
import uk.gov.companieshouse.api.accounts.service.CompanyAccountService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.transformer.SmallFullTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class SmallFullServiceTest {

    @Mock
    private SmallFull smallFull;

    @Mock
    private Transaction transaction;

    @Mock
    private SmallFullEntity smallFullEntity;

    @Mock
    private MessageDigest messageDigest;

    @Mock
    private SmallFullTransformer smallFullTransformer;

    @Mock
    private SmallFullRepository smallFullRepository;

    @Mock
    private CompanyAccountService companyAccountService;

    @Mock
    private DuplicateKeyException duplicateKeyException;

    @Mock
    private MongoException mongoException;

    @Mock
    private KeyIdGenerator keyIdGenerator;

    @InjectMocks
    private SmallFullService smallFullService;

    public void setUpCreate() {
    }

    @Test
    @DisplayName("Tests the successful creation of a smallFull resource")
    public void createAccountSuccess() throws DataException {
        setUpCreate();
        when(smallFullTransformer.transform(smallFull)).thenReturn(smallFullEntity);
        ResponseObject<SmallFull> result = smallFullService.create(smallFull, transaction, "", "");
        assertNotNull(result);
        assertEquals(smallFull, result.getData());
    }

    @Test
    @DisplayName("Tests the duplicate key when creating a smallFull resource")
    public void createSmallfullDuplicateKey() throws DataException {
        setUpCreate();
        doReturn(smallFullEntity).when(smallFullTransformer).transform(ArgumentMatchers
                .any(SmallFull.class));
        when(smallFullRepository.insert(smallFullEntity)).thenThrow(duplicateKeyException);
        ResponseObject response = smallFullService.create(smallFull, transaction, "", "");
        assertNotNull(response);
        assertEquals(response.getStatus(), ResponseStatus.DUPLICATE_KEY_ERROR);
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Tests the mongo exception when creating a small full")
    void createSmallfullMongoExceptionFailure() throws DataException {
        setUpCreate();
        doReturn(smallFullEntity).when(smallFullTransformer).transform(ArgumentMatchers
                .any(SmallFull.class));
        when(smallFullRepository.insert(smallFullEntity)).thenThrow(mongoException);
        Executable executable = () -> {
            smallFullService.create(smallFull, transaction, "", "");
        };
        assertThrows(DataException.class, executable);
    }

    @Test
    @DisplayName("Tests the successful find of a small full resource")
    public void findSmallfull() throws DataException {
        when(smallFullRepository.findById("")).thenReturn(Optional.ofNullable(smallFullEntity));
        when(smallFullTransformer.transform(smallFullEntity)).thenReturn(smallFull);
        ResponseObject<SmallFull> result = smallFullService
                .findById("", "");
        assertNotNull(result);
        assertEquals(smallFull, result.getData());
    }

    @Test
    @DisplayName("Tests mongo exception thrown on find of a small full resource")
    public void findSmallfullMongoException() throws DataException {
        when(smallFullRepository.findById("")).thenThrow(mongoException);
        Executable executable = () -> {
            smallFullService.findById("", "");
        };
        assertThrows(DataException.class, executable);
    }
}
