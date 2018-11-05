package uk.gov.companieshouse.api.accounts.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.mongodb.MongoException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.PropertySource;
import org.springframework.dao.DuplicateKeyException;
import uk.gov.companieshouse.api.accounts.Kind;
import uk.gov.companieshouse.api.accounts.ResourceName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.BasicLinkType;
import uk.gov.companieshouse.api.accounts.model.entity.StatementDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.StatementEntity;
import uk.gov.companieshouse.api.accounts.model.rest.CompanyAccount;
import uk.gov.companieshouse.api.accounts.model.rest.Statement;
import uk.gov.companieshouse.api.accounts.repository.StatementRepository;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.transformer.StatementTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
@PropertySource("classpath:LegalStatements.properties")
public class StatementServiceTest {


    private static final String ETAG = "etag";
    private static final String LEGAL_STATEMENT_ID = "abcdef";
    private static final String COMPANY_ACCOUNTS_ID = "123123";
    private static final String TRANSACTION_SELF_LINK = "/transactions/123456-123456-123456";
    private static final String STATEMENT_SELF_LINK = TRANSACTION_SELF_LINK + "/"
        + ResourceName.COMPANY_ACCOUNT.getName() + "/"
        + COMPANY_ACCOUNTS_ID + "/"
        + ResourceName.SMALL_FULL.getName() + "/"
        + ResourceName.STATEMENTS.getName();

    private static final String LEGAL_STATEMENT_SECTION_477_KEY = "section_477";
    private static final String LEGAL_STATEMENT_SECTION_477 = "Testing place holder: {period_end_on}";

    private StatementEntity statementEntity;
    private Map<String, String> legalStatements;

    @Mock
    private HttpServletRequest requestMock;
    @Mock
    private CompanyAccount companyAccountMock;
    @Mock
    private Transaction transactionMock;
    @Mock
    private Statement statementMock;
    @Mock
    private StatementTransformer statementTransformerMock;
    @Mock
    private StatementRepository statementRepositoryMock;
    @Mock
    private KeyIdGenerator KeyIdGeneratorMock;
    @Mock
    private SmallFullService smallFullServiceMock;
    @Mock
    private StatementsServiceProperties statementsServicePropertiesMock;
    @Mock
    private StatementEntity statementEntityMock;

    @InjectMocks
    private StatementService statementService;

    @BeforeAll
    void setUpBeforeAll() {
        statementEntity = createStatementEntity();
        legalStatements = createLegalStatements();
    }

    @Test
    @DisplayName("Tests the successful creation of a Statement resource")
    void shouldCreateStatement() throws DataException {
        when(requestMock.getAttribute(anyString())).thenReturn(companyAccountMock);
        when(companyAccountMock.getPeriodEndOn()).thenReturn(LocalDate.of(2018, Month.NOVEMBER, 1));
        when(statementsServicePropertiesMock.getStatements()).thenReturn(legalStatements);
        when(statementTransformerMock.transform(statementMock)).thenReturn(statementEntity);

        ResponseObject<Statement> result =
            statementService.create(statementMock, transactionMock, "", requestMock);

        assertNotNull(result);
        assertEquals(ResponseStatus.CREATED, result.getStatus());
        assertEquals(statementMock, result.getData());
    }

    @Test
    @DisplayName("Tests the duplicate key when creating a Statement resource")
    void shouldNotCreateStatementHttpDuplicateKeyError() throws DataException {
        when(requestMock.getAttribute(anyString())).thenReturn(companyAccountMock);
        when(statementTransformerMock.transform(statementMock)).thenReturn(statementEntity);
        when(statementRepositoryMock.insert(ArgumentMatchers.any(StatementEntity.class)))
            .thenThrow(DuplicateKeyException.class);

        ResponseObject<Statement> result =
            statementService.create(statementMock, transactionMock, "", requestMock);

        assertNotNull(result);
        assertEquals(ResponseStatus.DUPLICATE_KEY_ERROR, result.getStatus());
        assertNull(result.getData());
    }

    @Test
    @DisplayName("Tests the mongo exception when creating a Statement resource")
    void shouldThrowMongoExceptionWhenCreating() throws DataException {
        when(requestMock.getAttribute(anyString())).thenReturn(companyAccountMock);
        when(statementTransformerMock.transform(statementMock)).thenReturn(statementEntity);
        when(statementRepositoryMock.insert(ArgumentMatchers.any(StatementEntity.class)))
            .thenThrow(MongoException.class);

        assertThrows(DataException.class,
            () -> statementService.create(statementMock, transactionMock, "", requestMock));
    }

