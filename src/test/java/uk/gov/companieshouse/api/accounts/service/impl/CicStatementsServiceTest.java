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
import uk.gov.companieshouse.api.accounts.model.entity.CicStatementsEntity;
import uk.gov.companieshouse.api.accounts.model.rest.CicStatements;
import uk.gov.companieshouse.api.accounts.model.rest.ReportStatements;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.repository.CicStatementsRepository;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transformer.CicStatementsTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;
import uk.gov.companieshouse.api.accounts.validation.CicStatementsValidator;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.model.transaction.TransactionLinks;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
class CicStatementsServiceTest {

    @Mock
    private CicStatementsRepository repository;

    @Mock
    private CicStatementsTransformer transformer;

    @Mock
    private CicStatementsValidator validator;

    @Mock
    private CicReportService cicReportService;

    @Mock
    private KeyIdGenerator keyIdGenerator;

    @InjectMocks
    private CicStatementsService service;

    @Mock
    private CicStatements rest;

    @Mock
    private ReportStatements reportStatements;

    @Mock
    private CicStatementsEntity entity;

    @Mock
    private Transaction transaction;

    @Mock
    private HttpServletRequest request;

    @Mock
    private TransactionLinks transactionLinks;

    @Mock
    private Errors errors;

    @Mock
    private Map<String, String> cicStatementsLinks;

    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";
    private static final String TRANSACTION_SELF_LINK = "transactionSelfLink";
    private static final String CIC_STATEMENTS_SELF_LINK = "cicStatementsSelfLink";
    private static final String RESOURCE_ID = "resourceId";

    private static final String PROVIDED_CONSULTATION_WITH_STAKEHOLDERS = "providedConsultationWithStakeholders";
    private static final String PROVIDED_DIRECTORS_REMUNERATION = "providedDirectorsRemuneration";
    private static final String PROVIDED_TRANSFER_OF_ASSETS = "providedTransferOfAssets";

    private static final String DEFAULT_CONSULTATION_WITH_STAKEHOLDERS = "No consultation with stakeholders";
    private static final String DEFAULT_DIRECTORS_REMUNERATION = "No remuneration was received";
    private static final String DEFAULT_TRANSFER_OF_ASSETS = "No transfer of assets other than for full consideration";

