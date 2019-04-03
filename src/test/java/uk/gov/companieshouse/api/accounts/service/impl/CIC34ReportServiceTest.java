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
import uk.gov.companieshouse.api.accounts.links.CompanyAccountLinkType;
import uk.gov.companieshouse.api.accounts.model.entity.CIC34ReportEntity;
import uk.gov.companieshouse.api.accounts.model.rest.CIC34Report;
import uk.gov.companieshouse.api.accounts.repository.CIC34ReportRepository;
import uk.gov.companieshouse.api.accounts.service.CompanyAccountService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transformer.CIC34ReportTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.model.transaction.TransactionLinks;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class CIC34ReportServiceTest {

    @Mock
    private CIC34ReportRepository repository;

    @Mock
    private CIC34ReportTransformer transformer;

    @Mock
    private CompanyAccountService companyAccountService;

    @Mock
    private KeyIdGenerator keyIdGenerator;

    @InjectMocks
    private CIC34ReportService service;

    @Mock
    private CIC34Report rest;

    @Mock
    private CIC34ReportEntity entity;

    @Mock
    private Transaction transaction;

    @Mock
    private HttpServletRequest request;

    @Mock
    private TransactionLinks transactionLinks;

    @Mock
    private Map<String, String> cic34ReportLinks;

    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";
    private static final String TRANSACTION_SELF_LINK = "transactionSelfLink";
    private static final String CIC34_REPORT_SELF_LINK = "cic34ReportSelfLink";
    private static final String RESOURCE_ID = "resourceId";

    @Test
    @DisplayName("Create a CIC34 report - success path")
    void createCIC34ReportSuccess() throws DataException {

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        when(transformer.transform(rest)).thenReturn(entity);

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.CIC34_REPORT.getName()))
                .thenReturn(RESOURCE_ID);

        when(rest.getLinks()).thenReturn(cic34ReportLinks);
        when(cic34ReportLinks.get(BasicLinkType.SELF.getLink())).thenReturn(CIC34_REPORT_SELF_LINK);

        ResponseObject<CIC34Report> response =
                service.create(rest, transaction, COMPANY_ACCOUNTS_ID, request);

        verify(rest, times(1)).setLinks(anyMap());
        verify(rest, times(1)).setEtag(anyString());
        verify(rest, times(1)).setKind(Kind.CIC34_REPORT.getValue());

        verify(entity, times(1)).setId(RESOURCE_ID);

        verify(repository, times(1)).insert(entity);

        verify(companyAccountService, times(1))
                .addLink(COMPANY_ACCOUNTS_ID, CompanyAccountLinkType.CIC34_REPORT, CIC34_REPORT_SELF_LINK);

        assertNotNull(response);
        assertEquals(ResponseStatus.CREATED, response.getStatus());
        assertEquals(rest, response.getData());
        assertNull(response.getErrors());
    }

    @Test
    @DisplayName("Create a CIC34 report - duplicate key exception")
    void createCIC34ReportDuplicateKey() throws DataException {

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        when(transformer.transform(rest)).thenReturn(entity);

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.CIC34_REPORT.getName()))
                .thenReturn(RESOURCE_ID);

        when(repository.insert(entity)).thenThrow(DuplicateKeyException.class);

        ResponseObject<CIC34Report> response =
                service.create(rest, transaction, COMPANY_ACCOUNTS_ID, request);

        verify(rest, times(1)).setLinks(anyMap());
        verify(rest, times(1)).setEtag(anyString());
        verify(rest, times(1)).setKind(Kind.CIC34_REPORT.getValue());

        verify(entity, times(1)).setId(RESOURCE_ID);

        verify(companyAccountService, never())
                .addLink(COMPANY_ACCOUNTS_ID, CompanyAccountLinkType.CIC34_REPORT, CIC34_REPORT_SELF_LINK);

        assertNotNull(response);
        assertEquals(ResponseStatus.DUPLICATE_KEY_ERROR, response.getStatus());
        assertNull(response.getData());
        assertNull(response.getErrors());
    }

    @Test
    @DisplayName("Create a CIC34 report - Mongo exception")
    void createCIC34ReportMongoException() {

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        when(transformer.transform(rest)).thenReturn(entity);

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.CIC34_REPORT.getName()))
                .thenReturn(RESOURCE_ID);

        when(repository.insert(entity)).thenThrow(MongoException.class);

        assertThrows(DataException.class, () ->
                service.create(rest, transaction, COMPANY_ACCOUNTS_ID, request));

        verify(rest, times(1)).setLinks(anyMap());
        verify(rest, times(1)).setEtag(anyString());
        verify(rest, times(1)).setKind(Kind.CIC34_REPORT.getValue());

        verify(entity, times(1)).setId(RESOURCE_ID);

        verify(companyAccountService, never())
                .addLink(COMPANY_ACCOUNTS_ID, CompanyAccountLinkType.CIC34_REPORT, CIC34_REPORT_SELF_LINK);
    }

    @Test
    @DisplayName("Update a CIC34 report - success path")
    void updateCIC34ReportSuccess() throws DataException {

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        when(transformer.transform(rest)).thenReturn(entity);

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.CIC34_REPORT.getName()))
                .thenReturn(RESOURCE_ID);

        ResponseObject<CIC34Report> response =
                service.update(rest, transaction, COMPANY_ACCOUNTS_ID, request);

        verify(rest, times(1)).setLinks(anyMap());
        verify(rest, times(1)).setEtag(anyString());
        verify(rest, times(1)).setKind(Kind.CIC34_REPORT.getValue());

        verify(entity, times(1)).setId(RESOURCE_ID);

        verify(repository, times(1)).save(entity);

        assertNotNull(response);
        assertEquals(ResponseStatus.UPDATED, response.getStatus());
        assertEquals(rest, response.getData());
        assertNull(response.getErrors());
    }

    @Test
    @DisplayName("Update a CIC34 report - Mongo exception")
    void updateCIC34ReportMongoException() {

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        when(transformer.transform(rest)).thenReturn(entity);

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.CIC34_REPORT.getName()))
                .thenReturn(RESOURCE_ID);

        when(repository.save(entity)).thenThrow(MongoException.class);

        assertThrows(DataException.class, () ->
                service.update(rest, transaction, COMPANY_ACCOUNTS_ID, request));

        verify(rest, times(1)).setLinks(anyMap());
        verify(rest, times(1)).setEtag(anyString());
        verify(rest, times(1)).setKind(Kind.CIC34_REPORT.getValue());

        verify(entity, times(1)).setId(RESOURCE_ID);
    }

    @Test
    @DisplayName("Find a CIC34 report - success path")
    void findCIC34ReportSuccess() throws DataException {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.CIC34_REPORT.getName()))
                .thenReturn(RESOURCE_ID);

        when(repository.findById(RESOURCE_ID)).thenReturn(Optional.ofNullable(entity));

        when(transformer.transform(entity)).thenReturn(rest);

        ResponseObject<CIC34Report> response =
                service.find(COMPANY_ACCOUNTS_ID, request);

        assertNotNull(response);
        assertEquals(ResponseStatus.FOUND, response.getStatus());
        assertEquals(rest, response.getData());
        assertNull(response.getErrors());
    }

    @Test
    @DisplayName("Find a CIC34 report - not found")
    void findCIC34ReportNotFound() throws DataException {

        CIC34ReportEntity entity = null;

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.CIC34_REPORT.getName()))
                .thenReturn(RESOURCE_ID);

        when(repository.findById(RESOURCE_ID)).thenReturn(Optional.ofNullable(entity));

        ResponseObject<CIC34Report> response =
                service.find(COMPANY_ACCOUNTS_ID, request);

        verify(transformer, never()).transform(entity);

        assertNotNull(response);
        assertEquals(ResponseStatus.NOT_FOUND, response.getStatus());
        assertNull(response.getData());
        assertNull(response.getErrors());
    }

    @Test
    @DisplayName("Find a CIC34 report - Mongo exception")
    void findCIC34ReportMongoException() {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.CIC34_REPORT.getName()))
                .thenReturn(RESOURCE_ID);

        when(repository.findById(RESOURCE_ID)).thenThrow(MongoException.class);

        assertThrows(DataException.class, () ->
                service.find(COMPANY_ACCOUNTS_ID, request));
    }

    @Test
    @DisplayName("Delete a CIC34 report - success path")
    void deleteCIC34ReportSuccess() throws DataException {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.CIC34_REPORT.getName()))
                .thenReturn(RESOURCE_ID);

        when(repository.existsById(RESOURCE_ID)).thenReturn(true);

        ResponseObject<CIC34Report> response =
                service.delete(COMPANY_ACCOUNTS_ID, request);

        verify(repository, times(1)).deleteById(RESOURCE_ID);
        verify(companyAccountService, times(1))
                .removeLink(COMPANY_ACCOUNTS_ID, CompanyAccountLinkType.CIC34_REPORT);

        assertNotNull(response);
        assertEquals(ResponseStatus.UPDATED, response.getStatus());
        assertNull(response.getData());
        assertNull(response.getErrors());
    }

    @Test
    @DisplayName("Delete a CIC34 report - not found")
    void deleteCIC34ReportNotFound() throws DataException {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.CIC34_REPORT.getName()))
                .thenReturn(RESOURCE_ID);

        when(repository.existsById(RESOURCE_ID)).thenReturn(false);

        ResponseObject<CIC34Report> response =
                service.delete(COMPANY_ACCOUNTS_ID, request);

        verify(repository, never()).deleteById(RESOURCE_ID);
        verify(companyAccountService, never())
                .removeLink(COMPANY_ACCOUNTS_ID, CompanyAccountLinkType.CIC34_REPORT);

        assertNotNull(response);
        assertEquals(ResponseStatus.NOT_FOUND, response.getStatus());
        assertNull(response.getData());
        assertNull(response.getErrors());
    }

    @Test
    @DisplayName("Delete a CIC34 report - Mongo exception")
    void deleteCIC34ReportMongoException() {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.CIC34_REPORT.getName()))
                .thenReturn(RESOURCE_ID);

        when(repository.existsById(RESOURCE_ID)).thenThrow(MongoException.class);

        assertThrows(DataException.class, () ->
                service.delete(COMPANY_ACCOUNTS_ID, request));

        verify(repository, never()).deleteById(RESOURCE_ID);
        verify(companyAccountService, never())
                .removeLink(COMPANY_ACCOUNTS_ID, CompanyAccountLinkType.CIC34_REPORT);
    }
}