    @Test
    @DisplayName("Tests the successful update of a Statement resource")
    void shouldUpdateStatement() throws DataException {
        when(requestMock.getAttribute(anyString())).thenReturn(companyAccountMock);
        when(statementTransformerMock.transform(statementMock)).thenReturn(statementEntity);

        ResponseObject<Statement> result =
            statementService.update(statementMock, transactionMock, "", requestMock);

        assertNotNull(result);
        assertEquals(ResponseStatus.UPDATED, result.getStatus());
    }

    @Test
    @DisplayName("Tests the mongo exception when updating a Statement resource")
    void shouldThrowMongoExceptionWhenUpdating() throws DataException {
        when(requestMock.getAttribute(anyString())).thenReturn(companyAccountMock);
        when(statementTransformerMock.transform(statementMock)).thenReturn(statementEntity);
        when(statementRepositoryMock.save(ArgumentMatchers.any(StatementEntity.class)))
            .thenThrow(MongoException.class);

        assertThrows(DataException.class,
            () -> statementService.update(statementMock, transactionMock, "", requestMock));
    }

    @Test
    @DisplayName("Tests the successful find of an an Statement resource")
    void shouldFindStatementResource() throws DataException {
        when(statementRepositoryMock.findById(anyString()))
            .thenReturn(Optional.ofNullable(statementEntity));

        Statement statement = createStatement();
        when(statementTransformerMock.transform(any(StatementEntity.class))).thenReturn(statement);

        ResponseObject<Statement> result = statementService.findById(anyString(), requestMock);

        assertNotNull(result);
        assertEquals(statement, result.getData());
        assertEquals(ResponseStatus.FOUND, result.getStatus());
    }

    @Test
    @DisplayName("Tests the unsuccessful find of an an Statement resource")
    void shouldNotFindStatementResource() throws DataException {
        when(statementRepositoryMock.findById(anyString()))
            .thenReturn(Optional.ofNullable(null));

        ResponseObject<Statement> result = statementService.findById(anyString(), requestMock);

        assertNotNull(result);
        assertNull(result.getData());
        assertEquals(ResponseStatus.NOT_FOUND, result.getStatus());
    }

    @Test
    @DisplayName("Tests the mongo exception thrown on find a Statement resource")
    void shouldThrowMongoExceptionWhenFindingById() throws DataException {
        when(statementRepositoryMock.findById(anyString()))
            .thenThrow(MongoException.class);

        assertThrows(DataException.class,
            () -> statementService.findById(anyString(), requestMock));
    }

    private StatementEntity createStatementEntity() {
        StatementEntity statementEntity = new StatementEntity();
        statementEntity.setId(LEGAL_STATEMENT_ID);
        statementEntity.setData(createStatementDataEntity());

        return statementEntity;
    }
    private Map<String, String> createLegalStatements() {
        Map<String, String> statements = new HashMap<>();
        statements.put(LEGAL_STATEMENT_SECTION_477_KEY, LEGAL_STATEMENT_SECTION_477);

        return statements;
    }

    private StatementDataEntity createStatementDataEntity() {
        StatementDataEntity statementDataEntity = new StatementDataEntity();
        statementDataEntity.setEtag(ETAG);
        statementDataEntity.setKind(Kind.SMALL_FULL_STATEMENT.getValue());
        statementDataEntity.setLinks(createSelfLink());
        statementDataEntity.setHasAgreedToLegalStatements(true); //TO SET real values
        statementDataEntity.setLegalStatements(new HashMap<>()); //to set real values

        return statementDataEntity;
    }

    private Map<String, String> createSelfLink() {
        Map<String, String> selfLink = new HashMap<>();
        selfLink
            .put(BasicLinkType.SELF.getLink(), STATEMENT_SELF_LINK);
        return selfLink;
    }

    private Statement createStatement() {
        Statement statement = new Statement();
        statement.setEtag(ETAG);
        statement.setKind(Kind.SMALL_FULL_STATEMENT.getValue());
        statement.setLinks(createSelfLink());
        statement.setHasAgreedToLegalStatements(true); //TO SET real values
        statement.setLegalStatements(new HashMap<>()); //to set real values

        return statement;
    }

}
