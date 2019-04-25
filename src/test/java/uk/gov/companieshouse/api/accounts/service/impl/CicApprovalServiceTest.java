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
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;
import uk.gov.companieshouse.api.accounts.ResourceName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.entity.CicReportApprovalEntity;
import uk.gov.companieshouse.api.accounts.model.rest.CicApproval;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.repository.CicReportApprovalRepository;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transformer.CicApprovalTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;
import uk.gov.companieshouse.api.accounts.validation.CicApprovalValidator;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.model.transaction.TransactionLinks;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class CicApprovalServiceTest {


    @Mock
    private HttpServletRequest request;

    @Mock
    private CicApproval cicApproval;

    @Mock
    private Transaction transaction;

    @Mock
    private TransactionLinks transactionLinks;

    @Mock
    private CicReportApprovalRepository cicReportApprovalRepository;

    @Mock
    private CicReportService cicReportService;

    @Mock
    private CicApprovalValidator cicApprovalValidator;

    @Mock
    private CicReportApprovalEntity cicReportApprovalEntity;

    @Mock
    private CicApprovalTransformer cicApprovalTransformer;

    @Mock
    private DuplicateKeyException duplicateKeyException;

    @Mock
    private MongoException mongoException;

    @Mock
    private KeyIdGenerator keyIdGenerator;

    @InjectMocks
    private CicApprovalService cicApprovalService;

    private static final String SELF_LINK = "self_link";
    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";
    private static final String RESOURCE_ID = "resourceId";

    @BeforeEach
    void setUp() {

        when(keyIdGenerator
            .generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.CIC_APPROVAL.getName()))
            .thenReturn(RESOURCE_ID);
    }

    @Test
    @DisplayName("Tests the successful creation of an CicApproval resource")
    public void canCreateAnCicReportApproval() throws DataException {
        when(cicApprovalTransformer.transform(cicApproval)).thenReturn(
            cicReportApprovalEntity);
        doReturn(new Errors()).when(cicApprovalValidator)
            .validateCicReportApproval(cicApproval, request);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(SELF_LINK);

        ResponseObject<CicApproval> result = cicApprovalService
            .create(cicApproval, transaction, COMPANY_ACCOUNTS_ID, request);
        assertNotNull(result);
        assertEquals(cicApproval, result.getData());
    }

    @Test
    @DisplayName("Tests the duplicate key when creating an CicApproval resource")
    public void createCicReportApprovalDuplicateKey() throws DataException {
        doReturn(cicReportApprovalEntity).when(cicApprovalTransformer)
            .transform(ArgumentMatchers
                .any(CicApproval.class));
        when(cicReportApprovalRepository.insert(cicReportApprovalEntity))
            .thenThrow(duplicateKeyException);
        doReturn(new Errors()).when(cicApprovalValidator)
            .validateCicReportApproval(cicApproval, request);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(SELF_LINK);

        ResponseObject response = cicApprovalService
            .create(cicApproval, transaction, COMPANY_ACCOUNTS_ID, request);
        assertNotNull(response);
        assertEquals(response.getStatus(), ResponseStatus.DUPLICATE_KEY_ERROR);
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Tests the mongo exception when creating an CicApproval")
    void createCicReportApprovalMongoExceptionFailure() throws DataException {
        doReturn(cicReportApprovalEntity).when(cicApprovalTransformer)
            .transform(ArgumentMatchers
                .any(CicApproval.class));
        doReturn(new Errors()).when(cicApprovalValidator)
            .validateCicReportApproval(cicApproval, request);
        when(cicReportApprovalRepository.insert(cicReportApprovalEntity)).thenThrow(mongoException);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(SELF_LINK);

        assertThrows(DataException.class,
            () -> cicApprovalService
                .create(cicApproval, transaction, COMPANY_ACCOUNTS_ID, request));
    }

    @Test
    @DisplayName("Tests the successful find of an CicApproval resource")
    public void findCicReportApproval() throws DataException {
        when(cicReportApprovalRepository.findById(RESOURCE_ID))
            .thenReturn(Optional.ofNullable(cicReportApprovalEntity));
        when(cicApprovalTransformer.transform(cicReportApprovalEntity))
            .thenReturn(cicApproval);
        ResponseObject<CicApproval> result = cicApprovalService
            .find(COMPANY_ACCOUNTS_ID, request);
        assertNotNull(result);
        assertEquals(cicApproval, result.getData());
    }

    @Test
    @DisplayName("Tests mongo exception thrown on find of an CicApproval resource")
    public void findCicReportApprovalMongoException() {
        when(cicReportApprovalRepository.findById(RESOURCE_ID)).thenThrow(mongoException);
        assertThrows(DataException.class, () -> cicApprovalService
            .find(COMPANY_ACCOUNTS_ID, request));
    }
}