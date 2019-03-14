package uk.gov.companieshouse.api.accounts.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
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

import uk.gov.companieshouse.api.accounts.ResourceName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.BasicLinkType;
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;
import uk.gov.companieshouse.api.accounts.model.entity.notes.creditorswithinoneyear.CreditorsWithinOneYearDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.notes.creditorswithinoneyear.CreditorsWithinOneYearEntity;
import uk.gov.companieshouse.api.accounts.model.rest.RestObject;
import uk.gov.companieshouse.api.accounts.model.rest.notes.creditorswithinoneyear.CreditorsWithinOneYear;
import uk.gov.companieshouse.api.accounts.model.validation.Error;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.repository.CreditorsWithinOneYearRepository;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.transformer.CreditorsWithinOneYearTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;
import uk.gov.companieshouse.api.accounts.validation.CreditorsWithinOneYearValidator;
import uk.gov.companieshouse.api.accounts.validation.ErrorType;
import uk.gov.companieshouse.api.accounts.validation.LocationType;
import uk.gov.companieshouse.api.model.transaction.TransactionLinks;

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
    private TransactionLinks mockTransactionLinks;

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
    private Errors mockErrors;

    @Mock
    private SmallFullService mockSmallFullService;

    @Mock
    private KeyIdGenerator mockKeyIdGenerator;

    private CreditorsWithinOneYearEntity creditorsWithinOneYearEntity;

    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";
    private static final String GENERATED_ID = "generatedId";
    private static final String SELF_LINK = "self_link";

    @BeforeEach
    void setUp() {

        CreditorsWithinOneYearDataEntity dataEntity = new CreditorsWithinOneYearDataEntity();

        Map<String, String> links = new HashMap<>();
        links.put(BasicLinkType.SELF.getLink(), SELF_LINK);
        dataEntity.setLinks(links);

        creditorsWithinOneYearEntity = new CreditorsWithinOneYearEntity();
        creditorsWithinOneYearEntity.setData(dataEntity);
    }

    @Test
    @DisplayName("Tests the successful creation of a creditors within one year resource")
    void canCreateCreditorsWithinOneYear() throws DataException {

        Errors errors = new Errors();

        when(mockCreditorsWithinOneYearValidator.validateCreditorsWithinOneYear(mockCreditorsWithinOneYear, mockTransaction, "", mockRequest)).thenReturn(errors);
        when(mockTransformer.transform(mockCreditorsWithinOneYear)).thenReturn(creditorsWithinOneYearEntity);

        when(mockTransaction.getLinks()).thenReturn(mockTransactionLinks);
        when(mockTransactionLinks.getSelf()).thenReturn(SELF_LINK);

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

        when(mockCreditorsWithinOneYearValidator.validateCreditorsWithinOneYear(mockCreditorsWithinOneYear, mockTransaction, "", mockRequest)).thenReturn(errors);

        doReturn(creditorsWithinOneYearEntity).when(mockTransformer).transform(ArgumentMatchers
            .any(CreditorsWithinOneYear.class));
        when(mockRepository.insert(creditorsWithinOneYearEntity)).thenThrow(mockDuplicateKeyException);

        when(mockTransaction.getLinks()).thenReturn(mockTransactionLinks);
        when(mockTransactionLinks.getSelf()).thenReturn(SELF_LINK);

        ResponseObject<CreditorsWithinOneYear> result = service.create(mockCreditorsWithinOneYear, mockTransaction, "", mockRequest);

        assertNotNull(result);
        assertEquals(result.getStatus(), ResponseStatus.DUPLICATE_KEY_ERROR);
        assertNull(result.getData());
    }

    @Test
    @DisplayName("Tests the mongo exception when creating creditors within one year")
    void createCreditorsWithinOneYearMongoExceptionFailure() throws DataException {

        Errors errors = new Errors();

        when(mockCreditorsWithinOneYearValidator.validateCreditorsWithinOneYear(mockCreditorsWithinOneYear, mockTransaction, "", mockRequest)).thenReturn(errors);

        doReturn(creditorsWithinOneYearEntity).when(mockTransformer).transform(ArgumentMatchers
            .any(CreditorsWithinOneYear.class));
        when(mockRepository.insert(creditorsWithinOneYearEntity)).thenThrow(mockMongoException);

        when(mockTransaction.getLinks()).thenReturn(mockTransactionLinks);
        when(mockTransactionLinks.getSelf()).thenReturn(SELF_LINK);

        assertThrows(DataException.class,
            () -> service.create(mockCreditorsWithinOneYear, mockTransaction, "", mockRequest));
    }

    @Test
    @DisplayName("Tests the successful update of a creditors within one year resource")
    void canUpdateACreditorsWithinOneYear() throws DataException {

        Errors errors = new Errors();
        
        when(mockCreditorsWithinOneYearValidator.validateCreditorsWithinOneYear(mockCreditorsWithinOneYear, mockTransaction, "", mockRequest)).thenReturn(errors);
        when(mockTransformer.transform(mockCreditorsWithinOneYear)).thenReturn(creditorsWithinOneYearEntity);

        when(mockTransaction.getLinks()).thenReturn(mockTransactionLinks);
        when(mockTransactionLinks.getSelf()).thenReturn(SELF_LINK);

        ResponseObject<CreditorsWithinOneYear> result = service.update(mockCreditorsWithinOneYear, mockTransaction,
            "", mockRequest);

        assertNotNull(result);
        assertEquals(mockCreditorsWithinOneYear, result.getData());
    }

    @Test
    @DisplayName("Tests for validation error response during creation of a creditors within one year resource")
    void validationErrorDuringCreateCreditorsWithinOneYear() throws DataException {

        Errors errors = new Errors();
        errors.addError(new Error("test.message.key", "location", LocationType.JSON_PATH.getValue(), ErrorType.VALIDATION.getType()));
        when(mockCreditorsWithinOneYearValidator.validateCreditorsWithinOneYear(mockCreditorsWithinOneYear, mockTransaction, "", mockRequest)).thenReturn(errors);

        ResponseObject<CreditorsWithinOneYear> result = service.create(mockCreditorsWithinOneYear, mockTransaction,
            "", mockRequest);

        assertEquals(ResponseStatus.VALIDATION_ERROR, result.getStatus());
        verify(mockSmallFullService, times(0)).addLink(anyString(), any(SmallFullLinkType.class), anyString(), any(HttpServletRequest.class));
    }
    
    @Test
    @DisplayName("Tests for validation error response during updating of a creditors within one year resource")
    void validationErrorDuringUpdateCreditorsWithinOneYear() throws DataException {

        Errors errors = new Errors();
        errors.addError(new Error("test.message.key", "location", LocationType.JSON_PATH.getValue(), ErrorType.VALIDATION.getType()));
        when(mockCreditorsWithinOneYearValidator.validateCreditorsWithinOneYear(mockCreditorsWithinOneYear, mockTransaction, "", mockRequest)).thenReturn(errors);

        ResponseObject<CreditorsWithinOneYear> result = service.update(mockCreditorsWithinOneYear, mockTransaction,
            "", mockRequest);

        assertEquals(ResponseStatus.VALIDATION_ERROR, result.getStatus());
    }
    
    @Test
    @DisplayName("Tests the mongo exception when updating a creditors within one year")
    void updateCreditorsWithinOneYearMongoExceptionFailure() throws DataException {

        when(mockCreditorsWithinOneYearValidator.validateCreditorsWithinOneYear(mockCreditorsWithinOneYear, mockTransaction, "", mockRequest)).thenReturn(new Errors());
        doReturn(creditorsWithinOneYearEntity).when(mockTransformer).transform(ArgumentMatchers
            .any(CreditorsWithinOneYear.class));
        when(mockRepository.save(creditorsWithinOneYearEntity)).thenThrow(mockMongoException);

        when(mockTransaction.getLinks()).thenReturn(mockTransactionLinks);
        when(mockTransactionLinks.getSelf()).thenReturn(SELF_LINK);

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
    void findCreditorsWithinOneYearResponseNotFound() throws DataException {

        creditorsWithinOneYearEntity = null;

        when(mockRepository.findById(""))
            .thenReturn(Optional.ofNullable(creditorsWithinOneYearEntity));

        ResponseObject<CreditorsWithinOneYear> result = service.findById("", mockRequest);

        assertNotNull(result);
        assertEquals(responseStatusNotFound(), result.getStatus());
    }

    @Test
    @DisplayName("Tests mongo exception thrown on find of a creditors within one year resource")
    void findCreditorsMongoException() {

        when(mockRepository.findById("")).thenThrow(mockMongoException);
        assertThrows(DataException.class, () -> service.findById("", mockRequest));
    }

    @Test
    @DisplayName("Test the successful delete of a creditors within one year resource")
    void deleteCreditorsWithinOneYear() throws DataException {
        when(mockKeyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.CREDITORS_WITHIN_ONE_YEAR.getName()))
                .thenReturn(GENERATED_ID);
        when(mockRepository.existsById(GENERATED_ID)).thenReturn(true);
        doNothing().when(mockRepository).deleteById(GENERATED_ID);

        ResponseObject<CreditorsWithinOneYear> responseObject = service.delete(COMPANY_ACCOUNTS_ID, mockRequest);

        assertNotNull(responseObject);
        assertEquals(responseObject.getStatus(), ResponseStatus.UPDATED);
        verify(mockSmallFullService, times(1))
                .removeLink(COMPANY_ACCOUNTS_ID, SmallFullLinkType.CREDITORS_WITHIN_ONE_YEAR_NOTE, mockRequest);
    }

    @Test
    @DisplayName("Test attempt to delete empty resource produces not found response")
    void deleteEmptyCreditorsWithinOneYear() throws DataException {
        when(mockKeyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.CREDITORS_WITHIN_ONE_YEAR.getName()))
                .thenReturn(GENERATED_ID);
        when(mockRepository.existsById(GENERATED_ID)).thenReturn(false);
        ResponseObject<CreditorsWithinOneYear> responseObject = service.delete(COMPANY_ACCOUNTS_ID, mockRequest);

        assertNotNull(responseObject);
        assertEquals(responseObject.getStatus(), ResponseStatus.NOT_FOUND);
        verify(mockSmallFullService, never())
                .removeLink(COMPANY_ACCOUNTS_ID, SmallFullLinkType.CREDITORS_WITHIN_ONE_YEAR_NOTE, mockRequest);
    }

    @Test
    @DisplayName("Tests mongo exception thrown on deletion of a creditors within one year resource")
    void deleteCreditorsWithinOneYearMongoException() {
        when(mockKeyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.CREDITORS_WITHIN_ONE_YEAR.getName()))
                .thenReturn(GENERATED_ID);
        when(mockRepository.existsById(GENERATED_ID)).thenReturn(true);
        doThrow(mockMongoException).when(mockRepository).deleteById(GENERATED_ID);

        assertThrows(DataException.class, () -> service.delete(COMPANY_ACCOUNTS_ID, mockRequest));
    }

    @Test
    @DisplayName("Tests correct response when error returned from validator")
    void validationError() throws DataException {
        when(mockCreditorsWithinOneYearValidator.validateCreditorsWithinOneYear(mockCreditorsWithinOneYear, mockTransaction, "", mockRequest)).thenReturn(mockErrors);
        when(mockErrors.hasErrors()).thenReturn(true);

        ResponseObject<CreditorsWithinOneYear>responseObject = service.create(mockCreditorsWithinOneYear, mockTransaction, "", mockRequest);

        assertEquals(responseObject.getStatus(), ResponseStatus.VALIDATION_ERROR);
    }

    private ResponseStatus responseStatusNotFound() {
        ResponseObject<RestObject> responseObject = new ResponseObject<>(ResponseStatus.NOT_FOUND);
        return responseObject.getStatus();
    }
}
