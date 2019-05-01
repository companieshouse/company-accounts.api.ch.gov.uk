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
import uk.gov.companieshouse.api.accounts.model.entity.notes.fixedassetsinvestments.FixedAssetsInvestmentsDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.notes.fixedassetsinvestments.FixedAssetsInvestmentsEntity;
import uk.gov.companieshouse.api.accounts.model.rest.RestObject;
import uk.gov.companieshouse.api.accounts.model.rest.notes.fixedassetsinvestments.FixedAssetsInvestments;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.repository.FixedAssetsInvestmentsRepository;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.model.transaction.TransactionLinks;
import uk.gov.companieshouse.api.accounts.transformer.FixedAssetsInvestmentsTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FixedAssetsInvestmentsServiceTest {

    @InjectMocks
    private FixedAssetsInvestmentsService service;

    @Mock
    private FixedAssetsInvestmentsTransformer mockTransformer;

    @Mock
    private FixedAssetsInvestments mockFixedAssetsInvestments;

    @Mock
    private Transaction mockTransaction;
    
    @Mock
    private TransactionLinks mockTransactionLinks;

    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private FixedAssetsInvestmentsRepository mockRepository;

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

    private FixedAssetsInvestmentsEntity fixedAssetsInvestmentsEntity;

    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";
    private static final String GENERATED_ID = "generatedId";
    private static final String SELF_LINK = "self_link";

    @BeforeEach
    void setUp() {

        FixedAssetsInvestmentsDataEntity dataEntity = new FixedAssetsInvestmentsDataEntity();

        Map<String, String> links = new HashMap<>();
        links.put(BasicLinkType.SELF.getLink(), "self_link");
        dataEntity.setLinks(links);

        fixedAssetsInvestmentsEntity = new FixedAssetsInvestmentsEntity();
        fixedAssetsInvestmentsEntity.setData(dataEntity);
    }

    @Test
    @DisplayName("Tests the successful creation of a fixed assets investments resource")
    void canCreateFixedAssetsInvestments() throws DataException {

        when(mockTransformer.transform(mockFixedAssetsInvestments)).thenReturn(fixedAssetsInvestmentsEntity);

        when(mockTransaction.getLinks()).thenReturn(mockTransactionLinks);
        when(mockTransactionLinks.getSelf()).thenReturn(SELF_LINK);
        
        ResponseObject<FixedAssetsInvestments> result = service.create(mockFixedAssetsInvestments, mockTransaction,
            "", mockRequest);

        assertNotNull(result);
        assertEquals(ResponseStatus.CREATED, result.getStatus());
        assertEquals(mockFixedAssetsInvestments, result.getData());
        verify(mockSmallFullService).addLink(anyString(), any(SmallFullLinkType.class), anyString(), any(HttpServletRequest.class));
    }

    @Test
    @DisplayName("Tests the duplicate key when creating a fixed assets investments resource")
    void createFixedAssetsInvestmentsDuplicateKey() throws DataException {

        doReturn(fixedAssetsInvestmentsEntity).when(mockTransformer).transform(ArgumentMatchers
            .any(FixedAssetsInvestments.class));
        when(mockRepository.insert(fixedAssetsInvestmentsEntity)).thenThrow(mockDuplicateKeyException);

        when(mockTransaction.getLinks()).thenReturn(mockTransactionLinks);
        when(mockTransactionLinks.getSelf()).thenReturn(SELF_LINK);
        
        ResponseObject<FixedAssetsInvestments> result = service.create(mockFixedAssetsInvestments, mockTransaction, "", mockRequest);

        assertNotNull(result);
        assertEquals(result.getStatus(), ResponseStatus.DUPLICATE_KEY_ERROR);
        assertNull(result.getData());
    }

    @Test
    @DisplayName("Tests the mongo exception when creating fixed assets investments")
    void createFixedAssetsInvestmentsMongoExceptionFailure() throws DataException {

        doReturn(fixedAssetsInvestmentsEntity).when(mockTransformer).transform(ArgumentMatchers
            .any(FixedAssetsInvestments.class));
        when(mockRepository.insert(fixedAssetsInvestmentsEntity)).thenThrow(mockMongoException);

        when(mockTransaction.getLinks()).thenReturn(mockTransactionLinks);
        when(mockTransactionLinks.getSelf()).thenReturn(SELF_LINK);
        
        assertThrows(DataException.class,
            () -> service.create(mockFixedAssetsInvestments, mockTransaction, "", mockRequest));
    }

    @Test
    @DisplayName("Tests the successful update of a fixed assets investments resource")
    void canUpdateAFixedAssetsInvestments() throws DataException {

        when(mockTransformer.transform(mockFixedAssetsInvestments)).thenReturn(fixedAssetsInvestmentsEntity);
        when(mockTransaction.getLinks()).thenReturn(mockTransactionLinks);
        when(mockTransactionLinks.getSelf()).thenReturn(SELF_LINK);
        
        ResponseObject<FixedAssetsInvestments> result = service.update(mockFixedAssetsInvestments, mockTransaction,
            "", mockRequest);

        assertNotNull(result);
        assertEquals(mockFixedAssetsInvestments, result.getData());
    }
    
    @Test
    @DisplayName("Tests the mongo exception when updating a fixed assets investments")
    void updateFixedAssetsInvestmentsMongoExceptionFailure() throws DataException {

        doReturn(fixedAssetsInvestmentsEntity).when(mockTransformer).transform(ArgumentMatchers
            .any(FixedAssetsInvestments.class));
        when(mockRepository.save(fixedAssetsInvestmentsEntity)).thenThrow(mockMongoException);

        when(mockTransaction.getLinks()).thenReturn(mockTransactionLinks);
        when(mockTransactionLinks.getSelf()).thenReturn(SELF_LINK);
        
        assertThrows(DataException.class,
            () -> service.update(mockFixedAssetsInvestments, mockTransaction, "", mockRequest));
    }

    @Test
    @DisplayName("Tests the successful find of a fixed assets investments resource")
    void findFixedAssetsInvestments() throws DataException {

        when(mockKeyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.FIXED_ASSETS_INVESTMENTS.getName()))
                .thenReturn(GENERATED_ID);

        when(mockRepository.findById(GENERATED_ID))
            .thenReturn(Optional.ofNullable(fixedAssetsInvestmentsEntity));
        when(mockTransformer.transform(fixedAssetsInvestmentsEntity)).thenReturn(mockFixedAssetsInvestments);

        ResponseObject<FixedAssetsInvestments> result = service.find(COMPANY_ACCOUNTS_ID, mockRequest);

        assertNotNull(result);
        assertEquals(mockFixedAssetsInvestments, result.getData());
    }

    @Test
    @DisplayName("Tests fixed assets investments response not found")
    void findFixedAssetsInvestmentsResponseNotFound() throws DataException {

        fixedAssetsInvestmentsEntity = null;

        when(mockKeyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.FIXED_ASSETS_INVESTMENTS.getName()))
                .thenReturn(GENERATED_ID);

        when(mockRepository.findById(GENERATED_ID))
            .thenReturn(Optional.ofNullable(fixedAssetsInvestmentsEntity));

        ResponseObject<FixedAssetsInvestments> result = service.find(COMPANY_ACCOUNTS_ID, mockRequest);

        assertNotNull(result);
        assertEquals(responseStatusNotFound(), result.getStatus());
    }

    @Test
    @DisplayName("Tests mongo exception thrown on find of a fixed assets investments resource")
    void findFixedAssetsInvestmentsMongoException() {

        when(mockKeyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.FIXED_ASSETS_INVESTMENTS.getName()))
                .thenReturn(GENERATED_ID);

        when(mockRepository.findById(GENERATED_ID)).thenThrow(mockMongoException);
        assertThrows(DataException.class, () -> service.find(COMPANY_ACCOUNTS_ID, mockRequest));
    }

    @Test
    @DisplayName("Test the successful delete of a fixed assets investments resource")
    void deleteFixedAssetsInvestments() throws DataException {
        when(mockKeyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.FIXED_ASSETS_INVESTMENTS.getName()))
                .thenReturn(GENERATED_ID);
        when(mockRepository.existsById(GENERATED_ID)).thenReturn(true);
        doNothing().when(mockRepository).deleteById(GENERATED_ID);

        ResponseObject<FixedAssetsInvestments> responseObject = service.delete(COMPANY_ACCOUNTS_ID, mockRequest);

        assertNotNull(responseObject);
        assertEquals(responseObject.getStatus(), ResponseStatus.UPDATED);
        verify(mockSmallFullService, times(1))
                .removeLink(COMPANY_ACCOUNTS_ID, SmallFullLinkType.FIXED_ASSETS_INVESTMENTS_NOTE, mockRequest);
    }

    @Test
    @DisplayName("Test attempt to delete empty resource produces not found response")
    void deleteEmptyFixedAssetsInvestments() throws DataException {
        when(mockKeyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.FIXED_ASSETS_INVESTMENTS.getName()))
                .thenReturn(GENERATED_ID);
        when(mockRepository.existsById(GENERATED_ID)).thenReturn(false);
        ResponseObject<FixedAssetsInvestments> responseObject = service.delete(COMPANY_ACCOUNTS_ID, mockRequest);

        assertNotNull(responseObject);
        assertEquals(responseObject.getStatus(), ResponseStatus.NOT_FOUND);
        verify(mockSmallFullService, never())
                .removeLink(COMPANY_ACCOUNTS_ID, SmallFullLinkType.FIXED_ASSETS_INVESTMENTS_NOTE, mockRequest);
    }

    @Test
    @DisplayName("Tests mongo exception thrown on deletion of a fixed assets investments resource")
    void deleteFixedAssetsInvestmentsMongoException() {
        when(mockKeyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.FIXED_ASSETS_INVESTMENTS.getName()))
                .thenReturn(GENERATED_ID);
        when(mockRepository.existsById(GENERATED_ID)).thenReturn(true);
        doThrow(mockMongoException).when(mockRepository).deleteById(GENERATED_ID);

        assertThrows(DataException.class, () -> service.delete(COMPANY_ACCOUNTS_ID, mockRequest));
    }

    private ResponseStatus responseStatusNotFound() {
        ResponseObject<RestObject> responseObject = new ResponseObject<>(ResponseStatus.NOT_FOUND);
        return responseObject.getStatus();
    }
}
