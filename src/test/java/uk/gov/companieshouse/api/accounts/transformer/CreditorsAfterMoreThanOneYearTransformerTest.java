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
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.creditorsaftermorethanoneyear.CreditorsAfterMoreThanOneYearDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.creditorsaftermorethanoneyear.CreditorsAfterMoreThanOneYearEntity;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.creditorsaftermorethanoneyear.CurrentPeriodEntity;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.creditorsaftermorethanoneyear.PreviousPeriodEntity;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.creditorsaftermorethanoneyear.CreditorsAfterMoreThanOneYear;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.creditorsaftermorethanoneyear.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.creditorsaftermorethanoneyear.PreviousPeriod;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
class CreditorsAfterMoreThanOneYearTransformerTest {

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
    void testTransformerWithEmptyRestObject() {

        CreditorsAfterMoreThanOneYearEntity creditorsAfterMoreThanOneYearEntity = creditorsAfterOneYearTransformer
                .transform(new CreditorsAfterMoreThanOneYear());

        assertNotNull(creditorsAfterMoreThanOneYearEntity);
        assertNull(creditorsAfterMoreThanOneYearEntity.getData().getEtag());
        assertEquals(new HashMap<>(), creditorsAfterMoreThanOneYearEntity.getData().getLinks());
    }

    @Test
    @DisplayName("Tests transformer with empty previous period Rest Object")
    void testRestToEntityTransformerWithEmptyPreviousPeriodRestObject() {

        CreditorsAfterMoreThanOneYear creditorsAfterMoreThanOneYear = new CreditorsAfterMoreThanOneYear();

        creditorsAfterMoreThanOneYear.setEtag(ETAG);
        creditorsAfterMoreThanOneYear.setKind(KIND);
        creditorsAfterMoreThanOneYear.setLinks(new HashMap<>());

        CurrentPeriod currentPeriod = createCurrentPeriodRestObject();

        creditorsAfterMoreThanOneYear.setCurrentPeriod(currentPeriod);

        CreditorsAfterMoreThanOneYearEntity creditorsAfterMoreThanOneYearEntity = creditorsAfterOneYearTransformer
                .transform(creditorsAfterMoreThanOneYear);

        assertNotNull(creditorsAfterMoreThanOneYearEntity);
        assertNull(creditorsAfterMoreThanOneYearEntity.getData().getPreviousPeriodEntity());
        assertFieldsMappedToEntity(creditorsAfterMoreThanOneYearEntity);
        assertEquals(new HashMap<>(), creditorsAfterMoreThanOneYearEntity.getData().getLinks());
    }

    @Test
    @DisplayName("Tests transformer with fully populated Rest object and validates values returned")
    void testRestToEntityTransformerWithFullyPopulatedObject() {

        CreditorsAfterMoreThanOneYear creditorsAfterMoreThanOneYear = new CreditorsAfterMoreThanOneYear();

        creditorsAfterMoreThanOneYear.setEtag(ETAG);
        creditorsAfterMoreThanOneYear.setKind(KIND);
        creditorsAfterMoreThanOneYear.setLinks(new HashMap<>());

        creditorsAfterMoreThanOneYear.setCurrentPeriod(createCurrentPeriodRestObject());
        creditorsAfterMoreThanOneYear.setPreviousPeriod(createPreviousPeriodRestObject());

        CreditorsAfterMoreThanOneYearEntity creditorsAfterMoreThanOneYearEntity = creditorsAfterOneYearTransformer
                .transform(creditorsAfterMoreThanOneYear);

        assertNotNull(creditorsAfterMoreThanOneYearEntity);
        assertFieldsMappedToEntity(creditorsAfterMoreThanOneYearEntity);
    }

    @Test
    @DisplayName("Tests transformer with empty entity object returns null values ")
    void testTransformerWithEmptyEntityObject() {

        CreditorsAfterMoreThanOneYear creditorsAfterMoreThanOneYear = creditorsAfterOneYearTransformer
                .transform(new CreditorsAfterMoreThanOneYearEntity());

        assertNotNull(creditorsAfterMoreThanOneYear);
        assertNull(creditorsAfterMoreThanOneYear.getEtag());
        assertEquals(new HashMap<>(), creditorsAfterMoreThanOneYear.getLinks());
    }

