package uk.gov.companieshouse.api.accounts.service.impl.smallfull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;
import uk.gov.companieshouse.api.accounts.model.entity.notes.currentassetsinvestments.CurrentAssetsInvestmentsEntity;
import uk.gov.companieshouse.api.accounts.model.rest.notes.CurrentAssetsInvestments;
import uk.gov.companieshouse.api.accounts.service.impl.BaseService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.model.transaction.TransactionLinks;

import javax.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CurrentAssetsInvestmentsServiceTest {

    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";

    private static final String TRANSACTION_SELF_LINK = "transactionSelfLink";

    private static final String EXPECTED_SELF_LINK = TRANSACTION_SELF_LINK + "/company-accounts/" +
            COMPANY_ACCOUNTS_ID + "/small-full/notes/current-assets-investments";

    private static final String INJECTED_SERVICE = "baseService";

    @Mock
    private CurrentAssetsInvestments currentAssetsInvestments;

    @Mock
    private Transaction transaction;

    @Mock
    private TransactionLinks links;

    @Mock
    private HttpServletRequest request;

    @Mock
    private ResponseObject<CurrentAssetsInvestments> responseObject;

    @Mock
    private BaseService<CurrentAssetsInvestments, CurrentAssetsInvestmentsEntity, SmallFullLinkType> baseService;

    @InjectMocks
    private CurrentAssetsInvestmentsService currentAssetsInvestmentsService;

    @BeforeEach
    void setUp() {

        ReflectionTestUtils.setField(currentAssetsInvestmentsService, INJECTED_SERVICE, baseService);
    }

    @Test
    @DisplayName("Create currentAssetsInvestments resource")
    void createCurrentAssetsInvestmentsResource() throws DataException {

        when(transaction.getLinks()).thenReturn(links);
        when(links.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        when(baseService
                .create(currentAssetsInvestments, transaction, COMPANY_ACCOUNTS_ID, request, EXPECTED_SELF_LINK))
                .thenReturn(responseObject);

        assertEquals(responseObject,
                currentAssetsInvestmentsService
                        .create(currentAssetsInvestments, transaction, COMPANY_ACCOUNTS_ID, request));
    }

    @Test
    @DisplayName("Update currentAssetsInvestments resource")
    void updateCurrentAssetsInvestmentsResource() throws DataException {

        when(transaction.getLinks()).thenReturn(links);
        when(links.getSelf()).thenReturn(TRANSACTION_SELF_LINK);

        when(baseService
                .update(currentAssetsInvestments, transaction, COMPANY_ACCOUNTS_ID, request, EXPECTED_SELF_LINK))
                .thenReturn(responseObject);

        assertEquals(responseObject,
                currentAssetsInvestmentsService
                        .update(currentAssetsInvestments, transaction, COMPANY_ACCOUNTS_ID, request));
    }

    @Test
    @DisplayName("Get currentAssetsInvestments resource")
    void getCurrentAssetsInvestmentsResource() throws DataException {

        when(baseService.find(COMPANY_ACCOUNTS_ID)).thenReturn(responseObject);

        assertEquals(responseObject,
                currentAssetsInvestmentsService.find(COMPANY_ACCOUNTS_ID, request));
    }

    @Test
    @DisplayName("Delete currentAssetsInvestments resource")
    void deleteCurrentAssetsInvestmentsResource() throws DataException {

        when(baseService.delete(COMPANY_ACCOUNTS_ID, request)).thenReturn(responseObject);

        assertEquals(responseObject,
                currentAssetsInvestmentsService.delete(COMPANY_ACCOUNTS_ID, request));
    }
}
