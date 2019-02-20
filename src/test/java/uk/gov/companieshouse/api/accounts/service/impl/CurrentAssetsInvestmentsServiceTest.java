package uk.gov.companieshouse.api.accounts.service.impl;

import com.mongodb.MongoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;
import uk.gov.companieshouse.api.accounts.ResourceName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.BasicLinkType;
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;
import uk.gov.companieshouse.api.accounts.model.entity.notes.currentassetsinvestments.CurrentAssetsInvestmentsDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.notes.currentassetsinvestments.CurrentAssetsInvestmentsEntity;
import uk.gov.companieshouse.api.accounts.model.rest.RestObject;
import uk.gov.companieshouse.api.accounts.model.rest.notes.CurrentAssetsInvestments;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.repository.CurrentAssetsInvestmentsRepository;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transformer.CurrentAssetsInvestmentsTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.model.transaction.TransactionLinks;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CurrentAssetsInvestmentsServiceTest {

    @InjectMocks
    private CurrentAssetsInvestmentsService service;

    @Mock
    private CurrentAssetsInvestmentsTransformer mockTransformer;

    @Mock
    private CurrentAssetsInvestments mockCurrentAssetsInvestments;

    @Mock
    private Transaction mockTransaction;

    @Mock
    private TransactionLinks mockTransactionLinks;

    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private CurrentAssetsInvestmentsRepository mockRepository;

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

    private CurrentAssetsInvestmentsEntity currentAssetsInvestmentsEntity;

    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";
    private static final String GENERATED_ID = "generatedId";
    private static final String SELF_LINK = "self_link";

    @BeforeEach
    void setUp() {

        CurrentAssetsInvestmentsDataEntity dataEntity = new CurrentAssetsInvestmentsDataEntity();

        Map<String, String> links = new HashMap<>();
        links.put(BasicLinkType.SELF.getLink(), "self_link");
        dataEntity.setLinks(links);

        currentAssetsInvestmentsEntity = new CurrentAssetsInvestmentsEntity();
        currentAssetsInvestmentsEntity.setData(dataEntity);
    }

    @Test
    @DisplayName("Tests the successful creation of a current assets investments resource")
    void canCreateCurrentAssetsInvestments() throws DataException {

        when(mockTransformer.transform(mockCurrentAssetsInvestments)).thenReturn(currentAssetsInvestmentsEntity);

        when(mockTransaction.getLinks()).thenReturn(mockTransactionLinks);
        when(mockTransactionLinks.getSelf()).thenReturn(SELF_LINK);

        ResponseObject<CurrentAssetsInvestments> result = service.create(mockCurrentAssetsInvestments, mockTransaction,
            "", mockRequest);

        assertNotNull(result);
        assertEquals(ResponseStatus.CREATED, result.getStatus());
        assertEquals(mockCurrentAssetsInvestments, result.getData());
        verify(mockSmallFullService).addLink(anyString(), any(SmallFullLinkType.class), anyString(), any(HttpServletRequest.class));
    }

    @Test
    @DisplayName("Tests the duplicate key when creating a current assets investments resource")
    void createCurrentAssetsInvestmentsDuplicateKey() throws DataException {

        doReturn(currentAssetsInvestmentsEntity).when(mockTransformer).transform(any(CurrentAssetsInvestments.class));
        when(mockRepository.insert(currentAssetsInvestmentsEntity)).thenThrow(mockDuplicateKeyException);

        when(mockTransaction.getLinks()).thenReturn(mockTransactionLinks);
        when(mockTransactionLinks.getSelf()).thenReturn(SELF_LINK);

        ResponseObject<CurrentAssetsInvestments> result = service.create(mockCurrentAssetsInvestments, mockTransaction, "", mockRequest);

        assertNotNull(result);
        assertEquals(result.getStatus(), ResponseStatus.DUPLICATE_KEY_ERROR);
        assertNull(result.getData());
    }

    @Test
    @DisplayName("Tests the mongo exception when creating current assets investments")
    void createCurrentAssetsInvestmentsMongoExceptionFailure() {

        doReturn(currentAssetsInvestmentsEntity).when(mockTransformer).transform(any(CurrentAssetsInvestments.class));

        when(mockTransaction.getLinks()).thenReturn(mockTransactionLinks);
        when(mockTransactionLinks.getSelf()).thenReturn(SELF_LINK);
        when(mockRepository.insert(currentAssetsInvestmentsEntity)).thenThrow(mockMongoException);

        assertThrows(DataException.class,
            () -> service.create(mockCurrentAssetsInvestments, mockTransaction, "", mockRequest));
    }

    @Test
    @DisplayName("Tests the successful update of a current assets investments resource")
    void canUpdateACurrentAssetsInvestments() throws DataException {

        when(mockTransformer.transform(mockCurrentAssetsInvestments)).thenReturn(currentAssetsInvestmentsEntity);

        when(mockTransaction.getLinks()).thenReturn(mockTransactionLinks);
        when(mockTransactionLinks.getSelf()).thenReturn(SELF_LINK);

        ResponseObject<CurrentAssetsInvestments> result = service.update(mockCurrentAssetsInvestments, mockTransaction,
            "", mockRequest);

        assertNotNull(result);
        assertEquals(mockCurrentAssetsInvestments, result.getData());
    }

    @Test
    @DisplayName("Tests the mongo exception when updating a current assets investments")
    void updateCurrentAssetsInvestmentsMongoExceptionFailure() {

        doReturn(currentAssetsInvestmentsEntity).when(mockTransformer).transform(any(CurrentAssetsInvestments.class));

        when(mockTransaction.getLinks()).thenReturn(mockTransactionLinks);
        when(mockTransactionLinks.getSelf()).thenReturn(SELF_LINK);
        when(mockRepository.save(currentAssetsInvestmentsEntity)).thenThrow(mockMongoException);

        assertThrows(DataException.class,
            () -> service.update(mockCurrentAssetsInvestments, mockTransaction, "", mockRequest));
    }

    @Test
    @DisplayName("Tests the successful find of a current assets investments resource")
    void findCurrentAssetsInvestments() throws DataException {

        when(mockRepository.findById(""))
            .thenReturn(Optional.ofNullable(currentAssetsInvestmentsEntity));
        when(mockTransformer.transform(currentAssetsInvestmentsEntity)).thenReturn(mockCurrentAssetsInvestments);

        ResponseObject<CurrentAssetsInvestments> result = service.findById("", mockRequest);

        assertNotNull(result);
        assertEquals(mockCurrentAssetsInvestments, result.getData());
    }

    @Test
    @DisplayName("Tests current assets investments response not found")
    void findCurrentAssetsInvestmentsResponseNotFound() throws DataException {

        currentAssetsInvestmentsEntity = null;

        when(mockRepository.findById(""))
            .thenReturn(Optional.ofNullable(currentAssetsInvestmentsEntity));

        ResponseObject<CurrentAssetsInvestments> result = service.findById("", mockRequest);

        assertNotNull(result);
        assertEquals(responseStatusNotFound(), result.getStatus());
    }

    @Test
    @DisplayName("Tests mongo exception thrown on find of a current assets investments resource")
    void findCurrentAssetsInvestmentsMongoException() {

        when(mockRepository.findById("")).thenThrow(mockMongoException);
        assertThrows(DataException.class, () -> service.findById("", mockRequest));
    }

    @Test
    @DisplayName("Test the successful delete of a current assets investments resource")
    void deleteCurrentAssetsInvestments() throws DataException {
        when(mockKeyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.CURRENT_ASSETS_INVESTMENTS.getName()))
            .thenReturn(GENERATED_ID);
        when(mockRepository.existsById(GENERATED_ID)).thenReturn(true);
        doNothing().when(mockRepository).deleteById(GENERATED_ID);

        ResponseObject<CurrentAssetsInvestments> responseObject = service.delete(COMPANY_ACCOUNTS_ID, mockRequest);

        assertNotNull(responseObject);
        assertEquals(responseObject.getStatus(), ResponseStatus.UPDATED);
        verify(mockSmallFullService, times(1))
            .removeLink(COMPANY_ACCOUNTS_ID, SmallFullLinkType.CURRENT_ASSETS_INVESTMENTS_NOTE, mockRequest);
    }

    @Test
    @DisplayName("Test attempt to delete empty resource produces not found response")
    void deleteEmptyCurrentAssetsInvestments() throws DataException {
        when(mockKeyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.CURRENT_ASSETS_INVESTMENTS.getName()))
            .thenReturn(GENERATED_ID);
        when(mockRepository.existsById(GENERATED_ID)).thenReturn(false);
        ResponseObject<CurrentAssetsInvestments> responseObject = service.delete(COMPANY_ACCOUNTS_ID, mockRequest);

        assertNotNull(responseObject);
        assertEquals(responseObject.getStatus(), ResponseStatus.NOT_FOUND);
        verify(mockSmallFullService, never())
            .removeLink(COMPANY_ACCOUNTS_ID, SmallFullLinkType.CURRENT_ASSETS_INVESTMENTS_NOTE, mockRequest);
    }

    @Test
    @DisplayName("Tests mongo exception thrown on deletion of a current assets investments resource")
    void deleteCurrentAssetsInvestmentsMongoException() {
        when(mockKeyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.CURRENT_ASSETS_INVESTMENTS.getName()))
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
