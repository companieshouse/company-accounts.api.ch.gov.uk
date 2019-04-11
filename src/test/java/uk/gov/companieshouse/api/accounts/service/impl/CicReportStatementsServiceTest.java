package uk.gov.companieshouse.api.accounts.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mongodb.MongoException;
import java.util.Optional;
import org.springframework.dao.DuplicateKeyException;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.accounts.Kind;
import uk.gov.companieshouse.api.accounts.ResourceName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.BasicLinkType;
import uk.gov.companieshouse.api.accounts.links.CicReportLinkType;
import uk.gov.companieshouse.api.accounts.model.entity.CicReportStatementsEntity;
import uk.gov.companieshouse.api.accounts.model.rest.CicReportStatements;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.repository.CicReportStatementsRepository;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transformer.CicReportStatementsTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.model.transaction.TransactionLinks;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class CicReportStatementsServiceTest {

    @Mock
    private CicReportStatementsRepository repository;

    @Mock
    private CicReportStatementsTransformer transformer;

    @Mock
    private CicReportService cicReportService;

    @Mock
    private KeyIdGenerator keyIdGenerator;

    @InjectMocks
    private CicReportStatementsService service;

    @Mock
    private CicReportStatements rest;

    @Mock
    private CicReportStatementsEntity entity;

    @Mock
    private Transaction transaction;

    @Mock
    private HttpServletRequest request;

    @Mock
    private TransactionLinks transactionLinks;

    @Mock
    private Errors errors;

    @Mock
    private Map<String, String> cicReportStatementsLinks;

    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";
    private static final String TRANSACTION_SELF_LINK = "transactionSelfLink";
    private static final String CIC_REPORT_STATEMENTS_SELF_LINK = "cicReportStatementsSelfLink";
    private static final String RESOURCE_ID = "resourceId";

    @Test
    @DisplayName("Create CIC report statements - success path")
    void createCicReportStatementsSuccess() throws DataException {

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        when(transformer.transform(rest)).thenReturn(entity);

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-"
                                    + ResourceName.CIC_REPORT.getName() + "-"
                                    + ResourceName.STATEMENTS.getName()))
                .thenReturn(RESOURCE_ID);

        when(rest.getLinks()).thenReturn(cicReportStatementsLinks);
        when(cicReportStatementsLinks.get(BasicLinkType.SELF.getLink())).thenReturn(CIC_REPORT_STATEMENTS_SELF_LINK);

        ResponseObject<CicReportStatements> response =
                service.create(rest, transaction, COMPANY_ACCOUNTS_ID, request);

        verify(rest, times(1)).setLinks(anyMap());
        verify(rest, times(1)).setEtag(anyString());
        verify(rest, times(1)).setKind(Kind.CIC_REPORT_STATEMENTS.getValue());

        verify(entity, times(1)).setId(RESOURCE_ID);

        verify(repository, times(1)).insert(entity);

        verify(cicReportService, times(1))
                .addLink(COMPANY_ACCOUNTS_ID, CicReportLinkType.STATEMENTS, CIC_REPORT_STATEMENTS_SELF_LINK, request);

        assertNotNull(response);
        assertEquals(ResponseStatus.CREATED, response.getStatus());
        assertEquals(rest, response.getData());
        assertNull(response.getErrors());
    }

    @Test
    @DisplayName("Create CIC report statements - duplicate key exception")
    void createCicReportStatementsDuplicateKey() throws DataException {

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        when(transformer.transform(rest)).thenReturn(entity);

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-"
                                    + ResourceName.CIC_REPORT.getName() + "-"
                                    + ResourceName.STATEMENTS.getName()))
                .thenReturn(RESOURCE_ID);

        when(repository.insert(entity)).thenThrow(DuplicateKeyException.class);

        ResponseObject<CicReportStatements> response =
                service.create(rest, transaction, COMPANY_ACCOUNTS_ID, request);

        verify(rest, times(1)).setLinks(anyMap());
        verify(rest, times(1)).setEtag(anyString());
        verify(rest, times(1)).setKind(Kind.CIC_REPORT_STATEMENTS.getValue());

        verify(entity, times(1)).setId(RESOURCE_ID);

        verify(cicReportService, never())
                .addLink(COMPANY_ACCOUNTS_ID, CicReportLinkType.STATEMENTS, CIC_REPORT_STATEMENTS_SELF_LINK, request);

        assertNotNull(response);
        assertEquals(ResponseStatus.DUPLICATE_KEY_ERROR, response.getStatus());
        assertNull(response.getData());
        assertNull(response.getErrors());
    }

    @Test
    @DisplayName("Create CIC report statements - Mongo exception")
    void createCicReportStatementsMongoException() throws DataException {

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        when(transformer.transform(rest)).thenReturn(entity);

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-"
                                    + ResourceName.CIC_REPORT.getName() + "-"
                                    + ResourceName.STATEMENTS.getName()))
                .thenReturn(RESOURCE_ID);

        when(repository.insert(entity)).thenThrow(MongoException.class);

        assertThrows(DataException.class, () ->
                service.create(rest, transaction, COMPANY_ACCOUNTS_ID, request));

        verify(rest, times(1)).setLinks(anyMap());
        verify(rest, times(1)).setEtag(anyString());
        verify(rest, times(1)).setKind(Kind.CIC_REPORT_STATEMENTS.getValue());

        verify(entity, times(1)).setId(RESOURCE_ID);

        verify(cicReportService, never())
                .addLink(COMPANY_ACCOUNTS_ID, CicReportLinkType.STATEMENTS, CIC_REPORT_STATEMENTS_SELF_LINK, request);
    }

    @Test
    @DisplayName("Update CIC report statements - success path")
    void updateCicReportStatementsSuccess() throws DataException {

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        when(transformer.transform(rest)).thenReturn(entity);

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-"
                                    + ResourceName.CIC_REPORT.getName() + "-"
                                    + ResourceName.STATEMENTS.getName()))
                .thenReturn(RESOURCE_ID);

        ResponseObject<CicReportStatements> response =
                service.update(rest, transaction, COMPANY_ACCOUNTS_ID, request);

        verify(rest, times(1)).setLinks(anyMap());
        verify(rest, times(1)).setEtag(anyString());
        verify(rest, times(1)).setKind(Kind.CIC_REPORT_STATEMENTS.getValue());

        verify(entity, times(1)).setId(RESOURCE_ID);

        verify(repository, times(1)).save(entity);

        assertNotNull(response);
        assertEquals(ResponseStatus.UPDATED, response.getStatus());
        assertEquals(rest, response.getData());
        assertNull(response.getErrors());
    }

    @Test
    @DisplayName("Update CIC report statements - Mongo exception")
    void updateCicReportStatementsMongoException() {

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        when(transformer.transform(rest)).thenReturn(entity);

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-"
                                    + ResourceName.CIC_REPORT.getName() + "-"
                                    + ResourceName.STATEMENTS.getName()))
                .thenReturn(RESOURCE_ID);

        when(repository.save(entity)).thenThrow(MongoException.class);

        assertThrows(DataException.class, () ->
                service.update(rest, transaction, COMPANY_ACCOUNTS_ID, request));

        verify(rest, times(1)).setLinks(anyMap());
        verify(rest, times(1)).setEtag(anyString());
        verify(rest, times(1)).setKind(Kind.CIC_REPORT_STATEMENTS.getValue());

        verify(entity, times(1)).setId(RESOURCE_ID);
    }

    @Test
    @DisplayName("Find CIC report statements - success path")
    void findCicReportStatementsSuccess() throws DataException {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-"
                                    + ResourceName.CIC_REPORT.getName() + "-"
                                    + ResourceName.STATEMENTS.getName()))
                .thenReturn(RESOURCE_ID);

        when(repository.findById(RESOURCE_ID)).thenReturn(Optional.ofNullable(entity));

        when(transformer.transform(entity)).thenReturn(rest);

        ResponseObject<CicReportStatements> response =
                service.find(COMPANY_ACCOUNTS_ID, request);

        assertNotNull(response);
        assertEquals(ResponseStatus.FOUND, response.getStatus());
        assertEquals(rest, response.getData());
        assertNull(response.getErrors());
    }

    @Test
    @DisplayName("Find CIC report statements - not found")
    void findCicReportStatementsNotFound() throws DataException {

        CicReportStatementsEntity entity = null;

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-"
                                    + ResourceName.CIC_REPORT.getName() + "-"
                                    + ResourceName.STATEMENTS.getName()))
                .thenReturn(RESOURCE_ID);

        when(repository.findById(RESOURCE_ID)).thenReturn(Optional.ofNullable(entity));

        ResponseObject<CicReportStatements> response =
                service.find(COMPANY_ACCOUNTS_ID, request);

        verify(transformer, never()).transform(entity);

        assertNotNull(response);
        assertEquals(ResponseStatus.NOT_FOUND, response.getStatus());
        assertNull(response.getData());
        assertNull(response.getErrors());
    }

    @Test
    @DisplayName("Find CIC report statements - Mongo exception")
    void findCicReportStatementsMongoException() {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-"
                                    + ResourceName.CIC_REPORT.getName() + "-"
                                    + ResourceName.STATEMENTS.getName()))
                .thenReturn(RESOURCE_ID);

        when(repository.findById(RESOURCE_ID)).thenThrow(MongoException.class);

        assertThrows(DataException.class, () ->
                service.find(COMPANY_ACCOUNTS_ID, request));
    }

    @Test
    @DisplayName("Delete CIC report statements - success path")
    void deleteCicReportStatementsSuccess() throws DataException {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-"
                                    + ResourceName.CIC_REPORT.getName() + "-"
                                    + ResourceName.STATEMENTS.getName()))
                .thenReturn(RESOURCE_ID);

        when(repository.existsById(RESOURCE_ID)).thenReturn(true);

        ResponseObject<CicReportStatements> response =
                service.delete(COMPANY_ACCOUNTS_ID, request);

        verify(repository, times(1)).deleteById(RESOURCE_ID);
        verify(cicReportService, times(1))
                .removeLink(COMPANY_ACCOUNTS_ID, CicReportLinkType.STATEMENTS, request);

        assertNotNull(response);
        assertEquals(ResponseStatus.UPDATED, response.getStatus());
        assertNull(response.getData());
        assertNull(response.getErrors());
    }

    @Test
    @DisplayName("Delete CIC report statements - not found")
    void deleteCicReportStatementsNotFound() throws DataException {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-"
                                    + ResourceName.CIC_REPORT.getName() + "-"
                                    + ResourceName.STATEMENTS.getName()))
                .thenReturn(RESOURCE_ID);

        when(repository.existsById(RESOURCE_ID)).thenReturn(false);

        ResponseObject<CicReportStatements> response =
                service.delete(COMPANY_ACCOUNTS_ID, request);

        verify(repository, never()).deleteById(RESOURCE_ID);
        verify(cicReportService, never())
                .removeLink(COMPANY_ACCOUNTS_ID, CicReportLinkType.STATEMENTS, request);

        assertNotNull(response);
        assertEquals(ResponseStatus.NOT_FOUND, response.getStatus());
        assertNull(response.getData());
        assertNull(response.getErrors());
    }

    @Test
    @DisplayName("Delete CIC report statements - Mongo exception")
    void deleteCicReportStatementsMongoException() throws DataException {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-"
                                    + ResourceName.CIC_REPORT.getName() + "-"
                                    + ResourceName.STATEMENTS.getName()))
                .thenReturn(RESOURCE_ID);

        when(repository.existsById(RESOURCE_ID)).thenThrow(MongoException.class);

        assertThrows(DataException.class, () ->
                service.delete(COMPANY_ACCOUNTS_ID, request));

        verify(repository, never()).deleteById(RESOURCE_ID);
        verify(cicReportService, never())
                .removeLink(COMPANY_ACCOUNTS_ID, CicReportLinkType.STATEMENTS, request);
    }
}
