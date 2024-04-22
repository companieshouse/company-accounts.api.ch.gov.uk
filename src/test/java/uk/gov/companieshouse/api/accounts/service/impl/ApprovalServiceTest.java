package uk.gov.companieshouse.api.accounts.service.impl;


import com.mongodb.MongoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;
import uk.gov.companieshouse.api.accounts.Kind;
import uk.gov.companieshouse.api.accounts.ResourceName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;
import uk.gov.companieshouse.api.accounts.model.entity.ApprovalEntity;
import uk.gov.companieshouse.api.accounts.model.rest.Approval;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.repository.ApprovalRepository;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transformer.ApprovalTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;
import uk.gov.companieshouse.api.accounts.validation.ApprovalValidator;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.model.transaction.TransactionLinks;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
class ApprovalServiceTest {
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

    @Mock
    private Errors errors;

    @InjectMocks
    private ApprovalService approvalService;

    private static final String TRANSACTION_SELF_LINK = "transactionSelfLink";
    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";
    private static final String RESOURCE_ID = "resourceId";
    private static final String APPROVAL_SELF_LINK = TRANSACTION_SELF_LINK + "/" +
                                                        ResourceName.COMPANY_ACCOUNT.getName() + "/" +
                                                        COMPANY_ACCOUNTS_ID + "/" + ResourceName.SMALL_FULL.getName() +
                                                        "/" + ResourceName.APPROVAL.getName();

