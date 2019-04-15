package uk.gov.companieshouse.api.accounts.service.impl;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mongodb.MongoException;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;
import uk.gov.companieshouse.api.accounts.ResourceName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;
import uk.gov.companieshouse.api.accounts.model.entity.SmallFullDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.SmallFullEntity;
import uk.gov.companieshouse.api.accounts.model.rest.SmallFull;
import uk.gov.companieshouse.api.accounts.repository.SmallFullRepository;
import uk.gov.companieshouse.api.accounts.service.CompanyAccountService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.transformer.SmallFullTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;
import uk.gov.companieshouse.api.model.transaction.TransactionLinks;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class SmallFullServiceTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private SmallFull smallFull;

    @Mock
    private Transaction transaction;

    @Mock
    private TransactionLinks transactionLinks;

    @Mock
    private SmallFullEntity smallFullEntity;

    @Mock
    private SmallFullDataEntity smallFullDataEntity;

    @Mock
    private Map<String, String> links;

    @Mock
    private SmallFullTransformer smallFullTransformer;

    @Mock
    private SmallFullRepository smallFullRepository;

    @Mock
    private CompanyAccountService companyAccountService;

    @Mock
    private DuplicateKeyException duplicateKeyException;

    @Mock
    private MongoException mongoException;

    @Mock
    private KeyIdGenerator keyIdGenerator;

    @InjectMocks
    private SmallFullService smallFullService;

    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";
    private static final String GENERATED_ID = "generatedId";
    private static final String SELF_LINK = "self_link";

    @BeforeEach
    void setUp() {
        when(keyIdGenerator.generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.SMALL_FULL.getName()))
                .thenReturn(GENERATED_ID);
    }

    @Test
    @DisplayName("Tests the successful creation of a smallFull resource")
    public void createAccountSuccess() throws DataException {
        when(smallFullTransformer.transform(smallFull)).thenReturn(smallFullEntity);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(SELF_LINK);

        ResponseObject<SmallFull> result = smallFullService.create(smallFull, transaction, COMPANY_ACCOUNTS_ID, request);
        assertNotNull(result);
        assertEquals(smallFull, result.getData());
    }

    @Test
    @DisplayName("Tests the duplicate key when creating a smallFull resource")
    public void createSmallfullDuplicateKey() throws DataException {
        doReturn(smallFullEntity).when(smallFullTransformer).transform(ArgumentMatchers
            .any(SmallFull.class));
        when(smallFullRepository.insert(smallFullEntity)).thenThrow(duplicateKeyException);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(SELF_LINK);

        ResponseObject response = smallFullService.create(smallFull, transaction, COMPANY_ACCOUNTS_ID, request);
        assertNotNull(response);
        assertEquals(response.getStatus(), ResponseStatus.DUPLICATE_KEY_ERROR);
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Tests the mongo exception when creating a small full")
    void createSmallfullMongoExceptionFailure() {
        doReturn(smallFullEntity).when(smallFullTransformer).transform(ArgumentMatchers
            .any(SmallFull.class));
        when(smallFullRepository.insert(smallFullEntity)).thenThrow(mongoException);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(SELF_LINK);

        Executable executable = () -> {
            smallFullService.create(smallFull, transaction, COMPANY_ACCOUNTS_ID, request);
        };
        assertThrows(DataException.class, executable);
    }

    @Test
    @DisplayName("Tests the successful find of a small full resource")
    public void findSmallfull() throws DataException {
        when(smallFullRepository.findById(GENERATED_ID)).thenReturn(Optional.ofNullable(smallFullEntity));
        when(smallFullTransformer.transform(smallFullEntity)).thenReturn(smallFull);
        ResponseObject<SmallFull> result = smallFullService
            .find(COMPANY_ACCOUNTS_ID, request);
        assertNotNull(result);
        assertEquals(smallFull, result.getData());
    }

    @Test
    @DisplayName("Tests mongo exception thrown on find of a small full resource")
    public void findSmallfullMongoException() {
        when(smallFullRepository.findById(GENERATED_ID)).thenThrow(mongoException);
        Executable executable = () -> {
            smallFullService.find(COMPANY_ACCOUNTS_ID, request);
        };
        assertThrows(DataException.class, executable);
    }

    @Test
    @DisplayName("Tests the successful removal of a small full link")
    void removeLinkSuccess() {

        when(smallFullRepository.findById(GENERATED_ID)).thenReturn(Optional.ofNullable(smallFullEntity));

        when(smallFullEntity.getData()).thenReturn(smallFullDataEntity);
        when(smallFullDataEntity.getLinks()).thenReturn(links);

        SmallFullLinkType smallFullLinkType = SmallFullLinkType.TANGIBLE_ASSETS_NOTE;

        assertAll(() -> smallFullService.removeLink(COMPANY_ACCOUNTS_ID, smallFullLinkType, request));

        verify(links, times(1)).remove(smallFullLinkType.getLink());
    }

    @Test
    @DisplayName("Tests the  removal of a small full link where the repository throws a Mongo exception")
    void removeLinkMongoException() {

        when(smallFullRepository.findById(GENERATED_ID)).thenReturn(Optional.ofNullable(smallFullEntity));

        when(smallFullEntity.getData()).thenReturn(smallFullDataEntity);
        when(smallFullDataEntity.getLinks()).thenReturn(links);

        when(smallFullRepository.save(smallFullEntity)).thenThrow(MongoException.class);

        SmallFullLinkType smallFullLinkType = SmallFullLinkType.TANGIBLE_ASSETS_NOTE;

        assertThrows(DataException.class,
                () -> smallFullService.removeLink(COMPANY_ACCOUNTS_ID, smallFullLinkType, request));

        verify(links, times(1)).remove(smallFullLinkType.getLink());
    }

    @Test
    @DisplayName("Tests the  removal of a small full link where the entity is not found")
    void removeLinkSmallFullEntityNotFound() {

        SmallFullEntity smallFullEntity = null;
        when(smallFullRepository.findById(GENERATED_ID)).thenReturn(Optional.ofNullable(smallFullEntity));

        SmallFullLinkType smallFullLinkType = SmallFullLinkType.TANGIBLE_ASSETS_NOTE;

        assertThrows(DataException.class,
                () -> smallFullService.removeLink(COMPANY_ACCOUNTS_ID, smallFullLinkType, request));

        verify(smallFullRepository, never()).save(smallFullEntity);
    }
}