    @Test
    @DisplayName("Tests transformer with empty previous period Entity Object")
    void testEntityToRestTransformerWithEmptyPreviousPeriodEntityObject() {

        CreditorsAfterMoreThanOneYearEntity creditorsAfterMoreThanOneYearEntity = new CreditorsAfterMoreThanOneYearEntity();
        CreditorsAfterMoreThanOneYearDataEntity creditorsAfterMoreThanOneYearDataEntity = new CreditorsAfterMoreThanOneYearDataEntity();

        creditorsAfterMoreThanOneYearDataEntity.setEtag(ETAG);
        creditorsAfterMoreThanOneYearDataEntity.setKind(KIND);
        creditorsAfterMoreThanOneYearDataEntity.setLinks(new HashMap<>());
        creditorsAfterMoreThanOneYearDataEntity.setCurrentPeriodEntity(createCurrentPeriodEntityObject());

        creditorsAfterMoreThanOneYearEntity.setData(creditorsAfterMoreThanOneYearDataEntity);

        CreditorsAfterMoreThanOneYear creditorsAfterMoreThanOneYear = creditorsAfterOneYearTransformer
                .transform(creditorsAfterMoreThanOneYearEntity);

        assertNotNull(creditorsAfterMoreThanOneYear);
        assertFieldsMappedToRest(creditorsAfterMoreThanOneYear);
        assertEquals(new HashMap<>(), creditorsAfterMoreThanOneYear.getLinks());
        assertEquals(ETAG, creditorsAfterMoreThanOneYear.getEtag());
        assertEquals(KIND, creditorsAfterMoreThanOneYear.getKind());
    }

    @Test
    @DisplayName("Tests transformer with fully populated Entity object and validates values returned")
    void testEntityToRestTransformerWithFullyPopulatedEntityObject() {

        CreditorsAfterMoreThanOneYearEntity creditorsAfterMoreThanOneYearEntity = new CreditorsAfterMoreThanOneYearEntity();
        CreditorsAfterMoreThanOneYearDataEntity creditorsAfterMoreThanOneYearDataEntity = new CreditorsAfterMoreThanOneYearDataEntity();

        creditorsAfterMoreThanOneYearDataEntity.setEtag(ETAG);
        creditorsAfterMoreThanOneYearDataEntity.setKind(KIND);
        creditorsAfterMoreThanOneYearDataEntity.setLinks(new HashMap<>());
        creditorsAfterMoreThanOneYearDataEntity.setCurrentPeriodEntity(createCurrentPeriodEntityObject());
        creditorsAfterMoreThanOneYearDataEntity.setPreviousPeriodEntity(createPreviousPeriodEntityObject());

        creditorsAfterMoreThanOneYearEntity.setData(creditorsAfterMoreThanOneYearDataEntity);

        CreditorsAfterMoreThanOneYear creditorsAfterMoreThanOneYear = creditorsAfterOneYearTransformer
                .transform(creditorsAfterMoreThanOneYearEntity);

        assertNotNull(creditorsAfterMoreThanOneYear);
        assertFieldsMappedToRest(creditorsAfterMoreThanOneYear);
        assertEquals(new HashMap<>(), creditorsAfterMoreThanOneYear.getLinks());
        assertEquals(ETAG, creditorsAfterMoreThanOneYear.getEtag());
        assertEquals(KIND, creditorsAfterMoreThanOneYear.getKind());
    }

