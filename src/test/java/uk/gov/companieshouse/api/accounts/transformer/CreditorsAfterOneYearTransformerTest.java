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
import uk.gov.companieshouse.api.accounts.model.entity.notes.creditorsafteroneyearentity.CreditorsAfterOneYearDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.notes.creditorsafteroneyearentity.CreditorsAfterOneYearEntity;
import uk.gov.companieshouse.api.accounts.model.entity.notes.creditorsafteroneyearentity.CurrentPeriodEntity;
import uk.gov.companieshouse.api.accounts.model.entity.notes.creditorsafteroneyearentity.PreviousPeriodEntity;
import uk.gov.companieshouse.api.accounts.model.rest.notes.creditorsafteroneyear.CreditorsAfterOneYear;
import uk.gov.companieshouse.api.accounts.model.rest.notes.creditorsafteroneyear.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.notes.creditorsafteroneyear.PreviousPeriod;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class CreditorsAfterOneYearTransformerTest {

    private static final Long BANK_LOANS_AND_OVERDRAFTS = 2L;
    private static final Long OTHER_CREDITORS = 3L;
    private static final Long FINANCE_LEASE = 4L;
    private static final Long TOTAL = 21L;
    private static final String DETAILS = "details";
    private static final String ETAG = "etag";
    private static final String KIND = "kind";

    private CreditorsAfterOneYearTransformer creditorsAfterOneYearTransformer =
            new CreditorsAfterOneYearTransformer();

    @Test
    @DisplayName("Tests transformer with empty rest object returns null values ")
    public void testTransformerWithEmptyRestObject() {

        CreditorsAfterOneYearEntity creditorsAfterOneYearEntity = creditorsAfterOneYearTransformer
                .transform(new CreditorsAfterOneYear());

        assertNotNull(creditorsAfterOneYearEntity);
        assertNull(creditorsAfterOneYearEntity.getData().getEtag());
        assertEquals(new HashMap<>(), creditorsAfterOneYearEntity.getData().getLinks());
    }

    @Test
    @DisplayName("Tests transformer with empty previous period Rest Object")
    public void testRestToEntityTransformerWithEmptyPreviousPeriodRestObject() {

        CreditorsAfterOneYear creditorsAfterOneYear = new CreditorsAfterOneYear();

        creditorsAfterOneYear.setEtag(ETAG);
        creditorsAfterOneYear.setKind(KIND);
        creditorsAfterOneYear.setLinks(new HashMap<>());

        CurrentPeriod currentPeriod = createCurrentPeriodRestObject();

        creditorsAfterOneYear.setCurrentPeriod(currentPeriod);

        CreditorsAfterOneYearEntity creditorsAfterOneYearEntity = creditorsAfterOneYearTransformer
                .transform(creditorsAfterOneYear);

        assertNotNull(creditorsAfterOneYearEntity);
        assertNull(creditorsAfterOneYearEntity.getData().getPreviousPeriodEntity());
        assertFieldsMappedToEntity(creditorsAfterOneYearEntity);
        assertEquals(new HashMap<>(), creditorsAfterOneYearEntity.getData().getLinks());
    }

    @Test
    @DisplayName("Tests transformer with fully populated Rest object and validates values returned")
    public void testRestToEntityTransformerWithFullyPopulatedObject() {

        CreditorsAfterOneYear creditorsAfterOneYear = new CreditorsAfterOneYear();

        creditorsAfterOneYear.setEtag(ETAG);
        creditorsAfterOneYear.setKind(KIND);
        creditorsAfterOneYear.setLinks(new HashMap<>());

        creditorsAfterOneYear.setCurrentPeriod(createCurrentPeriodRestObject());
        creditorsAfterOneYear.setPreviousPeriod(createPreviousPeriodRestObject());

        CreditorsAfterOneYearEntity creditorsAfterOneYearEntity = creditorsAfterOneYearTransformer
                .transform(creditorsAfterOneYear);

        assertNotNull(creditorsAfterOneYearEntity);
        assertFieldsMappedToEntity(creditorsAfterOneYearEntity);
    }

    private CurrentPeriod createCurrentPeriodRestObject() {

        CurrentPeriod currentPeriod = new CurrentPeriod();
        currentPeriod.setBankLoansAndOverdrafts(BANK_LOANS_AND_OVERDRAFTS);
        currentPeriod.setOtherCreditors(OTHER_CREDITORS);
        currentPeriod.setFinanceLeasesAndHirePurchaseContracts(FINANCE_LEASE);
        currentPeriod.setTotal(TOTAL);
        currentPeriod.setDetails(DETAILS);

        return currentPeriod;
    }


    private PreviousPeriod createPreviousPeriodRestObject() {

        PreviousPeriod previousPeriod = new PreviousPeriod();
        previousPeriod.setBankLoansAndOverdrafts(BANK_LOANS_AND_OVERDRAFTS);
        previousPeriod.setOtherCreditors(OTHER_CREDITORS);
        previousPeriod.setFinanceLeasesAndHirePurchaseContracts(FINANCE_LEASE);
        previousPeriod.setTotal(TOTAL);

        return previousPeriod;
    }

    private void assertFieldsMappedToEntity(CreditorsAfterOneYearEntity creditorsAfterOneYearEntity) {

        assertEquals(BANK_LOANS_AND_OVERDRAFTS,
                creditorsAfterOneYearEntity.getData().getCurrentPeriodEntity().getBankLoansAndOverdrafts());
        assertEquals(OTHER_CREDITORS,
                creditorsAfterOneYearEntity.getData().getCurrentPeriodEntity().getOtherCreditors());
        assertEquals(FINANCE_LEASE,
                creditorsAfterOneYearEntity.getData().getCurrentPeriodEntity().getFinanceLeasesAndHirePurchaseContracts());
        assertEquals(TOTAL,
                creditorsAfterOneYearEntity.getData().getCurrentPeriodEntity().getTotal());
        assertEquals(DETAILS,
                creditorsAfterOneYearEntity.getData().getCurrentPeriodEntity().getDetails());

        assertEquals(new HashMap<>(), creditorsAfterOneYearEntity.getData().getLinks());
        assertEquals(ETAG, creditorsAfterOneYearEntity.getData().getEtag());
        assertEquals(KIND, creditorsAfterOneYearEntity.getData().getKind());

        if (creditorsAfterOneYearEntity.getData().getPreviousPeriodEntity() != null) {
            assertEquals(BANK_LOANS_AND_OVERDRAFTS,
                    creditorsAfterOneYearEntity.getData().getPreviousPeriodEntity().getBankLoansAndOverdrafts());
            assertEquals(OTHER_CREDITORS,
                    creditorsAfterOneYearEntity.getData().getPreviousPeriodEntity().getOtherCreditors());
            assertEquals(FINANCE_LEASE,
                    creditorsAfterOneYearEntity.getData().getPreviousPeriodEntity().getFinanceLeasesAndHirePurchaseContracts());
            assertEquals(TOTAL,
                    creditorsAfterOneYearEntity.getData().getPreviousPeriodEntity().getTotal());
        }
    }

    @Test
    @DisplayName("Tests transformer with empty entity object returns null values ")
    public void testTransformerWithEmptyEntityObject() {

        CreditorsAfterOneYear creditorsAfterOneYear = creditorsAfterOneYearTransformer
                .transform(new CreditorsAfterOneYearEntity());

        assertNotNull(creditorsAfterOneYear);
        assertNull(creditorsAfterOneYear.getEtag());
        assertEquals(new HashMap<>(), creditorsAfterOneYear.getLinks());
    }

    @Test
    @DisplayName("Tests transformer with empty previous period Entity Object")
    public void testEntityToRestTransformerWithEmptyPreviousPeriodEntityObject() {

        CreditorsAfterOneYearEntity creditorsAfterOneYearEntity = new CreditorsAfterOneYearEntity();
        CreditorsAfterOneYearDataEntity creditorsAfterOneYearDataEntity = new CreditorsAfterOneYearDataEntity();

        creditorsAfterOneYearDataEntity.setEtag(ETAG);
        creditorsAfterOneYearDataEntity.setKind(KIND);
        creditorsAfterOneYearDataEntity.setLinks(new HashMap<>());
        creditorsAfterOneYearDataEntity.setCurrentPeriodEntity(createCurrentPeriodEntityObject());

        creditorsAfterOneYearEntity.setData(creditorsAfterOneYearDataEntity);

        CreditorsAfterOneYear creditorsAfterOneYear = creditorsAfterOneYearTransformer
                .transform(creditorsAfterOneYearEntity);

        assertNotNull(creditorsAfterOneYear);
        assertFieldsMappedToRest(creditorsAfterOneYear);
        assertEquals(new HashMap<>(), creditorsAfterOneYear.getLinks());
        assertEquals(ETAG, creditorsAfterOneYear.getEtag());
        assertEquals(KIND, creditorsAfterOneYear.getKind());
    }

    @Test
    @DisplayName("Tests transformer with fully populated Entity object and validates values returned")
    public void testEntityToRestTransformerWithFullyPopulatedEntityObject() {

        CreditorsAfterOneYearEntity creditorsAfterOneYearEntity = new CreditorsAfterOneYearEntity();
        CreditorsAfterOneYearDataEntity creditorsAfterOneYearDataEntity = new CreditorsAfterOneYearDataEntity();

        creditorsAfterOneYearDataEntity.setEtag(ETAG);
        creditorsAfterOneYearDataEntity.setKind(KIND);
        creditorsAfterOneYearDataEntity.setLinks(new HashMap<>());
        creditorsAfterOneYearDataEntity.setCurrentPeriodEntity(createCurrentPeriodEntityObject());
        creditorsAfterOneYearDataEntity.setPreviousPeriodEntity(createPreviousPeriodEntityObject());

        creditorsAfterOneYearEntity.setData(creditorsAfterOneYearDataEntity);

        CreditorsAfterOneYear creditorsAfterOneYear = creditorsAfterOneYearTransformer
                .transform(creditorsAfterOneYearEntity);

        assertNotNull(creditorsAfterOneYear);
        assertFieldsMappedToRest(creditorsAfterOneYear);
        assertEquals(new HashMap<>(), creditorsAfterOneYear.getLinks());
        assertEquals(ETAG, creditorsAfterOneYear.getEtag());
        assertEquals(KIND, creditorsAfterOneYear.getKind());
    }

    private CurrentPeriodEntity createCurrentPeriodEntityObject() {

        CurrentPeriodEntity currentPeriodEntity = new CurrentPeriodEntity();
        currentPeriodEntity.setBankLoansAndOverdrafts(BANK_LOANS_AND_OVERDRAFTS);
        currentPeriodEntity.setOtherCreditors(OTHER_CREDITORS);
        currentPeriodEntity.setFinanceLeasesAndHirePurchaseContracts(FINANCE_LEASE);
        currentPeriodEntity.setTotal(TOTAL);
        currentPeriodEntity.setDetails(DETAILS);

        return currentPeriodEntity;
    }

    private PreviousPeriodEntity createPreviousPeriodEntityObject() {

        PreviousPeriodEntity previousPeriodEntity = new PreviousPeriodEntity();
        previousPeriodEntity.setBankLoansAndOverdrafts(BANK_LOANS_AND_OVERDRAFTS);
        previousPeriodEntity.setOtherCreditors(OTHER_CREDITORS);
        previousPeriodEntity.setFinanceLeasesAndHirePurchaseContracts(FINANCE_LEASE);;
        previousPeriodEntity.setTotal(TOTAL);

        return previousPeriodEntity;
    }

    private void assertFieldsMappedToRest(CreditorsAfterOneYear creditorsAfterOneYear) {

        assertEquals(BANK_LOANS_AND_OVERDRAFTS, creditorsAfterOneYear.getCurrentPeriod().getBankLoansAndOverdrafts());
        assertEquals(OTHER_CREDITORS, creditorsAfterOneYear.getCurrentPeriod().getOtherCreditors());
        assertEquals(FINANCE_LEASE, creditorsAfterOneYear.getCurrentPeriod().getFinanceLeasesAndHirePurchaseContracts());
        assertEquals(TOTAL, creditorsAfterOneYear.getCurrentPeriod().getTotal());
        assertEquals(DETAILS, creditorsAfterOneYear.getCurrentPeriod().getDetails());

        assertEquals(new HashMap<>(), creditorsAfterOneYear.getLinks());
        assertEquals(ETAG, creditorsAfterOneYear.getEtag());
        assertEquals(KIND, creditorsAfterOneYear.getKind());

        if (creditorsAfterOneYear.getPreviousPeriod() != null) {

            assertEquals(BANK_LOANS_AND_OVERDRAFTS, creditorsAfterOneYear.getPreviousPeriod().getBankLoansAndOverdrafts());
            assertEquals(OTHER_CREDITORS, creditorsAfterOneYear.getPreviousPeriod().getOtherCreditors());
            assertEquals(FINANCE_LEASE, creditorsAfterOneYear.getPreviousPeriod().getFinanceLeasesAndHirePurchaseContracts());
            assertEquals(TOTAL, creditorsAfterOneYear.getPreviousPeriod().getTotal());
        }

    }
}
