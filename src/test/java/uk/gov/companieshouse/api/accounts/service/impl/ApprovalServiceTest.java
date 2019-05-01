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
import uk.gov.companieshouse.api.accounts.model.entity.ApprovalEntity;
import uk.gov.companieshouse.api.accounts.model.rest.Approval;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.repository.ApprovalRepository;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.transformer.ApprovalTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;
import uk.gov.companieshouse.api.accounts.validation.ApprovalValidator;
import uk.gov.companieshouse.api.model.transaction.TransactionLinks;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class ApprovalServiceTest {


    @Mock
    private HttpServletRequest request;

    @Mock
    private Approval approval;

    @Mock
    private Transaction transaction;

    @Mock
    private TransactionLinks transactionLinks;

    @Mock
    private ApprovalRepository approvalRepository;

    @Mock
    private SmallFullService smallFullService;

    @Mock
    private ApprovalValidator approvalValidator;

    @Mock
    private ApprovalEntity approvalEntity;

    @Mock
    private ApprovalTransformer approvalTransformer;

    @Mock
    private DuplicateKeyException duplicateKeyException;

    @Mock
    private MongoException mongoException;

    @Mock
    private KeyIdGenerator keyIdGenerator;

    @InjectMocks
    private ApprovalService approvalService;

    private static final String SELF_LINK = "self_link";
    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";
    private static final String RESOURCE_ID = "resourceId";

    @BeforeEach
    void setUp() {

        when(keyIdGenerator
                .generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.APPROVAL.getName()))
                        .thenReturn(RESOURCE_ID);
    }

    @Test
    @DisplayName("Tests the successful creation of an Approval resource")
    public void canCreateAnApproval() throws DataException {
        when(approvalTransformer.transform(approval)).thenReturn(approvalEntity);
        doReturn(new Errors()).when(approvalValidator).validateApproval(approval, request);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(SELF_LINK);

        ResponseObject<Approval> result = approvalService
            .create(approval, transaction, COMPANY_ACCOUNTS_ID, request);
        assertNotNull(result);
        assertEquals(approval, result.getData());
    }

    @Test
    @DisplayName("Tests the duplicate key when creating an Approval resource")
    public void createApprovalDuplicateKey() throws DataException {
        doReturn(approvalEntity).when(approvalTransformer).transform(ArgumentMatchers
            .any(Approval.class));
        when(approvalRepository.insert(approvalEntity)).thenThrow(duplicateKeyException);
        doReturn(new Errors()).when(approvalValidator).validateApproval(approval, request);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(SELF_LINK);

        ResponseObject response = approvalService.create(approval, transaction, COMPANY_ACCOUNTS_ID, request);
        assertNotNull(response);
        assertEquals(response.getStatus(), ResponseStatus.DUPLICATE_KEY_ERROR);
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Tests the mongo exception when creating an Approval")
    void createApprovalMongoExceptionFailure() throws DataException {
        doReturn(approvalEntity).when(approvalTransformer).transform(ArgumentMatchers
            .any(Approval.class));
        doReturn(new Errors()).when(approvalValidator).validateApproval(approval, request);
        when(approvalRepository.insert(approvalEntity)).thenThrow(mongoException);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(SELF_LINK);

        assertThrows(DataException.class,
            () -> approvalService.create(approval, transaction, COMPANY_ACCOUNTS_ID, request));
    }

    @Test
    @DisplayName("Tests the successful find of an Approval resource")
    public void findApproval() throws DataException {
        when(approvalRepository.findById(RESOURCE_ID))
            .thenReturn(Optional.ofNullable(approvalEntity));
        when(approvalTransformer.transform(approvalEntity)).thenReturn(approval);
        ResponseObject<Approval> result = approvalService
            .find(COMPANY_ACCOUNTS_ID, request);
        assertNotNull(result);
        assertEquals(approval, result.getData());
    }

    @Test
    @DisplayName("Tests mongo exception thrown on find of an Approval resource")
    public void findApprovalMongoException() {
        when(approvalRepository.findById(RESOURCE_ID)).thenThrow(mongoException);
        assertThrows(DataException.class, () -> approvalService.find(COMPANY_ACCOUNTS_ID, request));
    }
}