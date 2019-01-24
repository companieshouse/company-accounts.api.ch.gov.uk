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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mongodb.MongoException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;
import uk.gov.companieshouse.api.accounts.ResourceName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.BasicLinkType;
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;
import uk.gov.companieshouse.api.accounts.model.entity.notes.creditorsafteroneyearentity.CreditorsAfterOneYearDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.notes.creditorsafteroneyearentity.CreditorsAfterOneYearEntity;
import uk.gov.companieshouse.api.accounts.model.rest.RestObject;
import uk.gov.companieshouse.api.accounts.model.rest.notes.creditorsafteroneyear.CreditorsAfterOneYear;
import uk.gov.companieshouse.api.accounts.repository.CreditorsAfterOneYearRepository;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.transformer.CreditorsAfterOneYearTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CreditorsAfterOneYearServiceTest {

    @InjectMocks
    private CreditorsAfterOneYearService mockCreditorsAfterOneYearService;

    @Mock
    private CreditorsAfterOneYearTransformer mockTransformer;

    @Mock
    private CreditorsAfterOneYear mockCreditorsAfterOneYear;

    @Mock
    private Transaction mockTransaction;

    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private CreditorsAfterOneYearRepository mockRepository;

    @Mock
    private DuplicateKeyException mockDuplicateKeyException;

    @Mock
    private KeyIdGenerator mockKeyIdGenerator;

    @Mock
    private MongoException mockMongoException;

    @Mock
    private SmallFullService mockSmallFullService;

    private CreditorsAfterOneYearEntity creditorsAfterOneYearEntity;

    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";
    private static final String CREDITORS_AFTER_ID = "creditorsAfterId";


    @BeforeAll
    void setUp() {

        CreditorsAfterOneYearDataEntity dataEntity = new CreditorsAfterOneYearDataEntity();

        Map<String, String> links = new HashMap<>();
        links.put(BasicLinkType.SELF.getLink(), "self_link");
        dataEntity.setLinks(links);

        creditorsAfterOneYearEntity = new CreditorsAfterOneYearEntity();
        creditorsAfterOneYearEntity.setData(dataEntity);
    }

    @Test
    @DisplayName("Tests the successful creation of a creditors after one year resource")
    void canCreateCreditorsAfterOneYear() throws DataException {

        when(mockTransformer.transform(mockCreditorsAfterOneYear)).thenReturn(creditorsAfterOneYearEntity);

        ResponseObject<CreditorsAfterOneYear> result =
                mockCreditorsAfterOneYearService.create(mockCreditorsAfterOneYear, mockTransaction,
                "", mockRequest);

        assertNotNull(result);
        assertEquals(ResponseStatus.CREATED, result.getStatus());
        assertEquals(mockCreditorsAfterOneYear, result.getData());
        verify(mockSmallFullService).addLink(anyString(), any(SmallFullLinkType.class),
                anyString(), any(HttpServletRequest.class));
    }

    @Test
    @DisplayName("Tests the duplicate key when creating a creditors after one year resource")
    void createCreditorsAfterOneYearDuplicateKey() throws DataException {

        doReturn(creditorsAfterOneYearEntity).when(mockTransformer).transform(ArgumentMatchers
                .any(CreditorsAfterOneYear.class));
        when(mockRepository.insert(creditorsAfterOneYearEntity)).thenThrow(mockDuplicateKeyException);

        ResponseObject<CreditorsAfterOneYear> result =
                mockCreditorsAfterOneYearService.create(mockCreditorsAfterOneYear,
                        mockTransaction, "", mockRequest);

        assertNotNull(result);
        assertEquals(result.getStatus(), ResponseStatus.DUPLICATE_KEY_ERROR);
        assertNull(result.getData());
    }

    @Test
    @DisplayName("Tests the mongo exception when creating creditors after one year")
    void createCreditorsAfterOneYearMongoExceptionFailure() {

        doReturn(creditorsAfterOneYearEntity).when(mockTransformer).transform(ArgumentMatchers
                .any(CreditorsAfterOneYear.class));
        when(mockRepository.insert(creditorsAfterOneYearEntity)).thenThrow(mockMongoException);

        assertThrows(DataException.class,
                () -> mockCreditorsAfterOneYearService.create(mockCreditorsAfterOneYear,
                        mockTransaction, "", mockRequest));
    }

    @Test
    @DisplayName("Test the successful delete of a creditors within one year resource")
    void deleteCreditorsAfterOneYear() throws DataException {
        when(mockKeyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.CREDITORS_AFTER_ONE_YEAR.getName()))
                .thenReturn(CREDITORS_AFTER_ID);
        when(mockRepository.existsById(CREDITORS_AFTER_ID)).thenReturn(true);
        doNothing().when(mockRepository).deleteById(CREDITORS_AFTER_ID);

        ResponseObject<CreditorsAfterOneYear> responseObject = mockCreditorsAfterOneYearService.delete(COMPANY_ACCOUNTS_ID, mockRequest);

        assertNotNull(responseObject);
        assertEquals(responseObject.getStatus(), ResponseStatus.UPDATED);
        verify(mockSmallFullService, Mockito.times(1))
                .removeLink(COMPANY_ACCOUNTS_ID, SmallFullLinkType.CREDITORS_AFTER_MORE_THAN_ONE_YEAR_NOTE, mockRequest);
    }

    @Test
    @DisplayName("Test attempt to delete empty resource produces not found response")
    void deleteEmptyCreditorsAfterOneYear() throws DataException {
        when(mockKeyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.CREDITORS_AFTER_ONE_YEAR.getName()))
                .thenReturn(CREDITORS_AFTER_ID);
        when(mockRepository.existsById(CREDITORS_AFTER_ID)).thenReturn(false);
        ResponseObject<CreditorsAfterOneYear> responseObject = mockCreditorsAfterOneYearService.delete(COMPANY_ACCOUNTS_ID, mockRequest);

        assertNotNull(responseObject);
        assertEquals(responseObject.getStatus(), ResponseStatus.NOT_FOUND);
        verify(mockSmallFullService, never())
                .removeLink(COMPANY_ACCOUNTS_ID, SmallFullLinkType.CREDITORS_AFTER_MORE_THAN_ONE_YEAR_NOTE, mockRequest);
    }

    @Test
    @DisplayName("Tests mongo exception thrown on deletion of a creditors after one year resource")
    void deleteCreditorsAfterOneYearMongoException() {
        when(mockKeyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.CREDITORS_AFTER_ONE_YEAR.getName()))
                .thenReturn(CREDITORS_AFTER_ID);
        when(mockRepository.existsById(CREDITORS_AFTER_ID)).thenReturn(true);
        doThrow(mockMongoException).when(mockRepository).deleteById(CREDITORS_AFTER_ID);

        assertThrows(DataException.class, () -> mockCreditorsAfterOneYearService.delete(COMPANY_ACCOUNTS_ID, mockRequest));
    }

    @DisplayName("Tests the successful update of a creditors after one year resource")
    void canUpdateACreditorsAfterOneYear() throws DataException {

        when(mockTransformer.transform(mockCreditorsAfterOneYear)).thenReturn(creditorsAfterOneYearEntity);

        ResponseObject<CreditorsAfterOneYear> result = mockCreditorsAfterOneYearService.update(mockCreditorsAfterOneYear, mockTransaction,
                "", mockRequest);

        assertNotNull(result);
        assertEquals(mockCreditorsAfterOneYear, result.getData());
    }

    @Test
    @DisplayName("Tests the mongo exception when updating a creditors after one year")
    void updateCreditorsAfterOneYearMongoExceptionFailure() throws DataException {

        doReturn(creditorsAfterOneYearEntity).when(mockTransformer).transform(ArgumentMatchers
                .any(CreditorsAfterOneYear.class));
        when(mockRepository.save(creditorsAfterOneYearEntity)).thenThrow(mockMongoException);

        assertThrows(DataException.class,
                () -> mockCreditorsAfterOneYearService.update(mockCreditorsAfterOneYear, mockTransaction, "", mockRequest));
    }

    @Test
    @DisplayName("Tests the successful find of a creditors after one year resource")
    void findCreditorsAfterOneYear() throws DataException {

        when(mockRepository.findById(""))
                .thenReturn(Optional.ofNullable(creditorsAfterOneYearEntity));
        when(mockTransformer.transform(creditorsAfterOneYearEntity)).thenReturn(mockCreditorsAfterOneYear);

        ResponseObject<CreditorsAfterOneYear> result = mockCreditorsAfterOneYearService.findById("", mockRequest);

    }

    @Test
    @DisplayName("Tests creditors within one year response not found")
    void findCreditorsAfterOneYearResponseNotFound() throws DataException {

        creditorsAfterOneYearEntity = null;

        when(mockRepository.findById(""))
                .thenReturn(Optional.ofNullable(creditorsAfterOneYearEntity));

        ResponseObject<CreditorsAfterOneYear> result = mockCreditorsAfterOneYearService.findById("", mockRequest);

        assertNotNull(result);
        assertEquals(responseStatusNotFound(), result.getStatus());
    }

    @Test
    @DisplayName("Tests mongo exception thrown on find of a creditors after one year resource")
    void findCreditorsMongoException() {

        when(mockRepository.findById("")).thenThrow(mockMongoException);
        assertThrows(DataException.class, () -> mockCreditorsAfterOneYearService.findById("", mockRequest));
    }

    private ResponseStatus responseStatusNotFound() {
        ResponseObject<RestObject> responseObject = new ResponseObject<>(ResponseStatus.NOT_FOUND);
        return responseObject.getStatus();
    }
}
