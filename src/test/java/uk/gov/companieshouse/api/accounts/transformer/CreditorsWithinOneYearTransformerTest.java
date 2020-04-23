package uk.gov.companieshouse.api.accounts.transformer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.HashMap;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.accounts.enumeration.AccountingNoteType;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.creditorswithinoneyear.CreditorsWithinOneYearDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.creditorswithinoneyear.CreditorsWithinOneYearEntity;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.creditorswithinoneyear.CurrentPeriodEntity;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.creditorswithinoneyear.PreviousPeriodEntity;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.creditorswithinoneyear.CreditorsWithinOneYear;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.creditorswithinoneyear.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.creditorswithinoneyear.PreviousPeriod;


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
    @DisplayName("Tests transformer with empty rest object returns null values ")
    public void testTransformerWithEmptyRestObject() {

        CreditorsWithinOneYearEntity creditorsWithinOneYearEntity = creditorsWithinOneYearTransformer
            .transform(new CreditorsWithinOneYear());

        assertNotNull(creditorsWithinOneYearEntity);
        assertNull(creditorsWithinOneYearEntity.getData().getEtag());
        assertEquals(new HashMap<>(), creditorsWithinOneYearEntity.getData().getLinks());
    }

    @Test
    @DisplayName("Get accounting note type")
    void getAccountingNoteType() {

        assertEquals(AccountingNoteType.SMALL_FULL_CREDITORS_WITHIN,
                creditorsWithinOneYearTransformer.getAccountingNoteType());
    }

    @Test
    @DisplayName("Tests transformer with empty previous period Rest Object")
    public void testRestToEntityTransformerWithEmptyPreviousPeriodRestObject() {

        CreditorsWithinOneYear creditorsWithinOneYear = new CreditorsWithinOneYear();

        creditorsWithinOneYear.setEtag(ETAG);
        creditorsWithinOneYear.setKind(KIND);
        creditorsWithinOneYear.setLinks(new HashMap<>());

        CurrentPeriod currentPeriod = createCurrentPeriodRestObject();

        creditorsWithinOneYear.setCurrentPeriod(currentPeriod);

        CreditorsWithinOneYearEntity creditorsWithinOneYearEntity = creditorsWithinOneYearTransformer
            .transform(creditorsWithinOneYear);

        assertNotNull(creditorsWithinOneYearEntity);
        assertNull(creditorsWithinOneYearEntity.getData().getPreviousPeriodEntity());
        assertEqualsEntityObject(creditorsWithinOneYearEntity);
        assertEquals(new HashMap<>(), creditorsWithinOneYearEntity.getData().getLinks());
    }

    @Test
    @DisplayName("Tests transformer with fully populated Rest object and validates values returned")
    public void testRestToEntityTransformerWithFullyPopulatedObject() {

        CreditorsWithinOneYear creditorsWithinOneYear = new CreditorsWithinOneYear();

        creditorsWithinOneYear.setEtag(ETAG);
        creditorsWithinOneYear.setKind(KIND);
        creditorsWithinOneYear.setLinks(new HashMap<>());
        creditorsWithinOneYear.setCurrentPeriod(createCurrentPeriodRestObject());
        creditorsWithinOneYear.setPreviousPeriod(createPreiousPeriodRestObject());

        CreditorsWithinOneYearEntity creditorsWithinOneYearEntity = creditorsWithinOneYearTransformer
            .transform(creditorsWithinOneYear);

        assertNotNull(creditorsWithinOneYearEntity);
        assertEqualsEntityObject(creditorsWithinOneYearEntity);
    }

    @Test
    @DisplayName("Tests transformer with empty entity object returns null values ")
    public void testTransformerWithEmptyEntityObject() {

        CreditorsWithinOneYear creditorsWithinOneYear = creditorsWithinOneYearTransformer
            .transform(new CreditorsWithinOneYearEntity());

        assertNotNull(creditorsWithinOneYear);
        assertNull(creditorsWithinOneYear.getEtag());
        assertEquals(new HashMap<>(), creditorsWithinOneYear.getLinks());
    }

    @Test
    @DisplayName("Tests transformer with empty previous period Entity Object")
    public void testEntityToRestTransformerWithEmptyPreviousPeriodEntityObject() {

        CreditorsWithinOneYearEntity creditorsWithinOneYearEntity = new CreditorsWithinOneYearEntity();
        CreditorsWithinOneYearDataEntity creditorsWithinOneYearDataEntity = new CreditorsWithinOneYearDataEntity();

        creditorsWithinOneYearDataEntity.setEtag(ETAG);
        creditorsWithinOneYearDataEntity.setKind(KIND);
        creditorsWithinOneYearDataEntity.setLinks(new HashMap<>());
        creditorsWithinOneYearDataEntity.setCurrentPeriodEntity(createCurrentPeriodEntityObject());

        creditorsWithinOneYearEntity.setData(creditorsWithinOneYearDataEntity);

        CreditorsWithinOneYear creditorsWithinOneYear = creditorsWithinOneYearTransformer
            .transform(creditorsWithinOneYearEntity);

        assertNotNull(creditorsWithinOneYear);
        assertEqualsRestObject(creditorsWithinOneYear);
        assertEquals(new HashMap<>(), creditorsWithinOneYear.getLinks());
        assertEquals(ETAG, creditorsWithinOneYear.getEtag());
        assertEquals(KIND, creditorsWithinOneYear.getKind());
    }

    @Test
    @DisplayName("Tests transformer with fully populated Entity object and validates values returned")
    public void testEntityToRestTransformerWithFullyPopulatedEntityObject() {

        CreditorsWithinOneYearEntity creditorsWithinOneYearEntity = new CreditorsWithinOneYearEntity();
        CreditorsWithinOneYearDataEntity creditorsWithinOneYearDataEntity = new CreditorsWithinOneYearDataEntity();

        creditorsWithinOneYearDataEntity.setEtag(ETAG);
        creditorsWithinOneYearDataEntity.setKind(KIND);
        creditorsWithinOneYearDataEntity.setLinks(new HashMap<>());
        creditorsWithinOneYearDataEntity.setCurrentPeriodEntity(createCurrentPeriodEntityObject());
        creditorsWithinOneYearDataEntity.setPreviousPeriodEntity(createPreviousPeriodEntityObject());

        creditorsWithinOneYearEntity.setData(creditorsWithinOneYearDataEntity);

        CreditorsWithinOneYear creditorsWithinOneYear = creditorsWithinOneYearTransformer
            .transform(creditorsWithinOneYearEntity);

        assertNotNull(creditorsWithinOneYear);
        assertEqualsRestObject(creditorsWithinOneYear);
        assertEquals(new HashMap<>(), creditorsWithinOneYear.getLinks());
        assertEquals(ETAG, creditorsWithinOneYear.getEtag());
        assertEquals(KIND, creditorsWithinOneYear.getKind());
    }

    private PreviousPeriodEntity createPreviousPeriodEntityObject() {

        PreviousPeriodEntity previousPeriodEntity = new PreviousPeriodEntity();
        previousPeriodEntity.setAccrualsAndDeferredIncome(ACCRUALS_AND_DEFERRED_INCOME);
        previousPeriodEntity.setBankLoansAndOverdrafts(BANK_LOANS_AND_OVERDRAFTS);
        previousPeriodEntity.setOtherCreditors(OTHER_CREDITORS);
        previousPeriodEntity.setFinanceLeasesAndHirePurchaseContracts(FINANCE_LEASE);
        previousPeriodEntity.setTaxationAndSocialSecurity(TAXATION_AND_SOCIAL_SECURITY);
        previousPeriodEntity.setTradeCreditors(TRADE_CREDITORS);
        previousPeriodEntity.setTotal(TOTAL);

        return previousPeriodEntity;
    }

    private CurrentPeriodEntity createCurrentPeriodEntityObject() {

        CurrentPeriodEntity currentPeriodEntity = new CurrentPeriodEntity();
        currentPeriodEntity.setAccrualsAndDeferredIncome(ACCRUALS_AND_DEFERRED_INCOME);
        currentPeriodEntity.setBankLoansAndOverdrafts(BANK_LOANS_AND_OVERDRAFTS);
        currentPeriodEntity.setOtherCreditors(OTHER_CREDITORS);
        currentPeriodEntity.setFinanceLeasesAndHirePurchaseContracts(FINANCE_LEASE);
        currentPeriodEntity.setTaxationAndSocialSecurity(TAXATION_AND_SOCIAL_SECURITY);
        currentPeriodEntity.setTradeCreditors(TRADE_CREDITORS);
        currentPeriodEntity.setTotal(TOTAL);
        currentPeriodEntity.setDetails(DETAILS);

        return currentPeriodEntity;
    }

    private PreviousPeriod createPreiousPeriodRestObject() {

        PreviousPeriod previousPeriod = new PreviousPeriod();
        previousPeriod.setAccrualsAndDeferredIncome(ACCRUALS_AND_DEFERRED_INCOME);
        previousPeriod.setBankLoansAndOverdrafts(BANK_LOANS_AND_OVERDRAFTS);
        previousPeriod.setOtherCreditors(OTHER_CREDITORS);
        previousPeriod.setFinanceLeasesAndHirePurchaseContracts(FINANCE_LEASE);
        previousPeriod.setTaxationAndSocialSecurity(TAXATION_AND_SOCIAL_SECURITY);
        previousPeriod.setTradeCreditors(TRADE_CREDITORS);
        previousPeriod.setTotal(TOTAL);

        return previousPeriod;
    }

    private CurrentPeriod createCurrentPeriodRestObject() {

        CurrentPeriod currentPeriod = new CurrentPeriod();
        currentPeriod.setAccrualsAndDeferredIncome(ACCRUALS_AND_DEFERRED_INCOME);
        currentPeriod.setBankLoansAndOverdrafts(BANK_LOANS_AND_OVERDRAFTS);
        currentPeriod.setOtherCreditors(OTHER_CREDITORS);
        currentPeriod.setFinanceLeasesAndHirePurchaseContracts(FINANCE_LEASE);
        currentPeriod.setTaxationAndSocialSecurity(TAXATION_AND_SOCIAL_SECURITY);
        currentPeriod.setTradeCreditors(TRADE_CREDITORS);
        currentPeriod.setTotal(TOTAL);
        currentPeriod.setDetails(DETAILS);

        return currentPeriod;
    }

    private void assertEqualsEntityObject(CreditorsWithinOneYearEntity creditorsWithinOneYearEntity) {

        assertEquals(ACCRUALS_AND_DEFERRED_INCOME, creditorsWithinOneYearEntity.getData().getCurrentPeriodEntity().getAccrualsAndDeferredIncome());
        assertEquals(BANK_LOANS_AND_OVERDRAFTS, creditorsWithinOneYearEntity.getData().getCurrentPeriodEntity().getBankLoansAndOverdrafts());
        assertEquals(OTHER_CREDITORS, creditorsWithinOneYearEntity.getData().getCurrentPeriodEntity().getOtherCreditors());
        assertEquals(FINANCE_LEASE, creditorsWithinOneYearEntity.getData().getCurrentPeriodEntity().getFinanceLeasesAndHirePurchaseContracts());
        assertEquals(TAXATION_AND_SOCIAL_SECURITY, creditorsWithinOneYearEntity.getData().getCurrentPeriodEntity().getTaxationAndSocialSecurity());
        assertEquals(TRADE_CREDITORS, creditorsWithinOneYearEntity.getData().getCurrentPeriodEntity().getTradeCreditors());
        assertEquals(TOTAL, creditorsWithinOneYearEntity.getData().getCurrentPeriodEntity().getTotal());
        assertEquals(DETAILS, creditorsWithinOneYearEntity.getData().getCurrentPeriodEntity().getDetails());

        assertEquals(new HashMap<>(), creditorsWithinOneYearEntity.getData().getLinks());
        assertEquals(ETAG, creditorsWithinOneYearEntity.getData().getEtag());
        assertEquals(KIND, creditorsWithinOneYearEntity.getData().getKind());

        if (creditorsWithinOneYearEntity.getData().getPreviousPeriodEntity() != null) {
            assertEquals(ACCRUALS_AND_DEFERRED_INCOME, creditorsWithinOneYearEntity.getData().getPreviousPeriodEntity().getAccrualsAndDeferredIncome());
            assertEquals(BANK_LOANS_AND_OVERDRAFTS, creditorsWithinOneYearEntity.getData().getPreviousPeriodEntity().getBankLoansAndOverdrafts());
            assertEquals(OTHER_CREDITORS, creditorsWithinOneYearEntity.getData().getPreviousPeriodEntity().getOtherCreditors());
            assertEquals(FINANCE_LEASE, creditorsWithinOneYearEntity.getData().getPreviousPeriodEntity().getFinanceLeasesAndHirePurchaseContracts());
            assertEquals(TAXATION_AND_SOCIAL_SECURITY, creditorsWithinOneYearEntity.getData().getPreviousPeriodEntity().getTaxationAndSocialSecurity());
            assertEquals(TRADE_CREDITORS, creditorsWithinOneYearEntity.getData().getPreviousPeriodEntity().getTradeCreditors());
            assertEquals(TOTAL, creditorsWithinOneYearEntity.getData().getPreviousPeriodEntity().getTotal());
        }
    }

    private void assertEqualsRestObject(CreditorsWithinOneYear creditorsWithinOneYear) {

        assertEquals(ACCRUALS_AND_DEFERRED_INCOME, creditorsWithinOneYear.getCurrentPeriod().getAccrualsAndDeferredIncome());
        assertEquals(BANK_LOANS_AND_OVERDRAFTS, creditorsWithinOneYear.getCurrentPeriod().getBankLoansAndOverdrafts());
        assertEquals(OTHER_CREDITORS, creditorsWithinOneYear.getCurrentPeriod().getOtherCreditors());
        assertEquals(FINANCE_LEASE, creditorsWithinOneYear.getCurrentPeriod().getFinanceLeasesAndHirePurchaseContracts());
        assertEquals(TAXATION_AND_SOCIAL_SECURITY, creditorsWithinOneYear.getCurrentPeriod().getTaxationAndSocialSecurity());
        assertEquals(TRADE_CREDITORS, creditorsWithinOneYear.getCurrentPeriod().getTradeCreditors());
        assertEquals(TOTAL, creditorsWithinOneYear.getCurrentPeriod().getTotal());
        assertEquals(DETAILS, creditorsWithinOneYear.getCurrentPeriod().getDetails());

        assertEquals(new HashMap<>(), creditorsWithinOneYear.getLinks());
        assertEquals(ETAG, creditorsWithinOneYear.getEtag());
        assertEquals(KIND, creditorsWithinOneYear.getKind());

        if (creditorsWithinOneYear.getPreviousPeriod() != null) {
            assertEquals(ACCRUALS_AND_DEFERRED_INCOME, creditorsWithinOneYear.getPreviousPeriod().getAccrualsAndDeferredIncome());
            assertEquals(BANK_LOANS_AND_OVERDRAFTS, creditorsWithinOneYear.getPreviousPeriod().getBankLoansAndOverdrafts());
            assertEquals(OTHER_CREDITORS, creditorsWithinOneYear.getPreviousPeriod().getOtherCreditors());
            assertEquals(FINANCE_LEASE, creditorsWithinOneYear.getPreviousPeriod().getFinanceLeasesAndHirePurchaseContracts());
            assertEquals(TAXATION_AND_SOCIAL_SECURITY, creditorsWithinOneYear.getPreviousPeriod().getTaxationAndSocialSecurity());
            assertEquals(TRADE_CREDITORS, creditorsWithinOneYear.getPreviousPeriod().getTradeCreditors());
            assertEquals(TOTAL, creditorsWithinOneYear.getPreviousPeriod().getTotal());
        }

    }
}