    @Test
    @DisplayName("Get accounting note type")
    void getAccountingNoteType() {

        assertEquals(AccountingNoteType.SMALL_FULL_CREDITORS_AFTER,
                creditorsAfterOneYearTransformer.getAccountingNoteType());
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

    private void assertFieldsMappedToEntity(CreditorsAfterMoreThanOneYearEntity creditorsAfterMoreThanOneYearEntity) {

        assertEquals(BANK_LOANS_AND_OVERDRAFTS,
                creditorsAfterMoreThanOneYearEntity.getData().getCurrentPeriodEntity().getBankLoansAndOverdrafts());
        assertEquals(OTHER_CREDITORS,
                creditorsAfterMoreThanOneYearEntity.getData().getCurrentPeriodEntity().getOtherCreditors());
        assertEquals(FINANCE_LEASE,
                creditorsAfterMoreThanOneYearEntity.getData().getCurrentPeriodEntity().getFinanceLeasesAndHirePurchaseContracts());
        assertEquals(TOTAL,
                creditorsAfterMoreThanOneYearEntity.getData().getCurrentPeriodEntity().getTotal());
        assertEquals(DETAILS,
                creditorsAfterMoreThanOneYearEntity.getData().getCurrentPeriodEntity().getDetails());

        assertEquals(new HashMap<>(), creditorsAfterMoreThanOneYearEntity.getData().getLinks());
        assertEquals(ETAG, creditorsAfterMoreThanOneYearEntity.getData().getEtag());
        assertEquals(KIND, creditorsAfterMoreThanOneYearEntity.getData().getKind());

        if (creditorsAfterMoreThanOneYearEntity.getData().getPreviousPeriodEntity() != null) {
            assertEquals(BANK_LOANS_AND_OVERDRAFTS,
                    creditorsAfterMoreThanOneYearEntity.getData().getPreviousPeriodEntity().getBankLoansAndOverdrafts());
            assertEquals(OTHER_CREDITORS,
                    creditorsAfterMoreThanOneYearEntity.getData().getPreviousPeriodEntity().getOtherCreditors());
            assertEquals(FINANCE_LEASE,
                    creditorsAfterMoreThanOneYearEntity.getData().getPreviousPeriodEntity().getFinanceLeasesAndHirePurchaseContracts());
            assertEquals(TOTAL,
                    creditorsAfterMoreThanOneYearEntity.getData().getPreviousPeriodEntity().getTotal());
        }
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

    private void assertFieldsMappedToRest(CreditorsAfterMoreThanOneYear creditorsAfterMoreThanOneYear) {

        assertEquals(BANK_LOANS_AND_OVERDRAFTS, creditorsAfterMoreThanOneYear.getCurrentPeriod().getBankLoansAndOverdrafts());
        assertEquals(OTHER_CREDITORS, creditorsAfterMoreThanOneYear.getCurrentPeriod().getOtherCreditors());
        assertEquals(FINANCE_LEASE, creditorsAfterMoreThanOneYear.getCurrentPeriod().getFinanceLeasesAndHirePurchaseContracts());
        assertEquals(TOTAL, creditorsAfterMoreThanOneYear.getCurrentPeriod().getTotal());
        assertEquals(DETAILS, creditorsAfterMoreThanOneYear.getCurrentPeriod().getDetails());

        assertEquals(new HashMap<>(), creditorsAfterMoreThanOneYear.getLinks());
        assertEquals(ETAG, creditorsAfterMoreThanOneYear.getEtag());
        assertEquals(KIND, creditorsAfterMoreThanOneYear.getKind());

        if (creditorsAfterMoreThanOneYear.getPreviousPeriod() != null) {

            assertEquals(BANK_LOANS_AND_OVERDRAFTS, creditorsAfterMoreThanOneYear.getPreviousPeriod().getBankLoansAndOverdrafts());
            assertEquals(OTHER_CREDITORS, creditorsAfterMoreThanOneYear.getPreviousPeriod().getOtherCreditors());
            assertEquals(FINANCE_LEASE, creditorsAfterMoreThanOneYear.getPreviousPeriod().getFinanceLeasesAndHirePurchaseContracts());
            assertEquals(TOTAL, creditorsAfterMoreThanOneYear.getPreviousPeriod().getTotal());
        }

    }
}
