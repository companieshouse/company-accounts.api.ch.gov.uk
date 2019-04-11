package uk.gov.companieshouse.api.accounts.service.impl;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mongodb.MongoException;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
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
import uk.gov.companieshouse.api.accounts.links.CicReportLinkType;
import uk.gov.companieshouse.api.accounts.links.CompanyAccountLinkType;
import uk.gov.companieshouse.api.accounts.model.entity.CicReportDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.CicReportEntity;
import uk.gov.companieshouse.api.accounts.model.rest.CicReport;
import uk.gov.companieshouse.api.accounts.repository.CicReportRepository;
import uk.gov.companieshouse.api.accounts.service.CompanyAccountService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transformer.CicReportTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.model.transaction.TransactionLinks;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class CicReportServiceTest {

    @Mock
    private CicReportRepository repository;

    @Mock
    private CicReportTransformer transformer;

    @Mock
    private CompanyAccountService companyAccountService;

    @Mock
    private KeyIdGenerator keyIdGenerator;

    @InjectMocks
    private CicReportService service;

    @Mock
    private CicReport cicReport;

    @Mock
    private CicReportEntity cicReportEntity;

    @Mock
    private CicReportDataEntity cicReportDataEntity;

    @Mock
    private Transaction transaction;

    @Mock
    private TransactionLinks transactionLinks;

    @Mock
    private Map<String, String> cicReportLinks;

    @Mock
    private HttpServletRequest request;

    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";
    private static final String TRANSACTION_SELF_LINK = "transactionSelfLink";
    private static final String CIC_REPORT_ID = "cicReportId";
    private static final String CIC_REPORT_SELF_LINK = "cicReportSelfLink";
    private static final String CIC_REPORT_APPROVAL_LINK = "cicReportApprovalLink";

    @Test
    @DisplayName("Create cic report - success")
    void createCicReportSuccess() throws DataException {

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        when(transformer.transform(cicReport)).thenReturn(cicReportEntity);

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.CIC_REPORT.getName()))
                .thenReturn(CIC_REPORT_ID);

        when(cicReport.getLinks()).thenReturn(cicReportLinks);
        when(cicReportLinks.get(CicReportLinkType.SELF.getLink())).thenReturn(CIC_REPORT_SELF_LINK);

        ResponseObject<CicReport> response =
                service.create(cicReport, transaction, COMPANY_ACCOUNTS_ID, request);

        assertNotNull(response);
        assertEquals(ResponseStatus.CREATED, response.getStatus());
        assertEquals(cicReport, response.getData());
        assertNull(response.getErrors());

        verify(cicReport).setEtag(anyString());
        verify(cicReport).setKind(Kind.CIC_REPORT.getValue());
        verify(cicReport).setLinks(anyMap());

        verify(cicReportEntity).setId(CIC_REPORT_ID);

        verify(repository).insert(cicReportEntity);

        verify(companyAccountService)
                .addLink(COMPANY_ACCOUNTS_ID, CompanyAccountLinkType.CIC_REPORT, CIC_REPORT_SELF_LINK);
    }

    @Test
    @DisplayName("Create cic report - duplicate key exception")
    void createCicReportDuplicateKeyException() throws DataException {

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        when(transformer.transform(cicReport)).thenReturn(cicReportEntity);

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.CIC_REPORT.getName()))
                .thenReturn(CIC_REPORT_ID);

        when(repository.insert(cicReportEntity)).thenThrow(DuplicateKeyException.class);

        ResponseObject<CicReport> response =
                service.create(cicReport, transaction, COMPANY_ACCOUNTS_ID, request);

        assertNotNull(response);
        assertEquals(ResponseStatus.DUPLICATE_KEY_ERROR, response.getStatus());
        assertNull(response.getData());
        assertNull(response.getErrors());

        verify(cicReport).setEtag(anyString());
        verify(cicReport).setKind(Kind.CIC_REPORT.getValue());
        verify(cicReport).setLinks(anyMap());

        verify(cicReportEntity).setId(CIC_REPORT_ID);

        verify(companyAccountService, never())
                .addLink(eq(COMPANY_ACCOUNTS_ID), eq(CompanyAccountLinkType.CIC_REPORT), anyString());
    }

    @Test
    @DisplayName("Create cic report - Mongo exception")
    void createCicReportMongoException() {

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        when(transformer.transform(cicReport)).thenReturn(cicReportEntity);

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.CIC_REPORT.getName()))
                .thenReturn(CIC_REPORT_ID);

        when(repository.insert(cicReportEntity)).thenThrow(MongoException.class);

        assertThrows(DataException.class, () ->
                service.create(cicReport, transaction, COMPANY_ACCOUNTS_ID, request));
    }

    @Test
    @DisplayName("Find cic report - success")
    void findCicReportSuccess() throws DataException {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.CIC_REPORT.getName()))
                .thenReturn(CIC_REPORT_ID);

        when(repository.findById(CIC_REPORT_ID)).thenReturn(Optional.ofNullable(cicReportEntity));

        when(transformer.transform(cicReportEntity)).thenReturn(cicReport);

        ResponseObject<CicReport> response = service.find(COMPANY_ACCOUNTS_ID, request);

        assertNotNull(response);
        assertEquals(ResponseStatus.FOUND, response.getStatus());
        assertEquals(cicReport, response.getData());
        assertNull(response.getErrors());
    }

    @Test
    @DisplayName("Find cic report - not found")
    void findCicReportNotFound() throws DataException {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.CIC_REPORT.getName()))
                .thenReturn(CIC_REPORT_ID);

        CicReportEntity cicReportEntity = null;

        when(repository.findById(CIC_REPORT_ID)).thenReturn(Optional.ofNullable(cicReportEntity));

        ResponseObject<CicReport> response = service.find(COMPANY_ACCOUNTS_ID, request);

        assertNotNull(response);
        assertEquals(ResponseStatus.NOT_FOUND, response.getStatus());
        assertNull(response.getData());
        assertNull(response.getErrors());
    }

    @Test
    @DisplayName("Find cic report - Mongo exception")
    void findCicReportMongoException() {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.CIC_REPORT.getName()))
                .thenReturn(CIC_REPORT_ID);

        when(repository.findById(CIC_REPORT_ID)).thenThrow(MongoException.class);

        assertThrows(DataException.class, () -> service.find(COMPANY_ACCOUNTS_ID, request));
    }

    @Test
    @DisplayName("Delete cic report - success")
    void deleteCicReportSuccess() throws DataException {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.CIC_REPORT.getName()))
                .thenReturn(CIC_REPORT_ID);

        when(repository.existsById(CIC_REPORT_ID)).thenReturn(true);

        ResponseObject<CicReport> response = service.delete(COMPANY_ACCOUNTS_ID, request);

        assertNotNull(response);
        assertEquals(ResponseStatus.UPDATED, response.getStatus());
        assertNull(response.getData());
        assertNull(response.getErrors());

        verify(repository).deleteById(CIC_REPORT_ID);
        verify(companyAccountService).removeLink(COMPANY_ACCOUNTS_ID, CompanyAccountLinkType.CIC_REPORT);
    }

    @Test
    @DisplayName("Delete cic report - not found")
    void deleteCicReportNotFound() throws DataException {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.CIC_REPORT.getName()))
                .thenReturn(CIC_REPORT_ID);

        when(repository.existsById(CIC_REPORT_ID)).thenReturn(false);

        ResponseObject<CicReport> response = service.delete(COMPANY_ACCOUNTS_ID, request);

        assertNotNull(response);
        assertEquals(ResponseStatus.NOT_FOUND, response.getStatus());
        assertNull(response.getData());
        assertNull(response.getErrors());

        verify(repository, never()).deleteById(CIC_REPORT_ID);
        verify(companyAccountService, never())
                .removeLink(COMPANY_ACCOUNTS_ID, CompanyAccountLinkType.CIC_REPORT);
    }

    @Test
    @DisplayName("Delete cic report - Mongo exception")
    void deleteCicReportMongoException() {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.CIC_REPORT.getName()))
                .thenReturn(CIC_REPORT_ID);

        when(repository.existsById(CIC_REPORT_ID)).thenThrow(MongoException.class);

        assertThrows(DataException.class, () -> service.delete(COMPANY_ACCOUNTS_ID, request));

        verify(repository, never()).deleteById(CIC_REPORT_ID);
        verify(companyAccountService, never())
                .removeLink(COMPANY_ACCOUNTS_ID, CompanyAccountLinkType.CIC_REPORT);
    }

    @Test
    @DisplayName("Add link - success")
    void addLinkSuccess() {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.CIC_REPORT.getName()))
                .thenReturn(CIC_REPORT_ID);

        when(repository.findById(CIC_REPORT_ID)).thenReturn(Optional.ofNullable(cicReportEntity));

        when(cicReportEntity.getData()).thenReturn(cicReportDataEntity);
        when(cicReportDataEntity.getLinks()).thenReturn(cicReportLinks);

        CicReportLinkType linkType = CicReportLinkType.APPROVAL;

        assertAll(() ->
                service.addLink(
                        COMPANY_ACCOUNTS_ID, linkType, CIC_REPORT_APPROVAL_LINK, request));

        verify(cicReportLinks).put(linkType.getLink(), CIC_REPORT_APPROVAL_LINK);
        verify(repository).save(cicReportEntity);
    }

    @Test
    @DisplayName("Add link - not found")
    void addLinkNotFound() {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.CIC_REPORT.getName()))
                .thenReturn(CIC_REPORT_ID);

        CicReportEntity cicReportEntity = null;

        when(repository.findById(CIC_REPORT_ID)).thenReturn(Optional.ofNullable(cicReportEntity));

        assertThrows(DataException.class, () ->
                service.addLink(
                        COMPANY_ACCOUNTS_ID, CicReportLinkType.APPROVAL, CIC_REPORT_APPROVAL_LINK, request));
    }

    @Test
    @DisplayName("Add link - Mongo exception")
    void addLinkMongoException() {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.CIC_REPORT.getName()))
                .thenReturn(CIC_REPORT_ID);

        when(repository.findById(CIC_REPORT_ID)).thenThrow(MongoException.class);

        assertThrows(DataException.class, () ->
                service.addLink(
                        COMPANY_ACCOUNTS_ID, CicReportLinkType.APPROVAL, CIC_REPORT_APPROVAL_LINK, request));
    }

    @Test
    @DisplayName("Remove link - success")
    void removeLinkSuccess() {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.CIC_REPORT.getName()))
                .thenReturn(CIC_REPORT_ID);

        when(repository.findById(CIC_REPORT_ID)).thenReturn(Optional.ofNullable(cicReportEntity));

        when(cicReportEntity.getData()).thenReturn(cicReportDataEntity);
        when(cicReportDataEntity.getLinks()).thenReturn(cicReportLinks);

        CicReportLinkType linkType = CicReportLinkType.APPROVAL;

        assertAll(() ->
                service.removeLink(
                        COMPANY_ACCOUNTS_ID, linkType, request));

        verify(cicReportLinks).remove(linkType.getLink());
        verify(repository).save(cicReportEntity);
    }

    @Test
    @DisplayName("Remove link - not found")
    void removeLinkNotFound() {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.CIC_REPORT.getName()))
                .thenReturn(CIC_REPORT_ID);

        CicReportEntity cicReportEntity = null;

        when(repository.findById(CIC_REPORT_ID)).thenReturn(Optional.ofNullable(cicReportEntity));

        assertThrows(DataException.class, () ->
                service.removeLink(
                        COMPANY_ACCOUNTS_ID, CicReportLinkType.APPROVAL, request));
    }

    @Test
    @DisplayName("Remove link - Mongo exception")
    void removeLinkMongoException() {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.CIC_REPORT.getName()))
                .thenReturn(CIC_REPORT_ID);

        when(repository.findById(CIC_REPORT_ID)).thenThrow(MongoException.class);

        assertThrows(DataException.class, () ->
                service.removeLink(
                        COMPANY_ACCOUNTS_ID, CicReportLinkType.APPROVAL, request));
    }
}
