package uk.gov.companieshouse.api.accounts.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mongodb.MongoException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.BasicLinkType;
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;
import uk.gov.companieshouse.api.accounts.model.entity.notes.creditorswithinoneyear.CreditorsWithinOneYearDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.notes.creditorswithinoneyear.CreditorsWithinOneYearEntity;
import uk.gov.companieshouse.api.accounts.model.rest.notes.creditorsWithinOneYear.CreditorsWithinOneYear;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.repository.CreditorsWithinOneYearRepository;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.transformer.CreditorsWithinOneYearTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;
import uk.gov.companieshouse.api.accounts.validation.CreditorsWithinOneYearValidator;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CreditorsWithinOneYearServiceTest {

    @InjectMocks
    private CreditorsWithinOneYearService service;

    @Mock
    private CreditorsWithinOneYearTransformer mockTransformer;

    @Mock
    private CreditorsWithinOneYear mockCreditorsWithinOneYear;

    @Mock
    private Transaction mockTransaction;

    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private CreditorsWithinOneYearRepository mockRepository;

    @Mock
    private CreditorsWithinOneYearValidator mockCreditorsWithinOneYearValidator;

    @Mock
    private DuplicateKeyException mockDuplicateKeyException;

    @Mock
    private MongoException mockMongoException;

    @Mock
    private SmallFullService mockSmallFullService;

    @Mock
    private KeyIdGenerator mockKeyIdGenerator;

    private CreditorsWithinOneYearEntity creditorsWithinOneYearEntity;

    @BeforeEach
    void setUp() {

        CreditorsWithinOneYearDataEntity dataEntity = new CreditorsWithinOneYearDataEntity();

        Map<String, String> links = new HashMap<>();
        links.put(BasicLinkType.SELF.getLink(), "self_link");
        dataEntity.setLinks(links);

        creditorsWithinOneYearEntity = new CreditorsWithinOneYearEntity();
        creditorsWithinOneYearEntity.setData(dataEntity);
    }

    @Test
    @DisplayName("Tests the successful creation of a creditors within one year resource")
    void canCreateCreditorsWithinOneYear() throws DataException {

        Errors errors = new Errors();

        when(mockCreditorsWithinOneYearValidator.validateCreditorsWithinOneYear(mockCreditorsWithinOneYear, mockTransaction)).thenReturn(errors);
        when(mockTransformer.transform(mockCreditorsWithinOneYear)).thenReturn(creditorsWithinOneYearEntity);

        ResponseObject<CreditorsWithinOneYear> result = service.create(mockCreditorsWithinOneYear, mockTransaction,
            "", mockRequest);

        assertNotNull(result);
        assertEquals(ResponseStatus.CREATED, result.getStatus());
        assertEquals(mockCreditorsWithinOneYear, result.getData());
        verify(mockSmallFullService).addLink(anyString(), any(SmallFullLinkType.class), anyString(), any(HttpServletRequest.class));
    }

    @Test
    @DisplayName("Tests the duplicate key when creating a creditors within one year resource")
    void createCreditorsWithinOneYearDuplicateKey() throws DataException {

        Errors errors = new Errors();

        when(mockCreditorsWithinOneYearValidator.validateCreditorsWithinOneYear(mockCreditorsWithinOneYear, mockTransaction)).thenReturn(errors);

        doReturn(creditorsWithinOneYearEntity).when(mockTransformer).transform(ArgumentMatchers
            .any(CreditorsWithinOneYear.class));
        when(mockRepository.insert(creditorsWithinOneYearEntity)).thenThrow(mockDuplicateKeyException);

        ResponseObject<CreditorsWithinOneYear> result = service.create(mockCreditorsWithinOneYear, mockTransaction, "", mockRequest);

        assertNotNull(result);
        assertEquals(result.getStatus(), ResponseStatus.DUPLICATE_KEY_ERROR);
        assertNull(result.getData());
    }

    @Test
    @DisplayName("Tests the mongo exception when creating creditors within one year")
    void createCreditorsWithinOneYearMongoExceptionFailure() throws DataException {

        Errors errors = new Errors();

        when(mockCreditorsWithinOneYearValidator.validateCreditorsWithinOneYear(mockCreditorsWithinOneYear, mockTransaction)).thenReturn(errors);

        doReturn(creditorsWithinOneYearEntity).when(mockTransformer).transform(ArgumentMatchers
            .any(CreditorsWithinOneYear.class));
        when(mockRepository.insert(creditorsWithinOneYearEntity)).thenThrow(mockMongoException);

        assertThrows(DataException.class,
            () -> service.create(mockCreditorsWithinOneYear, mockTransaction, "", mockRequest));
    }

    @Test
    @DisplayName("Tests the successful update of a creditors within one year resource")
    void canUpdateACreditorsWithinOneYear() throws DataException {

        when(mockTransformer.transform(mockCreditorsWithinOneYear)).thenReturn(creditorsWithinOneYearEntity);

        ResponseObject<CreditorsWithinOneYear> result = service.update(mockCreditorsWithinOneYear, mockTransaction,
            "", mockRequest);

        assertNotNull(result);
        assertEquals(mockCreditorsWithinOneYear, result.getData());
    }

    @Test
    @DisplayName("Tests the mongo exception when updating a creditors within one year")
    void updateCreditorsWithinOneYearMongoExceptionFailure() {

        doReturn(creditorsWithinOneYearEntity).when(mockTransformer).transform(ArgumentMatchers
            .any(CreditorsWithinOneYear.class));
        when(mockRepository.save(creditorsWithinOneYearEntity)).thenThrow(mockMongoException);

        assertThrows(DataException.class,
            () -> service.update(mockCreditorsWithinOneYear, mockTransaction, "", mockRequest));
    }

    @Test
    @DisplayName("Tests the successful find of a creditors within one year resource")
    void findCreditorsWithinOneYear() throws DataException {

        when(mockRepository.findById(""))
            .thenReturn(Optional.ofNullable(creditorsWithinOneYearEntity));
        when(mockTransformer.transform(creditorsWithinOneYearEntity)).thenReturn(mockCreditorsWithinOneYear);

        ResponseObject<CreditorsWithinOneYear> result = service.findById("", mockRequest);

        assertNotNull(result);
        assertEquals(mockCreditorsWithinOneYear, result.getData());
    }

    @Test
    @DisplayName("Tests creditors within one year response not found")
    void findCreditrsWithinOneYearResponseNotFound() throws DataException {

        creditorsWithinOneYearEntity = null;

        when(mockRepository.findById(""))
            .thenReturn(Optional.ofNullable(creditorsWithinOneYearEntity));

        ResponseObject<CreditorsWithinOneYear> result = service.findById("", mockRequest);

        assertNotNull(result);
        assertEquals(responseStatusNotFound(), result.getStatus());
    }

    @Test
    @DisplayName("Tests mongo exception thrown on find of a creditors within one year resource")
    void findDebtorsMongoException() {

        when(mockRepository.findById("")).thenThrow(mockMongoException);
        assertThrows(DataException.class, () -> service.findById("", mockRequest));
    }

    @Test
    @DisplayName("Test the successful delete of a creditors within one year resource")
    void deleteCreditorsWithinOneYear() throws DataException {
        when(mockRepository.existsById("")).thenReturn(true);
        doNothing().when(mockRepository).deleteById("");

        ResponseObject<CreditorsWithinOneYear> responseObject = service.deleteById("", mockRequest);

        assertNotNull(responseObject);
        assertEquals(responseObject.getStatus(), ResponseStatus.UPDATED);
    }

    @Test
    @DisplayName("Test attempt to delete empty resource produces not found response")
    void deleteEmptyCreditorsWithinOneYear() throws DataException {
        when(mockRepository.existsById("")).thenReturn(false);
        ResponseObject<CreditorsWithinOneYear> responseObject = service.deleteById("", mockRequest);

        assertNotNull(responseObject);
        assertEquals(responseObject.getStatus(), ResponseStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Tests mongo exception thrown on deletion of a creditors within one year resource")
    void deleteCreditorsWithinOneYearMongoException() {
        when(mockRepository.existsById("")).thenReturn(true);
        doThrow(mockMongoException).when(mockRepository).deleteById("");

        assertThrows(DataException.class, () -> service.deleteById("", mockRequest));
    }

    private ResponseStatus responseStatusNotFound() {
        ResponseObject responseObject = new ResponseObject<>(ResponseStatus.NOT_FOUND);
        return responseObject.getStatus();
    }
}
