package uk.gov.companieshouse.api.accounts.transformer.smallfull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.api.accounts.model.entity.notes.debtors.CurrentPeriodEntity;
import uk.gov.companieshouse.api.accounts.model.entity.notes.debtors.DebtorsDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.notes.debtors.DebtorsEntity;
import uk.gov.companieshouse.api.accounts.model.entity.notes.debtors.PreviousPeriodEntity;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.debtors.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.debtors.Debtors;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.debtors.PreviousPeriod;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class DebtorsTransformerTest {

    private static final String ETAG = "etag";
    private static final String KIND = "kind";
    private static final String DETAILS = "debtors note text";
    private static final Long GREATER_THAT_ONE_YEAR = 1L;
    private static final Long OTHER_DEBTORS = 2L;
    private static final Long PREPAYMENTS = 3L;
    private static final Long TRADE_DEBTORS = 4L;
    private static final Long TOTAL = 10L;

    private static final Long GREATER_THAT_ONE_YEAR_PREVIOUS = 2L;
    private static final Long OTHER_DEBTORS_PREVIOUS = 4L;
    private static final Long PREPAYMENTS_PREVIOUS = 6L;
    private static final Long TRADE_DEBTORS_PREVIOUS = 8L;
    private static final Long TOTAL_PREVIOUS = 20L;

    private DebtorsTransformer debtorsTransformer = new DebtorsTransformer();

    @Test
    @DisplayName("Tests debtors transformer with empty object which should result in null values")
    public void testTransformerWithEmptyObject() {

        DebtorsEntity debtorsEntity = debtorsTransformer.transform(new Debtors());

        assertNotNull(debtorsEntity);
        assertNull(debtorsEntity.getData().getEtag());
        assertEquals(new HashMap<>(), debtorsEntity.getData().getLinks());
    }

    @Test
    @DisplayName("Tests debtors transformer with empty previous period")
    public void testTransformerWithEmptyPreviousPeriod() {

        Debtors debtors = new Debtors();
        CurrentPeriod currentPeriod = new CurrentPeriod();

        debtors.setEtag(ETAG);
        debtors.setKind(KIND);
        debtors.setLinks(new HashMap<>());

        currentPeriod.setDetails(DETAILS);
        currentPeriod.setGreaterThanOneYear(GREATER_THAT_ONE_YEAR);
        currentPeriod.setOtherDebtors(OTHER_DEBTORS);
        currentPeriod.setPrepaymentsAndAccruedIncome(PREPAYMENTS);
        currentPeriod.setTradeDebtors(TRADE_DEBTORS);
        currentPeriod.setTotal(TOTAL);

        debtors.setCurrentPeriod(currentPeriod);

        DebtorsEntity debtorsEntity = debtorsTransformer.transform(debtors);

        assertNotNull(debtorsEntity);
        assertNull(debtorsEntity.getData().getPreviousPeriodEntity());
        assertEquals(new HashMap<>(), debtorsEntity.getData().getLinks());
    }

    @Test
    @DisplayName("Tests debtors transformer with populated Rest object and validates values returned")
    public void testRestToEntityTransformerWithPopulatedObject() {

        Debtors debtors = new Debtors();
        CurrentPeriod currentPeriod = new CurrentPeriod();
        PreviousPeriod previousPeriod = new PreviousPeriod();

        debtors.setEtag(ETAG);
        debtors.setKind(KIND);
        debtors.setLinks(new HashMap<>());

        currentPeriod.setDetails(DETAILS);
        currentPeriod.setGreaterThanOneYear(GREATER_THAT_ONE_YEAR);
        currentPeriod.setOtherDebtors(OTHER_DEBTORS);
        currentPeriod.setPrepaymentsAndAccruedIncome(PREPAYMENTS);
        currentPeriod.setTradeDebtors(TRADE_DEBTORS);
        currentPeriod.setTotal(TOTAL);

        previousPeriod.setGreaterThanOneYear(GREATER_THAT_ONE_YEAR_PREVIOUS);
        previousPeriod.setOtherDebtors(OTHER_DEBTORS_PREVIOUS);
        previousPeriod.setPrepaymentsAndAccruedIncome(PREPAYMENTS_PREVIOUS);
        previousPeriod.setTradeDebtors(TRADE_DEBTORS_PREVIOUS);
        previousPeriod.setTotal(TOTAL_PREVIOUS);

        debtors.setCurrentPeriod(currentPeriod);
        debtors.setPreviousPeriod(previousPeriod);

        DebtorsEntity debtorsEntity = debtorsTransformer.transform(debtors);
        DebtorsDataEntity debtorsDataEntity = debtorsEntity.getData();

        assertEquals(ETAG, debtorsDataEntity.getEtag());
        assertEquals(KIND, debtorsDataEntity.getKind());
        assertEquals(new HashMap<>(), debtorsDataEntity.getLinks());

        assertEquals(DETAILS, debtorsDataEntity.getCurrentPeriodEntity().getDetails());
        assertEquals(GREATER_THAT_ONE_YEAR, debtorsDataEntity.getCurrentPeriodEntity().getGreaterThanOneYear());
        assertEquals(OTHER_DEBTORS, debtorsDataEntity.getCurrentPeriodEntity().getOtherDebtors());
        assertEquals(PREPAYMENTS, debtorsDataEntity.getCurrentPeriodEntity().getPrepaymentsAndAccruedIncome());
        assertEquals(TRADE_DEBTORS, debtorsDataEntity.getCurrentPeriodEntity().getTradeDebtors());
        assertEquals(TOTAL, debtorsDataEntity.getCurrentPeriodEntity().getTotal());

        assertEquals(GREATER_THAT_ONE_YEAR_PREVIOUS, debtorsDataEntity.getPreviousPeriodEntity().getGreaterThanOneYear());
        assertEquals(OTHER_DEBTORS_PREVIOUS, debtorsDataEntity.getPreviousPeriodEntity().getOtherDebtors());
        assertEquals(PREPAYMENTS_PREVIOUS, debtorsDataEntity.getPreviousPeriodEntity().getPrepaymentsAndAccruedIncome());
        assertEquals(TRADE_DEBTORS_PREVIOUS, debtorsDataEntity.getPreviousPeriodEntity().getTradeDebtors());
        assertEquals(TOTAL_PREVIOUS, debtorsDataEntity.getPreviousPeriodEntity().getTotal());
    }

    @Test
    @DisplayName("Tests debtors transformer with populated Entity object and validates values returned")
    public void testEntityToRestTransformerWithPopulatedObject() {

        DebtorsEntity debtorsEntity = new DebtorsEntity();
        DebtorsDataEntity debtorsDataEntity = new DebtorsDataEntity();
        CurrentPeriodEntity currentPeriodEntity = new CurrentPeriodEntity();
        PreviousPeriodEntity previousPeriodEntity = new PreviousPeriodEntity();

        debtorsDataEntity.setEtag(ETAG);
        debtorsDataEntity.setKind(KIND);
        debtorsDataEntity.setLinks(new HashMap<>());

        currentPeriodEntity.setDetails(DETAILS);
        currentPeriodEntity.setGreaterThanOneYear(GREATER_THAT_ONE_YEAR);
        currentPeriodEntity.setOtherDebtors(OTHER_DEBTORS);
        currentPeriodEntity.setPrepaymentsAndAccruedIncome(PREPAYMENTS);
        currentPeriodEntity.setTradeDebtors(TRADE_DEBTORS);
        currentPeriodEntity.setTotal(TOTAL);

        previousPeriodEntity.setGreaterThanOneYear(GREATER_THAT_ONE_YEAR_PREVIOUS);
        previousPeriodEntity.setOtherDebtors(OTHER_DEBTORS_PREVIOUS);
        previousPeriodEntity.setPrepaymentsAndAccruedIncome(PREPAYMENTS_PREVIOUS);
        previousPeriodEntity.setTradeDebtors(TRADE_DEBTORS_PREVIOUS);
        previousPeriodEntity.setTotal(TOTAL_PREVIOUS);

        debtorsDataEntity.setPreviousPeriodEntity(previousPeriodEntity);
        debtorsDataEntity.setCurrentPeriodEntity(currentPeriodEntity);
        debtorsEntity.setData(debtorsDataEntity);
        Debtors debtors = debtorsTransformer.transform(debtorsEntity);

        assertNotNull(debtors);
        assertEquals(KIND, debtors.getKind());
        assertEquals(ETAG, debtors.getEtag());
        assertEquals(new HashMap<>(), debtors.getLinks());

        assertEquals(DETAILS, debtors.getCurrentPeriod().getDetails());
        assertEquals(GREATER_THAT_ONE_YEAR, debtors.getCurrentPeriod().getGreaterThanOneYear());
        assertEquals(OTHER_DEBTORS, debtors.getCurrentPeriod().getOtherDebtors());
        assertEquals(PREPAYMENTS, debtors.getCurrentPeriod().getPrepaymentsAndAccruedIncome());
        assertEquals(TRADE_DEBTORS, debtors.getCurrentPeriod().getTradeDebtors());
        assertEquals(TOTAL, debtors.getCurrentPeriod().getTotal());

        assertEquals(GREATER_THAT_ONE_YEAR_PREVIOUS, debtors.getPreviousPeriod().getGreaterThanOneYear());
        assertEquals(OTHER_DEBTORS_PREVIOUS, debtors.getPreviousPeriod().getOtherDebtors());
        assertEquals(PREPAYMENTS_PREVIOUS, debtors.getPreviousPeriod().getPrepaymentsAndAccruedIncome());
        assertEquals(TRADE_DEBTORS_PREVIOUS, debtors.getPreviousPeriod().getTradeDebtors());
        assertEquals(TOTAL_PREVIOUS, debtors.getPreviousPeriod().getTotal());
    }
}