    @Test
    @DisplayName("Tests the successful creation of an Approval resource")
    void canCreateAnApproval() throws DataException {
        when(approvalValidator.validateApproval(approval, transaction, COMPANY_ACCOUNTS_ID, request)).thenReturn(errors);
        when(errors.hasErrors()).thenReturn(false);
        when(approvalTransformer.transform(approval)).thenReturn(approvalEntity);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.APPROVAL.getName()))
                .thenReturn(RESOURCE_ID);

        ResponseObject<Approval> result =
                approvalService.create(approval, transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(approval, result.getData());
        assertEquals(ResponseStatus.CREATED, result.getStatus());

        verify(approval).setEtag(anyString());
        verify(approval).setLinks(anyMap());
        verify(approval).setKind(Kind.APPROVAL.getValue());

        verify(smallFullService)
                .addLink(COMPANY_ACCOUNTS_ID, SmallFullLinkType.APPROVAL, APPROVAL_SELF_LINK, request);
    }

    @Test
    @DisplayName("Tests the creation of an Approval resource with validation errors")
    void createApprovalWithValidationErrors() throws DataException {
        when(approvalValidator.validateApproval(approval, transaction, COMPANY_ACCOUNTS_ID, request)).thenReturn(errors);
        when(errors.hasErrors()).thenReturn(true);

        ResponseObject<Approval> result =
                approvalService.create(approval, transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(errors, result.getErrors());
        assertEquals(ResponseStatus.VALIDATION_ERROR, result.getStatus());
    }

    @Test
    @DisplayName("Tests the duplicate key when creating an Approval resource")
    void createApprovalDuplicateKey() throws DataException {
        when(approvalValidator.validateApproval(approval, transaction, COMPANY_ACCOUNTS_ID, request)).thenReturn(errors);
        when(errors.hasErrors()).thenReturn(false);
        when(approvalTransformer.transform(approval)).thenReturn(approvalEntity);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.APPROVAL.getName()))
                .thenReturn(RESOURCE_ID);
        when(approvalRepository.insert(approvalEntity)).thenThrow(duplicateKeyException);

        ResponseObject<Approval> result = approvalService.create(approval, transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(ResponseStatus.DUPLICATE_KEY_ERROR, result.getStatus());
        assertNull(result.getData());

        verify(smallFullService, never())
                .addLink(COMPANY_ACCOUNTS_ID, SmallFullLinkType.APPROVAL, APPROVAL_SELF_LINK, request);
    }

    @Test
    @DisplayName("Tests the mongo exception when creating an Approval")
    void createApprovalMongoExceptionFailure() throws DataException {
        when(approvalValidator.validateApproval(approval, transaction, COMPANY_ACCOUNTS_ID, request)).thenReturn(errors);
        when(errors.hasErrors()).thenReturn(false);
        when(approvalTransformer.transform(approval)).thenReturn(approvalEntity);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.APPROVAL.getName()))
                .thenReturn(RESOURCE_ID);
        when(approvalRepository.insert(approvalEntity)).thenThrow(mongoException);

        assertThrows(DataException.class,
            () -> approvalService.create(approval, transaction, COMPANY_ACCOUNTS_ID, request));
    }

    @Test
    @DisplayName("Tests the successful update of an Approval resource")
    void canUpdateAnApproval() throws DataException {
        when(approvalValidator.validateApproval(approval, transaction, COMPANY_ACCOUNTS_ID, request)).thenReturn(errors);
        when(errors.hasErrors()).thenReturn(false);
        when(approvalTransformer.transform(approval)).thenReturn(approvalEntity);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.APPROVAL.getName()))
                .thenReturn(RESOURCE_ID);

        ResponseObject<Approval> result =
                approvalService.update(approval, transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(ResponseStatus.UPDATED, result.getStatus());

        verify(approval).setEtag(anyString());
        verify(approval).setLinks(anyMap());
        verify(approval).setKind(Kind.APPROVAL.getValue());
    }

    @Test
    @DisplayName("Tests the update of an Approval resource with validation errors")
    void updateApprovalWithValidationErrors() throws DataException {
        when(approvalValidator.validateApproval(approval, transaction, COMPANY_ACCOUNTS_ID, request)).thenReturn(errors);
        when(errors.hasErrors()).thenReturn(true);

        ResponseObject<Approval> result =
                approvalService.update(approval, transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(errors, result.getErrors());
        assertEquals(ResponseStatus.VALIDATION_ERROR, result.getStatus());
    }

    @Test
    @DisplayName("Tests the mongo exception when updating an Approval")
    void updateApprovalMongoExceptionFailure() throws DataException {
        when(approvalValidator.validateApproval(approval, transaction, COMPANY_ACCOUNTS_ID, request)).thenReturn(errors);
        when(errors.hasErrors()).thenReturn(false);
        when(approvalTransformer.transform(approval)).thenReturn(approvalEntity);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.APPROVAL.getName()))
                .thenReturn(RESOURCE_ID);
        when(approvalRepository.save(approvalEntity)).thenThrow(mongoException);

        assertThrows(DataException.class,
                () -> approvalService.update(approval, transaction, COMPANY_ACCOUNTS_ID, request));
    }

    @Test
    @DisplayName("Tests the successful find of an Approval resource")
    void findApproval() throws DataException {
        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.APPROVAL.getName()))
                .thenReturn(RESOURCE_ID);

        when(approvalRepository.findById(RESOURCE_ID)).thenReturn(Optional.of(approvalEntity));

        when(approvalTransformer.transform(approvalEntity)).thenReturn(approval);

        ResponseObject<Approval> result = approvalService.find(COMPANY_ACCOUNTS_ID, request);

        assertEquals(approval, result.getData());
        assertEquals(ResponseStatus.FOUND, result.getStatus());
    }

    @Test
    @DisplayName("Tests the find of an Approval resource which doesn't exist")
    void findApprovalNotFound() throws DataException {
        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.APPROVAL.getName()))
                .thenReturn(RESOURCE_ID);

        ApprovalEntity approvalEntity = null;
        when(approvalRepository.findById(RESOURCE_ID)).thenReturn(Optional.empty());

        ResponseObject<Approval> result = approvalService.find(COMPANY_ACCOUNTS_ID, request);

        assertNull(result.getData());
        assertEquals(ResponseStatus.NOT_FOUND, result.getStatus());

        verify(approvalTransformer, never()).transform(approvalEntity);
    }

    @Test
    @DisplayName("Tests mongo exception thrown on find of an Approval resource")
    void findApprovalMongoException() {
        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.APPROVAL.getName()))
                .thenReturn(RESOURCE_ID);

        when(approvalRepository.findById(RESOURCE_ID)).thenThrow(mongoException);

        assertThrows(DataException.class, () -> approvalService.find(COMPANY_ACCOUNTS_ID, request));
    }

    @Test
    @DisplayName("Tests the successful delete of an Approval resource")
    void deleteApproval() throws DataException {
        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.APPROVAL.getName()))
                .thenReturn(RESOURCE_ID);

        when(approvalRepository.existsById(RESOURCE_ID)).thenReturn(true);

        ResponseObject<Approval> result = approvalService.delete(COMPANY_ACCOUNTS_ID, request);

        assertEquals(ResponseStatus.UPDATED, result.getStatus());

        verify(approvalRepository).deleteById(RESOURCE_ID);
        verify(smallFullService).removeLink(COMPANY_ACCOUNTS_ID, SmallFullLinkType.APPROVAL, request);
    }

    @Test
    @DisplayName("Tests the delete of an Approval resource which doesn't exist")
    void deleteApprovalNotFound() throws DataException {
        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.APPROVAL.getName()))
                .thenReturn(RESOURCE_ID);

        when(approvalRepository.existsById(RESOURCE_ID)).thenReturn(false);

        ResponseObject<Approval> result = approvalService.delete(COMPANY_ACCOUNTS_ID, request);

        assertEquals(ResponseStatus.NOT_FOUND, result.getStatus());

        verify(approvalRepository, never()).deleteById(RESOURCE_ID);
    }

    @Test
    @DisplayName("Tests mongo exception thrown on delete of an Approval resource")
    void deleteApprovalMongoException() {
        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.APPROVAL.getName()))
                .thenReturn(RESOURCE_ID);

        when(approvalRepository.existsById(RESOURCE_ID)).thenThrow(mongoException);

        assertThrows(DataException.class, () -> approvalService.delete(COMPANY_ACCOUNTS_ID, request));
    }
}