    @Test
    @DisplayName("Create CIC statements - success path for REST object with all optional fields populated")
    void createCicStatementsSuccessWithAllFieldsPopulated() throws DataException {

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        when(rest.getReportStatements()).thenReturn(reportStatements);
        when(reportStatements.getConsultationWithStakeholders())
                .thenReturn(PROVIDED_CONSULTATION_WITH_STAKEHOLDERS);
        when(reportStatements.getDirectorsRemuneration())
                .thenReturn(PROVIDED_DIRECTORS_REMUNERATION);
        when(reportStatements.getTransferOfAssets())
                .thenReturn(PROVIDED_TRANSFER_OF_ASSETS);

        when(transformer.transform(rest)).thenReturn(entity);

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-"
                                    + ResourceName.CIC_STATEMENTS.getName()))
                .thenReturn(RESOURCE_ID);

        when(rest.getLinks()).thenReturn(cicStatementsLinks);
        when(cicStatementsLinks.get(BasicLinkType.SELF.getLink())).thenReturn(
                CIC_STATEMENTS_SELF_LINK);

        ResponseObject<CicStatements> response =
                service.create(rest, transaction, COMPANY_ACCOUNTS_ID, request);

        verify(rest, times(1)).setLinks(anyMap());
        verify(rest, times(1)).setEtag(anyString());
        verify(rest, times(1)).setKind(Kind.CIC_STATEMENTS.getValue());

        verify(reportStatements, never()).setConsultationWithStakeholders(DEFAULT_CONSULTATION_WITH_STAKEHOLDERS);
        verify(reportStatements, never()).setDirectorsRemuneration(DEFAULT_DIRECTORS_REMUNERATION);
        verify(reportStatements, never()).setTransferOfAssets(DEFAULT_TRANSFER_OF_ASSETS);

        verify(entity, times(1)).setId(RESOURCE_ID);

        verify(repository, times(1)).insert(entity);

        verify(cicReportService, times(1))
                .addLink(COMPANY_ACCOUNTS_ID, CicReportLinkType.STATEMENTS,
                        CIC_STATEMENTS_SELF_LINK, request);

        assertNotNull(response);
        assertEquals(ResponseStatus.CREATED, response.getStatus());
        assertEquals(rest, response.getData());
        assertNull(response.getErrors());
    }

    @Test
    @DisplayName("Create CIC statements - success path for REST object without optional fields populated")
    void createCicStatementsSuccessWithoutOptionalFieldsPopulated() throws DataException {

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        when(rest.getReportStatements()).thenReturn(reportStatements);
        when(reportStatements.getConsultationWithStakeholders()).thenReturn(null);
        when(reportStatements.getDirectorsRemuneration()).thenReturn(null);
        when(reportStatements.getTransferOfAssets()).thenReturn(null);

        when(transformer.transform(rest)).thenReturn(entity);

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-"
                + ResourceName.CIC_STATEMENTS.getName()))
                .thenReturn(RESOURCE_ID);

        when(rest.getLinks()).thenReturn(cicStatementsLinks);
        when(cicStatementsLinks.get(BasicLinkType.SELF.getLink())).thenReturn(
                CIC_STATEMENTS_SELF_LINK);

        ResponseObject<CicStatements> response =
                service.create(rest, transaction, COMPANY_ACCOUNTS_ID, request);

        verify(rest, times(1)).setLinks(anyMap());
        verify(rest, times(1)).setEtag(anyString());
        verify(rest, times(1)).setKind(Kind.CIC_STATEMENTS.getValue());

        verify(reportStatements, times(1))
                .setConsultationWithStakeholders(DEFAULT_CONSULTATION_WITH_STAKEHOLDERS);
        verify(reportStatements, times(1))
                .setDirectorsRemuneration(DEFAULT_DIRECTORS_REMUNERATION);
        verify(reportStatements, times(1))
                .setTransferOfAssets(DEFAULT_TRANSFER_OF_ASSETS);

        verify(entity, times(1)).setId(RESOURCE_ID);

        verify(repository, times(1)).insert(entity);

        verify(cicReportService, times(1))
                .addLink(COMPANY_ACCOUNTS_ID, CicReportLinkType.STATEMENTS,
                        CIC_STATEMENTS_SELF_LINK, request);

        assertNotNull(response);
        assertEquals(ResponseStatus.CREATED, response.getStatus());
        assertEquals(rest, response.getData());
        assertNull(response.getErrors());
    }

    @Test
    @DisplayName("Create CIC statements - duplicate key exception")
    void createCicStatementsDuplicateKey() throws DataException {

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        when(rest.getReportStatements()).thenReturn(reportStatements);
        when(reportStatements.getConsultationWithStakeholders())
                .thenReturn(PROVIDED_CONSULTATION_WITH_STAKEHOLDERS);
        when(reportStatements.getDirectorsRemuneration())
                .thenReturn(PROVIDED_DIRECTORS_REMUNERATION);
        when(reportStatements.getTransferOfAssets())
                .thenReturn(PROVIDED_TRANSFER_OF_ASSETS);

        when(transformer.transform(rest)).thenReturn(entity);

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-"
                + ResourceName.CIC_STATEMENTS.getName()))
                .thenReturn(RESOURCE_ID);

        when(repository.insert(entity)).thenThrow(DuplicateKeyException.class);

        ResponseObject<CicStatements> response =
                service.create(rest, transaction, COMPANY_ACCOUNTS_ID, request);

        verify(rest, times(1)).setLinks(anyMap());
        verify(rest, times(1)).setEtag(anyString());
        verify(rest, times(1)).setKind(Kind.CIC_STATEMENTS.getValue());

        verify(reportStatements, never()).setConsultationWithStakeholders(DEFAULT_CONSULTATION_WITH_STAKEHOLDERS);
        verify(reportStatements, never()).setDirectorsRemuneration(DEFAULT_DIRECTORS_REMUNERATION);
        verify(reportStatements, never()).setTransferOfAssets(DEFAULT_TRANSFER_OF_ASSETS);

        verify(entity, times(1)).setId(RESOURCE_ID);

        verify(cicReportService, never())
                .addLink(COMPANY_ACCOUNTS_ID, CicReportLinkType.STATEMENTS,
                        CIC_STATEMENTS_SELF_LINK, request);

        assertNotNull(response);
        assertEquals(ResponseStatus.DUPLICATE_KEY_ERROR, response.getStatus());
        assertNull(response.getData());
        assertNull(response.getErrors());
    }

    @Test
    @DisplayName("Create CIC statements - Mongo exception")
    void createCicStatementsMongoException() throws DataException {

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        when(rest.getReportStatements()).thenReturn(reportStatements);
        when(reportStatements.getConsultationWithStakeholders())
                .thenReturn(PROVIDED_CONSULTATION_WITH_STAKEHOLDERS);
        when(reportStatements.getDirectorsRemuneration())
                .thenReturn(PROVIDED_DIRECTORS_REMUNERATION);
        when(reportStatements.getTransferOfAssets())
                .thenReturn(PROVIDED_TRANSFER_OF_ASSETS);

        when(transformer.transform(rest)).thenReturn(entity);

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-"
                + ResourceName.CIC_STATEMENTS.getName()))
                .thenReturn(RESOURCE_ID);

        when(repository.insert(entity)).thenThrow(MongoException.class);

        assertThrows(DataException.class, () ->
                service.create(rest, transaction, COMPANY_ACCOUNTS_ID, request));

        verify(rest, times(1)).setLinks(anyMap());
        verify(rest, times(1)).setEtag(anyString());
        verify(rest, times(1)).setKind(Kind.CIC_STATEMENTS.getValue());

        verify(reportStatements, never()).setConsultationWithStakeholders(DEFAULT_CONSULTATION_WITH_STAKEHOLDERS);
        verify(reportStatements, never()).setDirectorsRemuneration(DEFAULT_DIRECTORS_REMUNERATION);
        verify(reportStatements, never()).setTransferOfAssets(DEFAULT_TRANSFER_OF_ASSETS);

        verify(entity, times(1)).setId(RESOURCE_ID);

        verify(cicReportService, never())
                .addLink(COMPANY_ACCOUNTS_ID, CicReportLinkType.STATEMENTS,
                        CIC_STATEMENTS_SELF_LINK, request);
    }

    @Test
    @DisplayName("Update CIC statements - success path")
    void updateCicStatementsSuccess() throws DataException {

        when(validator.validateCicStatementsUpdate(rest)).thenReturn(errors);
        when(errors.hasErrors()).thenReturn(false);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        when(transformer.transform(rest)).thenReturn(entity);

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-"
                + ResourceName.CIC_STATEMENTS.getName()))
                .thenReturn(RESOURCE_ID);

        ResponseObject<CicStatements> response =
                service.update(rest, transaction, COMPANY_ACCOUNTS_ID, request);

        verify(rest, times(1)).setLinks(anyMap());
        verify(rest, times(1)).setEtag(anyString());
        verify(rest, times(1)).setKind(Kind.CIC_STATEMENTS.getValue());

        verify(entity, times(1)).setId(RESOURCE_ID);

        verify(repository, times(1)).save(entity);

        assertNotNull(response);
        assertEquals(ResponseStatus.UPDATED, response.getStatus());
        assertEquals(rest, response.getData());
        assertNull(response.getErrors());
    }

    @Test
    @DisplayName("Update CIC statements - Mongo exception")
    void updateCicStatementsMongoException() {

        when(validator.validateCicStatementsUpdate(rest)).thenReturn(errors);
        when(errors.hasErrors()).thenReturn(false);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        when(transformer.transform(rest)).thenReturn(entity);

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-"
                + ResourceName.CIC_STATEMENTS.getName()))
                .thenReturn(RESOURCE_ID);

        when(repository.save(entity)).thenThrow(MongoException.class);

        assertThrows(DataException.class, () ->
                service.update(rest, transaction, COMPANY_ACCOUNTS_ID, request));

        verify(rest, times(1)).setLinks(anyMap());
        verify(rest, times(1)).setEtag(anyString());
        verify(rest, times(1)).setKind(Kind.CIC_STATEMENTS.getValue());

        verify(entity, times(1)).setId(RESOURCE_ID);
    }

    @Test
    @DisplayName("Find CIC statements - success path")
    void findCicStatementsSuccess() throws DataException {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-"
                + ResourceName.CIC_STATEMENTS.getName()))
                .thenReturn(RESOURCE_ID);

        when(repository.findById(RESOURCE_ID)).thenReturn(Optional.ofNullable(entity));

        when(transformer.transform(entity)).thenReturn(rest);

        ResponseObject<CicStatements> response =
                service.find(COMPANY_ACCOUNTS_ID, request);

        assertNotNull(response);
        assertEquals(ResponseStatus.FOUND, response.getStatus());
        assertEquals(rest, response.getData());
        assertNull(response.getErrors());
    }

    @Test
    @DisplayName("Find CIC statements - not found")
    void findCicStatementsNotFound() throws DataException {

        CicStatementsEntity entity = null;

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-"
                + ResourceName.CIC_STATEMENTS.getName()))
                .thenReturn(RESOURCE_ID);

        when(repository.findById(RESOURCE_ID)).thenReturn(Optional.ofNullable(entity));

        ResponseObject<CicStatements> response =
                service.find(COMPANY_ACCOUNTS_ID, request);

        verify(transformer, never()).transform(entity);

        assertNotNull(response);
        assertEquals(ResponseStatus.NOT_FOUND, response.getStatus());
        assertNull(response.getData());
        assertNull(response.getErrors());
    }

    @Test
    @DisplayName("Find CIC statements - Mongo exception")
    void findCicStatementsMongoException() {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-"
                + ResourceName.CIC_STATEMENTS.getName()))
                .thenReturn(RESOURCE_ID);

        when(repository.findById(RESOURCE_ID)).thenThrow(MongoException.class);

        assertThrows(DataException.class, () ->
                service.find(COMPANY_ACCOUNTS_ID, request));
    }

    @Test
    @DisplayName("Delete CIC statements - success path")
    void deleteCicStatementsSuccess() throws DataException {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-"
                + ResourceName.CIC_STATEMENTS.getName()))
                .thenReturn(RESOURCE_ID);

        when(repository.existsById(RESOURCE_ID)).thenReturn(true);

        ResponseObject<CicStatements> response =
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
    @DisplayName("Delete CIC statements - not found")
    void deleteCicStatementsNotFound() throws DataException {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-"
                + ResourceName.CIC_STATEMENTS.getName()))
                .thenReturn(RESOURCE_ID);

        when(repository.existsById(RESOURCE_ID)).thenReturn(false);

        ResponseObject<CicStatements> response =
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
    @DisplayName("Delete CIC statements - Mongo exception")
    void deleteCicStatementsMongoException() throws DataException {

        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-"
                + ResourceName.CIC_STATEMENTS.getName()))
                .thenReturn(RESOURCE_ID);

        when(repository.existsById(RESOURCE_ID)).thenThrow(MongoException.class);

        assertThrows(DataException.class, () ->
                service.delete(COMPANY_ACCOUNTS_ID, request));

        verify(repository, never()).deleteById(RESOURCE_ID);
        verify(cicReportService, never())
                .removeLink(COMPANY_ACCOUNTS_ID, CicReportLinkType.STATEMENTS, request);
    }
}
