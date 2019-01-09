package uk.gov.companieshouse.api.accounts.transformer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.accounts.model.entity.notes.CreditorsWithinOneYearDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.notes.CreditorsWithinOneYearEntity;
import uk.gov.companieshouse.api.accounts.model.rest.notes.CreditorsWithinOneYear;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;


@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class CreditorsWithinOneYearTransformerTest {

    private static final Long ACCRUALS_AND_DEFERRED_INCOME = 1L;
    private static final Long BANK_LOANS_AND_OVERDRAFTS = 2L;
    private static final Long OTHER_CREDITORS = 3L;
    private static final Long FINANCE_LEASE = 4L;
    private static final Long TAXATION_AND_SOCIAL_SECURITY = 5L;
    private static final Long TRADE_CREDITORS = 6L;
    private static final Long TOTAL = 21L;
    private static final String DETAILS = "details";
    private static final String ETAG = "etag";
    private static final String KIND = "kind";


    private CreditorsWithinOneYearTransformer creditorsWithinOneYearTransformer = new CreditorsWithinOneYearTransformer();

    @Test
    @DisplayName("Tests creditors within one year transformer with empty object returns null values")
    public void testTransformerWithEmptyObject() {

        CreditorsWithinOneYearEntity creditorsWithinOneYearEntity = creditorsWithinOneYearTransformer
            .transform(new CreditorsWithinOneYear());

        assertNotNull(creditorsWithinOneYearEntity);
        assertNull(creditorsWithinOneYearEntity.getData().getEtag());
        assertEquals(new HashMap<>(), creditorsWithinOneYearEntity.getData().getLinks());
    }

    @Test
    @DisplayName("Tests crediors within one year transformer with populated Rest object and validates values returned")
    public void testRestToEntityTransformerWithPopulatedObject() {

        CreditorsWithinOneYear creditorsWithinOneYear = new CreditorsWithinOneYear();
        creditorsWithinOneYear.setAccrualsAndDeferredIncome(ACCRUALS_AND_DEFERRED_INCOME);
        creditorsWithinOneYear.setBankLoansAndOverdrafts(BANK_LOANS_AND_OVERDRAFTS);
        creditorsWithinOneYear.setOtherCreditors(OTHER_CREDITORS);
        creditorsWithinOneYear.setFinanceLeasesAndHirePurchaseContracts(FINANCE_LEASE);
        creditorsWithinOneYear.setTaxationAndSocialSecurity(TAXATION_AND_SOCIAL_SECURITY);
        creditorsWithinOneYear.setTradeCreditors(TRADE_CREDITORS);
        creditorsWithinOneYear.setTotal(TOTAL);
        creditorsWithinOneYear.setDetails(DETAILS);

        creditorsWithinOneYear.setEtag(ETAG);
        creditorsWithinOneYear.setKind(KIND);
        creditorsWithinOneYear.setLinks(new HashMap<>());

        CreditorsWithinOneYearEntity creditorsWithinOneYearEntity = creditorsWithinOneYearTransformer
            .transform(creditorsWithinOneYear);
        CreditorsWithinOneYearDataEntity creditorsWithinOneYearDataEntity = creditorsWithinOneYearEntity.getData();

        assertNotNull(creditorsWithinOneYearDataEntity);
        assertEquals(ACCRUALS_AND_DEFERRED_INCOME, creditorsWithinOneYearDataEntity.getAccrualsAndDeferredIncome());
        assertEquals(BANK_LOANS_AND_OVERDRAFTS, creditorsWithinOneYearDataEntity.getBankLoansAndOverdrafts());
        assertEquals(OTHER_CREDITORS, creditorsWithinOneYearDataEntity.getOtherCreditors());
        assertEquals(FINANCE_LEASE, creditorsWithinOneYearDataEntity.getFinanceLeasesAndHirePurchaseContracts());
        assertEquals(TAXATION_AND_SOCIAL_SECURITY, creditorsWithinOneYearDataEntity.getTaxationAndSocialSecurity());
        assertEquals(TRADE_CREDITORS, creditorsWithinOneYearDataEntity.getTradeCreditors());
        assertEquals(TOTAL, creditorsWithinOneYearDataEntity.getTotal());
        assertEquals(new HashMap<>(), creditorsWithinOneYearDataEntity.getLinks());
        assertEquals(DETAILS, creditorsWithinOneYearDataEntity.getDetails());
        assertEquals(ETAG, creditorsWithinOneYearDataEntity.getEtag());
        assertEquals(KIND, creditorsWithinOneYearDataEntity.getKind());
    }

    @Test
    @DisplayName("Tests crediors within one year transformer with populated Entity object and validates values returned")
    public void testEntityToRestTransformerWithPopulatedObject() {

        CreditorsWithinOneYearEntity creditorsWithinOneYearEntity = new CreditorsWithinOneYearEntity();
        CreditorsWithinOneYearDataEntity creditorsWithinOneYearDataEntity = new CreditorsWithinOneYearDataEntity();
        creditorsWithinOneYearDataEntity.setAccrualsAndDeferredIncome(ACCRUALS_AND_DEFERRED_INCOME);
        creditorsWithinOneYearDataEntity.setBankLoansAndOverdrafts(BANK_LOANS_AND_OVERDRAFTS);
        creditorsWithinOneYearDataEntity.setOtherCreditors(OTHER_CREDITORS);
        creditorsWithinOneYearDataEntity.setFinanceLeasesAndHirePurchaseContracts(FINANCE_LEASE);
        creditorsWithinOneYearDataEntity.setTaxationAndSocialSecurity(TAXATION_AND_SOCIAL_SECURITY);
        creditorsWithinOneYearDataEntity.setTradeCreditors(TRADE_CREDITORS);
        creditorsWithinOneYearDataEntity.setTotal(TOTAL);
        creditorsWithinOneYearDataEntity.setDetails(DETAILS);

        creditorsWithinOneYearDataEntity.setEtag(ETAG);
        creditorsWithinOneYearDataEntity.setKind(KIND);
        creditorsWithinOneYearDataEntity.setLinks(new HashMap<>());

        creditorsWithinOneYearEntity.setData(creditorsWithinOneYearDataEntity);

        CreditorsWithinOneYear creditorsWithinOneYear = creditorsWithinOneYearTransformer
            .transform(creditorsWithinOneYearEntity);

        assertNotNull(creditorsWithinOneYear);
        assertEquals(ACCRUALS_AND_DEFERRED_INCOME, creditorsWithinOneYear.getAccrualsAndDeferredIncome());
        assertEquals(BANK_LOANS_AND_OVERDRAFTS, creditorsWithinOneYear.getBankLoansAndOverdrafts());
        assertEquals(OTHER_CREDITORS, creditorsWithinOneYear.getOtherCreditors());
        assertEquals(FINANCE_LEASE, creditorsWithinOneYear.getFinanceLeasesAndHirePurchaseContracts());
        assertEquals(TAXATION_AND_SOCIAL_SECURITY, creditorsWithinOneYear.getTaxationAndSocialSecurity());
        assertEquals(TRADE_CREDITORS, creditorsWithinOneYear.getTradeCreditors());
        assertEquals(TOTAL, creditorsWithinOneYear.getTotal());
        assertEquals(new HashMap<>(), creditorsWithinOneYear.getLinks());
        assertEquals(DETAILS, creditorsWithinOneYear.getDetails());
        assertEquals(ETAG, creditorsWithinOneYear.getEtag());
        assertEquals(KIND, creditorsWithinOneYear.getKind());
    }
}
